package com.vonchange.jdbc.abstractjdbc.core;


import com.vonchange.common.util.Assert;
import com.vonchange.common.util.ConvertUtil;
import com.vonchange.common.util.MarkdownUtil;
import com.vonchange.common.util.StringPool;
import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.jdbc.abstractjdbc.config.ConstantJdbc;
import com.vonchange.jdbc.abstractjdbc.config.Constants;
import com.vonchange.jdbc.abstractjdbc.count.CountSqlParser;
import com.vonchange.jdbc.abstractjdbc.handler.AbstractMapPageWork;
import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import com.vonchange.jdbc.abstractjdbc.handler.BeanHandler;
import com.vonchange.jdbc.abstractjdbc.handler.BeanInsertHandler;
import com.vonchange.jdbc.abstractjdbc.handler.BeanListHandler;
import com.vonchange.jdbc.abstractjdbc.handler.BigDataBeanListHandler;
import com.vonchange.jdbc.abstractjdbc.handler.BigDataMapListHandler;
import com.vonchange.jdbc.abstractjdbc.handler.MapBeanListHandler;
import com.vonchange.jdbc.abstractjdbc.handler.MapHandler;
import com.vonchange.jdbc.abstractjdbc.handler.MapListHandler;
import com.vonchange.jdbc.abstractjdbc.handler.ScalarHandler;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.jdbc.abstractjdbc.template.MyJdbcTemplate;
import com.vonchange.jdbc.abstractjdbc.util.ConvertMap;
import com.vonchange.jdbc.abstractjdbc.util.NameQueryUtil;
import com.vonchange.jdbc.abstractjdbc.util.sql.SqlFill;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.tpl.EntityUtil;
import com.vonchange.mybatis.tpl.MybatisTpl;
import com.vonchange.mybatis.tpl.model.EntityField;
import com.vonchange.mybatis.tpl.model.EntityInfo;
import com.vonchange.mybatis.tpl.model.SqlWithParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * jdbc core
 * by von_change
 */
public abstract class AbstractJdbcCore implements JdbcRepository {

    private static final Logger log = LoggerFactory.getLogger(AbstractJdbcCore.class);

    protected abstract Dialect getDefaultDialect();

    protected abstract DataSourceWrapper getWriteDataSource();


    protected abstract boolean logRead();

    protected abstract boolean logWrite();

    protected abstract boolean logFullSql();



    private Dialect dialect = null;

    private static final Integer batchSizeDefault=500;

    private Dialect getDialect(DataSourceWrapper dataSourceWrapper) {
        if (null != dataSourceWrapper && null != dataSourceWrapper.getDialect()) {
            return dataSourceWrapper.getDialect();
        }
        if (null == dialect) {
            dialect = getDefaultDialect();
        }
        return dialect;
    }

    private MyJdbcTemplate initJdbcTemplate(DataSourceWrapper dataSourceWrapper, Constants.EnumRWType enumRWType) {
        return getJdbcTemplate(dataSourceWrapper, enumRWType);
    }

    private static final Map<String, MyJdbcTemplate> myJdbcTemplateMap = new ConcurrentHashMap<>();

    private DataSourceWrapper getDataSourceWrapper(DataSourceWrapper dataSourceWrapper, Constants.EnumRWType enumRWType) {
        if (null != dataSourceWrapper) {
            return dataSourceWrapper;
        }
        if (enumRWType.equals(Constants.EnumRWType.read)) {
            return getReadDataSource();
        }
        return getWriteDataSource();
    }

