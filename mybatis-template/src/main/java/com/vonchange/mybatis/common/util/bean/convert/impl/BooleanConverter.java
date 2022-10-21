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

package com.vonchange.mybatis.common.util.bean.convert.impl;

import com.vonchange.mybatis.common.util.bean.convert.TypeConversionException;
import com.vonchange.mybatis.common.util.bean.convert.TypeConvertCommon;
import com.vonchange.mybatis.common.util.bean.convert.TypeConverter;

import static com.vonchange.mybatis.common.util.StringPool.FALSE;
import static com.vonchange.mybatis.common.util.StringPool.N;
import static com.vonchange.mybatis.common.util.StringPool.NO;
import static com.vonchange.mybatis.common.util.StringPool.OFF;
import static com.vonchange.mybatis.common.util.StringPool.ON;
import static com.vonchange.mybatis.common.util.StringPool.ONE;
import static com.vonchange.mybatis.common.util.StringPool.TRUE;
import static com.vonchange.mybatis.common.util.StringPool.Y;
import static com.vonchange.mybatis.common.util.StringPool.YES;
import static com.vonchange.mybatis.common.util.StringPool.ZERO;

/**
 * Converts given object to <code>Boolean</code>.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li>object is converted to string, trimmed. Then common boolean strings are matched:
 * "yes", "y", "true", "on", "1" for <code>true</code>; and opposite values
 * for <code>false</code>.</li>
 * </ul>
 */
public class BooleanConverter extends TypeConvertCommon<Boolean> implements TypeConverter<Boolean> {

	public Boolean convert(final Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Boolean.class) {
			return (Boolean) value;
		}

		String stringValue = value.toString();
		if (stringValue.isEmpty()) {
			return Boolean.FALSE;
		}

		stringValue = stringValue.trim().toLowerCase();
		if (stringValue.equals(YES) ||
				stringValue.equals(Y) ||
				stringValue.equals(TRUE) ||
				stringValue.equals(ON) ||
				stringValue.equals(ONE)) {
			return Boolean.TRUE;
		}
		if (stringValue.equals(NO) ||
				stringValue.equals(N) ||
				stringValue.equals(FALSE) ||
				stringValue.equals(OFF) ||
				stringValue.equals(ZERO)) {
			return Boolean.FALSE;
		}

		throw new TypeConversionException(value);
	}

}
