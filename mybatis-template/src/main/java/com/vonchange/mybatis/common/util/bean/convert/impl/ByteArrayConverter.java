package com.vonchange.mybatis.common.util.bean.convert.impl;

import com.vonchange.mybatis.common.util.StringUtils;
import com.vonchange.mybatis.common.util.bean.convert.Converter;
import com.vonchange.mybatis.common.util.bean.convert.TypeConversionException;
import com.vonchange.mybatis.common.util.bean.convert.TypeConvertCommon;
import com.vonchange.mybatis.common.util.bean.convert.TypeConverter;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class ByteArrayConverter extends TypeConvertCommon<byte[]> implements TypeConverter<byte[]> {
	public static final char[] NUMBER_DELIMITERS = new char[] {',', ';', '\n'};


	@Override
	public byte[] convert(final Object value) {
		if (value == null) {
			return null;
		}

		final Class valueClass = value.getClass();

		if (!valueClass.isArray()) {
			// source is not an array
			return convertValueToArray(value);
		}

		// source is an array
		return convertArrayToArray(value);
	}

	/**
	 * Converts type using type converter manager.
	 */
	protected byte convertType(final Object value) {
		return Converter.get().toByte(value);
		//return typeConverterManager.convertType(value, byte.class).byteValue();
	}

	/**
	 * Creates an array with single element.
	 */
	protected byte[] convertToSingleElementArray(final Object value) {
		return new byte[] {convertType(value)};
	}

	/**
	 * Converts non-array value to array. Detects various
	 * types and collections, iterates them to make conversion
	 * and to create target array.
 	 */
	protected byte[] convertValueToArray(final Object value) {
		if (value instanceof Blob) {
			final Blob blob = (Blob) value;
			try {
				final long length = blob.length();
				if (length > Integer.MAX_VALUE) {
					throw new TypeConversionException("Blob is too big.");
				}
				return blob.getBytes(1, (int) length);
			} catch (SQLException sex) {
				throw new TypeConversionException(value, sex);
			}
		}

		if (value instanceof Collection) {
			final Collection collection = (Collection) value;
			final byte[] target = new byte[collection.size()];

			int i = 0;
			for (final Object element : collection) {
				target[i] = convertType(element);
				i++;
			}

			return target;
		}

		if (value instanceof Iterable) {
			final Iterable iterable = (Iterable) value;

			final ArrayList<Byte> byteArrayList = new ArrayList<>();

			for (final Object element : iterable) {
				final byte convertedValue = convertType(element);
				byteArrayList.add(Byte.valueOf(convertedValue));
			}

			final byte[] array = new byte[byteArrayList.size()];

			for (int i = 0; i < byteArrayList.size(); i++) {
				final Byte b = byteArrayList.get(i);
				array[i] = b.byteValue();
			}

			return array;
		}

		if (value instanceof CharSequence) {
			final String[] strings = StringUtils.splitc(value.toString(), NUMBER_DELIMITERS);
			return convertArrayToArray(strings);
		}

		// everything else:
		return convertToSingleElementArray(value);
	}

	/**
	 * Converts array value to array.
	 */
	protected byte[] convertArrayToArray(final Object value) {
		final Class valueComponentType = value.getClass().getComponentType();

		final byte[] result;

		if (valueComponentType.isPrimitive()) {
			result = convertPrimitiveArrayToArray(value, valueComponentType);
		} else {
			// convert object array to target array
			final Object[] array = (Object[]) value;
			result = new byte[array.length];

			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}

		return result;
	}


	/**
	 * Converts primitive array to target array.
	 */
	protected byte[] convertPrimitiveArrayToArray(final Object value, final Class primitiveComponentType) {
		byte[] result = null;

		if (primitiveComponentType == byte.class) {
			return (byte[]) value;
		}

		if (primitiveComponentType == int.class) {
			final int[] array = (int[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == long.class) {
			final long[] array = (long[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == float.class) {
			final float[] array = (float[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == double.class) {
			final double[] array = (double[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == short.class) {
			final short[] array = (short[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == char.class) {
			final char[] array = (char[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == boolean.class) {
			final boolean[] array = (boolean[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) (array[i] ? 1 : 0);
			}
		}
		return result;
	}

}