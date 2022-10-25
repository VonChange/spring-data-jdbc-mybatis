package com.vonchange.mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude={JdbcConfiguration.class})
@SpringBootApplication
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class MybatisSqlExtendTestApplication {

    public static void main(String[] args) {

        SpringApplication.run(MybatisSqlExtendTestApplication.class, args);
    }

}
