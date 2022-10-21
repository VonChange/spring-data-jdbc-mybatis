package com.vonchange.mybatis.tpl.extra;

/**
 * 分析后的实体类
 */
public class AnalyeNamed {
    private String condition;
    //private String type;
    /**
     * 1 String  2 其他 base 3:list 4.arr
     */
    private  String namedFull;
    private String itemProperty;
    private  String link;
    //private Object value;
    private  String column;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }






    public String getNamedFull() {
        return namedFull;
    }

    public void setNamedFull(String namedFull) {
        this.namedFull = namedFull;
    }

    public String getItemProperty() {
        return itemProperty;
    }

    public void setItemProperty(String itemProperty) {
        this.itemProperty = itemProperty;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }


}
