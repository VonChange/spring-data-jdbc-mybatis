package com.vonchange.nine.demo.dao;


import com.vonchange.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
class UserInfoQueryDaoTest {

    @Resource
    private UserInfoQueryDao userInfoQueryDao;
    @Test
    void findList() {
         userInfoQueryDao.findList("u001").stream().forEach(item->{
             log.info("item {}", JsonUtil.toJson(item));
         });;
    }
    @Test
    void findMapList() {
        userInfoQueryDao.findMapList("u001").stream().forEach(item->{
            log.info("item {}", JsonUtil.toJson(item));
        });;
    }
}