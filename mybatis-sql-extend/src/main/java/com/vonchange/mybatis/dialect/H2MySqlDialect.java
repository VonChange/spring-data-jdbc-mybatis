package com.vonchange.mybatis.dialect;

public class H2MySqlDialect extends MySQLDialect {
    @Override
    public int getBigDataFetchSize() {
        return 500;
    }

    @Override
    public int getFetchSize() {
        return -1;
    }

    @Override
    public String getDialogName() {
        return "h2-mysql";
    }
}
