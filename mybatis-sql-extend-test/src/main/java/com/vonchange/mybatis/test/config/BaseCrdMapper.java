package com.vonchange.mybatis.test.config;

public interface BaseCrdMapper<T, ID> {

    public <S extends T> int insert(S instance);
}