    private MyJdbcTemplate getJdbcTemplate(DataSourceWrapper dataSourceWrapper, Constants.EnumRWType enumRWType) {
        DataSourceWrapper dataSource = getDataSourceWrapper(dataSourceWrapper, enumRWType);
        if (myJdbcTemplateMap.containsKey(dataSource.getKey())) {
            log.debug("\n====== use dataSource {}", dataSource.getKey());
            return myJdbcTemplateMap.get(dataSource.getKey());
        }
        log.debug("\n====== init dataSource {}", dataSource.getKey());
        return initJdbcTemplate(dataSource);
    }
    private synchronized  MyJdbcTemplate initJdbcTemplate(DataSourceWrapper dataSource){
        MyJdbcTemplate myJdbcTemplate = new MyJdbcTemplate(dataSource.getDataSource());
        myJdbcTemplate.setFetchSizeBigData(getDialect(dataSource).getBigDataFetchSize());
        myJdbcTemplate.setFetchSize(getDialect(dataSource).getFetchSize());
        myJdbcTemplateMap.put(dataSource.getKey(), myJdbcTemplate);
        return myJdbcTemplate;
    }

    public final <T> int insertBatch(List<T> entityList, boolean ifNullInsert,int batchSize) {
        return insertBatch(null, entityList,ifNullInsert, batchSize);
    }

    public final <T> int insertBatch(DataSourceWrapper dataSourceWrapper, List<T> entityList, boolean ifNullInsert, int batchSize) {
        if (null == entityList || entityList.isEmpty()) {
            return 0;
        }
        SqlWithParam sqlParameter = generateInsertSql(entityList.get(0),ifNullInsert);
        String sql = sqlParameter.getSql();
        List<Object[]> list = new ArrayList<>();
        int i = 0;
        if (batchSize <= 0) {
            batchSize = batchSizeDefault;
        }
        List<List<Object[]>> listSplit = new ArrayList<>();
        for (T t : entityList) {
            if (i != 0 && i % batchSize == 0) {
                listSplit.add(list);
                list = new ArrayList<>();
            }
            list.add(generateInsertSqlParams(t,ifNullInsert));
            i++;
        }
        if (!list.isEmpty()) {
            listSplit.add(list);
        }
        int num = 0;
        for (List<Object[]> item : listSplit) {
            int[] result = updateBatch(dataSourceWrapper, sql, item);
            num += result.length;
            log.debug("\ninsertBatch {}", result);
        }
        return num;
    }

    public final <T> int insert(DataSourceWrapper dataSourceWrapper, T entity) {
        SqlWithParam sqlParameter = generateInsertSql(entity,false);
        return insert(dataSourceWrapper, entity, sqlParameter.getSql(), sqlParameter.getColumnReturns(),
                sqlParameter.getParams());
    }

    public final <T> int insert(T entity) {
        return insert(null, entity);
    }

    public final <T> int update(DataSourceWrapper dataSourceWrapper, T entity) {
        SqlWithParam sqlParameter = generateUpdateEntitySql(entity, false);
        return update(dataSourceWrapper, sqlParameter.getSql(), sqlParameter.getParams());
    }

    public final <T> int update(T entity) {
        return update(null, entity);
    }

    public final <T> int updateAllField(DataSourceWrapper dataSourceWrapper, T entity) {
        SqlWithParam sqlParameter = generateUpdateEntitySql(entity, true);
        return update(dataSourceWrapper, sqlParameter.getSql(), sqlParameter.getParams());
    }

    public final <T> int updateAllField(T entity) {
        return updateAllField(null, entity);
    }


    private void initEntityInfo(Class<?> clazz) {
        EntityUtil.initEntityInfo(clazz);
    }

    private static Object getPublicPro(Object bean, String name) {
        return BeanUtil.getProperty(bean, name);
    }

    private <T> Object[] generateInsertSqlParams(T entity,boolean ifNullInsert) {
        initEntityInfo(entity.getClass());
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
        List<EntityField> entityFieldList = entityInfo.getEntityFields();
        List<Object> values = new ArrayList<>();
        Object value ;
        for (EntityField entityField : entityFieldList) {
             value= BeanUtil.getProperty(entity,entityField.getFieldName());
             if(insertColumn(entityField,value,ifNullInsert)){
                 values.add(value);
             }
        }
        return values.toArray();
    }
    private boolean insertColumn(EntityField entityField,Object value,boolean ifNullInsert){
        if(entityField.getIsId()){
            if(null!=value){
                return true;
            }
        }
        if(entityField.getIsColumn()){
            if(null!=value){
               return true;
            }
            return ifNullInsert;
        }
        return false;
    }

