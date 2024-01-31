package com.vonchange.mybatis.dialect;


import com.vonchange.common.util.ConvertUtil;
import com.vonchange.common.util.UtilAll;
import com.vonchange.mybatis.config.Constant;


/**
 *
 * Created by 冯昌义 on 2018/4/16.
 */
public class OracleDialect implements Dialect {
    @Override
    public String getPageSql(String sql, int beginNo, int pageSize) {
        if(beginNo==0){
            String  sqlLimit="{}" +
                    " fetch first {} rows only" ;
            return 	UtilAll.UString.format(sqlLimit, sql, ConvertUtil.toString(pageSize));
        }
        String sqlOrg="{}" +
                " offset {} rows fetch next {} rows only " ;
        return 	UtilAll.UString.format(sqlOrg, sql, ConvertUtil.toString(beginNo), ConvertUtil.toString(pageSize));
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
        return Constant.Dialog.ORACLE;
    }

    @Override
    public LikeTemplate getLikeTemplate() {
        return new LikeTemplate(" ''%''||#{{}}||''%'' "," ''%''||#{{}} "," #{{}}||''%'' ");
    }
}
