package com.vonchange.jdbc.abstractjdbc.util;

import com.vonchange.common.util.StringPool;
import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.jdbc.abstractjdbc.model.EnumStep;
import com.vonchange.jdbc.abstractjdbc.model.SplitMap;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.tpl.EntityUtil;
import com.vonchange.mybatis.tpl.MybatisTpl;
import com.vonchange.mybatis.tpl.OrmUtil;
import com.vonchange.mybatis.tpl.model.EntityField;
import com.vonchange.mybatis.tpl.model.EntityInfo;
import com.vonchange.mybatis.tpl.model.SqlWithParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.vonchange.common.util.StringPool.EMPTY;

public class NameQueryUtil {
    private static final Logger log = LoggerFactory.getLogger(NameQueryUtil.class);
    private static final String ParamPre="p";
    private static final Map<String, SplitMap> map=new HashMap<>();
    static {
        map.put("eq",new SplitMap("=",EnumStep.Condition));
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
        map.put("order_by",new SplitMap("order_by",EnumStep.End));
        map.put("and", new SplitMap("and",EnumStep.Join));
        map.put("or", new SplitMap("or",EnumStep.Join));
        map.put("desc", new SplitMap("desc",EnumStep.NUll));
        map.put("asc", new SplitMap("desc",EnumStep.NUll));
    }

    public static SqlWithParam nameSql(String method,Class<?> entityType, Map<String, Object> parameter){
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entityType);
        if(!method.startsWith("find")){
            return null;
        }
        String newMethod=UtilAll.UString.substringAfter(method,"By");
        if("".equals(newMethod)){
            return null;
        }
        String[] splits=  OrmUtil.toSql(newMethod).split(StringPool.UNDERSCORE);
        StringBuilder sql=new StringBuilder();
        sql.append("select ")
                .append(entityInfo.getEntityFields().stream().map(EntityField::getColumnName).collect(Collectors.joining(",")))
                .append(" from ").append(entityInfo.getTableName()).append(" where ");
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
                    index=genMybatisSql(lastSplit,sql,index);
                    index++;
                }
                sql.append(sqlSplit).append(StringPool.SPACE);
                lastSplit= columnsMap.get(mapKey);
            }
        }
        if(lastSplit.getEnumStep().equals(EnumStep.Column)||lastSplit.getEnumStep().equals(EnumStep.Condition)){
            genMybatisSql(lastSplit,sql,index);
        }
        Map<String,Object> newParam= new HashMap<>();
        int k=0;
        for (Map.Entry<String, Object> entry : parameter.entrySet()) {
            newParam.put(ParamPre+k,entry.getValue());
            k++;
        }
       log.debug("gen sql {}",sql);
       return MybatisTpl.generate(method,sql.toString(),newParam);
    }

    private static int genMybatisSql(SplitMap lastSplit, StringBuilder sql, int index){
        String split =lastSplit.getSplit();
        if(lastSplit.getEnumStep().equals(EnumStep.Column)){
            sql.append(" = ");
        }
        if(split.equals("in")||split.equals("not in")){
            sql.append(UtilAll.UString.format("<foreach collection=\"{}\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach> ",ParamPre+index));
            return index;
        }
        if(split.equals("between")){
            sql.append(UtilAll.UString.format("#{{}} and #{{}} ",ParamPre+index,ParamPre+(index+1)));
            return index+1;
        }
        sql.append(UtilAll.UString.format("#{{}} ",ParamPre+index));
        return index;

    }


}