    private <T> SqlWithParam generateInsertSql(T entity,boolean ifNullInsert) {
        initEntityInfo(entity.getClass());
        SqlWithParam sqlParameter = new SqlWithParam();
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
        String tableName = entityInfo.getTableName();
        List<String> columns=new ArrayList<>();
        List<String> params=new ArrayList<>();
        List<Object> values=new ArrayList<>();
        List<EntityField> entityFieldList = entityInfo.getEntityFields();
        Object value;
        for (EntityField entityField : entityFieldList) {
            if(entityField.getIsColumn()){
                value= BeanUtil.getProperty(entity,entityField.getFieldName());
                if(insertColumn(entityField,value,ifNullInsert)){
                    columns.add(entityField.getColumnName());
                    params.add(StringPool.QUESTION_MARK);
                    values.add(value);
                }
            }
        }
        String insertSql = UtilAll.UString.format("insert into {}({}) values ({})", tableName,
                String.join(StringPool.COMMA,columns),String.join(StringPool.COMMA,params));
        sqlParameter.setColumnReturns(entityInfo.getColumnReturns());
        sqlParameter.setSql(insertSql);
        sqlParameter.setParams(values.toArray());
        return sqlParameter;
    }


    private <T> SqlWithParam generateUpdateEntitySql(T entity, boolean isNullUpdate) {
        SqlWithParam sqlParameter = new SqlWithParam();
        initEntityInfo(entity.getClass());
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
        String tableName = entityInfo.getTableName();
        String idColumnName = entityInfo.getIdColumnName();
        if (null == idColumnName) {
            throw new JdbcMybatisRuntimeException("need entity field @ID");
        }
        Object idValue = getPublicPro(entity, entityInfo.getIdFieldName());
        if (null == idValue) {
            throw new JdbcMybatisRuntimeException("ID value is null,can not update");
        }
        List<EntityField> entityFieldList = entityInfo.getEntityFields();
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Object value;
        for (EntityField entityField : entityFieldList) {
            value= BeanUtil.getProperty(entity,entityField.getFieldName());
            if(entityField.getIsColumn()&&!entityField.getIsId()){
                if(null!=value){
                    columns.add(UtilAll.UString.format("{} = {}",entityField.getColumnName(),StringPool.QUESTION_MARK));
                    values.add(value);
                    continue;
                }
                if(isNullUpdate&&!entityField.getUpdateNotNull()){
                    columns.add(UtilAll.UString.format("{} = {}",entityField.getColumnName(),StringPool.QUESTION_MARK));
                    values.add(null);
                }
            }
        }
        values.add(idValue);
        // 0:tableName 1:setSql 2:idName
        String sql = UtilAll.UString.format("update {} set {} where {} = ?", tableName,
                String.join(StringPool.COMMA,columns), idColumnName);
        sqlParameter.setSql(sql);
        sqlParameter.setParams(values.toArray());
        return sqlParameter;
    }

    // crud end
    public final <T,ID> T queryById(DataSourceWrapper dataSourceWrapper, Class<T> type, ID id) {
        SqlWithParam sqlWithParam = genIdEqQuery(true,false,type,id);
        return queryOne(dataSourceWrapper, type, sqlWithParam.getSql(), id);
    }
    @Override
    public <T, ID> List<T> queryByIds(DataSourceWrapper dataSourceWrapper, Class<T> domainType, List<ID> ids) {
        SqlWithParam sqlWithParam = genIdInQuery(true,domainType,ids);
        return queryList(dataSourceWrapper, domainType, sqlWithParam.getSql(), sqlWithParam.getParams());
    }

