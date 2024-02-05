package com.vonchange.nine.demo.dao;

import com.vonchange.jdbc.mybatis.core.support.QueryRepository;
import com.vonchange.nine.demo.domain.UserInfoDO;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;



public interface UserQueryRepository extends QueryRepository {

  List<UserInfoDO> findList(@Param("userName") String userName,
                            @Param("createTime") Date createTime);
}