package com.vonchange.nine.demo.dao;

import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import com.vonchange.nine.demo.domain.EnumDelete;
import com.vonchange.nine.demo.domain.EnumStatus;
import com.vonchange.nine.demo.domain.SearchParam;
import com.vonchange.nine.demo.domain.UserBaseDO;
import com.vonchange.nine.demo.domain.UserBaseVO;
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
public class UserBaseRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(UserBaseRepositoryTest.class);

    @Resource
    private UserBaseRepository userBaseRepository;

    @Test
    public void findList() {
        List<UserBaseDO> userBaseDOList = userBaseRepository.findList("test",LocalDateTime.now().plusHours(1L),
                EnumDelete.unDeleted);
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n {}",JsonUtil.toJson(userBaseDO));
        });
        UserBaseDO userBaseDO = userBaseRepository.findOne("test");
        log.info("\n {}",userBaseDO.toString());
    }


    @Test
    public void findInList() {
        List<UserBaseDO> userBaseDOList = userBaseRepository.findInList(Arrays.asList("test"), Arrays.asList(0));
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n {}",JsonUtil.toJson(userBaseDO));
        });

    }

    @Test
    public void findOne() {
        UserBaseDO userBaseDO = userBaseRepository.findOne("test");
        log.info("\n {}",userBaseDO.toString());
    }

    @Test
    public void findListBase() {
        List<UserBaseDO> userBaseDOList = userBaseRepository.findListBase("张三日子");
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n {}",userBaseDO.toString());
        });
    }

    @Test
    public void findListByBean() {
        SearchParam searchParam = new SearchParam();
        searchParam.setUserName("张三日子");
        List<UserBaseDO> userBaseDOList = userBaseRepository.findListByBean(searchParam);
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n {}",userBaseDO.toString());
        });
    }
    @Test
    public void findListVo() {
        List<UserBaseVO> userBaseVOList = userBaseRepository.findListVo("张三日子",new Date());
        userBaseVOList.forEach(userBaseVO -> {
            log.info("\n {}",userBaseVO.toString());
        });
    }
    @Test
    public void findListByPage() {
        Pageable pageable = new PageRequest(0,3);
                //PageRequest.of(0,3);
        Page<UserBaseDO> personRepositoryBy = userBaseRepository.findList(pageable,"张三日子",null);
        log.info("\n {}",personRepositoryBy.toString());
        personRepositoryBy.getContent().forEach(userBaseDO -> {
            log.info("\n {}",userBaseDO.toString());
        });
    }
    @Test
    public void findUserName() {
        String userName = userBaseRepository.findUserName("test");
        log.info("\n userName {}",userName);
    }

    @Test
    public void findById() {
        UserBaseDO userBaseDO = userBaseRepository.findById(1L);
        log.info("\n userBaseDO {}",userBaseDO);
    }

    @Test
    public void findByXX() {
        List<UserBaseDO> userBaseDOList = userBaseRepository.findByUserName("test");
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n userBaseDO {}",userBaseDO);
        });

    }
    @Test
    public void findLongList() {
        List<Long> idList = userBaseRepository.findLongList();
        idList.forEach(id -> {
            log.info("\n id {}",id);
        });

    }
    @Test
    public void findListByIds() {
        List<UserBaseDO> userBaseDOListQ = userBaseRepository.findListByIds("test",null,Arrays.asList(1L,2L));
        userBaseDOListQ.forEach(userBaseDO -> {
            log.info("\n {}",userBaseDO.toString());
        });
        List<UserBaseDO> userBaseDOList = userBaseRepository.findListByIds("test",new Date(), Arrays.asList(1L,2L));
        userBaseDOList.forEach(userBaseDO -> {
            log.info("\n {}",userBaseDO.toString());
        });

    }

    @Test
    @Transactional
    @Rollback
    public void updateIsDelete() {
        int result = userBaseRepository.updateIsDelete(1,1L);
        log.info("result {}",result);
    }
    @Test
    public void insert() throws IOException {
        UserBaseDO userBaseDO = new UserBaseDO();
        //userBaseDO.setId(3L);
        userBaseDO.setUserName("test");
        userBaseDO.setCode(UUID.randomUUID().toString());
        userBaseDO.setStatus(EnumStatus.on);

        //userBaseDO.setHeadImageData(FileUtils.readFileToByteArray(new File("/Users/vonchange/work/docment/cat.jpg")));
       // userBaseDO.setCode("1");
        //userBaseDO.setCreateTime(LocalDateTime.now().plusHours(1L));
        int result  = userBaseRepository.insert(userBaseDO);
        log.info("\nresult {} {} ",result,userBaseDO.toString());
        UserBaseDO userBaseDOFind =userBaseRepository.findById(userBaseDO.getId());
        //FileUtils.writeByteArrayToFile(new File("/Users/vonchange/work/docment/catcc.jpg"),userBaseDOFind.getHeadImageData());
        log.info("\nuserBaseDOFind {}",userBaseDOFind.toString());
    }


    @Test
    public void insertDuplicateKey() {
        UserBaseDO userBaseDO = new UserBaseDO();
        userBaseDO.setUserName("UUUUU");
        userBaseDO.setMobilePhone("110");
        int result  = userBaseRepository.insertDuplicateKey(userBaseDO);
        log.info("\nresult {} {}",result,userBaseDO.getId());

    }
    @Test
    //@Transactional
    //@Rollback
    public void update() {
        UserBaseDO userBaseDO = new UserBaseDO();
        userBaseDO.setUserName("test_ss");
        userBaseDO.setId(1L);
        int result  = userBaseRepository.update(userBaseDO);
        log.info("\nresult {}",result);
        //UserBaseDO userBaseDOFind =userBaseRepository.findById(1L);
        //log.info("\nuserBaseDOFind {}",userBaseDOFind.toString());
    }

    @Test
    public void updateAllField() {
        UserBaseDO userBaseDO = new UserBaseDO();
        userBaseDO.setUserName(null);
        userBaseDO.setId(1L);
        int result  = userBaseRepository.updateAllField(userBaseDO);
        log.info("\nresult {}",result);
    }

    //13

    /**
     * 批量插入
     */
    @Test
    @Transactional
    public void insertBatch() {
        int result  = userBaseRepository.update(new UserBaseDO(1L,"testxx","",1,null,null));
        log.info("result {}",result);
        long start = System.currentTimeMillis();
        List<UserBaseDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            list.add(new UserBaseDO(null,"三e"+i,"1100"+i,null, LocalDateTime.now(),null));
        }
        int resultx = userBaseRepository.insertBatch(list,5000);
        log.info("id {}",list.get(0).getId());
        log.info("result {}",resultx);
        log.info("time {}",System.currentTimeMillis()-start);//1554
    }



    /**
     * 批量插入
     */
    @Test
    @Transactional
    public void insertBatchDuplicateKey() {
        int result  = userBaseRepository.update(new UserBaseDO(1L,"testxx","",1,null,null));
        log.info("result {}",result);
        long start = System.currentTimeMillis();
        List<UserBaseDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            list.add(new UserBaseDO(null,"三e"+i,"1100"+i,null, LocalDateTime.now(),null));
        }
        int resultx = userBaseRepository.insertBatchDuplicateKey(list,5000);
        log.info("id {}",list.get(0).getId());
        log.info("result {}",resultx);
        int resulty = userBaseRepository.insertBatchDuplicateKey(list,5000);
        log.info("result {}",resulty);
        log.info("time {}",System.currentTimeMillis()-start);//1554
    }

    @Test
    //@Transactional
    //@Rollback
    public void updateBatchBySqlId() {
        int result  = userBaseRepository.update(new UserBaseDO(1L,"testxx","",1,null,null));
        log.info("result {}",result);
        long start = System.currentTimeMillis();
        List<UserBaseDO> list = new ArrayList<>();
        for (int i=0;i<2;i++) {
            list.add(new UserBaseDO(1L+i,"RRR"+i,null,null,null,new Date()));
        }
        int resultx  = userBaseRepository.batchUpdate(list);
        log.info("resultx {}",resultx);
        log.info("time {}",System.currentTimeMillis()-start);
    }

    @Test
    @Transactional
    //@Rollback
    public void insertBatchNormal() {
        int result  = userBaseRepository.update(new UserBaseDO(1L,"testxx","",1,null,null));
        log.info("result {}",result);
        long start = System.currentTimeMillis();
        List<UserBaseDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            list.add(new UserBaseDO(null,"三e"+i,"1100"+i,null, LocalDateTime.now(),null));
        }
        int resultx  = userBaseRepository.insertBatchNormal(list);
        System.out.println(list.get(0).getId());
        log.info("resultx {}",resultx);
        log.info("time {}",System.currentTimeMillis()-start);//908
    }

    @Test
    @Transactional
    //@Rollback
    public void bachUpdate() {
        int result  = userBaseRepository.update(new UserBaseDO(1L,"testxx","",1,null,null));
        log.info("result {}",result);
        long start = System.currentTimeMillis();
        List<UserBaseDO> list = new ArrayList<>();
        for (int i=0;i<10000;i++) {
            list.add(new UserBaseDO(null,"三e"+i,"1100"+i,null, LocalDateTime.now(),null));
        }
        int resultx  = userBaseRepository.batchInsert(list);
        System.out.println(list.get(0).getId());
        log.info("resultx {}",resultx);
        log.info("time {}",System.currentTimeMillis()-start);//563
    }


    @Test
    @Transactional
    public void findBigData() {

        long start = System.currentTimeMillis();
        List<UserBaseDO> list = new ArrayList<>();
        for (int i=0;i<10006;i++) {
            list.add(new UserBaseDO(null,"三e"+i,"1100"+i,null, LocalDateTime.now(),null));
        }
        int resultx = userBaseRepository.insertBatch(list,5000);
        log.info("id {}",list.get(0).getId());
        log.info("result {}",resultx);
        log.info("time {}",System.currentTimeMillis()-start);//1554
        AbstractPageWork<UserBaseDO> abstractPageWork = new AbstractPageWork<UserBaseDO>() {
            @Override
            protected void doPage(List<UserBaseDO> pageContentList, int pageNum, Map<String, Object> extData) {
                pageContentList.forEach(userBaseDO -> {
                    log.info("{}",userBaseDO.toString());
                });

            }

            @Override
            protected int getPageSize() {
                return 500;
            }
        };
       userBaseRepository.findBigData(abstractPageWork,"三");
        log.info("{} {} {}",abstractPageWork.getSize(),abstractPageWork.getTotalPages(),abstractPageWork.getTotalElements());
    }
}