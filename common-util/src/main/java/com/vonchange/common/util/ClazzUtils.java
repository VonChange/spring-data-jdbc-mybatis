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
		// ||clazz== Time.class||clazz == Timestamp.class
		return Converter.hasConvertKey(clazz) || clazz.isPrimitive() || clazz.isEnum();
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Class<?> clazz, Object obj) {
		if (obj != null && !clazz.isInstance(obj))
			throw new ClassCastException(cannotCastMsg(clazz, obj));
		return (T) obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	private static String cannotCastMsg(Class<?> clazz, Object obj) {
		return "Cannot cast " + obj.getClass().getName() + " to " + clazz.getName();
	}

}