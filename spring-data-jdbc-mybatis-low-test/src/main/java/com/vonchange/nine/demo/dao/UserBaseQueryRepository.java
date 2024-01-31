package com.vonchange.nine.demo.dao;

import com.vonchange.nine.demo.domain.UserBaseDO;
import com.vonchange.jdbc.mybatis.core.support.BaseQueryRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;



public interface UserBaseQueryRepository extends BaseQueryRepository {

  List<UserBaseDO> findList(@Param("userName") String userName,
                            @Param("createTime") Date createTime);
}