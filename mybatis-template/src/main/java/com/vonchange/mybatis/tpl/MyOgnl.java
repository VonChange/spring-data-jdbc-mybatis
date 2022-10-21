package com.vonchange.mybatis.tpl;

import com.vonchange.mybatis.common.util.StringUtils;

import java.util.Collection;
import java.util.Map;

public class MyOgnl {

    public static boolean isEmpty(Object value){
        if(null==value){
            return true;
        }
        if(value instanceof String){
            if(StringUtils.isBlank((String)value)){
                return true;
            }
            return false;
        }
        if(value  instanceof Collection){
            if(((Collection)value).isEmpty()){
                return true;
            }
            return false;
        }
        if(value.getClass().isArray()){
            if(((Object[])value).length == 0){
                return true;
            }
            return false;
        }
        if(value  instanceof Map){
            if(((Map)value).isEmpty()){
                return true;
            }
            return false;
        }
        return false;
    }
    public static boolean isNotEmpty(Object value){
        return !isEmpty(value);
    }

}
