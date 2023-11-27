package com.vonchange.common.util.bean.convert.impl;

import com.vonchange.common.util.DateUtil;
import com.vonchange.common.util.StringUtils;
import com.vonchange.common.util.bean.convert.TypeConversionException;
import com.vonchange.common.util.bean.convert.TypeConvertCommon;
import com.vonchange.common.util.exception.ParseException;
import com.vonchange.common.util.time.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class DateConverter extends TypeConvertCommon<Date> {

	@Override
	public Date convert(final Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Date) {
			return (Date) value;
		}
		if (value instanceof Calendar) {
			return new Date(((Calendar) value).getTimeInMillis());
		}
		if (value instanceof LocalDateTime) {
			return TimeUtil.toDate((LocalDateTime) value);
		}
		if (value instanceof LocalDate) {
			return TimeUtil.toDate((LocalDate) value);
		}
		if (value instanceof Number) {
			return new Date(((Number) value).longValue());
		}
		final String stringValue = value.toString().trim();
		if(StringUtils.containsOnlyDigits(stringValue)){
			try {
				long milliseconds = Long.parseLong(stringValue);
				return new Date(milliseconds);
			} catch (NumberFormatException nfex) {
				throw new TypeConversionException(value, nfex);
			}
		}
	    Date result;
		try {
			result=	DateUtil.parseDate(stringValue,
					"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd","yyyy-MM-ddTHH:mm:ssZ");
		} catch (ParseException e) {
			throw new TypeConversionException(value, e);
		}
		return result;
	}

}