package com.vonchange.common.util;

import com.vonchange.common.util.bean.convert.Converter;

public class ClazzUtils {
	private ClazzUtils() {
		throw new IllegalStateException("Utility class");
	}
	/**
	 * isBaseType
	 */
	public static boolean isBaseType(Class<?> clazz) {
		//||clazz== Time.class||clazz == Timestamp.class
		return Converter.hasConvertKey(clazz)|| clazz.isPrimitive()||clazz.isEnum();
	}


}