package com.vonchange.mybatis.test.config;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("@UserMapper.findList")
    List<UserBaseDO> findList(@Param("userName") String userName,
                              @Param("createTime") LocalDateTime createTime);
}
