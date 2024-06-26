package com.vonchange.nine.demo.dao;

import com.vonchange.common.util.JsonUtil;
import com.vonchange.common.util.StringPool;
import com.vonchange.jdbc.model.SqlParam;
import com.vonchange.jdbc.util.NameQueryUtil;
import com.vonchange.nine.demo.domain.UserInfoDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Slf4j
@SpringBootTest
class UserInfoMethodDaoTest {



    @Resource
    private UserInfoMethodDao userInfoMethodDao;


    @Test
    public  void methodSql() {
        //OrderByCreateTimeDesc findByUserCodeInAndCreateTimeOrderByCreateTimeDesc
        SqlParam sqlParam = NameQueryUtil.nameSql(
                "findByUserCodeInOrderByCreateTimeDesc",UserInfoDO.class,Arrays.asList(new String[]{"233","333"},9));
        log.info("\nnameSql {}", sqlParam.getSql());
    }
    @Test
    public  void orderSql() {
        String  sql = NameQueryUtil.orderSql(
                "orderByCreateTimeDesc",UserInfoDO.class);
        log.info("\nsql {}", sql);
    }
    @Test
    void findByUserCode() {
        UserInfoDO userInfo = userInfoMethodDao.findByUserCode("u001");
        log.info("\nuserInfo {}", JsonUtil.toJson(userInfo));
    }

    @Test
    void findAllByExample() {
        List<UserInfoDO> userInfo = userInfoMethodDao
                .findAll(UserExample.builder().userCodeIn(Arrays.asList("u000","u001","u002"))
                        .userNameLike("ch%").createTimeDesc(true).build());
        log.info("\nuserInfo {}", JsonUtil.toJson(userInfo));
    }
    @Test
    void findOneByExample() {
        userInfoMethodDao
                .findOne(UserExample.builder().userCodeIn(Arrays.asList("u000","u002"))
                        .userNameLike("ch%").createTimeDesc(true).build()).ifPresent(u->{
                    log.info("\nuserInfo {}", JsonUtil.toJson(u));
                });
    }

    @Test
    void findAll() {
        userInfoMethodDao
                .findAll().forEach(u->{
                    log.info("\nuserInfo {}", JsonUtil.toJson(u));
                });
    }
    @Test
    void countByExample() {
        Long num = userInfoMethodDao
                .count(UserExample.builder().userCodeIn(Arrays.asList("u000","u001","u002"))
                        .userNameLike("ch%").createTimeDesc(true).build());
        log.info("\nuserInfo count {}", num);
    }
    @Test
    void findByCreateTimeBetween() {
        List<UserInfoDO> userInfoList = userInfoMethodDao.findByCreateTimeBetween(
                Arrays.asList(LocalDateTime.now().minusMinutes(60L),LocalDateTime.now().plusMinutes(3L)));
        log.info("\nuserInfo {}", JsonUtil.toJson(userInfoList));
    }

    @Test
    void countByUserCode() {
        boolean  countNum =
                userInfoMethodDao.countByUserCodeIn(Arrays.asList("u001","u002"));
        log.info("\ncountNum {}", countNum);
    }

    @Test
    void findByUserCodeIn() {
        List<UserInfoDO> userInfoDOList = userInfoMethodDao.findByUserCodeIn(
                Arrays.asList("u001","u002"));
        userInfoDOList.forEach(UserInfoDO -> {
            log.info("\nUserInfoDOList {}", JsonUtil.toJson(UserInfoDO));
        });
       // userInfoDOList = userInfoMethodDao.findByUserCodeIn(
              //  null);
    }

    @Test
    void findPageByUserCodeIn() {
        Page<UserInfoDO> userInfoDOPage = userInfoMethodDao.findPageByUserCodeIn(
                PageRequest.of(0,1),
                Arrays.asList("u001","u002"));
        log.info("userInfoDOPage \n{}", JsonUtil.toJson(userInfoDOPage));
    }

