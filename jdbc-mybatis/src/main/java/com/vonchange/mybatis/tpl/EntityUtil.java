package com.vonchange.mybatis.tpl;


import com.vonchange.common.util.ClazzUtils;
import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.bean.BeanUtil;
import com.vonchange.common.util.bean.MethodAccessData;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import com.vonchange.mybatis.tpl.annotation.ColumnNot;
import com.vonchange.mybatis.tpl.annotation.InsertOnlyProperty;
import com.vonchange.mybatis.tpl.annotation.InsertReturn;
import com.vonchange.mybatis.tpl.model.EntityField;
import com.vonchange.mybatis.tpl.model.EntityInfo;
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
        Column column;
        List<String> columnReturns = new ArrayList<>();
        MethodAccessData methodAccessData = BeanUtil.methodAccessData(clazz);
        String writeMethod;
        int i=0;
        for (Field field : fieldList) {
            Class<?> type = field.getType();
            writeMethod="set"+ UtilAll.UString.capitalize(field.getName());
            boolean isColumnField =methodAccessData.getMethodIndexMap()
                    .containsKey(writeMethod)&&ClazzUtils.isBaseType(type);
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
            entityField.setIsColumn(true);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Id||annotation instanceof org.springframework.data.annotation.Id) {
                    entityField.setIsId(true);
                    entity.setIdFieldName(fieldName);
                    entity.setIdColumnName(columnName);
                    entity.setIdType(type.getSimpleName());
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
                if (annotation instanceof Transient ||annotation instanceof ColumnNot) {
                    entityField.setIsColumn(false);

                }
            }
            entityFieldList.add(entityField);
            fieldMap.put(fieldName, i);
            i++;
        }
        entity.setColumnReturns(columnReturns);
        entity.setEntityFields(entityFieldList);
        entity.setFieldMap(fieldMap);
        entityMap.put(clazz.getName(), entity);
    }

    private static void getFieldList(Class<?> clazz, List<Field> fieldList) {
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if(null!=clazz.getSuperclass()){
            getFieldList(clazz.getSuperclass(), fieldList);
        }

    }
}
