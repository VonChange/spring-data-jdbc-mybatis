package com.vonchange.nine.demo.dao;

import com.vonchange.jdbc.mybatis.core.support.CrudExtendRepository;
import com.vonchange.nine.demo.domain.UserInfoDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserInfoMethodDao extends CrudExtendRepository<UserInfoDO, Long> {
    UserInfoDO findByUserCode(String userCode);

    List<UserInfoDO> findByCreateTimeBetween(LocalDateTime begin,LocalDateTime end);
    boolean countByUserCodeIn(List<String> userCodes);
    List<UserInfoDO> findByUserCodeIn(List<String> userCodes);
    Page<UserInfoDO> findPageByUserCodeIn(Pageable pageable,List<String> userCodes);
    List<UserInfoDO> findByUserCodeInOrderByCreateTimeDesc(List<String> userCodes);
}
