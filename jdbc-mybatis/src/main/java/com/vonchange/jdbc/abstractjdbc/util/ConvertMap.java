package com.vonchange.jdbc.abstractjdbc.util;

import com.vonchange.common.util.ClazzUtils;
import com.vonchange.common.util.ConvertUtil;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.tpl.EntityUtil;
import com.vonchange.mybatis.tpl.OrmUtil;
import com.vonchange.mybatis.tpl.model.EntityField;
import com.vonchange.mybatis.tpl.model.EntityInfo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConvertMap {
    private ConvertMap() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, Object> toMap(T entity, Class<?> clazz) throws IntrospectionException {
        if (entity instanceof Map) {
            return (Map<String, Object>) entity;
        }
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        String propertyName;
        Object value;
        Map<String, Object> map = new HashMap<>();
        for (PropertyDescriptor property : propertyDescriptors) {
            if (!ClazzUtils.isBaseType(property.getPropertyType())) {
                continue;
            }
            propertyName = property.getName();
            value = BeanUtil.getProperty(entity, propertyName);
            // property.getValue(propertyName);
            map.put(propertyName, value);
        }
        return map;
    }

    /**
     * Map to JavaBean
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertMap(T entity, Class<?> type, Map<String, Object> map)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        if (null != entity) {
            type = entity.getClass();
        }
        if (null == entity) {
            try {
                entity = (T) type.newInstance();
            } catch (InstantiationException e) {
                throw new JdbcMybatisRuntimeException(
                        "java.lang.InstantiationException " + type.getName() + " need no-arguments constructor");
            }
        }
        EntityInfo entityInfo = EntityUtil.getEntityInfo(type);
        if (null != entityInfo) {
            List<EntityField> entityFieldList = entityInfo.getEntityFields();
            for (EntityField entityField : entityFieldList) {
                String columnNameLower = entityField.getColumnName().toLowerCase();
                String fieldNameLower = entityField.getFieldName().toLowerCase();
                Object value = null;
                if (map.containsKey(fieldNameLower)) {
                    value = map.get(fieldNameLower);
                }
                if (map.containsKey(columnNameLower)) {
                    value = map.get(columnNameLower);
                }
                if (null != value) {
                    value = ConvertUtil.toObject(value, entityField.getType());
                    BeanUtil.setProperty(entity,entityField.getFieldName(),value);
                }
            }
            return entity;
        }
        return entity;
    }

    public static <T> T convertMap(Class<?> type, Map<String, Object> map)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        return convertMap(null, type, map);
    }

    public static Map<String, Object> newMap(Map<String, Object> map) {
        if (null == map || map.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> newMap = new LinkedHashMap<>();
        String key;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            key = entry.getKey();
            newMap.put(key.toLowerCase(), entry.getValue());
            key = OrmUtil.toFiled(entry.getKey());
            newMap.put(key.toLowerCase(), entry.getValue());
        }
        return newMap;
    }

}
