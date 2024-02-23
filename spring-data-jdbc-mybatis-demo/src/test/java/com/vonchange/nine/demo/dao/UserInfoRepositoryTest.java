package com.vonchange.nine.demo.dao;

import com.vonchange.common.util.JsonUtil;
import com.vonchange.jdbc.mapper.AbstractPageWork;
import com.vonchange.jdbc.util.NameQueryUtil;
import com.vonchange.nine.demo.domain.SearchParam;
import com.vonchange.nine.demo.domain.UserInfoDO;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



@SpringBootTest
//@Transactional
//@Slf4j
public class UserInfoRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(UserInfoRepositoryTest.class);

    @Resource
    private UserInfoRepository userInfoRepository;


    @Test
    public void findByUserCodes() {
        List<UserInfoDO> userInfoDOList = userInfoRepository.findByUserCodes(
                Arrays.asList("u001","u002"));
        userInfoDOList.forEach(UserInfoDO -> {
            log.info("\nUserInfoDOList {}",JsonUtil.toJson(UserInfoDO));
        });
    }

    @Test
    public void findUserNameByCode() {
        String sqlDialect=System.getenv("ID_END");
        log.info("XXX:: "+sqlDialect);
        String userName = userInfoRepository.findUserNameByCode("u000");
        log.info("\n userName {}",userName);
    }

    @Test
    public void findUserList() {
        List<UserInfoDO> userInfoDOList = userInfoRepository.findUserList(Arrays.asList("u000","u001","u002"),
                "ch",null);//LocalDateTime.now().plusHours(1L)
        userInfoDOList.forEach(userInfoDO -> {
            log.info("\nUserInfoDOList {}",JsonUtil.toJson(userInfoDO));
        });
    }
    @Test
    public void findUserPage() {
        Pageable pageable = PageRequest.of(0,10);
        Page<UserInfoDO> userInfoDOPage = userInfoRepository
                .findUserList(pageable,Arrays.asList("u000","u001","u002"),
                        "ch",LocalDateTime.now().plusHours(1L));
        log.info("\n {}",userInfoDOPage.getTotalElements());
        userInfoDOPage.getContent().forEach(UserInfoDO -> {
            log.info("\n {}",UserInfoDO.toString());
        });
    }

    @Test
    public void findUserBySearchParam() {
        SearchParam searchParam = new SearchParam();
        searchParam.setUserName("chang");
        //searchParam.setUserCodes(Arrays.asList("u000","u001","u002"));
        searchParam.setCreateTime(toDate(LocalDateTime.now().plusHours(1L)));
        searchParam.setSort(NameQueryUtil.orderSql("orderByCreateTimeDescId",UserInfoDO.class));
        List<UserInfoDO> userInfoDOList = userInfoRepository.findUserBySearchParam(searchParam);
        userInfoDOList.forEach(userInfoDO -> {
            log.info("\n {}", JsonUtil.toJson(userInfoDO));
        });
    }
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }



    @Test
    @Transactional
    @Rollback
    public void updateIsDelete() {
        int result = userInfoRepository.updateIsDelete(1,1L);
        log.info("result {}",result);
        userInfoRepository.findById(1L)
                .ifPresent(u->log.info("\nuserInfoDO {}",JsonUtil.toJson(u)));

    }


    @Test
    @Transactional
    public void batchUpdate() {
        long start = System.currentTimeMillis();
        List<UserInfoDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            UserInfoDO userInfoDO = UserInfoDO.builder().id(0L+i).userName("name:"+i)
                    .build();
            list.add(userInfoDO);
        }
        int resultNum  = userInfoRepository.batchUpdate(list);
        log.info("resultNum {}",resultNum);
        log.info("time {}",System.currentTimeMillis()-start);
        Iterable<UserInfoDO> userInfoDOList = userInfoRepository.findAllById(Arrays.asList(1L,2L));
        log.info("userInfoDOList {}",JsonUtil.toJson(userInfoDOList));
    }

    @Test
    @Transactional
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
                //userInfoRepository.saveAllNotNull(list,1000);
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
       userInfoRepository.findBigData(abstractPageWork,"name");
        log.info("{} {} {}",abstractPageWork.getSize(),abstractPageWork.getTotalPages(),abstractPageWork.getTotalElements());
    }


}