package com.vonchange.jdbc.util;


import com.vonchange.common.util.ClazzUtils;
import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.common.util.bean.MethodAccessData;
import com.vonchange.jdbc.annotation.ColumnNot;
import com.vonchange.jdbc.annotation.InsertOnlyProperty;
import com.vonchange.jdbc.annotation.InsertReturn;
import com.vonchange.jdbc.model.BaseEntityField;
import com.vonchange.jdbc.model.EntityField;
import com.vonchange.jdbc.model.EntityInfo;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
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
    private static final Map<String, List<BaseEntityField>> entitySimpleMap = new ConcurrentHashMap<>();
    private static Logger logger = LoggerFactory.getLogger(EntityUtil.class);
   /* public static void initEntityInfo(Class<?> clazz) {
        String entityName = clazz.getName();
        if(!entityMap.containsKey(entityName)){
            initEntity(clazz);
        }
    }*/

    public static  EntityInfo getEntityInfo(Class<?> clazz){
        String entityName = clazz.getName();
        if(!entityMap.containsKey(entityName)){
            initEntity(clazz);
        }
        return entityMap.get(entityName);
    }
    public static  List<BaseEntityField> getEntitySimple(Class<?> clazz){
        String entityName = clazz.getName();
        if(!entitySimpleMap.containsKey(entityName)){
            initSimpleEntity(clazz);
        }
        return entitySimpleMap.get(entityName);
    }
    private static synchronized   void initSimpleEntity(Class<?> clazz) {
        if(ClazzUtils.isBaseType(clazz)){
            return;
        }
        String entityName=clazz.getName();
        logger.debug("initSimpleEntity {}",entityName);
        List<Field> fieldList = new ArrayList<>();
        getFieldList(clazz,fieldList);
        List<BaseEntityField> entityFieldList = new ArrayList<>();
        Map<String,Integer> fieldMap = new HashMap<>();
        Column column;
        MethodAccessData methodAccessData = BeanUtil.methodAccessData(clazz);
        int i=0;
        for (Field field : fieldList) {
            Class<?> type = field.getType();
            boolean isColumnField =BeanUtil.containsProperty(methodAccessData,field.getName(),"set")
                    &&ClazzUtils.isBaseTypeWithArray(type);
            String fieldName = field.getName();
            if(!isColumnField||fieldMap.containsKey(fieldName)){
                continue;
            }
            BaseEntityField entityField = new BaseEntityField();
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
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Transient ||annotation instanceof javax.persistence.Transient ||annotation instanceof ColumnNot) {
                    entityField.setIfColumn(false);
                }
            }
            entityFieldList.add(entityField);
            fieldMap.put(fieldName, i);
            i++;
        }
        entitySimpleMap.put(clazz.getName(), entityFieldList);
    }
    private static synchronized   void initEntity(Class<?> clazz) {
        if(ClazzUtils.isBaseType(clazz)){
            return;
        }
        String entityName=clazz.getName();
        logger.debug("init {}",entityName);
        EntityInfo entity = new EntityInfo();
        Table table=clazz.getAnnotation(Table.class);
        String tableName=null;
        if(null!=table){
            tableName=table.name();
        }
        if(UtilAll.UString.isBlank(tableName)){
            String tableEntity= clazz.getSimpleName();
            tableName= OrmUtil.toSql(tableEntity);
        }
        entity.setTableName(tableName);
        List<Field> fieldList = new ArrayList<>();
        getFieldList(clazz,fieldList);
        List<EntityField> entityFieldList = new ArrayList<>();
        Map<String,Integer> fieldMap = new HashMap<>();
        Map<String,Integer> columnMap = new HashMap<>();
        Column column;
        List<String> columnReturns = new ArrayList<>();
        MethodAccessData methodAccessData = BeanUtil.methodAccessData(clazz);
        int i=0;
        for (Field field : fieldList) {
            Class<?> type = field.getType();
            boolean isColumnField =BeanUtil.containsProperty(methodAccessData,field.getName(),"set")
                    &&ClazzUtils.isBaseType(type);
            String fieldName = field.getName();
            if(!isColumnField||fieldMap.containsKey(fieldName)){
                continue;
            }
            EntityField entityField = new EntityField();
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
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Id||annotation instanceof org.springframework.data.annotation.Id) {
                    entityField.setIfId(true);
                    entity.setIdFieldName(fieldName);
                    entity.setIdColumnName(columnName);
                    entity.setIdType(type.getSimpleName());
                    columnReturns.add(columnName);
                    continue;
                }
                if (annotation instanceof InsertOnlyProperty) {
                    entityField.setUpdateNot(true);
                    continue;
                }
                if(annotation instanceof Version||annotation instanceof javax.persistence.Version){
                    boolean flag=  ClazzUtils.isVersionType(type);
                    if(!flag){
                        throw new JdbcMybatisRuntimeException("@Version only support Long or Integer");
                    }
                    entityField.setVersion(true);
                    continue;
                }
                if (annotation instanceof ReadOnlyProperty) {
                    entityField.setInsertNot(true);
                    entityField.setUpdateNot(true);
                    continue;
                }
                if(annotation instanceof InsertReturn){
                    columnReturns.add(columnName);
                    continue;
                }
                if (annotation instanceof Transient ||annotation instanceof javax.persistence.Transient||annotation instanceof ColumnNot) {
                    entityField.setIfColumn(false);
                }
            }
            entityFieldList.add(entityField);
            fieldMap.put(fieldName, i);
            columnMap.put(columnName,i);
            i++;
        }
        entity.setColumnReturns(columnReturns);
        entity.setEntityFields(entityFieldList);
        entity.setFieldMap(fieldMap);
        entity.setColumnMap(columnMap);
        entityMap.put(clazz.getName(), entity);
    }

    private static void getFieldList(Class<?> clazz, List<Field> fieldList) {
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if(null!=clazz.getSuperclass()){
            getFieldList(clazz.getSuperclass(), fieldList);
        }

    }
}
