package com.vonchange.jdbc.model;

public class SqlMatch {
    private String split;
    private EnumStep enumStep;
    public SqlMatch(){

    }
    public SqlMatch(String split, EnumStep enumStep) {
        this.split = split;
        this.enumStep = enumStep;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public EnumStep getEnumStep() {
        return enumStep;
    }

    public void setEnumStep(EnumStep enumStep) {
        this.enumStep = enumStep;
    }
}
