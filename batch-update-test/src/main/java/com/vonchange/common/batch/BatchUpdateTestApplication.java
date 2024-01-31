package com.vonchange.common.batch;

import com.vonchange.jdbc.mybatis.core.config.EnableJdbcRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableJdbcRepositories
//@MapperScan("com.vonchange.common.batch")
public class BatchUpdateTestApplication {

    public static void main(String[] args) {

        SpringApplication.run(BatchUpdateTestApplication.class, args);
    }

}
