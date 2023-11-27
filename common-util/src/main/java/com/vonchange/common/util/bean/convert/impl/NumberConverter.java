package com.vonchange.common.util.bean.convert.impl;

import com.vonchange.common.util.StringUtils;
import com.vonchange.common.util.bean.convert.TypeConversionException;
import com.vonchange.common.util.bean.convert.TypeConvertCommon;
import com.vonchange.common.util.bean.convert.TypeConverter;

public class NumberConverter extends TypeConvertCommon<Number> implements TypeConverter<Number> {
    @Override
    public Number convert(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return (Number) value;
        }

        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue() ? 1 :0;
        }

        try {
            String stringValue = value.toString().trim();
            if (StringUtils.startsWithChar(stringValue, '+')) {
                stringValue = stringValue.substring(1);
            }
            if(stringValue.contains(".")){
                return Double.valueOf(stringValue);
            }
            return Long.valueOf(stringValue);
        } catch (NumberFormatException nfex) {
            throw new TypeConversionException(value, nfex);
        }
    }
}