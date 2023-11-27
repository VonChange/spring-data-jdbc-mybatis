package com.vonchange.jdbc.abstractjdbc.core;

import com.vonchange.jdbc.abstractjdbc.handler.AbstractMapPageWork;
import com.vonchange.jdbc.abstractjdbc.handler.AbstractPageWork;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface JdbcRepository extends JdbcCudRepository {
    DataSourceWrapper getReadDataSource();

    <T> List<T> queryList(Class<T> type, String sqlId, Map<String, Object> parameter);

    int update(String sqlId, Map<String, Object> parameter);

    int insert(String sqlId, Map<String, Object> parameter);

    <T> T queryOne(Class<T> type, String sqlId, Map<String, Object> parameter);

    <T> T queryById(Class<T> type, Object id);

    <T> Page<T> queryPage(Class<T> type, String sqlId, Pageable pageable, Map<String, Object> parameter);

    <T> T queryOneColumn(Class<?> targetType, String sqlId, Map<String, Object> parameter);

    <T> void queryBigData(Class<T> type, String sqlId, AbstractPageWork<T> pageWork, Map<String, Object> parameter);

    // map
    void queryMapBigData(String sqlId, AbstractMapPageWork pageWork, Map<String, Object> parameter);

    Map<String, Object> queryMapOne(String sqlId, Map<String, Object> parameter);

    Page<Map<String, Object>> queryMapPage(String sqlId, Pageable pageable, Map<String, Object> parameter);

    List<Map<String, Object>> queryMapList(String sqlId, Map<String, Object> parameter);

    // 带 dataSource
    <T> List<T> queryList(DataSourceWrapper dataSourceWrapper, Class<T> type, String sqlId,
            Map<String, Object> parameter);

    int update(DataSourceWrapper dataSourceWrapper, String sqlId, Map<String, Object> parameter);

    int insert(DataSourceWrapper dataSourceWrapper, String sqlId, Map<String, Object> parameter);

    <T> T queryOne(DataSourceWrapper dataSourceWrapper, Class<T> type, String sqlId, Map<String, Object> parameter);

    <T> T queryById(DataSourceWrapper dataSourceWrapper, Class<T> type, Object id);

    <T> Page<T> queryPage(DataSourceWrapper dataSourceWrapper, Class<T> type, String sqlId, Pageable pageable,
            Map<String, Object> parameter);

    <T> T queryOneColumn(DataSourceWrapper dataSourceWrapper, Class<?> targetType, String sqlId,
            Map<String, Object> parameter);

    <T> void queryBigData(DataSourceWrapper dataSourceWrapper, Class<? extends T> type, String sqlId,
            AbstractPageWork<T> pageWork, Map<String, Object> parameter);

    // map
    void queryMapBigData(DataSourceWrapper dataSourceWrapper, String sqlId, AbstractMapPageWork pageWork,
            Map<String, Object> parameter);

    Map<String, Object> queryMapOne(DataSourceWrapper dataSourceWrapper, String sqlId, Map<String, Object> parameter);

    Page<Map<String, Object>> queryMapPage(DataSourceWrapper dataSourceWrapper, String sqlId, Pageable pageable,
            Map<String, Object> parameter);

    List<Map<String, Object>> queryMapList(DataSourceWrapper dataSourceWrapper, String sqlId,
            Map<String, Object> parameter);

    <T> Map<String, T> queryToMap(Class<T> c, String sqlId, String keyInMap, Map<String, Object> parameter);

    <T> Map<String, T> queryToMap(DataSourceWrapper dataSourceWrapper, Class<T> c, String sqlId, String keyInMap,
            Map<String, Object> parameter);

}
