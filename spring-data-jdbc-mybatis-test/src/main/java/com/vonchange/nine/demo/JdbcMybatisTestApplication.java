package com.vonchange.nine.demo;

import com.vonchange.jdbc.mybatis.core.config.EnableJdbcRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude={JdbcConfiguration.class})
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
//(basePackages ="com.vonchange.nine.demo.dao")
@SpringBootApplication
@EnableJdbcRepositories
public class JdbcMybatisTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(JdbcMybatisTestApplication.class, args);
    }
}
