package com.vonchange.common;

import com.vonchange.jdbc.mybatis.MybatisJdbcTemplate;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.dialect.MySQLDialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import javax.sql.DataSource;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableJdbcRepositories
public class App 
{


    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new MybatisJdbcTemplate(dataSource) {
            @Override
            protected Dialect dialect() {
                return new MySQLDialect();
            }
        };
    }
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }
}
