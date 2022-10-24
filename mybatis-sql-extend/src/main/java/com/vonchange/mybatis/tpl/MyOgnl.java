package com.vonchange.mybatis.tpl;



import java.util.Collection;
import java.util.Map;

public class MyOgnl {
    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
    public static boolean isEmpty(Object value){
        if(null==value){
            return true;
        }
        if(value instanceof String){
            if(isBlank((String)value)){
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
