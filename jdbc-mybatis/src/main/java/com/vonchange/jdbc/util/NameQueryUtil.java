package com.vonchange.jdbc.util;

import com.vonchange.common.util.StringPool;
import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.map.MyHashMap;
import com.vonchange.jdbc.config.EnumNameQueryType;
import com.vonchange.jdbc.model.EnumStep;
import com.vonchange.jdbc.model.SplitMap;
import com.vonchange.mybatis.exception.EnumErrorCode;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.jdbc.model.EntityField;
import com.vonchange.jdbc.model.EntityInfo;
import com.vonchange.jdbc.model.SqlWithParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NameQueryUtil {
    private static final Logger log = LoggerFactory.getLogger(NameQueryUtil.class);
    private static final String ParamPre="p";
    private static final Map<String, SplitMap> map=new HashMap<>();
    static {
        map.put("eq",new SplitMap("=", EnumStep.Condition));
        map.put("equals",new SplitMap("=",EnumStep.Condition));
        map.put("is",new SplitMap("=",EnumStep.Condition));
        map.put("lt",new SplitMap("<",EnumStep.Condition));
        map.put("before",new SplitMap("<",EnumStep.Condition));
        //map.put("less_than",new SplitMap("<",EnumStep.Condition));
        map.put("lte",new SplitMap("<=",EnumStep.Condition));
        //map.put("less_than_equal",new SplitMap("<=",EnumStep.Condition));
        map.put("gt",new SplitMap(">",EnumStep.Condition));
        map.put("after",new SplitMap(">",EnumStep.Condition));
        //map.put("greater_than",new SplitMap(">",EnumStep.Condition));
        map.put("gte",new SplitMap(">=",EnumStep.Condition));
        //map.put("greater_than_equal",new SplitMap(">=",EnumStep.Condition));
        map.put("not",new SplitMap("!=",EnumStep.Condition));
        map.put("in",new SplitMap("in",EnumStep.Condition));
        map.put("not_in",new SplitMap("not in",EnumStep.Condition));
        map.put("like",new SplitMap("like",EnumStep.Condition));
        map.put("not_like",new SplitMap("like",EnumStep.Condition));
        map.put("between",new SplitMap("between",EnumStep.Condition));
        map.put("order_by",new SplitMap("order by",EnumStep.End));
        map.put("and", new SplitMap("and",EnumStep.Join));
        map.put("or", new SplitMap("or",EnumStep.Join));
        map.put("desc", new SplitMap("desc",EnumStep.ORDER));
        map.put("asc", new SplitMap("asc",EnumStep.ORDER));
    }
    private static final Map<String, SplitMap> orderMap=new HashMap<>();
    static {
        orderMap.put("desc", new SplitMap("desc",EnumStep.ORDER));
        orderMap.put("asc", new SplitMap("asc",EnumStep.ORDER));
    }
    public static String orderSql(String orderBy,Class<?> entityType){
        if(orderBy.contains(StringPool.SPACE)){
            throw  new JdbcMybatisRuntimeException("orderBy can  not contain  space");
        }
        if(!orderBy.startsWith("orderBy")){
            throw  new JdbcMybatisRuntimeException("must start with orderBy");
        }
        orderBy=orderBy.substring(7);
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entityType);
        String[] splits=  OrmUtil.toSql(orderBy).split(StringPool.UNDERSCORE);
        Map<String,SplitMap> columnsMap= new HashMap<>();
        for (EntityField entityField : entityInfo.getEntityFields()) {
            columnsMap.put(entityField.getColumnName(),new SplitMap(entityField.getColumnName(),EnumStep.Column));
        }
        columnsMap.putAll(orderMap);
        SplitMap lastSplit =new SplitMap("",EnumStep.Column);
        StringBuilder sql=new StringBuilder(StringPool.SPACE);
        int i =0;
        while (i<splits.length) {
            StringBuilder stringBuilder=new StringBuilder(splits[i]);
            String sqlSplit=null;
            String mapKey=null;
            i++;
            boolean flag=false;
            //max 5
            for(int j=i-1;j<i+3;j++){
                if(j>=splits.length){
                    break;
                }
                if(columnsMap.containsKey(stringBuilder.toString())){
                    mapKey=stringBuilder.toString();
                    sqlSplit=columnsMap.get(mapKey).getSplit();
                    i=j+1;
                    flag=true;
                }
                if(j<splits.length-1){
                    stringBuilder.append(StringPool.UNDERSCORE).append(splits[j+1]);
                }
            }
            if(!flag){
                throw new JdbcMybatisRuntimeException("{} can not generate order by",orderBy);
            }
            if(null!=sqlSplit){
                if(lastSplit.getEnumStep().equals(EnumStep.ORDER)){
                    sql.append(StringPool.COMMA).append(sqlSplit);
                }
                if(lastSplit.getEnumStep().equals(EnumStep.Column)){
                    EnumStep thisStep= columnsMap.get(mapKey).getEnumStep();
                    if(thisStep.equals(EnumStep.Column)){
                        if(!"".equals(lastSplit.getSplit())){
                            sql.append(StringPool.COMMA);
                        }
                        sql.append(sqlSplit);
                    }
                    if(thisStep.equals(EnumStep.ORDER)){
                        sql.append(StringPool.SPACE).append(sqlSplit);
                    }
                }
                lastSplit= columnsMap.get(mapKey);
            }
        }
        return "order by"+sql.toString();
    }

    private static Map<String,String> methodMap=new HashMap<>();
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
        return UtilAll.UString.tplByMap(methodMap.get(method),new MyHashMap().set("table",entityInfo.getTableName()).set("idColumn",entityInfo.getIdColumnName())
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
    public static SqlWithParam nameSql(String method,Class<?> entityType, List<Object> params){
        if(methodMap.containsKey(method)){
            String sql= simpleNameSql(method,entityType);
            return new SqlWithParam(sql, params.toArray());
        }
        EnumNameQueryType enumNameQueryType = nameQueryType(method);
        if(null==enumNameQueryType)  throw new JdbcMybatisRuntimeException(EnumErrorCode.CanNotGenNameQuery,EnumErrorCode.CanNotGenNameQueryMessage,method);
        EntityInfo  entityInfo=EntityUtil.getEntityInfo(entityType);
        boolean found=false;
        if(enumNameQueryType.equals(EnumNameQueryType.Find)){
            found=true;
        }
        String newMethod=UtilAll.UString.substringAfter(method,"By");
        String[] splits=  OrmUtil.toSql(newMethod).split(StringPool.UNDERSCORE);
        StringBuilder sql=new StringBuilder();
        sql.append(UtilAll.UString.format("select {} from {} where ",
                found?entityInfo.getEntityFields().stream().map(EntityField::getColumnName).collect(Collectors.joining(",")):"count(*)",
                entityInfo.getTableName()
        ));
        Map<String,SplitMap> columnsMap= new HashMap<>();
        for (EntityField entityField : entityInfo.getEntityFields()) {
            columnsMap.put(entityField.getColumnName(),new SplitMap(entityField.getColumnName(),EnumStep.Column));
        }
        columnsMap.putAll(map);
        int index =0;
        SplitMap lastSplit =new SplitMap("",EnumStep.Column);
        int i=0;
        while (i<splits.length) {
            StringBuilder stringBuilder=new StringBuilder(splits[i]);
            String sqlSplit=null;
            String mapKey=null;
            i++;
            boolean flag=false;
            //max 5
            for(int j=i-1;j<i+3;j++){
                if(j>=splits.length){
                    break;
                }
                if(columnsMap.containsKey(stringBuilder.toString())){
                    mapKey=stringBuilder.toString();
                    sqlSplit=columnsMap.get(mapKey).getSplit();
                    i=j+1;
                    flag=true;
                }
                if(j<splits.length-1){
                    stringBuilder.append(StringPool.UNDERSCORE).append(splits[j+1]);
                }
            }
            if(!flag){
                throw new JdbcMybatisRuntimeException("{} can not generate sql by method name,please define in the markdown",method);
            }
            if(null!=sqlSplit){
                EnumStep thisStep= columnsMap.get(mapKey).getEnumStep();
                if(thisStep.equals(EnumStep.Join)||thisStep.equals(EnumStep.End)){
                    index=genMybatisSql(lastSplit,sql,index,params.get(index));
                    index++;
                }
                sql.append(sqlSplit).append(StringPool.SPACE);
                lastSplit= columnsMap.get(mapKey);
            }
        }
        if(lastSplit.getEnumStep().equals(EnumStep.Column)||lastSplit.getEnumStep().equals(EnumStep.Condition)){
            genMybatisSql(lastSplit,sql,index,params.get(index));
        }
        SqlWithParam sqlWithParam = new SqlWithParam();
        sqlWithParam.setSql(sql.toString());
        List<Object> newParams= new ArrayList<>();
        for (Object value : params) {
            if (value instanceof Collection) {
                Collection<?> collection= (Collection<?>) value;
                newParams.addAll(collection);
            }else{
                newParams.add(value);
            }
        }
        sqlWithParam.setParams(newParams.toArray());
       log.debug("gen sql {}",sql);
       return sqlWithParam;
    }

    private static int genMybatisSql(SplitMap lastSplit, StringBuilder sql, int index,Object value){
        String split =lastSplit.getSplit();
        if(lastSplit.getEnumStep().equals(EnumStep.Column)){
            sql.append(" = ");
        }
        if(split.equals("in")||split.equals("not in")){
             if (!(value instanceof Collection||value.getClass().isArray())){
                 throw new  JdbcMybatisRuntimeException("in query parameter must collection or array");
             }
            StringBuilder inSb=new StringBuilder("(");
            if (value instanceof Collection) {
                for (Object o : (Collection<?>) value) {
                    inSb.append("?,");
                }
            }
            if (value.getClass().isArray()) {
                for (Object o : (Object[]) value) {
                    inSb.append("?,");
                }
            }
            sql.append(inSb.substring(0,inSb.length()-1)).append(")");
            return index;
        }
        if(split.equals("between")){
            sql.append("? and ? ");
            return index+1;
        }
        sql.append("? ");
        return index;

    }


}
