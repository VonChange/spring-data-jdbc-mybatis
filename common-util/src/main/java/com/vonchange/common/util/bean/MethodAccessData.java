package com.vonchange.common.util.bean;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.util.HashMap;
import java.util.Map;

public class MethodAccessData {
    private MethodAccess methodAccess;
    private Map<String,Integer> methodIndexMap;
    Map<Integer,Class>   paramTypeMap=new HashMap<>();
    public MethodAccessData(){

    }

    public MethodAccessData(MethodAccess methodAccess, Map<String, Integer> methodIndexMap, Map<Integer, Class> paramTypeMap) {
        this.methodAccess = methodAccess;
        this.methodIndexMap = methodIndexMap;
        this.paramTypeMap = paramTypeMap;
    }

    public MethodAccess getMethodAccess() {
        return methodAccess;
    }

    public Map<Integer, Class> getParamTypeMap() {
        return paramTypeMap;
    }

    public void setParamTypeMap(Map<Integer, Class> paramTypeMap) {
        this.paramTypeMap = paramTypeMap;
    }

    public void setMethodAccess(MethodAccess methodAccess) {
        this.methodAccess = methodAccess;
    }

    public Map<String, Integer> getMethodIndexMap() {
        return methodIndexMap;
    }

    public void setMethodIndexMap(Map<String, Integer> methodIndexMap) {
        this.methodIndexMap = methodIndexMap;
    }
}
