package com.vonchange.jdbc.abstractjdbc.core;

import com.vonchange.jdbc.abstractjdbc.config.Constants;
import com.vonchange.jdbc.abstractjdbc.util.sql.SqlFill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcLogUtil {
    private static final Logger log = LoggerFactory.getLogger(JdbcLogUtil.class);
    public static void logSql(Constants.EnumRWType enumRWType, String sql, Object... params) {
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
