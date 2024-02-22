package com.vonchange.jdbc.util;

import com.vonchange.common.util.Assert;
import com.vonchange.common.util.ClazzUtils;
import com.vonchange.common.util.ConvertUtil;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.tpl.EntityUtil;
import com.vonchange.mybatis.tpl.model.EntityField;
import com.vonchange.mybatis.tpl.model.EntityInfo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertMap {
    private ConvertMap() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, Object> toMap(T entity)  {
        if (entity instanceof Map) {
            return (Map<String, Object>) entity;
        }
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(entity.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
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
    public static <T> T toBean(Map<String, Object> orgMap,T entity) {
        Assert.notNull(entity,"entity can not null");
        Class<?> type = entity.getClass();
        Map<String, Object> newMap=new HashMap<>();
        for (Map.Entry<String, Object> entry : orgMap.entrySet()) {
            newMap.put(entry.getKey().toUpperCase(),entry.getValue());
        }
        EntityInfo entityInfo = EntityUtil.getEntityInfo(type);
        if (null != entityInfo) {
            List<EntityField> entityFieldList = entityInfo.getEntityFields();
            for (EntityField entityField : entityFieldList) {
                String columnNameUpper = entityField.getColumnName().toUpperCase();
                String fieldNameUpper = entityField.getFieldName().toUpperCase();
                Object value = null;
                if (newMap.containsKey(columnNameUpper)) {
                    value = newMap.get(columnNameUpper);
                }
                if (newMap.containsKey(fieldNameUpper)) {
                    value = newMap.get(fieldNameUpper);
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

    public static <T> T toBean(Map<String, Object> map,Class<?> type){
        Assert.notNull(type,"need class type");
        T   entity =null;
        try {
           entity = (T) type.newInstance();
        } catch (InstantiationException e) {
            throw new JdbcMybatisRuntimeException(
                    "java.lang.InstantiationException {} need no-arguments constructor",type.getName());
        } catch (IllegalAccessException e) {
            throw new JdbcMybatisRuntimeException(
                    "java.lang.IllegalAccessException {} need no-arguments constructor",type.getName());
        }
        return toBean(map,entity);
    }

}
