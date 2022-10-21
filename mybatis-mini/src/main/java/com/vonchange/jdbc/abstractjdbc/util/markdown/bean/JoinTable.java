package com.vonchange.jdbc.abstractjdbc.util.markdown.bean;

/**
 * @author 冯昌义
 *  2017/12/5.
 */
public class JoinTable {
    private String sql;
    private String tables;
    private String views;
    private String js;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }
}
