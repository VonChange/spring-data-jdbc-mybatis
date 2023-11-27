package com.vonchange.mybatis.test.config;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import com.vonchange.mybatis.dialect.MySQLDialect;
import com.vonchange.mybatis.language.MybatisSqlLanguageUtil;

// mybatis plus 扩展 MybatisXMLLanguageDriver 配置 mybatis-plus.configuration.default-scripting-language
public class SimpleLanguageDriver extends XMLLanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        String sqlInXml = MybatisSqlLanguageUtil.sqlInXml("mapper", script, new MySQLDialect());
        return super.createSqlSource(configuration, sqlInXml, parameterType);
    }
}
