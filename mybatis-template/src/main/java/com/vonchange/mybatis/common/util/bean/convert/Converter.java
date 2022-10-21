// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package com.vonchange.mybatis.common.util.bean.convert;


import com.vonchange.mybatis.common.util.bean.convert.impl.BigDecimalConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.BigIntegerConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.BooleanConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.ByteArrayConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.ByteConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.CharacterConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.DateConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.DoubleConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.FloatConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.IntegerConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.LocalDateConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.LocalDateTimeConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.LocalTimeConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.LongConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.ShortConverter;
import com.vonchange.mybatis.common.util.bean.convert.impl.StringConverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Converter {

	private static final Converter CONVERTER = new Converter();
	private static  Map<Class<?>,TypeConverter> convertMap= new HashMap<>();
	static{
		convertMap.put(Boolean.class,new BooleanConverter());
		convertMap.put(BigDecimal.class,new BigDecimalConverter());
		convertMap.put(BigInteger.class,new BigIntegerConverter());
		convertMap.put(Byte.class,new ByteConverter());
		convertMap.put(Character.class,new CharacterConverter());
		convertMap.put(Double.class,new DoubleConverter());
		convertMap.put(Float.class,new FloatConverter());
		convertMap.put(Integer.class,new IntegerConverter());
		convertMap.put(Long.class,new LongConverter());
		convertMap.put(Short.class,new ShortConverter());
		convertMap.put(String.class,new StringConverter());
		convertMap.put(LocalDate.class,new LocalDateConverter());
		convertMap.put(LocalDateTime.class,new LocalDateTimeConverter());
		convertMap.put(LocalTime.class,new LocalTimeConverter());
		convertMap.put(Date.class,new DateConverter());
		convertMap.put(byte[].class,new ByteArrayConverter());
	}

	/**
	 * Returns default instance.
	 */
	public static Converter get() {
		return CONVERTER;
	}

	public static boolean hasConvertKey(Class<?> type){
		return convertMap.containsKey(type);
	}
	@SuppressWarnings("unchecked")
    public static <T> TypeConverter<T> getConvert(Class<?> type) {
       /* for (Map.Entry<Class<?>,TypeConverter> entry:convertMap.entrySet()) {
            if(entry.getKey().isAssignableFrom(type)){
                return entry.getValue();
            }
        }*/
        return convertMap.get(type);
    }
	// ---------------------------------------------------------------- boolean

	/**
	 * Converts value to <code>Boolean</code>.
	 */
	public Boolean toBoolean(final Object value) {
		return (Boolean) getConvert(Boolean.class).convert(value);
	}



	/**
	 * Converts value to <code>Boolean</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Boolean toBoolean(final Object value, final Boolean defaultValue) {
		final Boolean result = toBoolean(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>boolean</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public boolean toBooleanValue(final Object value, final boolean defaultValue) {
		final Boolean result = toBoolean(value);
		if (result == null) {
			return defaultValue;
		}
		return result.booleanValue();
	}

	/**
	 * Converts value to <code>boolean</code> with common default value.
	 */
	public boolean toBooleanValue(final Object value) {
		return toBooleanValue(value, false);
	}

	// ---------------------------------------------------------------- integer

	/**
	 * Converts value to <code>Integer</code>.
	 */
	public Integer toInteger(final Object value) {
		return (Integer) getConvert(Integer.class).convert(value);
	}

	/**
	 * Converts value to <code>Integer</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Integer toInteger(final Object value, final Integer defaultValue) {
		final Integer result = toInteger(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>int</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public int toIntValue(final Object value, final int defaultValue) {
		final Integer result = toInteger(value);
		if (result == null) {
			return defaultValue;
		}
		return result.intValue();
	}

	/**
	 * Converts value to <code>int</code> with common default value.
	 */
	public int toIntValue(final Object value) {
		return toIntValue(value, 0);
	}

	// ---------------------------------------------------------------- long

	/**
	 * Converts value to <code>Long</code>.
	 */
	public Long toLong(final Object value) {
		return (Long) getConvert(Long.class).convert(value);
	}

	/**
	 * Converts value to <code>Long</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Long toLong(final Object value, final Long defaultValue) {
		final Long result = toLong(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>long</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public long toLongValue(final Object value, final long defaultValue) {
		final Long result = toLong(value);
		if (result == null) {
			return defaultValue;
		}
		return result.longValue();
	}

	/**
	 * Converts value to <code>long</code> with common default value.
	 */
	public long toLongValue(final Object value) {
		return toLongValue(value, 0);
	}

	// ---------------------------------------------------------------- float

	/**
	 * Converts value to <code>Float</code>.
	 */
	public Float toFloat(final Object value) {
		return (Float) getConvert(Float.class).convert(value);
	}

	/**
	 * Converts value to <code>Float</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Float toFloat(final Object value, final Float defaultValue) {
		final Float result = toFloat(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>float</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public float toFloatValue(final Object value, final float defaultValue) {
		final Float result = toFloat(value);
		if (result == null) {
			return defaultValue;
		}
		return result.floatValue();
	}

	/**
	 * Converts value to <code>float</code> with common default value.
	 */
	public float toFloatValue(final Object value) {
		return toFloatValue(value, 0);
	}

	// ---------------------------------------------------------------- double

	/**
	 * Converts value to <code>Double</code>.
	 */
	public Double toDouble(final Object value) {
		return (Double) getConvert(Double.class).convert(value);
	}

	/**
	 * Converts value to <code>Double</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Double toDouble(final Object value, final Double defaultValue) {
		final Double result = toDouble(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>double</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public double toDoubleValue(final Object value, final double defaultValue) {
		final Double result = toDouble(value);
		if (result == null) {
			return defaultValue;
		}
		return result.doubleValue();
	}

	/**
	 * Converts value to <code>double</code> with common default value.
	 */
	public double toDoubleValue(final Object value) {
		return toDoubleValue(value, 0);
	}

	// ---------------------------------------------------------------- short

	/**
	 * Converts value to <code>Short</code>.
	 */
	public Short toShort(final Object value) {
		return (Short) getConvert(Short.class).convert(value);
	}

	/**
	 * Converts value to <code>Short</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Short toShort(final Object value, final Short defaultValue) {
		final Short result = toShort(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>short</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public short toShortValue(final Object value, final short defaultValue) {
		final Short result = toShort(value);
		if (result == null) {
			return defaultValue;
		}
		return result.shortValue();
	}

	/**
	 * Converts value to <code>short</code> with common default value.
	 */
	public short toShortValue(final Object value) {
		return toShortValue(value, (short) 0);
	}

	// ---------------------------------------------------------------- character

	/**
	 * Converts value to <code>Character</code>.
	 */
	public Character toCharacter(final Object value) {
		return (Character) getConvert(Character.class).convert(value);
	}

	/**
	 * Converts value to <code>Character</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Character toCharacter(final Object value, final Character defaultValue) {
		final Character result = toCharacter(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>char</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public char toCharValue(final Object value, final char defaultValue) {
		final Character result = toCharacter(value);
		if (result == null) {
			return defaultValue;
		}
		return result.charValue();
	}

	/**
	 * Converts value to <code>char</code> with common default value.
	 */
	public char toCharValue(final Object value) {
		return toCharValue(value, (char) 0);
	}

	// ---------------------------------------------------------------- byte

	/**
	 * Converts value to <code>Byte</code>.
	 */
	public Byte toByte(final Object value) {
		return (Byte) getConvert(Byte.class).convert(value);
	}

	/**
	 * Converts value to <code>Byte</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Byte toByte(final Object value, final Byte defaultValue) {
		final Byte result = toByte(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>byte</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public byte toByteValue(final Object value, final byte defaultValue) {
		final Byte result = toByte(value);
		if (result == null) {
			return defaultValue;
		}
		return result.byteValue();
	}

	/**
	 * Converts value to <code>byte</code> with common default value.
	 */
	public byte toByteValue(final Object value) {
		return toByteValue(value, (byte) 0);
	}

	// ---------------------------------------------------------------- array



	// ---------------------------------------------------------------- string

	/**
	 * Converts value to <code>String</code>.
	 */
	public String toString(final Object value) {
		return (String) getConvert(String.class).convert(value);
	}

	/**
	 * Converts value to <code>String</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public String toString(final Object value, final String defaultValue) {
		final String result = toString(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}


	// ---------------------------------------------------------------- bigs

	/**
	 * Converts value to <code>BigInteger</code>.
	 */
	public BigInteger toBigInteger(final Object value) {
		return (BigInteger) getConvert(BigInteger.class).convert(value);
	}

	/**
	 * Converts value to <code>BigInteger</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public BigInteger toBigInteger(final Object value, final BigInteger defaultValue) {
		final BigInteger result = toBigInteger(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>BigDecimal</code>.
	 */
	public BigDecimal toBigDecimal(final Object value) {
		return (BigDecimal) getConvert(BigDecimal.class).convert(value);
	}

	/**
	 * Converts value to <code>BigDecimal</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public BigDecimal toBigDecimal(final Object value, final BigDecimal defaultValue) {
		final BigDecimal result = toBigDecimal(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>LocalDate</code>.
	 */
	public LocalDate toLocalDate(final Object value) {
		return (LocalDate) getConvert(LocalDate.class).convert(value);
	}

	/**
	 * Converts value to <code>LocalDate</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public LocalDate toLocalDate(final Object value, final LocalDate defaultValue) {
		final LocalDate result = toLocalDate(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}
	/**
	 * Converts value to <code>LocalDateTime</code>.
	 */
	public LocalDateTime toLocalDateTime(final Object value) {
		return (LocalDateTime) getConvert(LocalDateTime.class).convert(value);
	}

	/**
	 * Converts value to <code>LocalDateTime</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public LocalDateTime toLocalDateTime(final Object value, final LocalDateTime defaultValue) {
		final LocalDateTime result = toLocalDateTime(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}


	/**
	 * Converts value to <code>LocalTime</code>.
	 */
	public LocalTime toLocalTime(final Object value) {
		return (LocalTime) getConvert(LocalTime.class).convert(value);
	}

	/**
	 * Converts value to <code>LocalTime</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public LocalTime toLocalTime(final Object value, final LocalTime defaultValue) {
		final LocalTime result = toLocalTime(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>Date</code>.
	 */
	public Date toDate(final Object value) {
		return (Date) getConvert(Date.class).convert(value);
	}

	/**
	 * Converts value to <code>LocalTime</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Date toDate(final Object value, final Date defaultValue) {
		final Date result = toDate(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}
}