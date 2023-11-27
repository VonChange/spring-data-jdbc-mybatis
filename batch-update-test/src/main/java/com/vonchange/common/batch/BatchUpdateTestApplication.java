package com.vonchange.common.batch;

import com.vonchange.spring.data.mybatis.mini.jdbc.repository.config.EnableMybatisMini;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableMybatisMini
//@MapperScan("com.vonchange.common.batch")
public class BatchUpdateTestApplication {

    public static void main(String[] args) {

        SpringApplication.run(BatchUpdateTestApplication.class, args);
    }

}
