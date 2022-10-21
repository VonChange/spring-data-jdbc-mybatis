package com.vonchange.mybatis.common.util.bean.convert.impl;

import com.vonchange.mybatis.common.util.StringUtils;
import com.vonchange.mybatis.common.util.bean.convert.TypeConversionException;
import com.vonchange.mybatis.common.util.bean.convert.TypeConvertCommon;
import com.vonchange.mybatis.common.util.bean.convert.TypeConverter;
import com.vonchange.mybatis.common.util.time.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class DateConverter extends TypeConvertCommon<Date> implements TypeConverter<Date> {

	@Override
	public Date convert(final Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Date) {
			return (Date) value;
		}
		if (value instanceof Calendar) {
			return new Date(((Calendar)value).getTimeInMillis());
		}
		if (value instanceof LocalDateTime) {
			return TimeUtil.toDate((LocalDateTime)value);
		}
		if (value instanceof LocalDate) {
			return TimeUtil.toDate((LocalDate)value);
		}
		if (value instanceof Number) {
			return new Date(((Number) value).longValue());
		}

		final String stringValue = value.toString().trim();

		if (!StringUtils.containsOnlyDigits(stringValue)) {
			// try to parse default string format
			return TimeUtil.toDate(LocalDateTime.parse(stringValue));
		}

		try {
			long milliseconds = Long.parseLong(stringValue);
			return new Date(milliseconds);
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}