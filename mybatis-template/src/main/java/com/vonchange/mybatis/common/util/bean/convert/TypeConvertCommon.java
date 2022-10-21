package com.vonchange.mybatis.common.util.bean.convert;

public abstract   class TypeConvertCommon<T> implements TypeConverter<T>{

    public abstract T convert(Object value);

    public T convert(final Object value, final T defaultValue){
        if (value == null) {
            return defaultValue;
        }
        try {
            return convert(value);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
}

