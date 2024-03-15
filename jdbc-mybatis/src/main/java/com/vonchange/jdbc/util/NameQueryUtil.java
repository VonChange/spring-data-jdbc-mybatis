package com.vonchange.jdbc.util;

import com.vonchange.common.util.Assert;
import com.vonchange.common.util.ClazzUtils;
import com.vonchange.common.util.Two;
import com.vonchange.common.util.StringPool;
import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.common.util.map.VarMap;
import com.vonchange.jdbc.config.EnumNameQueryType;
import com.vonchange.jdbc.core.CrudUtil;
import com.vonchange.jdbc.model.BaseEntityField;
import com.vonchange.jdbc.model.EntityField;
import com.vonchange.jdbc.model.EntityInfo;
import com.vonchange.jdbc.model.EnumCondition;
import com.vonchange.jdbc.model.EnumStep;
import com.vonchange.jdbc.model.QueryColumn;
import com.vonchange.jdbc.model.SqlMatch;
import com.vonchange.jdbc.model.SqlParam;
import com.vonchange.mybatis.exception.EnumErrorCode;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.tpl.MyOgnl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NameQueryUtil {
    private static final Logger log = LoggerFactory.getLogger(NameQueryUtil.class);
    private static final String ParamPre="p";

    private static final Map<String, SqlMatch> map=new HashMap<>();
    static {
        map.put("eq",new SqlMatch("=", EnumStep.Condition));
        map.put("equals",new SqlMatch("=",EnumStep.Condition));
        map.put("is",new SqlMatch("=",EnumStep.Condition));
        map.put("lt",new SqlMatch("<",EnumStep.Condition));
        map.put("before",new SqlMatch("<",EnumStep.Condition));
        //map.put("less_than",new SplitMap("<",EnumStep.Condition));
        map.put("lte",new SqlMatch("<=",EnumStep.Condition));
        //map.put("less_than_equal",new SplitMap("<=",EnumStep.Condition));
        map.put("gt",new SqlMatch(">",EnumStep.Condition));
        map.put("after",new SqlMatch(">",EnumStep.Condition));
        //map.put("greater_than",new SplitMap(">",EnumStep.Condition));
        map.put("gte",new SqlMatch(">=",EnumStep.Condition));
        //map.put("greater_than_equal",new SplitMap(">=",EnumStep.Condition));
        map.put("not",new SqlMatch("!=",EnumStep.Condition));
        map.put("in",new SqlMatch("in",EnumStep.Condition));
        map.put("not_in",new SqlMatch("not in",EnumStep.Condition));
        map.put("like",new SqlMatch("like",EnumStep.Condition));
        map.put("not_like",new SqlMatch("like",EnumStep.Condition));
        map.put("between",new SqlMatch("between",EnumStep.Condition));
        map.put("order_by",new SqlMatch("order by",EnumStep.End));
        map.put("and", new SqlMatch("and",EnumStep.Join));
        map.put("or", new SqlMatch("or",EnumStep.Join));
        map.put("desc", new SqlMatch("desc",EnumStep.ORDER));
        map.put("asc", new SqlMatch("asc",EnumStep.ORDER));
    }
    private static final Map<String, SqlMatch> orderMap=new HashMap<>();
    static {
        orderMap.put("desc", new SqlMatch("desc",EnumStep.ORDER));
        orderMap.put("asc", new SqlMatch("asc",EnumStep.ORDER));
    }
    public static String orderSql(String orderBy,Class<?> entityType){
        return orderSql(orderBy,entityType,null);
    }
    public static String orderSql(String orderBy,Class<?> entityType,EntityInfo entityInfo){
        if(orderBy.contains(StringPool.SPACE)){
            return   StringPool.EMPTY;
        }
        if(!orderBy.startsWith("orderBy")){
            throw  new JdbcMybatisRuntimeException("must start with orderBy");
        }
        orderBy=orderBy.substring(7);
        if(UtilAll.UString.isBlank(orderBy)){
            return   StringPool.EMPTY;
        }
        if(null==entityInfo){
            entityInfo = EntityUtil.getEntityInfo(entityType);
        }
        String[] splits=  OrmUtil.toSql(orderBy).split(StringPool.UNDERSCORE);
        Map<String,SqlMatch> columnsMap= new HashMap<>();
        for (EntityField entityField : entityInfo.getEntityFields()) {
            columnsMap.put(entityField.getColumnName(),new SqlMatch(entityField.getColumnName(),EnumStep.Column));
        }
        columnsMap.putAll(orderMap);
        int i =0;
        List<Two<String,String>> orderColumn=new ArrayList<>();
        Two<String,String> two =new Two<>();
        while (i<splits.length) {
            String mapKey=splits[i];
            i++;
            SqlMatch sqlMatch = null;
            //max 5
            for(int j=i-1;j<i+3;j++){
                if(j>=splits.length){
                    break;
                }
                if(columnsMap.containsKey(mapKey)){
                    sqlMatch=columnsMap.get(mapKey);
                    i=j+1;
                }
                if(j<splits.length-1){
                    mapKey=mapKey+StringPool.UNDERSCORE+splits[j+1];
                }
            }
            if(null==sqlMatch){
                throw new JdbcMybatisRuntimeException("{} can not generate order by",orderBy);
            }
            if(sqlMatch.getEnumStep().equals(EnumStep.Column)){
                two.setFirst(sqlMatch.getSplit());
            }
            if(sqlMatch.getEnumStep().equals(EnumStep.ORDER)){
                two.setSecond(sqlMatch.getSplit());
                orderColumn.add(two);
                two =new Two<>();
            }
        }
        orderColumn.add(two);

        return " order by "+orderColumn.stream().filter(item->null!=item.getFirst()).map(item->item.getFirst()+StringPool.SPACE+
                (null==item.getSecond()?StringPool.EMPTY:item.getSecond()))
                .collect(Collectors.joining(","));
    }

    private static final Map<String,String> methodMap=new HashMap<>();
    static {
        methodMap.put("findById","select ${columns} from ${table} where ${idColumn} = ?");
        methodMap.put("deleteById","delete  from ${table} where ${idColumn} = ?");
        methodMap.put("deleteAll","delete from ${table}");
        methodMap.put("existsById","select count(1)  from ${table} where ${idColumn} = ?");
        methodMap.put("findAll","select ${columns} from ${table}");
        methodMap.put("countAll","select count(1) {} from ${table}");
    }

    public static String simpleNameSql(String method,Class<?> entityType){
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entityType);
        return UtilAll.UString.tplByMap(methodMap.get(method),
                new VarMap().set("table",entityInfo.getTableName()).set("idColumn",entityInfo.getIdColumnName())
                .set("columns",entityInfo.getEntityFields().stream().map(EntityField::getColumnName).collect(Collectors.joining(",")))
        );
    }
    private static EnumNameQueryType nameQueryType(String method){
        if(!method.contains("By")){
            return null;
        }
        if(method.startsWith("find")){
            return EnumNameQueryType.Find;
        }
        if(method.startsWith("count")){
            return EnumNameQueryType.Count;
        }
        if(method.startsWith("exist")){
            return EnumNameQueryType.Exists;
        }
        return null;
    }
    private static String fromSql(EnumNameQueryType enumNameQueryType,EntityInfo  entityInfo){
        String tableName=entityInfo.getTableName();
       if(enumNameQueryType.equals(EnumNameQueryType.Find)){
           return UtilAll.UString.format("select {} from {}",
                   entityInfo.getEntityFields().stream().map(EntityField::getColumnName)
                           .collect(Collectors.joining(",")),tableName);
       }
       if(enumNameQueryType.equals(EnumNameQueryType.Count)||enumNameQueryType.equals(EnumNameQueryType.Exists)){
           return "select count(*) from "+tableName;
       }
       return null;
    }
    public static  <S> SqlParam exampleSql(EnumNameQueryType enumNameQueryType,
                                           Class<?> entityClass, S example){
        Assert.notNull(example,"param can not null");
        if(ClazzUtils.isBaseType(example.getClass())){
            throw new JdbcMybatisRuntimeException("not support {} {}",example,example.getClass());
        }
        if(ClazzUtils.isBaseType(entityClass)){
            throw new JdbcMybatisRuntimeException("entityClass not support {}",entityClass);
        }
        boolean found=enumNameQueryType.equals(EnumNameQueryType.Find);
        EntityInfo  entityInfo =EntityUtil.getEntityInfo(entityClass);
        List<BaseEntityField>  baseEntityFields=EntityUtil.getEntitySimple(example.getClass());
        String  sql=UtilAll.UString.format("select {} from {}",
                found?entityInfo.getEntityFields().stream().map(EntityField::getColumnName).collect(Collectors.joining(",")):"count(*)",
                entityInfo.getTableName());
        List<String> orders = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        List<String> querySql=new ArrayList<>();
        QueryColumn queryColumn;
        Two<String,Collection<?>> two;
        for (BaseEntityField baseEntityField : baseEntityFields) {
            queryColumn = fieldQuery(example,baseEntityField);
            if(null==queryColumn){
                continue;
            }
            if(queryColumn.getCondition().equals(EnumCondition.Order.getCondition())){
                orders.add(queryColumn.getColumn());
                continue;
            }
            two =CrudUtil.conditionSql(queryColumn);
            querySql.add(two.getFirst());
            values.addAll(two.getSecond());
        }
        if(!querySql.isEmpty()){
            sql=sql+ " where " + String.join(" and ", querySql);
        }
        if(found&&!orders.isEmpty()){
            sql=sql+" order by "+String.join(",", orders);
        }
        return new SqlParam(sql,values);
    }

    private static <S> QueryColumn fieldQuery(S example, BaseEntityField baseEntityField){
        String ormField= baseEntityField.getColumnName();
        Object value = BeanUtil.getPropertyT(example,baseEntityField.getFieldName());
        if(null==value){
            return null;
        }
        String[] splits=  ormField.split(StringPool.UNDERSCORE);
        if(splits.length==1){
            return new QueryColumn(ormField, EnumCondition.Eq.getCondition(),value);
        }
        int size = splits.length;
        String last = splits[size-1];
        String lastTwo = splits[size-2];
        String column;
        int ormFieldSize=ormField.length();
        if(map.containsKey(last)){
            column= ormField.substring(0,ormFieldSize-last.length()-1);
            if(map.get(last).getEnumStep().equals(EnumStep.Condition)){
                String two=lastTwo+StringPool.SPACE+last;
                if(map.containsKey(two)){
                    //匹配了2个
                    if(size<3){
                        throw new JdbcMybatisRuntimeException("{} field not allowed",two);
                    }
                    column=ormField.substring(0,ormField.length()-two.length()-1);
                    return new QueryColumn(column,two,value);
                }
                return new QueryColumn(column,last,value);
            }
            if(map.get(last).getEnumStep().equals(EnumStep.ORDER)){
                return new QueryColumn(column+StringPool.SPACE+last,EnumCondition.Order.getCondition(), null);
            }
        }
        return new QueryColumn(ormField, EnumCondition.Eq.getCondition(),value);
    }



    public static SqlParam nameSql(String method, Class<?> entityType, List<Object> params){
        for (Object param : params) {
            if(MyOgnl.isEmpty(param)){
                throw new JdbcMybatisRuntimeException("name query params can not null or empty");
            }
        }
        if(methodMap.containsKey(method)){
            String sql= simpleNameSql(method,entityType);
            return new SqlParam(sql, params);
        }
        EnumNameQueryType enumNameQueryType = nameQueryType(method);
        if(null==enumNameQueryType)  throw new JdbcMybatisRuntimeException(EnumErrorCode.CanNotGenNameQuery,EnumErrorCode.CanNotGenNameQueryMessage,method);
        EntityInfo  entityInfo=EntityUtil.getEntityInfo(entityType);
        //排除orderBy
        String orderBy=UtilAll.UString.substringAfterLast(method,"OrderBy");
        String newMethod=UtilAll.UString.substringAfter(method,"By");
        if(!StringPool.EMPTY.equals(orderBy)){
            newMethod=UtilAll.UString.substringBeforeLast(newMethod,"OrderBy");
        }
        String[] splits=  OrmUtil.toSql(newMethod).split(StringPool.UNDERSCORE);
        String sql=fromSql(enumNameQueryType,entityInfo);
        Map<String,SqlMatch> columnsMap= new HashMap<>();
        for (EntityField entityField : entityInfo.getEntityFields()) {
            columnsMap.put(entityField.getColumnName(),new SqlMatch(entityField.getColumnName(),EnumStep.Column));
        }
        columnsMap.putAll(map);
        int i=0;
        List<QueryColumn> queryColumns =new ArrayList<>();
        QueryColumn queryColumn=new QueryColumn();
        while (i<splits.length) {
            String mapKey=splits[i];
            SqlMatch sqlMatch = null;
            i++;
            //max 5
            for(int j=i-1;j<i+3;j++){
                if(j>=splits.length){
                    break;
                }
                if(columnsMap.containsKey(mapKey)){
                    sqlMatch=columnsMap.get(mapKey);
                    i=j+1;
                }
                if(j<splits.length-1){
                    mapKey=mapKey+StringPool.UNDERSCORE+splits[j+1];
                }
            }
            if(null==sqlMatch){
                throw new JdbcMybatisRuntimeException("{} can not generate sql by method name,please define in the markdown",method);
            }
            //queryColumn.setJoin("");
            if(sqlMatch.getEnumStep().equals(EnumStep.Column)){
                queryColumn.setColumn(sqlMatch.getSplit());
            }
            if(sqlMatch.getEnumStep().equals(EnumStep.Condition)){
                queryColumn.setCondition(sqlMatch.getSplit());
            }
            if(sqlMatch.getEnumStep().equals(EnumStep.Join)) {
                queryColumns.add(queryColumn);
                queryColumn = new QueryColumn();
                queryColumn.setJoin(sqlMatch.getSplit());
            }
        }
        queryColumns.add(queryColumn);
        StringBuilder sqlBuilder= new StringBuilder(sql);
        sqlBuilder.append(" where ");
        List<Object> values = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < queryColumns.size(); columnIndex++) {
            QueryColumn queryColumn1= queryColumns.get(columnIndex);
            queryColumn1.setValue(params.get(columnIndex));
            Two<String,Collection<?>> two =  CrudUtil.conditionSql(queryColumn1);
            sqlBuilder.append(two.getFirst());
            values.addAll(two.getSecond());
        }

       log.debug("gen sql {}",sqlBuilder);
       return  new SqlParam(sqlBuilder+orderSql("orderBy"+orderBy,entityType,entityInfo),values);
    }
    private static Collection<?> listParam(Object value){
        if (value instanceof Collection) {
            return (Collection<?>) value;
        }
        if (value.getClass().isArray()) {
            return Arrays.asList((Object[]) value);
        }
        return Collections.singletonList(value);
    }




}
