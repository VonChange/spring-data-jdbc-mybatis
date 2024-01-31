package com.vonchange.jdbc.abstractjdbc.handler;


import com.vonchange.mybatis.tpl.OrmUtil;

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
    public static Map<String,Object> rowToMap(ResultSet rs,boolean lower,boolean orm,String genColumn) throws SQLException {
        Map<String,Object> resultMap = new LinkedHashMap<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(col);
            }
            if(lower){
                columnName=columnName.toLowerCase();
            }
            if(orm){
                columnName= OrmUtil.toFiled(columnName.toLowerCase());
            }
            if(columnName.equalsIgnoreCase("GENERATED_KEY")){
                columnName=genColumn;
            }
            resultMap.put(columnName, rs.getObject(col));
        }
        return resultMap;
    }
    public static Map<String,Object> rowToMap(ResultSet rs) throws SQLException{
        return  rowToMap(rs,false,false);
    }
    public static Map<String,Object> rowToMap(ResultSet rs,String genColumn) throws SQLException{
        return  rowToMap(rs,false,false,genColumn);
    }
    public static Map<String,Object> rowToMap(ResultSet rs,boolean lower,boolean orm) throws SQLException {
        return rowToMap(rs,lower,orm,null);
    }

}
