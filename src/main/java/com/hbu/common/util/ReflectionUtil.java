package com.hbu.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenwei
 * @date 2018/6/20
 */
public class ReflectionUtil {
	/**
	 * 用于生成类字段的映射，其中键是字段值，值是字段名。
	 */
	public static Map<Integer, String> generateMapOfValueNameInteger(Class<?> clazz) {
		Map<Integer, String> valuesName = new HashMap<>();
		try {
			for (Field field : clazz.getFields()) {
				valuesName.put((Integer) field.get(int.class), field.getName());
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return valuesName;
	}

	public static Map<Short, String> generateMapOfValueNameShort(Class<?> clazz) {
		Map<Short, String> valuesName = new HashMap<>();
		try {
			for (Field field : clazz.getFields()) {
				if (field.getType().isPrimitive()) {
					valuesName.put((Short) field.get(short.class), field.getName());
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return valuesName;
	}

	public static Object castTo(Class type, String value) {
		if (type == byte.class || type == Byte.class) {
			return Byte.valueOf(value);
		}
		if (type == short.class || type == Short.class) {
			return Short.valueOf(value);
		}
		if (type == int.class || type == Integer.class) {
			return Integer.valueOf(value);
		}
		if (type == long.class || type == Long.class) {
			return Long.valueOf(value);
		}
		if (type == boolean.class || type == Boolean.class) {
			return Boolean.valueOf(value);
		}
		return value;
	}
}
