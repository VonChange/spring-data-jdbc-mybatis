package com.vonchange.mybatis.tpl;


import com.vonchange.common.ibatis.mapping.BoundSql;
import com.vonchange.common.ibatis.mapping.ParameterMapping;
import com.vonchange.common.ibatis.mapping.SqlSource;
import com.vonchange.common.ibatis.reflection.MetaObject;
import com.vonchange.common.ibatis.scripting.LanguageDriver;
import com.vonchange.common.ibatis.scripting.xmltags.XMLLanguageDriver;
import com.vonchange.common.ibatis.session.Configuration;
import com.vonchange.mybatis.common.util.ConvertUtil;
import com.vonchange.mybatis.common.util.StringUtils;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.tpl.exception.MybatisMinRuntimeException;
import com.vonchange.mybatis.tpl.extra.DynamicSql;
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
    public static final String MARKDOWN_SQL_ID ="markdown_sql_id";
    private MybatisTpl() {
        throw new IllegalStateException("Utility class");
    }
     @SuppressWarnings("unchecked")
     public static SqlWithParam generate(String sqlInXml, Map<String,Object> parameter, Dialect dialect){
         SqlWithParam sqlWithParam= new SqlWithParam();
        if(StringUtils.isBlank(sqlInXml)){
            sqlWithParam.setSql(null);
            sqlWithParam.setParams(null);
            return  sqlWithParam;
        }
         sqlInXml= DynamicSql.dynamicSql(sqlInXml,dialect);
         sqlInXml=sqlInXml.trim();
         if(sqlInXml.contains("</")){
             sqlInXml="<script>"+sqlInXml+"</script>";
             sqlInXml =  StringUtils.replaceEach(sqlInXml,new String[]{" > "," < "," >= "," <= "," <> "},
                     new String[]{" &gt; "," &lt; "," &gt;= "," &lt;= "," &lt;&gt; "});
         }
         if(null==parameter){
             parameter=new LinkedHashMap<>();
         }
         String id =null;
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
         BoundSql boundSql = null;
         try {
             SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlInXml, Map.class);
              boundSql=sqlSource.getBoundSql(parameter);
         }catch (Exception e){
             logger.error("解析sqlxml出错",e);
             logger.error("sqlxml:{}",sqlInXml);
             sqlWithParam.setSql(null);
             sqlWithParam.setParams(null);
             return  sqlWithParam;
         }

         if(boundSql.getSql().contains("#{")){
             return generate(boundSql.getSql(),parameter,dialect);
         }
         if(parameter.containsKey(MARKDOWN_SQL_ID)){
             id= ConvertUtil.toString(parameter.get(MARKDOWN_SQL_ID));
             parameter.remove(MARKDOWN_SQL_ID);
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
                         throw  new MybatisMinRuntimeException(id+" "+propertyName+" placeholder not found");
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
