package com.vonchange.nine.demo.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserBaseJpaRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(UserBaseJpaRepositoryTest.class);
    @Resource
    private UserBaseJpaRepository userBaseJpaRepository;

    @Test
    public void findList() {
        List<UserBase> userBaseList = userBaseJpaRepository.findList("张三日子");
        userBaseList.forEach(userBase -> {
            log.info("\n {}",userBase.toString());
        });
    }
    @Test
    public void ttt() {
        String goodsId= "xxx12345";
        System.out.println(goodsId.substring(goodsId.length()-1));
    }

}