    @Test
    void findByUserCodeInOrderByCreateTimeDesc() {
        List<UserInfoDO> userInfoDOList = userInfoMethodDao.findByUserCodeInOrderByCreateTimeDesc(
                Arrays.asList("u001","u002"));
        userInfoDOList.forEach(UserInfoDO -> {
            log.info("\nUserInfoDOList {}", JsonUtil.toJson(UserInfoDO));
        });
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void insert() throws IOException {
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setUserCode("L001");
        userInfoDO.setUserName("Bruce Lee");
        userInfoDO.setValid(true);
        userInfoDO.setOrder("order1111");
        //userInfoDO.setHeadImageData(IOUtils.toByteArray(UtilAll.UFile.getClassResource("db-init.sql")));
        userInfoMethodDao.insert(userInfoDO);
        log.info("\ninsert {}",JsonUtil.toJson(userInfoDO));
        //log.info("\nresultNum {} id {}",resultNum,userInfoDO.getId());
        userInfoMethodDao.findById(userInfoDO.getId()).ifPresent(u->{

            if(null!=u.getHeadImageData()){
                try {
                    log.info(IOUtils.toString(u.getHeadImageData(), StringPool.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("\nuserInfoDOFind {}",JsonUtil.toJson(u));
        });


    }
    @Test
    //@Transactional
    //@Rollback(value = false)
    public void update() {
        userInfoMethodDao.findById(2L).ifPresent(u->{
            UserInfoDO userInfoDO = new UserInfoDO();
            userInfoDO.setUserName("Jack ma");
            userInfoDO.setId(2L);
            userInfoDO.setVersion(u.getVersion());
            userInfoMethodDao.update(userInfoDO);
        });
        //int a=0;
        //log.info((1/a);
       userInfoMethodDao.findById(2L).ifPresent(u->{
            log.info("\nuserInfoDOFind {}",JsonUtil.toJson(u));
        });

    }



    @Test
    void findById() {
        userInfoMethodDao.findById(1L)
                .ifPresent(userInfoDO -> log.info("\nuserInfo {}", JsonUtil.toJson(userInfoDO)));
    }
    @Test
    void findAllById() {
        Iterable<UserInfoDO> userInfo = userInfoMethodDao.findAllById(Arrays.asList(1L,2L));
        log.info("\nuserInfo {}", JsonUtil.toJson(userInfo));
    }
    @Test
    void existsById() {
        boolean existsById= userInfoMethodDao.existsById(1L);
        log.info("\nexistsById {}", existsById);
        existsById= userInfoMethodDao.existsById(1000L);
        log.info("\nexistsById {}", existsById);
    }
    @Test
    void deleteById() {
        userInfoMethodDao.deleteById(1L);
        userInfoMethodDao.findById(1L)
                .ifPresent(u-> log.info("\nuserInfo {}", JsonUtil.toJson(u)));
    }
    @Test
    void deleteAllById() {
         userInfoMethodDao.deleteAllById(Arrays.asList(1L,2L));
        //log.info("\ndeleteById {}", num);
         userInfoMethodDao.findById(1L).ifPresent(u-> log.info("\nuserInfo {}", JsonUtil.toJson(u)));
        //num = userInfoMethodDao.deleteAllById(null);
        //log.info("\ndeleteById {}", num);
    }


    /**
     * 批量插入
     */
    @Test
    @Transactional
    @Rollback
    public void bachInsert() {
        long start = System.currentTimeMillis();
        List<UserInfoDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            UserInfoDO item=  UserInfoDO.builder().userName("name:"+i).userCode("u:"+i).build();
           // item.setVersion(1);
            //item.setIsDelete(0);
            list.add(item);
        }
        int resultNum = userInfoMethodDao.insertBatch(list,false);
        //int resultNum = userInfoMethodDao.update(list,false);
        log.info("resultNum {} id First {} id Last {}",resultNum,list.get(0).getId(),
                list.get(list.size()-1).getId());
        log.info("time {}",System.currentTimeMillis()-start);//1554
        Iterable<UserInfoDO> userInfoDOList = userInfoMethodDao.findAllById(
                Arrays.asList(10L,100L));
        log.info(JsonUtil.toJson(userInfoDOList));
    }
    @Test
    //@Transactional
    //@Rollback
    public void bachUpdate() {
        long start = System.currentTimeMillis();
        List<UserInfoDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            UserInfoDO item=  UserInfoDO.builder().id(i+1L).userName("name:"+i).userCode("u:"+i).build();
            //item.setVersion(1);
            //item.setIsDelete(0);
            list.add(item);
        }
        int resultNum = userInfoMethodDao.updateBatch(list,false);
        //int resultNum = userInfoMethodDao.update(list,false);
        log.info("resultNum {} id First {} id Last {}",resultNum,list.get(0).getId(),
                list.get(list.size()-1).getId());
        resultNum = userInfoMethodDao.updateBatch(list,false);
        log.info("resultNum {}",resultNum);
        log.info("time {}",System.currentTimeMillis()-start);//1554
    }

}