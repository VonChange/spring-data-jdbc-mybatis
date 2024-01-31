package com.vonchange.nine.demo;

import com.vonchange.jdbc.mybatis.core.config.EnableMybatisMini;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@SpringBootApplication(exclude={JdbcConfiguration.class})
@SpringBootApplication
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableMybatisMini(basePackages ="com.vonchange.nine.demo.dao")
@EnableJpaRepositories(basePackages = "com.vonchange.nine.demo.jpa")
public class MybatisMiniTestApplication {

    public static void main(String[] args) {

        SpringApplication.run(MybatisMiniTestApplication.class, args);
    }

}
