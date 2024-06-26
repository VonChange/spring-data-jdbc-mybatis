package com.vonchange.nine.demo.dao;

import com.vonchange.common.util.JsonUtil;
import com.vonchange.jdbc.client.CrudClient;
import com.vonchange.jdbc.mapper.AbstractPageWork;
import com.vonchange.nine.demo.domain.UserInfoDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class CrudClientTest {
    @Resource
    private CrudClient crudClient;
    @Resource
    private UserInfoRepository userInfoRepository;
    @Resource
    private IUserInfoCrudClientDao iUserInfoCrudClientDao;

    @Test
    public void findList() {
        for (UserInfoDO userInfoDO : iUserInfoCrudClientDao.findList("u000")) {
            log.info("userInfoDO {}", JsonUtil.toJson(userInfoDO));
        }
    }

    @Test
    public void findListBase() {
        for (UserInfoDO userInfoDO : crudClient.sqlId("findList").namespace(this)
                .param("userCode", "u000")
                .query(UserInfoDO.class).list()) {
            log.info("userInfoDO {}", JsonUtil.toJson(userInfoDO));
        }
    }

    @Test
    public void findListJdbc() {
        for (UserInfoDO userInfoDO : crudClient.jdbc().sql("select * from user_info\n" +
                        "where  is_delete=0\n" +
                        "and user_code = #{userCode}")
                .param("userCode", "u000")
                .query(UserInfoDO.class).list()) {
            log.info("userInfoDO {}", JsonUtil.toJson(userInfoDO));
        }
    }

    @Test
    public void findBigData() {
        long start = System.currentTimeMillis();
        List<UserInfoDO> list = new ArrayList<>();
        for (int i=0;i<10006;i++) {
            UserInfoDO userInfoDO = UserInfoDO.builder().userCode("code:"+i).userName("name:"+i)
                    .build();
            userInfoDO.setCreateTime(LocalDateTime.now());
            list.add(userInfoDO);
        }
        int resultNum = 0;
        userInfoRepository.insertBatch(list,false);
        log.info("resultNum {}",resultNum);
        log.info("time {}",System.currentTimeMillis()-start);//1554
        AbstractPageWork<UserInfoDO> abstractPageWork = new AbstractPageWork<UserInfoDO>() {
            @Override
            protected void doPage(List<UserInfoDO> pageContentList, int pageNum, Map<String, Object> extData) {
                pageContentList.forEach(UserInfoDO -> {
                    log.info("{}",UserInfoDO.toString());
                });

            }

            @Override
            protected int getPageSize() {
                return 500;
            }
        };
        crudClient.sqlId("findBigData").namespace(this).param("userName","name").queryBatch(UserInfoDO.class,abstractPageWork);
                //.findBigData(abstractPageWork,"name");
        log.info("{} {} {}",abstractPageWork.getSize(),abstractPageWork.getTotalPages(),abstractPageWork.getTotalElements());
    }
}
