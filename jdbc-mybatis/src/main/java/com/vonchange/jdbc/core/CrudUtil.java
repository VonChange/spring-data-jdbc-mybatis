package com.vonchange.jdbc.core;

import com.vonchange.common.util.ConvertUtil;
import com.vonchange.common.util.MarkdownUtil;
import com.vonchange.common.util.StringPool;
import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.jdbc.config.ConstantJdbc;
import com.vonchange.jdbc.config.EnumSqlRead;
import com.vonchange.jdbc.count.CountSqlParser;
import com.vonchange.jdbc.util.NameQueryUtil;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.tpl.EntityUtil;
import com.vonchange.mybatis.tpl.MybatisTpl;
import com.vonchange.mybatis.tpl.OrmUtil;
import com.vonchange.mybatis.tpl.model.EntityField;
import com.vonchange.mybatis.tpl.model.EntityInfo;
import com.vonchange.mybatis.tpl.model.SqlWithParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrudUtil {

    private static final Logger log = LoggerFactory.getLogger(CrudUtil.class);
    public static SqlWithParam getSqlParameter(String sqlId,
                                         Map<String, Object> parameter, Dialect dialect) {
        return MybatisTpl.generate(sqlId,parameter,dialect);
    }
    public static SqlWithParam nameQuery(String name,Class<?> entityType,
                                         List<Object> indexedParams) {
        SqlWithParam sqlWithParam= NameQueryUtil.nameSql(name,entityType,indexedParams);
        if(null==sqlWithParam){
            throw new JdbcMybatisRuntimeException("{} can not generate sql by method name,please define in the markdown",name);
        }
        return sqlWithParam;
    }

    public static  <T> SqlWithParam generateUpdateSql(T entity, boolean isNullUpdate) {
        SqlWithParam sqlParameter = new SqlWithParam();
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
        String tableName = entityInfo.getTableName();
        String idColumnName = entityInfo.getIdColumnName();
        if (null == idColumnName) {
            throw new JdbcMybatisRuntimeException("need entity field @ID");
        }
        Object idValue = BeanUtil.getPropertyT(entity,  entityInfo.getIdFieldName());
        if (null == idValue) {
            throw new JdbcMybatisRuntimeException("ID value is null,can not update");
        }
        List<EntityField> entityFieldList = entityInfo.getEntityFields();
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Object value;
        Long version=null;
        String versionColumn=null;
        for (EntityField entityField : entityFieldList) {
            if(entityField.getUpdateNot()){
                continue;
            }
            value= BeanUtil.getProperty(entity,entityField.getFieldName());
            if(entityField.getVersion()&&null!=value){
                versionColumn=entityField.getColumnName();
                version=ConvertUtil.toLong(value);
                value= version+1L;
            }
            if(entityField.getIsColumn()&&!entityField.getIsId()){
                if(null!=value){
                    columns.add(UtilAll.UString.format("{} = {}",entityField.getColumnName(),StringPool.QUESTION_MARK));
                    values.add(value);
                    continue;
                }
                if(isNullUpdate){
                    columns.add(UtilAll.UString.format("{} = {}",entityField.getColumnName(),StringPool.QUESTION_MARK));
                    values.add(null);
                }
            }
        }
        values.add(idValue);
        // 0:tableName 1:setSql 2:idName
        String sql = UtilAll.UString.format("update {} set {} where {} = ?", tableName,
                String.join(StringPool.COMMA,columns), idColumnName);
        if(null!=versionColumn){
            sql=sql+" and "+versionColumn +" = ?";
            values.add(version);
        }
        sqlParameter.setSql(sql);
        sqlParameter.setParams(values.toArray());
        return sqlParameter;
    }

    private static final Integer batchSizeDefault=2000;
    public static  <T> List<List<Object[]>> batchUpdateParam(List<T> entityList,boolean create, List<String> propertyNames,int batchSize) {
        if(CollectionUtils.isEmpty(entityList)){
            return new ArrayList<>();
        }
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entityList.get(0).getClass());
        List<Object[]> param = new ArrayList<>();
        int i = 0;
        List<List<Object[]>> listSplit = new ArrayList<>();
        if (batchSize <= 0) {
            batchSize = batchSizeDefault;
        }
        for (T t : entityList) {
            if (i != 0 && i % batchSize == 0) {
                listSplit.add(param);
                param = new ArrayList<>();
            }
            param.add(beanToObjects(entityInfo,create,t, propertyNames));
            i++;
        }
        if (!param.isEmpty()) {
            listSplit.add(param);
        }
        return listSplit;
    }
    public static  <T> List<Object[]> batchUpdateParam(List<T> entityList,boolean create, List<String> propertyNames) {
        if(CollectionUtils.isEmpty(entityList)){
            return new ArrayList<>();
        }
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entityList.get(0).getClass());
        List<Object[]> param = new ArrayList<>();
        for (T t : entityList) {
            param.add(beanToObjects(entityInfo,create,t, propertyNames));
        }
        return param;
    }
    private static <T>  Object[] beanToObjects(EntityInfo entityInfo,boolean create,T t, List<String> propertyNames) {
        List<Object> result = new ArrayList<>();
        Object value;
        for (String propertyName : propertyNames) {
            if(entityInfo.getFieldMap().containsKey(propertyName)){
                value=BeanUtil.getProperty(t, propertyName);
                if(create&&null==value&&entityInfo.getEntityFields().get(entityInfo.getFieldMap().get(propertyName)).getVersion()){
                    value=1;
                }
                result.add(value);
            }
        }
        return result.toArray();
    }


    public static  <T> SqlWithParam generateInsertSql(T entity, boolean ifNullInsert,boolean mybatis) {
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
        //sqlParameter.setIdFieldName(entityInfo.getIdFieldName());
        String tableName = entityInfo.getTableName();
        List<String> columns=new ArrayList<>();
        List<String> params=new ArrayList<>();
        Map<String,Object> valueMap=new HashMap<>();
        List<Object> values=new ArrayList<>();
        List<EntityField> entityFieldList = entityInfo.getEntityFields();
        Object value;
        for (EntityField entityField : entityFieldList) {
            if(entityField.getIsColumn()){
                value= BeanUtil.getProperty(entity,entityField.getFieldName());
                if(entityField.getVersion()){
                    if(null==value){
                        value=1;
                    }
                }
                if(insertColumn(entityField,value,ifNullInsert)){
                    columns.add(entityField.getColumnName());
                    params.add(mybatis?"#{"+entityField.getFieldName()+"}":StringPool.QUESTION_MARK);
                    valueMap.put(entityField.getFieldName(),value);
                    values.add(value);
                }
            }
        }
        String insertSql = UtilAll.UString.format("insert into {}({}) values ({})", tableName,
                String.join(StringPool.COMMA,columns),String.join(StringPool.COMMA,params));
        SqlWithParam sqlParameter = mybatis?MybatisTpl.generate("insert",insertSql,valueMap,null):new SqlWithParam();
        if(!mybatis){
             sqlParameter.setColumnReturns(entityInfo.getColumnReturns());
             sqlParameter.setSql(insertSql);
             sqlParameter.setParams(values.toArray());
        }
        return sqlParameter;
    }
    private static boolean insertColumn(EntityField entityField,Object value,boolean ifNullInsert){
        if(entityField.getInsertNot()){
            return false;
        }
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
    public static SqlWithParam countSql(String sqlId,SqlWithParam sqlWithParam, Map<String, Object> parameter, Dialect dialect){
        if(sqlWithParam.getSqlRead().equals(EnumSqlRead.markdown)){
            String countSqlId=sqlId+ ConstantJdbc.COUNTFLAG;
            String countSql = MarkdownUtil.getContent(sqlId+ ConstantJdbc.COUNTFLAG,false);
            if (null!=countSql) {
                return MybatisTpl.generate(countSqlId,parameter,dialect);
            }
        }
        return new SqlWithParam(generateMyCountSql(sqlWithParam.getSql()),sqlWithParam.getParams());
    }
    private static String generateMyCountSql(String sql) {
        StringBuilder sb = new StringBuilder();
        Matcher m = Pattern.compile("(/\\*)([\\w\\s\\@\\:]*)(\\*/)").matcher(sql);
        while (m.find()) {
            String group = m.group();
            sb.append(group);
        }
        CountSqlParser countSqlParser = new CountSqlParser();
        return sb + countSqlParser.getSmartCountSql(sql);
    }

    public static Map<String,Object> rowToMap(EntityInfo entityInfo,ResultSet rs, String idFieldName) throws SQLException {
        Map<String,Object> resultMap = new LinkedHashMap<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        String key;
        for (int col = 1; col <= cols; col++) {
            String columnName = JdbcUtils.lookupColumnName(rsmd, col);
            if(null!=idFieldName&&columnName.equalsIgnoreCase("GENERATED_KEY")){
                columnName=idFieldName;
            }
            key=mapKey(entityInfo,columnName);
            if(null!=key){
                resultMap.put(key, JdbcUtils.getResultSetValue(rs,col));//rs.getObject(col)
            }
        }
        return resultMap;
    }
    private static String mapKey(EntityInfo entityInfo,String columnName){
        if(null==entityInfo){
            return OrmUtil.toFiled(columnName);
        }
        if(entityInfo.getColumnMap().containsKey(columnName)){
            return entityInfo.getEntityFields().get(entityInfo.getColumnMap().get(columnName)).getFieldName();
        }
        if(entityInfo.getFieldMap().containsKey(columnName)){
            return columnName;
        }
        return null;
    }


}
