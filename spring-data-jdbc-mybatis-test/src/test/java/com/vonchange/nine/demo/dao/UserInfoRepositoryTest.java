package com.vonchange.nine.demo.dao;

import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import com.vonchange.nine.demo.domain.SearchParam;
import com.vonchange.nine.demo.domain.UserInfoDO;
import com.vonchange.nine.demo.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
//@Slf4j
public class UserInfoRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(UserInfoRepositoryTest.class);

    @Resource
    private UserInfoRepository userInfoRepository;

    @Test
    public void findListByUserCode() {
        List<UserInfoDO> userInfoDOList = userInfoRepository.findListByUserCode("u001");
        System.out.println(userInfoDOList.size());
        userInfoDOList.forEach(UserInfoDO -> {
            log.info("\nUserInfoDOList {}",JsonUtil.toJson(UserInfoDO));
        });
    }

    @Test
    public void findUserNameByCode() {
        String userName = userInfoRepository.findUserNameByCode("u000");
        log.info("\n userName {}",userName);
    }

    @Test
    public void findUserList() {
        List<UserInfoDO> UserInfoDOList = userInfoRepository.findUserList("change",LocalDateTime.now().plusHours(1L),
                0);
        UserInfoDOList.forEach(UserInfoDO -> {
            log.info("\nUserInfoDOList {}",JsonUtil.toJson(UserInfoDO));
        });
    }
    @Test
    public void findUserPage() {
        Pageable pageable = PageRequest.of(0,10);
        //PageRequest.of(0,3);
        Page<UserInfoDO> userInfoDOPage = userInfoRepository.findUserList(pageable,"change",LocalDateTime.now().plusHours(1L),0);
        log.info("\n {}",userInfoDOPage.getTotalElements());
        userInfoDOPage.getContent().forEach(UserInfoDO -> {
            log.info("\n {}",UserInfoDO.toString());
        });
    }

    @Test
    public void findUserBySearchParam() {
        SearchParam searchParam = new SearchParam();
        searchParam.setUserName("chang");
        List<UserInfoDO> userInfoDOList = userInfoRepository.findUserBySearchParam(searchParam);
        userInfoDOList.forEach(UserInfoDO -> {
            log.info("\n {}",userInfoDOList);
        });
    }



    @Test
    public void findById() {
        UserInfoDO UserInfoDO = userInfoRepository.findById(1L);
        log.info("\n UserInfoDO {}",UserInfoDO);
    }

    @Test
    public void findByXX() {
        List<UserInfoDO> UserInfoDOList = userInfoRepository.findByUserName("test");
        UserInfoDOList.forEach(UserInfoDO -> {
            log.info("\n UserInfoDO {}",UserInfoDO);
        });

    }
    @Test
    public void findLongList() {
        List<Long> idList = userInfoRepository.findLongList();
        idList.forEach(id -> {
            log.info("\n id {}",id);
        });

    }
    @Test
    public void findListByIds() {
        List<UserInfoDO> UserInfoDOListQ = userInfoRepository.findListByIds("test",null,Arrays.asList(1L,2L));
        UserInfoDOListQ.forEach(UserInfoDO -> {
            log.info("\n {}",UserInfoDO.toString());
        });
        List<UserInfoDO> UserInfoDOList = userInfoRepository.findListByIds("test",new Date(), Arrays.asList(1L,2L));
        UserInfoDOList.forEach(UserInfoDO -> {
            log.info("\n {}",UserInfoDO.toString());
        });

    }

    @Test
    @Transactional
    @Rollback
    public void updateIsDelete() {
        int result = userInfoRepository.updateIsDelete(1,1L);
        log.info("result {}",result);
    }
    @Test
    public void insert() throws IOException {
        UserInfoDO UserInfoDO = new UserInfoDO();
        //UserInfoDO.setId(3L);
        UserInfoDO.setUserName("test");
        UserInfoDO.setUserCode(UUID.randomUUID().toString());
        UserInfoDO.setStatus(1);

        //UserInfoDO.setHeadImageData(FileUtils.readFileToByteArray(new File("/Users/vonchange/work/docment/cat.jpg")));
       // UserInfoDO.setCode("1");
        //UserInfoDO.setCreateTime(LocalDateTime.now().plusHours(1L));
        int result  = userInfoRepository.insert(UserInfoDO);
        log.info("\nresult {} {} ",result,UserInfoDO.toString());
        UserInfoDO UserInfoDOFind =userInfoRepository.findById(UserInfoDO.getId());
        //FileUtils.writeByteArrayToFile(new File("/Users/vonchange/work/docment/catcc.jpg"),UserInfoDOFind.getHeadImageData());
        log.info("\nUserInfoDOFind {}",UserInfoDOFind.toString());
    }



    @Test
    //@Transactional
    //@Rollback
    public void update() {
        UserInfoDO UserInfoDO = new UserInfoDO();
        UserInfoDO.setUserName("test_ss");
        UserInfoDO.setId(1L);
        int result  = userInfoRepository.update(UserInfoDO);
        log.info("\nresult {}",result);
        //UserInfoDO UserInfoDOFind =userInfoRepository.findById(1L);
        //log.info("\nUserInfoDOFind {}",UserInfoDOFind.toString());
    }

    @Test
    public void updateAllField() {
        UserInfoDO UserInfoDO = new UserInfoDO();
        UserInfoDO.setUserName(null);
        UserInfoDO.setId(1L);
        int result  = userInfoRepository.updateAllField(UserInfoDO);
        log.info("\nresult {}",result);
    }

    //13

    /**
     * 批量插入
     */
    @Test
    @Transactional
    public void insertBatch() {
      /*  int result  = userInfoRepository.update(new UserInfoDO(1L,"testxx","",1,null,null));
        log.info("result {}",result);
        long start = System.currentTimeMillis();
        List<UserInfoDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            list.add(new UserInfoDO(null,"三e"+i,"1100"+i,null, LocalDateTime.now(),null));
        }
        int resultx = userInfoRepository.insertBatch(list,5000);
        log.info("id {}",list.get(0).getId());
        log.info("result {}",resultx);
        log.info("time {}",System.currentTimeMillis()-start);//1554*/
    }


    @Test
    //@Transactional
    //@Rollback
    public void updateBatchBySqlId() {
       /* int result  = userInfoRepository.update(new UserInfoDO(1L,"testxx","",1,null,null));
        log.info("result {}",result);
        long start = System.currentTimeMillis();
        List<UserInfoDO> list = new ArrayList<>();
        for (int i=0;i<2;i++) {
            list.add(new UserInfoDO(1L+i,"RRR"+i,null,null,null,new Date()));
        }
        int resultx  = userInfoRepository.batchUpdate(list);
        log.info("resultx {}",resultx);
        log.info("time {}",System.currentTimeMillis()-start);*/
    }

    @Test
    @Transactional
    //@Rollback
    public void insertBatchNormal() {
     /*   int result  = userInfoRepository.update(new UserInfoDO(1L,"testxx","",1,null,null));
        log.info("result {}",result);
        long start = System.currentTimeMillis();
        List<UserInfoDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            list.add(new UserInfoDO(null,"三e"+i,"1100"+i,null, LocalDateTime.now(),null));
        }
        int resultx  = userInfoRepository.insertBatchNormal(list);
        System.out.println(list.get(0).getId());
        log.info("resultx {}",resultx);
        log.info("time {}",System.currentTimeMillis()-start);//908*/
    }

    @Test
    @Transactional
    //@Rollback
    public void bachUpdate() {
       /* int result  = userInfoRepository.update(new UserInfoDO(1L,"testxx","",1,null,null));
        log.info("result {}",result);
        long start = System.currentTimeMillis();
        List<UserInfoDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            list.add(new UserInfoDO(null,"三e"+i,"1100"+i,null, LocalDateTime.now(),null));
        }
        int resultx  = userInfoRepository.batchInsert(list);
        System.out.println(list.get(0).getId());
        log.info("resultx {}",resultx);
        log.info("time {}",System.currentTimeMillis()-start);//563*/
    }


    @Test
    @Transactional
    public void findBigData() {

        long start = System.currentTimeMillis();
        List<UserInfoDO> list = new ArrayList<>();
        for (int i=0;i<10006;i++) {
            list.add(null);
            //new UserInfoDO(null,"三e"+i,"1100"+i,null, LocalDateTime.now(),null)
        }
        int resultx = userInfoRepository.insertBatch(list,5000);
        log.info("id {}",list.get(0).getId());
        log.info("result {}",resultx);
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
       userInfoRepository.findBigData(abstractPageWork,"三");
        log.info("{} {} {}",abstractPageWork.getSize(),abstractPageWork.getTotalPages(),abstractPageWork.getTotalElements());
    }
}