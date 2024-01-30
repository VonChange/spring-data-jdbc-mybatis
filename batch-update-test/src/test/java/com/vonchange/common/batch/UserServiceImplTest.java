package com.vonchange.common.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {
    @Resource
    private UserBaseDao userBaseDao;
    private static final int MAXCOUNT = 100000;

    @Test
    public void saveBatchCustomMini() {
        long stime = System.currentTimeMillis(); // 统计开始时间
        List<UserBaseDO> list = new ArrayList<>();
        for (int i = 0; i < MAXCOUNT; i++) {
            UserBaseDO user = new UserBaseDO();
            user.setUserName("test:" + i);
            user.setMobilePhone("123456899");
            // user.setDescInfo(longText);
            list.add(user);
        }
        //  批量插入
        userBaseDao.insertBatch(list, 1000);
        long etime = System.currentTimeMillis(); // 统计结束时间
        System.out.println("执行时间：" + (etime - stime));
        // 1424 1639 1905 ==1392 15651 62154 2260
    }

}