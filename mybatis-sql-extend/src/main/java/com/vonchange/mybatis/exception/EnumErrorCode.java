package com.vonchange.mybatis.exception;

public enum EnumErrorCode {
    Error,CanNotGenNameQuery;
    public static final String CanNotGenNameQueryMessage="{} can not generate sql by method name,must start with find or count,please define in the markdown";
}