    public final <T,ID> T queryById(Class<T> type, ID id) {
        return queryById(null, type, id);
    }
    public  <ID> int deleteById(DataSourceWrapper dataSourceWrapper, Class<?> domainType, ID id){
        SqlWithParam sqlWithParam = genIdEqQuery(false,false,domainType,id);
        return update(dataSourceWrapper,sqlWithParam.getSql(), id);
    }

    public <ID> int deleteByIds(DataSourceWrapper dataSourceWrapper, Class<?> domainType, List<ID> ids){
        SqlWithParam sqlWithParam = genIdInQuery(false,domainType,ids);
        return update(dataSourceWrapper,sqlWithParam.getSql(), sqlWithParam.getParams());
    }

    public <ID> boolean existsById(DataSourceWrapper dataSourceWrapper, Class<?> domainType, ID id){
        SqlWithParam sqlWithParam = genIdEqQuery(true,true,domainType,id);
        return ConvertUtil.toBoolean(queryOneColumn(dataSourceWrapper,sqlWithParam.getSql(),1,id));
    }


    private <ID> SqlWithParam genIdEqQuery(boolean select,boolean count,Class<?> type,ID id){
        if(null==id){
            throw new JdbcMybatisRuntimeException("id must not null");
        }
        initEntityInfo(type);
        EntityInfo entityInfo = EntityUtil.getEntityInfo(type);
        String columnStr=null;
        if(select){
            if(count){
                columnStr="count(1)";
            }else{
                List<EntityField> entityFieldList = entityInfo.getEntityFields();
                columnStr= entityFieldList.stream().filter(EntityField::getIsColumn).map(EntityField::getColumnName)
                        .collect(Collectors.joining(StringPool.COMMA));
            }
        }
        String idName = entityInfo.getIdColumnName();
        Assert.notNull(idName,"@Id must not null");
        String sql="delete";
        if(select){
            sql="select "+columnStr;
        }
        sql= sql+UtilAll.UString.format(
                " from {} where  {} = ?",   entityInfo.getTableName(),
                idName);
        SqlWithParam sqlWithParam = new SqlWithParam();
        sqlWithParam.setParams(new Object[]{id});
        sqlWithParam.setSql(sql);
        return sqlWithParam;
    }
    private <ID> SqlWithParam genIdInQuery(boolean select, Class<?> type, List<ID> ids){
        if(null==ids||ids.isEmpty()){
            throw new JdbcMybatisRuntimeException("ids must not empty");
        }
        initEntityInfo(type);
        EntityInfo entityInfo = EntityUtil.getEntityInfo(type);
        String columnStr=null;
        if(select){
            List<EntityField> entityFieldList = entityInfo.getEntityFields();
            columnStr= entityFieldList.stream().filter(EntityField::getIsColumn).map(EntityField::getColumnName)
                    .collect(Collectors.joining(StringPool.COMMA));
        }
        String idName = entityInfo.getIdColumnName();
        Assert.notNull(idName,"@Id must not null");
        StringBuilder idParam=new StringBuilder("(");
        for (ID id : ids) {
            idParam.append("?").append(",");
        }
        String sql="delete";
        if(select){
            sql="select "+columnStr;
        }
        sql= sql+UtilAll.UString.format(
                " from {} where  {} in {}",  entityInfo.getTableName(),
                idName,idParam.substring(0,idParam.length()-1)+")");
        SqlWithParam sqlWithParam = new SqlWithParam();
        sqlWithParam.setParams(ids.toArray());
        sqlWithParam.setSql(sql);
        return sqlWithParam;
    }

