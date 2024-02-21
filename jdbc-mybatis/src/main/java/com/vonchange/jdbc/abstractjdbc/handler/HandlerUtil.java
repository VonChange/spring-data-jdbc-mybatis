package com.vonchange.jdbc.abstractjdbc.handler;


import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 冯昌义
 * 2017/12/26.
 */
public class HandlerUtil {
    private HandlerUtil() { throw new IllegalStateException("Utility class");}
    public static Map<String,Object> rowToMap(ResultSet rs,String idColumnName) throws SQLException {
        Map<String,Object> resultMap = new LinkedHashMap<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        for (int col = 1; col <= cols; col++) {
            String columnName = JdbcUtils.lookupColumnName(rsmd, col);
            if(columnName.equalsIgnoreCase("GENERATED_KEY")){
                columnName=idColumnName;
            }
            resultMap.put(columnName, rs.getObject(col));
        }
        return resultMap;
    }
    public static Map<String,Object> rowToMap(ResultSet rs) throws SQLException{
        return  rowToMap(rs,false,false);
    }
    public static Map<String,Object> rowToMap(ResultSet rs,boolean lower,boolean orm) throws SQLException {
        return rowToMap(rs,null);
    }

}
