package com.vonchange.nine.demo.dao;


import com.vonchange.jdbc.client.CrudClient;
import com.vonchange.nine.demo.domain.UserInfoDO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class UserInfoCrudClientDao implements IUserInfoCrudClientDao{

    private CrudClient crudClient;

    @Override
    public List<UserInfoDO> findList(String userCode) {
        return crudClient.sqlId("findList").namespace(this)
                .param("userCode",userCode)
                .query(UserInfoDO.class).list();
    }
}
