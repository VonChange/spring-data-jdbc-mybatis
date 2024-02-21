package com.vonchange.mybatis.tpl;


import com.vonchange.common.ibatis.mapping.BoundSql;
import com.vonchange.common.ibatis.mapping.ParameterMapping;
import com.vonchange.common.ibatis.mapping.SqlSource;
import com.vonchange.common.ibatis.reflection.MetaObject;
import com.vonchange.common.ibatis.scripting.LanguageDriver;
import com.vonchange.common.ibatis.scripting.xmltags.XMLLanguageDriver;
import com.vonchange.common.ibatis.session.Configuration;
import com.vonchange.common.util.MarkdownUtil;
import com.vonchange.common.util.UtilAll;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.sql.DynamicSql;
import com.vonchange.mybatis.tpl.model.SqlWithParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * mybatis 模版主要代码
 */
public class MybatisTpl {
    private static   Logger logger = LoggerFactory.getLogger(MybatisTpl.class);
    private MybatisTpl() {
        throw new IllegalStateException("Utility class");
    }

     public static SqlWithParam generate(String sqlId, Map<String,Object> parameter){
        String sqlInXml=MarkdownUtil.getContent(sqlId);
         return generate(sqlId,sqlInXml,parameter);
     }
    @SuppressWarnings("unchecked")
    public static SqlWithParam generate(String sqlId,String sqlInXml, Map<String,Object> parameter){
        SqlWithParam sqlWithParam= new SqlWithParam();
        if(UtilAll.UString.isBlank(sqlInXml)){
            sqlWithParam.setSql(null);
            sqlWithParam.setParams(null);
            return  sqlWithParam;
        }
        sqlInXml= DynamicSql.dynamicSql(sqlInXml);
        sqlInXml=sqlInXml.trim();
        if(sqlInXml.contains("</")){
            sqlInXml="<script>"+sqlInXml+"</script>";
            sqlInXml =  UtilAll.UString.replaceEach(sqlInXml,new String[]{" > "," < "," >= "," <= "," <> "},
                    new String[]{" &gt; "," &lt; "," &gt;= "," &lt;= "," &lt;&gt; "});
        }
        if(null==parameter){
            parameter=new LinkedHashMap<>();
        }
        LanguageDriver languageDriver = new XMLLanguageDriver();
        Configuration configuration= new Configuration();
        Properties properties= new Properties();
        for (Map.Entry<String,Object> entry: parameter.entrySet()) {
            if(null==entry.getValue()){
                continue;
            }
            properties.put(entry.getKey(),entry.getValue());
        }
        configuration.setVariables(properties);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlInXml, Map.class);
        BoundSql boundSql;
        try{
             boundSql=sqlSource.getBoundSql(parameter);
        }catch (Exception e){
            throw new JdbcMybatisRuntimeException(e,"{} error builder mybatis dynamic sql ",sqlId);
        }
        List<ParameterMapping> list= boundSql.getParameterMappings();
        List<Object> argList= new ArrayList<>();
        List<String> propertyNames = new ArrayList<>();
        if(null!=list&&!list.isEmpty()){
            Map<String,Object> param =new LinkedHashMap<>();
            if(boundSql.getParameterObject() instanceof  Map){
                param = (Map<String, Object>) boundSql.getParameterObject();
            }
            for (ParameterMapping parameterMapping: list) {
                Object value;

                String propertyName= parameterMapping.getProperty();
                if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (param == null) {
                    value = null;
                }else {
                    MetaObject metaObject = configuration.newMetaObject(param);
                    if(!metaObject.hasGetter(propertyName)){
                        throw  new JdbcMybatisRuntimeException("{} placeholder #{{}}  not found",sqlId,propertyName);
                    }
                    value = metaObject.getValue(propertyName);
                }
                argList.add(value);
                propertyNames.add(propertyName);
            }
        }
        Object[] args=argList.toArray();
        String sql=boundSql.getSql();
        sqlWithParam.setSql(sql);
        sqlWithParam.setParams(args);
        sqlWithParam.setPropertyNames(propertyNames);
        return sqlWithParam;
    }



}
