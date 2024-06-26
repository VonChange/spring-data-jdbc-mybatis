package com.vonchange.mybatis.dialect;


import com.vonchange.common.util.ConvertUtil;
import com.vonchange.common.util.UtilAll;


/**
 *mysql方言
 * @author von_change@163.com
 *  2015-6-14 下午12:47:21
 */
public class MySQLDialect implements Dialect {

    @Override
    public String getPageSql(String sql, int beginNo, int pageSize)  {
    	return 	UtilAll.UString.format("{} limit {},{} ", sql, ConvertUtil.toString(beginNo), ConvertUtil.toString(pageSize));
    }



    @Override
    public int getBigDataFetchSize() {
        return Integer.MIN_VALUE;
    }

    @Override
    public int getFetchSize() {
        return -1;
    }

    @Override
    public String getDialogName() {
        return "mysql";
    }

    @Override
    public LikeTemplate getLikeTemplate() {
        return new LikeTemplate(" CONCAT('%',#{{}},'%') "," CONCAT('%',#{{}})"," CONCAT(#{{}},'%') ");
    }
}
