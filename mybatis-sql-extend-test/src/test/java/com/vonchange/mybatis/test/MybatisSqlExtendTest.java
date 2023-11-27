package com.vonchange.mybatis.test;

import com.vonchange.common.util.JsonUtil;
import com.vonchange.mybatis.test.config.UserBaseDO;
import com.vonchange.mybatis.test.config.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MybatisSqlExtendTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void  sqlExtendTest(){
        System.err.println("XXXXXX");
        List<UserBaseDO> userBaseDOList = userMapper.findList("test", LocalDateTime.now().plusHours(1L));
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n {}", JsonUtil.toJson(userBaseDO));
        });
        userBaseDOList = userMapper.findList("test", LocalDateTime.now().plusHours(1L));
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n {}", JsonUtil.toJson(userBaseDO));
        });
        userBaseDOList = userMapper.findList("test", LocalDateTime.now().plusHours(1L));
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n {}", JsonUtil.toJson(userBaseDO));
        });
    }

    @Test
    public void  sqlExtendTestOrg(){
        List<UserBaseDO> userBaseDOList = userMapper.findListOrg("test", LocalDateTime.now().plusHours(1L));
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n {}", JsonUtil.toJson(userBaseDO));
        });
    }

    @Test
    public void  insert(){
        UserBaseDO userBaseDO = new UserBaseDO();
       int  result  = userMapper.insert(userBaseDO);
        System.out.println(result);
    }
}
