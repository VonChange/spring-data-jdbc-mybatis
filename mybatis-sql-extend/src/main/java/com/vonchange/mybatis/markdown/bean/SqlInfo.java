package com.vonchange.mybatis.markdown.bean;


import com.vonchange.mybatis.markdown.MarkdownDTO;

/**
 * Created by 冯昌义 on 2018/1/22.
 */
public class SqlInfo {
    private String sqlId;
    private String sql;
    private  String innnerId;
    private MarkdownDTO markdownDTO;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getInnnerId() {
        return innnerId;
    }

    public void setInnnerId(String innnerId) {
        this.innnerId = innnerId;
    }

    public MarkdownDTO getMarkdownDTO() {
        return markdownDTO;
    }

    public void setMarkdownDTO(MarkdownDTO markdownDTO) {
        this.markdownDTO = markdownDTO;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }
}
