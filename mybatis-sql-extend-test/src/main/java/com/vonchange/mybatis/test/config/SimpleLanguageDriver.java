package com.vonchange.mybatis.test.config;


import com.vonchange.mybatis.dialect.MySQLDialect;
import com.vonchange.mybatis.language.MybatisSqlLanguageUtil;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

public class SimpleLanguageDriver extends XMLLanguageDriver implements LanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {

        String sqlInXml = MybatisSqlLanguageUtil.sqlInXml("mapper",script,new MySQLDialect());
        return super.createSqlSource(configuration, sqlInXml, parameterType);
    }
}
