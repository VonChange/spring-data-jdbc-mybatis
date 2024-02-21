package com.vonchange.jdbc.abstractjdbc.core;

import com.vonchange.common.util.StringPool;
import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.jdbc.abstractjdbc.util.NameQueryUtil;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.tpl.EntityUtil;
import com.vonchange.mybatis.tpl.MybatisTpl;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrudUtil {

    private static final Logger log = LoggerFactory.getLogger(CrudUtil.class);
    public static SqlWithParam getSqlParameter(String sqlId,
                                         Map<String, Object> parameter) {
        return MybatisTpl.generate(sqlId,parameter);
    }
    public static SqlWithParam nameQuery(String name,Class<?> entityType,
                                         List<Object> indexedParams) {
        SqlWithParam sqlWithParam= NameQueryUtil.nameSql(name,entityType,indexedParams);
        if(null==sqlWithParam){
            throw new JdbcMybatisRuntimeException("{} can not generate sql by method name,please define in the markdown",name);
        }
        return sqlWithParam;
    }

    public static  <T> SqlWithParam generateUpdateEntitySql(T entity, boolean isNullUpdate) {
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
        for (EntityField entityField : entityFieldList) {
            if(entityField.getUpdateNot()){
                continue;
            }
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
    public static  <T> Object[] generateInsertSqlParams(T entity,boolean ifNullInsert) {
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
    private static final Integer batchSizeDefault=2000;
    public  static  <T> List<List<Object[]>> insertBatchParam(List<T> entityList, boolean ifNullInsert, int batchSize) {
        if(CollectionUtils.isEmpty(entityList)){
            return new ArrayList<>();
        }
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
            list.add(CrudUtil.generateInsertSqlParams(t,ifNullInsert));
            i++;
        }
        if (!list.isEmpty()) {
            listSplit.add(list);
        }
        return listSplit;
    }
    public static  <T> List<List<Object[]>> batchUpdateParam(List<T> entityList, List<String> propertyNames,int size) {
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
            param.add(beanToObjects(t, propertyNames));
            i++;
        }
        if (!param.isEmpty()) {
            listSplit.add(param);
        }
        return listSplit;
    }
    private static <T>  Object[] beanToObjects(T t, List<String> propertyNames) {
        List<Object> result = new ArrayList<>();
        for (String propertyName : propertyNames) {
            result.add(BeanUtil.getProperty(t, propertyName));
        }
        return result.toArray();
    }


    public static  <T> SqlWithParam generateInsertSql(T entity, boolean ifNullInsert) {
        SqlWithParam sqlParameter = new SqlWithParam();
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
        sqlParameter.setIdFieldName(entityInfo.getIdFieldName());
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
    private static boolean insertColumn(EntityField entityField,Object value,boolean ifNullInsert){
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

    public static Map<String,Object> rowToMap(ResultSet rs, String idColumnName) throws SQLException {
        Map<String,Object> resultMap = new LinkedHashMap<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        for (int col = 1; col <= cols; col++) {
            String columnName = JdbcUtils.lookupColumnName(rsmd, col);
            if(null!=idColumnName&&columnName.equalsIgnoreCase("GENERATED_KEY")){
                columnName=idColumnName;
            }
            resultMap.put(columnName, rs.getObject(col));
        }
        return resultMap;
    }

  /*  public static Map<String, Object> toBean(Map<String, Object> map) {
        if (null == map || map.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> newMap = new LinkedHashMap<>();
        String key;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            key = entry.getKey();
            newMap.put(key.toLowerCase(), entry.getValue());
            key = OrmUtil.toFiled(entry.getKey());
            newMap.put(key.toLowerCase(), entry.getValue());
        }
        return newMap;
    }*/
}
