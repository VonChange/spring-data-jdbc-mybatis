package com.vonchange.mybatis.common.util.bean;

import com.vonchange.mybatis.common.util.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class BeanUtil {
    private static final Logger log = LoggerFactory.getLogger(BeanUtil.class);
    public Object getPropertyT(Object entity,String property) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(property, entity.getClass());
        return propertyDescriptor.getReadMethod().invoke(entity);
    }
    public void setPropertyT(Object entity,String property,Object value) throws InvocationTargetException, IllegalAccessException, IntrospectionException {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(property, entity.getClass());
        value= ConvertUtil.toObject(value,propertyDescriptor.getPropertyType());
        propertyDescriptor.getWriteMethod().invoke(entity,value);
    }
    public Object getProperty(Object entity,String property) {
        try {
            return getPropertyT(entity, property);
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            log.error("",e);
            return null;
        }
    }
    public void setProperty(Object entity,String property,Object value){
        try {
            setPropertyT(entity, property, value);
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            log.error("",e);
        }
    }

}

