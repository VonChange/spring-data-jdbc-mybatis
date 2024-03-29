package com.vonchange.nine.demo.dao;

import com.vonchange.nine.demo.domain.UserInfoDO;

import java.util.List;

public interface IUserInfoCrudClientDao {
    List<UserInfoDO> findList(String userCode);

}