    public final <T> List<T> queryList(DataSourceWrapper dataSourceWrapper, Class<T> type, String sqlId,
            Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, parameter);
        return queryList(dataSourceWrapper, type, sqlParameter.getSql(), sqlParameter.getParams());
    }

    public final <T> List<T> queryList(Class<T> type, String sqlId, Map<String, Object> parameter) {
        return queryList(null, type, sqlId, parameter);
    }

    public <T> T queryOne(DataSourceWrapper dataSourceWrapper, Class<T> type, String sqlId,
            Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper,sqlId, parameter);
        List<T> list = queryList(dataSourceWrapper, type, sqlParameter.getSql(), sqlParameter.getParams());
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            log.warn("{} expect one row but found {} rows", sqlId, list.size());
        }
        return list.get(0);
    }

    public <T> T queryOne(Class<T> type, String sqlId, Map<String, Object> parameter) {
        return queryOne(null, type, sqlId, parameter);
    }

    public Map<String, Object> queryMapOne(DataSourceWrapper dataSourceWrapper, String sqlId,
            Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, parameter);
        List<Map<String, Object>> list = queryListResultMap(dataSourceWrapper, sqlParameter.getSql(),
                sqlParameter.getParams());
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            log.warn("{} expect one row but found {} rows", sqlId, list.size());
        }
        return list.get(0);
    }

    public Map<String, Object> queryMapOne(String sqlId, Map<String, Object> parameter) {
        return queryMapOne(null, sqlId, parameter);
    }

    public final List<Map<String, Object>> queryMapList(DataSourceWrapper dataSourceWrapper, String sqlId,
            Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, parameter);
        return queryListResultMap(dataSourceWrapper, sqlParameter.getSql(), sqlParameter.getParams());
    }

    public final List<Map<String, Object>> queryMapList(String sqlId, Map<String, Object> parameter) {
        return queryMapList(null, sqlId, parameter);
    }

    public final void queryMapBigData(String sqlId, AbstractMapPageWork pageWork, Map<String, Object> parameter) {
        queryMapBigData(null, sqlId, pageWork, parameter);
    }

    public final void queryMapBigData(DataSourceWrapper dataSourceWrapper, String sqlId, AbstractMapPageWork pageWork,
            Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper,sqlId, parameter);
        queryForBigData(dataSourceWrapper, sqlParameter.getSql(), pageWork, sqlParameter.getParams());
    }

    public final <T> void queryBigData(Class<T> type, String sqlId, AbstractPageWork<T> pageWork,
            Map<String, Object> parameter) {
        queryBigData(null, type, sqlId, pageWork, parameter);
    }

    public final <T> void queryBigData(DataSourceWrapper dataSourceWrapper, Class<? extends T> type, String sqlId,
            AbstractPageWork<T> pageWork, Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, parameter);
        queryForBigData(dataSourceWrapper, type, sqlParameter.getSql(), pageWork, sqlParameter.getParams());
    }

    public final Page<Map<String, Object>> queryMapPage(String sqlId, Pageable pageable,
            Map<String, Object> parameter) {
        return queryMapPage(null, sqlId, pageable, parameter);
    }

    public final Page<Map<String, Object>> queryMapPage(DataSourceWrapper dataSourceWrapper, String sqlId,
            Pageable pageable, Map<String, Object> parameter) {
        SqlWithParam sqlParameter = pageSql(dataSourceWrapper,sqlId,parameter,pageable);
        List<Map<String, Object>> entities = queryListResultMap(dataSourceWrapper, sqlParameter.getSql(), sqlParameter.getParams());
        return new PageImpl<>(entities, pageable, sqlParameter.getTotal());
    }

    public <T> Page<T> queryPage(Class<T> type, String sqlId, Pageable pageable, Map<String, Object> parameter) {
        return queryPage(null, type, sqlId, pageable, parameter);
    }

    public <T> Page<T> queryPage(DataSourceWrapper dataSourceWrapper, Class<T> type, String sqlId, Pageable pageable,
            Map<String, Object> parameter) {
        SqlWithParam sqlParameter = pageSql(dataSourceWrapper,sqlId,parameter,pageable);
        List<T> entities = queryList(dataSourceWrapper, type, sqlParameter.getSql(), sqlParameter.getParams());
        return new PageImpl<>(entities, pageable, sqlParameter.getTotal());
    }
    private SqlWithParam pageSql(DataSourceWrapper dataSourceWrapper, String sqlId, Map<String, Object> parameter,
                            Pageable pageable){
        String countSqlId=sqlId+ConstantJdbc.COUNTFLAG;
        String countSql = MarkdownUtil.getContent(sqlId+ConstantJdbc.COUNTFLAG,false);
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, parameter);
        String sql = sqlParameter.getSql();
        long totalCount = 0L;
        if (null!=countSql) {
            totalCount=queryOneColumn(dataSourceWrapper,Long.class,countSqlId,parameter);
        }else{
            countSql = generateMyCountSql(sql);
            Object result = queryOneColumn(dataSourceWrapper, countSql, 1, sqlParameter.getParams());
            totalCount=ConvertUtil.toLong(result);
        }
        int pageNum = Math.max(pageable.getPageNumber(), 0);
        int firstEntityIndex = pageable.getPageSize() * pageNum;
        sql = getDialect(dataSourceWrapper).getPageSql(sql, firstEntityIndex, pageable.getPageSize());
        sqlParameter.setTotal(totalCount);
        sqlParameter.setSql(sql);
        return sqlParameter;
    }

    private boolean hasLimit(String sql) {
        String lowerSql = sql.toLowerCase();
        return lowerSql.contains("limit ") || lowerSql.contains("limit\n");
    }

    private long countMySqlResult(DataSourceWrapper dataSourceWrapper, String sql, String countSql,
            Map<String, Object> params) {
        Object result = null;
        if (!UtilAll.UString.isBlank(countSql)) {
            result = findBy(dataSourceWrapper, countSql, params);
        }
        if (UtilAll.UString.isBlank(countSql)) {
            SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sql, params);
            countSql = generateMyCountSql(sqlParameter.getSql());
            result = queryOneColumn(dataSourceWrapper, countSql, 1, sqlParameter.getParams());
        }
        if (null == result) {
            return 0L;
        }
        return ConvertUtil.toLong(result);
    }

    public <T> T queryOneColumn(Class<?> targetType, String sqlId, Map<String, Object> parameter) {
        return queryOneColumn(null, targetType, sqlId, parameter);
    }

    public <T> T queryOneColumn(DataSourceWrapper dataSourceWrapper, Class<?> targetType, String sqlId,
            Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, parameter);
        Object result = queryOneColumn(dataSourceWrapper, sqlParameter.getSql(), 1, sqlParameter.getParams());
        return ConvertUtil.toObject(result, targetType);
    }

    private Object findBy(DataSourceWrapper dataSourceWrapper, String sql, Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sql, parameter);
        return queryOneColumn(dataSourceWrapper, sqlParameter.getSql(), 1, sqlParameter.getParams());
    }

    private SqlWithParam getSqlParameter(DataSourceWrapper dataSourceWrapper,String sqlId,
            Map<String, Object> parameter) {
        if(!sqlId.contains(StringPool.DOT)){
            //namedQuery
            if(!parameter.containsKey(ConstantJdbc.EntityType)){
                throw new JdbcMybatisRuntimeException("This method is not supported for converting to SQL");
            }
            Class<?> entityType= (Class<?>) parameter.get(ConstantJdbc.EntityType);
            parameter.remove(ConstantJdbc.EntityType);
            SqlWithParam sqlWithParam= NameQueryUtil.nameSql(sqlId,entityType,parameter);
            if(null==sqlWithParam){
                throw new JdbcMybatisRuntimeException("{} can not generate sql by method name,please define in the markdown",sqlId);
            }
            return sqlWithParam;
        }
        return MybatisTpl.generate(sqlId,parameter, getDialect(dataSourceWrapper));
    }

    public <T> Map<String, T> queryToMap(Class<T> c, String sqlId, String keyInMap, Map<String, Object> parameter) {
        return queryToMap(null, c, sqlId, keyInMap, parameter);
    }

    public <T> Map<String, T> queryToMap(DataSourceWrapper dataSourceWrapper, Class<T> c, String sqlId, String keyInMap,
            Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, parameter);
        return queryMapList(dataSourceWrapper, c, sqlParameter.getSql(), keyInMap, sqlParameter.getParams());
    }


    private String generateMyCountSql(String sql) {
        StringBuilder sb = new StringBuilder();
        Matcher m = Pattern.compile("(/\\*)([\\w\\s\\@\\:]*)(\\*/)").matcher(sql);
        while (m.find()) {
            String group = m.group();
            sb.append(group);
        }
        CountSqlParser countSqlParser = new CountSqlParser();
        return sb + countSqlParser.getSmartCountSql(sql);
    }

    public <T> int batchUpdate(String sqlId, List<T> entityList, int size) {
        return batchUpdate(null, sqlId, entityList, size);
    }

    public <T> int batchUpdate(DataSourceWrapper dataSourceWrapper, String sqlId, List<T> entityList, int size) {
        if (null == entityList || entityList.isEmpty()) {
            return 0;
        }

        Map<String, Object> map = new LinkedHashMap<>();
        try {
            T entity = entityList.get(0);
            map = ConvertMap.toMap(entity, entity.getClass());
        } catch (IntrospectionException e) {
            log.error("IntrospectionException ", e);
        }
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, map);
        String sql = sqlParameter.getSql();
        List<Object[]> param = new ArrayList<>();
        int i = 0;
        List<List<Object[]>> listSplit = new ArrayList<>();
        int batchSize = size;
        if (size <= 0) {
            batchSize = batchSizeDefault;
        }
        for (T t : entityList) {
            if (i != 0 && i % batchSize == 0) {
                listSplit.add(param);
                param = new ArrayList<>();
            }
            param.add(beanToObjects(t, sqlParameter.getPropertyNames()));
            i++;
        }
        if (!param.isEmpty()) {
            listSplit.add(param);
        }
        int updateNum = 0;
        for (List<Object[]> item : listSplit) {
            int[] result = updateBatch(dataSourceWrapper, sql, item);
            log.debug("\nbatchUpdateBySql {}", result);
            updateNum += result.length;
        }
        return updateNum;
    }

    private <T> Object[] beanToObjects(T t, List<String> propertyNames) {
        List<Object> result = new ArrayList<>();
        for (String propertyName : propertyNames) {
            result.add(BeanUtil.getProperty(t, propertyName));
        }
        return result.toArray();
    }

    public int update(String sqlId, Map<String, Object> parameter) {
        return update(null, sqlId, parameter);
    }

    public int update(DataSourceWrapper dataSourceWrapper, String sqlId, Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, parameter);
        return update(dataSourceWrapper, sqlParameter.getSql(), sqlParameter.getParams());
    }

    public int insert(String sqlId, Map<String, Object> parameter) {
        return insert(null, sqlId, parameter);
    }

    public int insert(DataSourceWrapper dataSourceWrapper, String sqlId, Map<String, Object> parameter) {
        SqlWithParam sqlParameter = getSqlParameter(dataSourceWrapper, sqlId, parameter);
        return insert(dataSourceWrapper, sqlParameter.getSql(), sqlParameter.getParams());
    }

    // ================================= base
    public <T> List<T> queryList(DataSourceWrapper dataSourceWrapper, Class<T> type, String sql, Object... args) {
        logSql(Constants.EnumRWType.read, sql, args);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.read);
        return jdbcTemplate.query(sql, new BeanListHandler<>(type), args);
    }

    public List<Map<String, Object>> queryListResultMap(DataSourceWrapper dataSourceWrapper, String sql,
            Object... args) {
        logSql(Constants.EnumRWType.read, sql, args);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.read);
        return jdbcTemplate.query(sql, new MapListHandler(sql), args);
    }

    public void queryForBigData(DataSourceWrapper dataSourceWrapper, String sql, AbstractMapPageWork pageWork,
            Object... args) {
        logSql(Constants.EnumRWType.read, sql, args);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.read);
        jdbcTemplate.queryBigData(sql, new BigDataMapListHandler(pageWork, sql), args);
    }

    public <T> void queryForBigData(DataSourceWrapper dataSourceWrapper, Class<? extends T> type, String sql,
            AbstractPageWork<T> pageWork, Object... args) {
        logSql(Constants.EnumRWType.read, sql, args);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.read);
        jdbcTemplate.queryBigData(sql, new BigDataBeanListHandler<T>(type, pageWork, sql), args);
    }

    public <T> T queryOne(DataSourceWrapper dataSourceWrapper, Class<T> type, String sql, Object... args) {
        logSql(Constants.EnumRWType.read, sql, args);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.read);
        return jdbcTemplate.query(sql, new BeanHandler<>(type), args);
    }

    public Map<String, Object> queryUniqueResultMap(DataSourceWrapper dataSourceWrapper, String sql, Object... args) {
        logSql(Constants.EnumRWType.read, sql, args);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.read);
        return jdbcTemplate.query(sql, new MapHandler(sql), args);
    }

    public Object queryOneColumn(DataSourceWrapper dataSourceWrapper, String sql, int columnIndex, Object... args) {
        logSql(Constants.EnumRWType.read, sql, args);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.read);
        return jdbcTemplate.query(sql, new ScalarHandler(columnIndex), args);
    }

    public <T> Map<String, T> queryMapList(DataSourceWrapper dataSourceWrapper, Class<T> c, String sql, String keyInMap,
            Object... args) {
        logSql(Constants.EnumRWType.read, sql, args);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.read);
        return jdbcTemplate.query(sql, new MapBeanListHandler<>(c, keyInMap), args);
    }

    // write
    public <T> int insert(DataSourceWrapper dataSourceWrapper, T entity, String sql, List<String> columnReturn,
            Object[] parameter) {
        logSql(Constants.EnumRWType.write, sql, parameter);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.write);
        return jdbcTemplate.insert(sql, columnReturn, new BeanInsertHandler<>(entity), parameter);
    }

    public int insert(DataSourceWrapper dataSourceWrapper, String sql, Object[] parameter) {
        logSql(Constants.EnumRWType.write, sql, parameter);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.write);
        return jdbcTemplate.insert(sql, null, new ScalarHandler(), parameter);
    }

    public int update(DataSourceWrapper dataSourceWrapper, String sql, Object... args) {
        logSql(Constants.EnumRWType.write, sql, args);
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.write);
        return jdbcTemplate.update(sql, args);
    }

    public int[] updateBatch(DataSourceWrapper dataSourceWrapper, String sql, List<Object[]> batchArgs) {
        if (null == batchArgs || batchArgs.isEmpty()) {
            return new int[0];
        }
        logSql(Constants.EnumRWType.write, sql, batchArgs.get(0));
        MyJdbcTemplate jdbcTemplate = initJdbcTemplate(dataSourceWrapper, Constants.EnumRWType.write);
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }


    private void logSql(Constants.EnumRWType enumRWType, String sql, Object... params) {
        if (log.isDebugEnabled()) {
            log.debug("\norg sql: {}\nparams: {}", sql, params);
            String sqlResult = SqlFill.fill(sql, params);
            log.debug("\nresult sql: {}", sqlResult);
        }
        if (log.isInfoEnabled()) {
            if (enumRWType.equals(Constants.EnumRWType.write) && logWrite()) {
                if (logFullSql()) {
                    String sqlResult = SqlFill.fill(sql, params);
                    log.info("\nwrite result sql: {}", sqlResult);
                }else{
                    log.info("\nwrite org sql: {}\n参数为:{}", sql, params);
                }
            }
            if (enumRWType.equals(Constants.EnumRWType.read) && logRead()) {
                if (logFullSql()) {
                    String sqlResult = SqlFill.fill(sql, params);
                    log.info("\nread result sql: {}", sqlResult);
                }else{
                    log.info("\nread org sql: {}\n参数为:{}", sql, params);
                }
            }
        }
    }

}
