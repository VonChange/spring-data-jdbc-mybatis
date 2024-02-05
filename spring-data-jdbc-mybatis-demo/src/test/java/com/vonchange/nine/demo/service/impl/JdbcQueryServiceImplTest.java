package com.vonchange.nine.demo.service.impl;

import com.vonchange.common.util.map.MyHashMap;
import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.nine.demo.domain.UserInfoDO;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class JdbcQueryServiceImplTest {
    private static final Logger log = LoggerFactory.getLogger(JdbcQueryServiceImplTest.class);
    @Resource
    private JdbcRepository  jdbcRepository;
    @Test
    public void findList() {
        List<UserInfoDO> UserInfoDOList =jdbcRepository.queryList(UserInfoDO.class,"sql.UserBaseRepository.findList",new MyHashMap()
                .set("userName","张三日子").set("createTime",null));
        UserInfoDOList.forEach(UserInfoDO -> {
            log.info("\n {}",UserInfoDO.toString());
        });
    }
    @Test
    public void findListToMap() {
        List<Map<String,Object>> UserInfoDOList =jdbcRepository.queryMapList("sql.UserBaseRepository.findList",new MyHashMap()
                .set("userName","张三日子").set("createTime",null));
        UserInfoDOList.forEach(UserInfoDO -> {
            log.info("\n {}",UserInfoDO.toString());
        });
    }
    @Test
    public void findListBySql() {
        String sql ="select * from user_base\n" +
                "where user_name = #{userName}";
        List<UserInfoDO> UserInfoDOList = jdbcRepository.queryList(UserInfoDO.class,"@sql"+sql,new MyHashMap()
                .set("userName","张三日子").set("createTime",null));
        UserInfoDOList.forEach(UserInfoDO -> {
            log.info("\n {}",UserInfoDO.toString());
        });
    }

    @Test
    public void findListByMd() {
        String sql ="### id findListByMd \n" +
                "#### version 1.0 \n> 查询用户列表 含sql 片段\n" +
                "\n" +
                "```\n" +
                "-- main\n" +
                "select * from user_base\n" +
                "where [@sql findListWhereSql]\n" +
                "```\n" +
                "\n" +
                "> sql 片段\n" +
                "```\n" +
                "-- findListWhereSql\n" +
                "user_name = #{userName} and 1=1\n" +
                "{@and create_time  < createTime}\n" +
                "```";
        List<UserInfoDO> UserInfoDOList = jdbcRepository.queryList(UserInfoDO.class,"@md"+sql,new MyHashMap()
                .set("userName","张三日子").set("createTime",null));
        UserInfoDOList.forEach(UserInfoDO -> {
            log.info("\n {}",UserInfoDO.toString());
        });
    }
}