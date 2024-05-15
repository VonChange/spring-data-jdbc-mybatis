package com.vonchange.nine.demo.dao;

import com.vonchange.jdbc.mybatis.core.support.QueryRepository;
import com.vonchange.nine.demo.domain.UserInfoDO;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;


public interface UserInfoQueryDao extends QueryRepository {

  List<UserInfoDO> findList(@Param("userCode") String userCode);
  List<Map<String,Object>> findMapList(@Param("userCode") String userCode);
}