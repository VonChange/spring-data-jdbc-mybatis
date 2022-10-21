package com.vonchange.jdbc.abstractjdbc.model;

import java.util.List;

public class EntityUpdateResult {
    private String updateStr;
    private List<Object> valueList;

    public EntityUpdateResult( String updateStr, List<Object> valueList) {
        this.updateStr=updateStr;
        this.valueList = valueList;
    }

    public String getUpdateStr() {
        return updateStr;
    }

    public void setUpdateStr(String updateStr) {
        this.updateStr = updateStr;
    }



    public List<Object> getValueList() {
        return valueList;
    }

    public void setValueList(List<Object> valueList) {
        this.valueList = valueList;
    }


}
