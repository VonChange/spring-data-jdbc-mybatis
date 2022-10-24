package com.vonchange.nine.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Created by Zenger on 2018/5/21.
 */
@RestController
//@Slf4j
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    private static  int num =new Random().nextInt(1000);

    @Value("${test.hello:ss}")
     private String test;
    @GetMapping("/test/hello")
    public String hello() {
        log.info("hello {}",test);
        return "Demo project for Spring Boot2x ! 实例随机数"+num+test;
    }

}


