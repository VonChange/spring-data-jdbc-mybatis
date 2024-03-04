package com.vonchange.jdbc.model;

public enum EnumCondition {
    Eq("="),Order("order");
    EnumCondition(String condition){
        this.condition=condition;
    }
    private String condition;

    public String getCondition() {
        return condition;
    }
}
