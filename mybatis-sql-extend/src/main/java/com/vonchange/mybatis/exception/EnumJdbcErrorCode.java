package com.vonchange.mybatis.exception;

public enum EnumJdbcErrorCode {
    Error,CanNotGenNameQuery, OrderSqlMustStartWithOrderBy, OrderSqlCanNotGen, NotSupportClass, FieldNotAllowed, ParamEmpty, NeedIdAnnotation, MustArray, SqlIdNotFound, MybatisSqlError, PlaceholderNotFound, NewInstanceError, TypeError, SqlError, DataSourceNotFound;
    public static final String CanNotGenNameQueryMessage="{} can not generate sql by method name,must start with find or count,please define in the markdown";
}
