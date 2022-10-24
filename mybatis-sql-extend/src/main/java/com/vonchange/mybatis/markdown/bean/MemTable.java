package com.vonchange.mybatis.markdown.bean;

import java.util.Map;

/**
 * @author 冯昌义
 *  2017/12/5.
 */
public class MemTable {
    private Map<String,Object> info;
    private String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }
}
