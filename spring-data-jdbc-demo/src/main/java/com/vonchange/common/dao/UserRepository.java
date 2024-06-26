package com.vonchange.common.dao;

import com.vonchange.common.domain.ReqUser;
import com.vonchange.common.domain.UserDTO;
import com.vonchange.common.domain.UserInfoDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<UserInfoDO,Integer> {

    @Query("user.queryByUserCode")
    //@Query("select * from user_info where user_code =:userCode")
    List<UserDTO> queryByUserCode(@Param("userCode") String userCode);

    Page<UserInfoDO> findByUserCode(String userCode, Pageable pageable);
    //@Query("user.queryByBean")
    @Query("select * from user_info where  user_code = #{user.userCode}")
    List<UserInfoDO> queryByBean(@Param("user") ReqUser user);
    @Query("user.queryByUserCodes")
    //@Query("select * from user_info where user_code in (:userCodes)")
    List<UserDTO> queryByUserCodes(@Param("userCodes")List<String> userCodes);
    List<UserInfoDO> queryByUserName(@Param("userName") String userName);
    @Modifying
    @Query("update user_info set user_name = #{user.userName} where user_code = #{user.userCode}")
    int updateByUserCode(@Param("user") UserDTO user);
}