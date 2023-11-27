package com.vonchange.jdbc.abstractjdbc.core;

import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;

import java.util.List;

public interface JdbcCudRepository {
    <T> int  insert(T entity);
    <T> int  update(T entity);

    <T> int  updateAllField(T entity);
    <T> int  insertDuplicateKey(T entity);

    <T> int  insertBatch(List<T> entityList, int size);
    <T> int  updateBatch(List<T> entityList,int size);
    <T> int insertBatchDuplicateKey(List<T> entityList,int size);

    <T> int batchUpdate(String sqlId, List<T> entityList,int size);

    <T> int  insert(DataSourceWrapper dataSourceWrapper, T entity);
    <T> int  update(DataSourceWrapper dataSourceWrapper,T entity);
    <T> int  updateAllField(DataSourceWrapper dataSourceWrapper,T entity);
    <T> int  insertDuplicateKey(DataSourceWrapper dataSourceWrapper,T entity);

    <T> int  insertBatch(DataSourceWrapper dataSourceWrapper,List<T> entityList,int size);
    <T> int  updateBatch(DataSourceWrapper dataSourceWrapper,List<T> entityList,int size);
    <T> int batchUpdate(DataSourceWrapper dataSourceWrapper, String sqlId, List<T> entityList,int size);
    <T> int insertBatchDuplicateKey(DataSourceWrapper dataSourceWrapper, List<T> entityList,int size);
}
