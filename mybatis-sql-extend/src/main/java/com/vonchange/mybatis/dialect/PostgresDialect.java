package com.vonchange.mybatis.dialect;

import com.vonchange.common.util.ConvertUtil;
import com.vonchange.common.util.UtilAll;

public class PostgresDialect implements Dialect {
    @Override
    public String getPageSql(String sql, int beginNo, int pageSize) {
        return 	UtilAll.UString.format("{} limit {} OFFSET {} ", sql,ConvertUtil.toString(pageSize), ConvertUtil.toString(beginNo));
    }

    @Override
    public int getBigDataFetchSize() {
        return 500;
    }

    @Override
    public int getFetchSize() {
        return 500;
    }

    @Override
    public String getDialogName() {
        return "postgres";
    }

    @Override
    public LikeTemplate getLikeTemplate() {
        return new LikeTemplate(" CONCAT('%',#{{}},'%') "," CONCAT('%',#{{}})"," CONCAT(#{{}},'%') ");
    }
}
