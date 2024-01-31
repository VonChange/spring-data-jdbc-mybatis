package com.vonchange.mybatis.test.config;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface BaseCrudMapper<T, ID> {

    @Insert("@UserMapper.insert")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    public ID  insert(T instance);
}
