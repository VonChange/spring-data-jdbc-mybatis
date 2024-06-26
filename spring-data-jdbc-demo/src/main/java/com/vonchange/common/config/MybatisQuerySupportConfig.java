package com.vonchange.common.config;

import com.vonchange.jdbc.mybatis.MybatisJdbcTemplate;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.dialect.MySQLDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import javax.sql.DataSource;
@Configuration
public class MybatisQuerySupportConfig {
    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new MybatisJdbcTemplate(dataSource) {@Override protected Dialect dialect() {return new MySQLDialect();}};
    }
}
