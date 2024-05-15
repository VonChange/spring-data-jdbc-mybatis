package com.vonchange.mybatis.dialect;

public class OracleLowDialect implements Dialect {
    @Override
    public String getPageSql(String sql, int beginNo, int pageSize) {
        return  "select * from (select row_.*, rownum rownum_ from (" + sql + ")row_ where rownum <= " + (beginNo + pageSize) +
                " ) where rownum_ >= " + (beginNo + 1);
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
        return "oracle_low";
    }

    @Override
    public LikeTemplate getLikeTemplate() {
        return new LikeTemplate(" ''%''||#'{'{0}'}'||''%'' "," ''%''||#'{'{0}'}' "," #'{'{0}'}'||''%'' ");
    }
}
