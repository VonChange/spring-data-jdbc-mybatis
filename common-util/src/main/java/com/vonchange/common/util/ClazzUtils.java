package com.vonchange.common.util;

import com.vonchange.common.util.bean.convert.Converter;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;

public class ClazzUtils {
	private ClazzUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * isBaseType
	 */
	public static boolean isBaseType(Class<?> clazz) {
		// ||clazz== Time.class||clazz == Timestamp.class
		return Converter.hasConvertKey(clazz) || clazz.isPrimitive() ||
		Enum.class.isAssignableFrom(clazz) ||
				URI.class == clazz || URL.class == clazz ||
				Locale.class == clazz || Class.class == clazz;
	}
	public static boolean isBaseTypeWithArray(Class<?> clazz) {
		return isBaseType(clazz)||(clazz.isArray()&& isBaseType(clazz.getComponentType()))|| Collection.class.isAssignableFrom(clazz) ;
	}
	public static boolean isVersionType(Class<?> clazz) {
		return Integer.class==clazz||Long.class==clazz;
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Class<?> clazz, Object obj) {
		if (obj != null && !clazz.isInstance(obj))
			throw new ClassCastException(cannotCastMsg(clazz, obj));
		return (T) obj;
	}
	public static boolean isClassExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}


	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	private static String cannotCastMsg(Class<?> clazz, Object obj) {
		return "Cannot cast " + obj.getClass().getName() + " to " + clazz.getName();
	}

}