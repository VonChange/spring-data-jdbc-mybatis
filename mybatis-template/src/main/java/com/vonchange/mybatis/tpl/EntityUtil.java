package com.vonchange.mybatis.tpl;


import com.vonchange.common.util.ClazzUtils;
import com.vonchange.common.util.UtilAll;
import com.vonchange.mybatis.tpl.annotation.ColumnNot;
import com.vonchange.mybatis.tpl.annotation.InsertReturn;
import com.vonchange.mybatis.tpl.annotation.UpdateNotNull;
import com.vonchange.mybatis.tpl.model.EntityField;
import com.vonchange.mybatis.tpl.model.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created by 冯昌义 on 2018/4/19.
 */
public class EntityUtil {
    private static final Map<String, EntityInfo> entityMap = new ConcurrentHashMap<>();
    private static Logger logger = LoggerFactory.getLogger(EntityUtil.class);
    public static void initEntityInfo(Class<?> clazz) {
        String entityName = clazz.getName();
        if(!entityMap.containsKey(entityName)){
            initEntity(clazz);
        }
    }

    public static  EntityInfo getEntityInfo(Class<?> clazz){
        String entityName = clazz.getName();
        if(!entityMap.containsKey(entityName)){
            initEntity(clazz);
        }
        return entityMap.get(entityName);
    }

    private static synchronized   void initEntity(Class<?> clazz) {
        logger.debug("初始化 {}", clazz.getName());
        if(ClazzUtils.isBaseType(clazz)){
            return;
        }
        EntityInfo entity = new EntityInfo();
        Table table=clazz.getAnnotation(Table.class);
        String tableName=null;
        if(null!=table){
            tableName=table.name();
        }
        if(UtilAll.UString.isBlank(tableName)){
            String tableEntity= clazz.getSimpleName();
            if(clazz.getSimpleName().toLowerCase().endsWith("do")){
                tableEntity=clazz.getSimpleName().substring(0,clazz.getSimpleName().length()-2);
            }
            tableName= OrmUtil.toSql(tableEntity);
        }
        entity.setTableName(tableName);
        List<Field> fieldList = new ArrayList<>();
        getFieldList(clazz,fieldList);
                //clazz.getDeclaredFields();// 只有本类
        List<EntityField> entityFieldList = new ArrayList<>();
        Map<String,Boolean> fieldMap = new HashMap<>();
        Column column;
        List<String> columnReturns = new ArrayList<>();
        for (Field field : fieldList) {
            Class<?> type = field.getType();
            Boolean isBaseType = ClazzUtils.isBaseType(type);
            String fieldName = field.getName();
            if(Boolean.FALSE.equals(isBaseType)||fieldMap.containsKey(fieldName)){
                continue;
            }
            EntityField entityField = new EntityField();
            PropertyDescriptor descriptor = null;
            try {
                descriptor = new PropertyDescriptor(field.getName(),clazz);
            } catch (IntrospectionException e) {
                logger.error("", e);
                continue;
            }
            entityField.setWriteMethod(descriptor.getWriteMethod());
            entityField.setFieldName(fieldName);
            column=field.getAnnotation(Column.class);
            String columnName =null;
            if(null!=column){
                columnName=column.name();
            }
            if(UtilAll.UString.isBlank(columnName)){
                columnName = OrmUtil.toSql(fieldName);
            }
            entityField.setColumnName(columnName);
            entityField.setType(type);
            entityField.setIsBaseType(isBaseType);
            entityField.setIsColumn(true);
            entityField.setUpdateNotNull(false);

            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof ColumnNot) {
                    entityField.setIsColumn(false);
                    continue;
                }
                if (annotation instanceof Id) {
                    entityField.setIsId(true);
                    entity.setIdFieldName(fieldName);
                    entity.setIdColumnName(columnName);
                    entity.setIdType(type.getSimpleName());
                    if(null==entity.getGenColumn()){
                        entity.setGenColumn(columnName);
                    }
                }
                if(annotation instanceof InsertReturn){
                    columnReturns.add(columnName);
                }
                if (annotation instanceof UpdateNotNull) {
                    entityField.setUpdateNotNull(true);
                }
            }
            entityFieldList.add(entityField);
            fieldMap.put(fieldName, true);
        }
        entity.setColumnReturns(columnReturns);
        entity.setEntityFields(entityFieldList);
        entityMap.put(clazz.getName(), entity);
    }

    private static void getFieldList(Class<?> clazz, List<Field> fieldList) {
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if(null!=clazz.getSuperclass()){
            getFieldList(clazz.getSuperclass(), fieldList);
        }

    }
}
