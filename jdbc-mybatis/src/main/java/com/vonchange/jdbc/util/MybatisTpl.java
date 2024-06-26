package com.vonchange.jdbc.util;


import com.vonchange.common.ibatis.mapping.BoundSql;
import com.vonchange.common.ibatis.mapping.ParameterMapping;
import com.vonchange.common.ibatis.mapping.SqlSource;
import com.vonchange.common.ibatis.reflection.MetaObject;
import com.vonchange.common.ibatis.scripting.LanguageDriver;
import com.vonchange.common.ibatis.scripting.xmltags.XMLLanguageDriver;
import com.vonchange.common.ibatis.session.Configuration;
import com.vonchange.common.util.MarkdownUtil;
import com.vonchange.common.util.StringPool;
import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.exception.ErrorMsg;
import com.vonchange.jdbc.model.SqlParam;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.exception.EnumJdbcErrorCode;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.sql.DynamicSql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
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

     public static SqlParam generate(String sqlId, Map<String,Object> parameter, Dialect dialect){
         if(!sqlId.contains(StringPool.DOT)||sqlId.contains(StringPool.SPACE)){
             throw  new JdbcMybatisRuntimeException(EnumJdbcErrorCode.SqlIdNotFound,
                     ErrorMsg.builder().message("{} sqlId error can not found in markdown",sqlId));
         }
        String sqlInXml=MarkdownUtil.getContent(sqlId);
         return generate(sqlId,sqlInXml,parameter,dialect);
     }
    @SuppressWarnings("unchecked")
    public static SqlParam generate(String sqlId, String sqlInXml, Map<String,Object> parameter, Dialect dialect){
        if(UtilAll.UString.isBlank(sqlInXml)){
            throw  new JdbcMybatisRuntimeException(EnumJdbcErrorCode.SqlIdNotFound,
                    ErrorMsg.builder().message("{} sql in markdown null",sqlId));
        }
        sqlInXml= DynamicSql.dynamicSql(sqlInXml,dialect);
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
            throw  new JdbcMybatisRuntimeException(EnumJdbcErrorCode.MybatisSqlError,
                    ErrorMsg.builder().message("{} error builder mybatis dynamic sql",sqlId).throwable(e));
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
                        throw  new JdbcMybatisRuntimeException(EnumJdbcErrorCode.PlaceholderNotFound,
                                ErrorMsg.builder().message("{} placeholder #{{}}  not found",sqlId,propertyName));
                    }
                    value = metaObject.getValue(propertyName);
                }
                argList.add(value);
                propertyNames.add(propertyName);
            }
        }
        Object[] args=argList.toArray();
        String sql=boundSql.getSql();
        SqlParam sqlParam = new SqlParam(sql, Arrays.asList(args));
        sqlParam.setPropertyNames(propertyNames);
        return sqlParam;
    }



}
