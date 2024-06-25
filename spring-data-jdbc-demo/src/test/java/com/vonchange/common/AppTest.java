package com.vonchange.common;

import com.vonchange.common.dao.UserRepository;
import com.vonchange.common.domain.ReqUser;
import com.vonchange.common.domain.UserDTO;
import com.vonchange.common.domain.UserInfoDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple App.
 */
@Slf4j
@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AppTest {
    @Resource
    private UserRepository userRepository;
    @Test
    public  void queryByUserCode() {
       List<UserDTO> userInfoDOList= userRepository.queryByUserCode("u001");
       log.info("queryByUserCode:{}",userInfoDOList);
    }
    @Test
    public  void queryByUserCodes() {
        List<UserDTO> userInfoDOList= userRepository.queryByUserCodes(Arrays.asList("u001","u002","u003"));
        log.info("queryByUserCodes:{}",userInfoDOList);
    }
    @Test
    public  void queryByBean() {
        ReqUser userInfoDO=new ReqUser();
        userInfoDO.setUserCode("u001");
        List<UserInfoDO> userInfoDOList= userRepository.queryByBean(userInfoDO);
        log.info("queryByBean:{}",userInfoDOList);
    }

    @Test
    public  void findByUserCode() {
        Page<UserInfoDO> page= userRepository.findByUserCode("u001", PageRequest.of(0, 1));
        log.info("findByUserCode:{} {}",page,page.getContent());
    }
    @Test
    public  void queryByUserName() {
        List<UserInfoDO> userInfoDOList= userRepository.queryByUserName("name:0");
        log.info("queryByUserName:{}",userInfoDOList);
    }
    @Test
    public  void findById() {
        userRepository.findById(1).ifPresent(item->log.info("findById:{}",item));
    }
    @Test
    public  void updateByUserCode() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserCode("u001");
        userDTO.setUserName("updateByUserCode");
        int num = userRepository.updateByUserCode(userDTO);
        log.info("updateByUserCode:{}",num);
        List<UserDTO> userInfoDOList= userRepository.queryByUserCode("u001");
        log.info("updateByUserCode:{}",userInfoDOList);
    }

    @Test
    @Rollback
    @Transactional
    public  void insert() {
        UserInfoDO userInfoDO =userRepository.save(UserInfoDO.builder().userCode("u009").userName("insert").mobileNo("13888888888").build());
        log.info("insert:{}",userInfoDO);
    }
    @Test
    public  void update() {
        UserInfoDO userInfoDO =userRepository.save(UserInfoDO.builder().id(1L).userCode("u000001").userName("update").mobileNo("13888888888").build());
        log.info("insert:{}",userInfoDO);
    }

    @Test
    public  void findAll() {
        userRepository.findAll().forEach(item->{log.info("item {}",item);});
    }

}
