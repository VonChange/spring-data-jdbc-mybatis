package com.vonchange.jdbc.util;

import com.vonchange.common.util.Assert;
import com.vonchange.common.util.ClazzUtils;
import com.vonchange.common.util.ConvertUtil;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.jdbc.config.EnumMappedClass;
import com.vonchange.jdbc.core.CrudUtil;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.jdbc.model.EntityInfo;
import org.springframework.jdbc.support.JdbcUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
        for (Map.Entry<String, Object> entry : orgMap.entrySet()) {
            BeanUtil.setProperty(entity,entry.getKey(),entry.getValue());
        }
        return entity;
    }
    public static EnumMappedClass enumMappedClass(Class<?> mappedClass){
        if(ClazzUtils.isBaseType(mappedClass)){
            return EnumMappedClass.base;
        }
        if(mappedClass == Map.class){
            return EnumMappedClass.map;
        }
        return EnumMappedClass.bean;
    }
    public static <T> T toMappedClass(ResultSet rs,  Class<?> mappedClass,EnumMappedClass enumMappedClass,EntityInfo entityInfo) throws SQLException {
        if(null==enumMappedClass){
            enumMappedClass=enumMappedClass(mappedClass);
        }
        if(enumMappedClass.equals(EnumMappedClass.base)){
            return ConvertUtil.toObject(JdbcUtils.getResultSetValue(rs, 1),mappedClass);
        }
        if(enumMappedClass.equals(EnumMappedClass.map)){
            return (T) CrudUtil.rowToMap(null,rs,null);
        }
        if(null==entityInfo){
            entityInfo = EntityUtil.getEntityInfo(mappedClass);
        }
        return ConvertMap.toBean(CrudUtil.rowToMap(entityInfo,rs,null),mappedClass);
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
