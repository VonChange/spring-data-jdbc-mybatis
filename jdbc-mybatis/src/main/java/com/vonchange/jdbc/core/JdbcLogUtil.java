package com.vonchange.jdbc.core;

import com.vonchange.jdbc.config.EnumRWType;
import com.vonchange.jdbc.util.SqlFill;
import com.vonchange.jdbc.model.SqlParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcLogUtil {
    private static final Logger log = LoggerFactory.getLogger(JdbcLogUtil.class);
    public static void logSql(EnumRWType enumRWType, SqlParam sqlParam) {
        logSql(enumRWType, sqlParam.getSql(), sqlParam.getParams());
    }
    public static void logSql(EnumRWType enumRWType, String sql, Object... params) {
        if (log.isDebugEnabled()) {
            try{
                String sqlResult = SqlFill.fill(sql, params);
                log.debug("full sql: {}", sqlResult);
            }catch (Exception e){
                log.warn("logFull {}",e.getMessage());
                log.debug("org sql: {} params: {}", sql, params);
            }
        }
    }
}
