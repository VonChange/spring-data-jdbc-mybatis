package com.vonchange.jdbc.core;

import com.vonchange.common.util.StringPool;
import com.vonchange.jdbc.config.EnumRWType;
import com.vonchange.jdbc.config.EnumSqlRead;
import com.vonchange.jdbc.handler.AbstractPageWork;
import com.vonchange.jdbc.handler.BeanInsertHandler;
import com.vonchange.jdbc.handler.BigDataBeanListHandler;
import com.vonchange.jdbc.handler.ScalarHandler;
import com.vonchange.jdbc.model.DataSourceWrapper;
import com.vonchange.jdbc.template.MyJdbcTemplate;
import com.vonchange.jdbc.util.ConvertMap;
import com.vonchange.jdbc.mapper.BeanRowMapper;
import com.vonchange.jdbc.mapper.SingleColumnRowMapper;
import com.vonchange.mybatis.tpl.MybatisTpl;
import com.vonchange.mybatis.tpl.model.SqlWithParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCrudClient implements CrudClient{
    private static final Logger log = LoggerFactory.getLogger(DefaultCrudClient.class);
    private final MyJdbcTemplate classicOps;
    private  DataSourceWrapper dataSourceWrapper;

    private final Map<Class<?>, RowMapper<?>> rowMapperCache = new ConcurrentHashMap<>();

    public DefaultCrudClient(DataSourceWrapper dataSource) {
        this.dataSourceWrapper=dataSource;
        MyJdbcTemplate myJdbcTemplate = new MyJdbcTemplate(dataSource.getDataSource());
        myJdbcTemplate.setFetchSizeBigData(dataSource.getDialect().getBigDataFetchSize());
        myJdbcTemplate.setFetchSize(dataSource.getDialect().getFetchSize());
        this.classicOps=myJdbcTemplate;
    }


    public final <T> int insert(T entity) {
        SqlWithParam sqlParameter = CrudUtil.generateInsertSql(entity,false,false);
        JdbcLogUtil.logSql(EnumRWType.write, sqlParameter);
        return classicOps.insert(sqlParameter.getSql(),sqlParameter.getColumnReturns(), new BeanInsertHandler<>(entity), sqlParameter.getParams());
    }
    public final <T> int update(T entity) {
        SqlWithParam sqlParameter = CrudUtil.generateUpdateSql(entity, false);
        JdbcLogUtil.logSql(EnumRWType.write, sqlParameter);
        return classicOps.update(sqlParameter.getSql(), sqlParameter.getParams());
    }
    public final <T> int insert(List<T> entities,boolean ifNullInsertByFirstEntity) {
        if(CollectionUtils.isEmpty(entities)){
            return 0;
        }
        SqlWithParam sqlParameter = CrudUtil.generateInsertSql(entities.get(0),ifNullInsertByFirstEntity,true);
        String sql = sqlParameter.getSql();
         List<List<Object[]>> list=  CrudUtil.batchUpdateParam(entities,true,sqlParameter.getPropertyNames(),-1);
         int num=0;
        for (List<Object[]> objects : list) {
            num+=classicOps.batchUpdate(sql,objects).length;
        }
        return num;
    }
    public final <T> int update(List<T> entities,boolean isNullUpdateByFirstEntity) {
        if(CollectionUtils.isEmpty(entities)){
            return 0;
        }
        SqlWithParam sqlParameter = CrudUtil.generateUpdateSql(entities.get(0),isNullUpdateByFirstEntity);
        String sql = sqlParameter.getSql();
        List<List<Object[]>> list=  CrudUtil.batchUpdateParam(entities,true,sqlParameter.getPropertyNames(),-1);
        int num=0;
        for (List<Object[]> objects : list) {
            num+=classicOps.batchUpdate(sql,objects).length;
        }
        return num;
    }



    @Override
    public StatementSpec sqlId(String sqlId) {
        return new DefaultStatementSpec(sqlId);
    }


    private class DefaultStatementSpec implements StatementSpec {
        private final String sqlId;
        private  String sql;
        private  List<Object> indexedParams = new ArrayList<>();

        private final Map<String,Object> namedParams = new LinkedHashMap<>();


        public DefaultStatementSpec(String sqlId) {
            this.sqlId = sqlId;
        }
        @Override
        public StatementSpec param(Object value) {
            this.indexedParams.add(value);
            return this;
        }

        @Override
        public StatementSpec param(String name, Object value) {
            this.namedParams.put(name,value);
            return this;
        }

        @Override
        public StatementSpec params(Map<String, ?> paramMap) {
            this.namedParams.putAll(paramMap);
            return this;
        }
        @Override
        public  <T> void queryBatch(Class<T> mappedClass, AbstractPageWork<T> pageWork){
            SqlWithParam sqlParameter = CrudUtil.getSqlParameter(sqlId, this.namedParams, dataSourceWrapper.getDialect());
            JdbcLogUtil.logSql(EnumRWType.read,sqlParameter);
            classicOps.queryBigData(sqlParameter.getSql(), new BigDataBeanListHandler<T>(mappedClass, pageWork), sqlParameter.getParams());
        }
        public <T> Page<T>  queryPage(Class<T> mappedClass, Pageable pageable){
            SqlWithParam sqlParameter = getSqlParameter(mappedClass);
            String sql = sqlParameter.getSql();
            SqlWithParam countSqlParam= CrudUtil.countSql(sqlId,sqlParameter,this.namedParams, dataSourceWrapper.getDialect());
            JdbcLogUtil.logSql(EnumRWType.read,sqlParameter);
            Long total = classicOps.query(countSqlParam.getSql(), new ScalarHandler<>(Long.class),
                    sqlParameter.getParams());
            if(null==total) total=0L;
            int pageNum = Math.max(pageable.getPageNumber(), 0);
            int firstEntityIndex = pageable.getPageSize() * pageNum;
            sql = dataSourceWrapper.getDialect().getPageSql(sql, firstEntityIndex, pageable.getPageSize());
            List<T> entities = classicOps.query(sql,new BeanRowMapper<>(mappedClass),sqlParameter.getParams());
            return new PageImpl<>(entities, pageable, total);
        }
        public <T> MappedQuerySpec<T> query(Class<T> mappedClass){
            SqlWithParam sqlWithParam=getSqlParameter(mappedClass);
            this.sql=sqlWithParam.getSql();
            this.indexedParams=new ArrayList<>();
            Collections.addAll(this.indexedParams, sqlWithParam.getParams());
            RowMapper<?> rowMapper = rowMapperCache.computeIfAbsent(mappedClass, key ->
                    BeanUtils.isSimpleProperty(mappedClass) ? new SingleColumnRowMapper<>(mappedClass) :
                            new BeanRowMapper<>(mappedClass));
            return query((RowMapper<T>) rowMapper);
        }
        private <T>  SqlWithParam getSqlParameter(Class<T> mappedClass){
            if(sqlId.contains(StringPool.SPACE)){
                if(sqlId.contains("[@")||sqlId.contains("#{")){
                    SqlWithParam sqlWithParam= MybatisTpl.generate("mybatis_sql",sqlId,namedParams,dataSourceWrapper.getDialect());
                    sqlWithParam.setSqlRead(EnumSqlRead.mybatis);
                    return sqlWithParam;
                }
                SqlWithParam sqlWithParam = new SqlWithParam();
                sqlWithParam.setSql(sqlId);
                sqlWithParam.setParams(this.indexedParams.toArray());
                sqlWithParam.setSqlRead(EnumSqlRead.sql);
                return sqlWithParam;
            }
            if(sqlId.contains(StringPool.DOT)){
                SqlWithParam sqlWithParam = CrudUtil.getSqlParameter(sqlId,namedParams,dataSourceWrapper.getDialect());
                sqlWithParam.setSqlRead(EnumSqlRead.markdown);
                return sqlWithParam;
            }
            SqlWithParam sqlWithParam =  CrudUtil.nameQuery(sqlId,mappedClass,this.indexedParams);
            sqlWithParam.setSqlRead(EnumSqlRead.name);
            return sqlWithParam;
        }


        public <T> MappedQuerySpec<T> query(RowMapper<T> rowMapper) {
            return new IndexedParamMappedQuerySpec<>(rowMapper);
        }
        @Override
        public int update() {
            // update only EnumSqlRead.markdown
            SqlWithParam sqlParameter = CrudUtil.getSqlParameter(sqlId,this.namedParams,dataSourceWrapper.getDialect());
            return  classicOps.update(sqlParameter.getSql(),sqlParameter.getParams());
        }
        public <T> int updateBatch(List<T> entities) {
            if(CollectionUtils.isEmpty(entities)){
                return 0;
            }
            T entity = entities.get(0);
            Map<String, Object> map = ConvertMap.toMap(entity);
            SqlWithParam sqlParameter = CrudUtil.getSqlParameter(sqlId,map,dataSourceWrapper.getDialect());
            String sql = sqlParameter.getSql();
            List<List<Object[]>> list= CrudUtil.batchUpdateParam(entities,false,sqlParameter.getPropertyNames(),-1);
            int num=0;
            for (List<Object[]> objects : list) {
                num+=classicOps.batchUpdate(sql,objects).length;
            }
            return  num;
        }

        private class IndexedParamMappedQuerySpec<T> implements MappedQuerySpec<T> {

            private final RowMapper<T> rowMapper;

            public IndexedParamMappedQuerySpec(RowMapper<T> rowMapper) {
                this.rowMapper = rowMapper;
            }

            @Override
            public List<T> list() {
                JdbcLogUtil.logSql(EnumRWType.read,sql,indexedParams.toArray());
                return classicOps.query(sql, this.rowMapper, indexedParams.toArray());
            }

           @Override
           public Iterable<T> iterable() {
               return list();
           }

           @Override
           public T single() {
               List<T> list =list();
               if(CollectionUtils.isEmpty(list)){
                   return null;
               }
               if(list.size()>1){
                   log.warn("{} result list size more than 1,actual size {}",sqlId,list.size());
               }
               return list.get(0);
           }
        }
    }


}
