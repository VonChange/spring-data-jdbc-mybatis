package com.vonchange.jdbc.core;

import com.vonchange.jdbc.client.JdbcClient;
import com.vonchange.jdbc.config.ConstantJdbc;
import com.vonchange.jdbc.config.EnumRWType;
import com.vonchange.jdbc.config.EnumSqlRead;
import com.vonchange.jdbc.mapper.AbstractPageWork;
import com.vonchange.jdbc.mapper.BeanInsertMapper;
import com.vonchange.jdbc.mapper.BeanMapper;
import com.vonchange.jdbc.mapper.BigDataBeanMapper;
import com.vonchange.jdbc.mapper.ScalarMapper;
import com.vonchange.jdbc.model.DataSourceWrapper;
import com.vonchange.jdbc.model.SqlParam;
import com.vonchange.jdbc.util.ConvertMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCrudClient implements CrudClient{
    private static final Logger log = LoggerFactory.getLogger(DefaultCrudClient.class);
    private final MyJdbcTemplate classicOps;
    private final DataSourceWrapper dataSourceWrapper;

    private final Map<Class<?>, RowMapper<?>> rowMapperCache = new ConcurrentHashMap<>();

    public DefaultCrudClient(DataSourceWrapper dataSource) {
        this.dataSourceWrapper=dataSource;
        MyJdbcTemplate myJdbcTemplate = new MyJdbcTemplate(dataSource.getDataSource());
        myJdbcTemplate.setFetchSizeBigData(dataSource.getDialect().getBigDataFetchSize());
        myJdbcTemplate.setFetchSize(dataSource.getDialect().getFetchSize());
        this.classicOps=myJdbcTemplate;
    }


    public final <T> int insert(T entity) {
        SqlParam sqlParameter = CrudUtil.generateInsertSql(entity,false,false);
        JdbcLogUtil.logSql(EnumRWType.write, sqlParameter);
        return classicOps.insert(sqlParameter.getSql(),sqlParameter.getColumnReturns(),
                new BeanInsertMapper<>(entity), sqlParameter.getParams());
    }
    public final <T> int update(T entity) {
        SqlParam sqlParameter = CrudUtil.generateUpdateSql(entity, false,false);
        JdbcLogUtil.logSql(EnumRWType.write, sqlParameter);
        int resultNum= classicOps.update(sqlParameter.getSql(), sqlParameter.getParams());
        if(sqlParameter.getVersion()&&resultNum<1){
            throw new OptimisticLockingFailureException(ConstantJdbc.OptimisticLockingFailureExceptionMessage);
        }
        return resultNum;
    }
    public final <T> int insert(List<T> entities,boolean ifNullInsertByFirstEntity) {
        if(CollectionUtils.isEmpty(entities)){
            return 0;
        }
        SqlParam sqlParameter = CrudUtil.generateInsertSql(entities.get(0),ifNullInsertByFirstEntity,true);
        String sql = sqlParameter.getSql();
        return classicOps.insertBatch(sql,CrudUtil.batchUpdateParam(entities,true,sqlParameter.getPropertyNames()),sqlParameter.getColumnReturns(),new BeanInsertMapper<>(entities));
    }
    public final <T> int update(List<T> entities,boolean ifNullUpdateByFirstEntity) {
        if(CollectionUtils.isEmpty(entities)){
            return 0;
        }
        SqlParam sqlParameter = CrudUtil.generateUpdateSql(entities.get(0),ifNullUpdateByFirstEntity,true);
        String sql = sqlParameter.getSql();
        return classicOps.updateBatch(sql,CrudUtil.batchUpdateParam(entities,false,sqlParameter.getPropertyNames()),sqlParameter.getVersion());
    }



    @Override
    public StatementSpec sqlId(String sqlId) {
        return new DefaultStatementSpec(sqlId);
    }

    @Override
    public JdbcClient jdbc() {
        return JdbcClient.create(this.dataSourceWrapper);
    }

    @Override
    public <T, S> MappedQuerySpec<T> findByExample(S example) {
        return null;
    }

    private class DefaultStatementSpec implements StatementSpec {
        private final String sqlId;

        private SqlParam sqlParam;

        private final Map<String,Object> namedParams = new HashMap<>();


        public DefaultStatementSpec(String sqlId) {
            this.sqlId = sqlId;
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
            SqlParam sqlParameter = CrudUtil.getSqlParameter(sqlId, this.namedParams, dataSourceWrapper.getDialect());
            JdbcLogUtil.logSql(EnumRWType.read,sqlParameter);
            classicOps.queryBigData(sqlParameter.getSql(), new BigDataBeanMapper<T>(mappedClass, pageWork), sqlParameter.getParams());
        }

        public <T> MappedQuerySpec<T> query(Class<T> mappedClass){
            this.sqlParam =getSqlParameter(mappedClass);
            return query(new BeanMapper<>(mappedClass));
        }
        private <T> SqlParam getSqlParameter(Class<T> mappedClass){
            SqlParam sqlParam = CrudUtil.getSqlParameter(sqlId,namedParams,dataSourceWrapper.getDialect());
            sqlParam.setSqlRead(EnumSqlRead.markdown);
            return sqlParam;
        }


        public <T> MappedQuerySpec<T> query(ResultSetExtractor<List<T>> resultSetExtractor) {
            return new IndexedParamMappedQuerySpec<>(resultSetExtractor);
        }
        @Override
        public int update() {
            // update only EnumSqlRead.markdown
            SqlParam sqlParameter = CrudUtil.getSqlParameter(sqlId,this.namedParams,dataSourceWrapper.getDialect());
            return  classicOps.update(sqlParameter.getSql(),sqlParameter.getParams());
        }
        public <T> int updateBatch(List<T> entities) {
            if(CollectionUtils.isEmpty(entities)){
                return 0;
            }
            T entity = entities.get(0);
            Map<String, Object> map = ConvertMap.toMap(entity);
            SqlParam sqlParameter = CrudUtil.getSqlParameter(sqlId,map,dataSourceWrapper.getDialect());
            String sql = sqlParameter.getSql();
            return  classicOps.updateBatch(sql,CrudUtil.batchUpdateParam(entities,false,sqlParameter.getPropertyNames()),false);
        }

        private class IndexedParamMappedQuerySpec<T> implements MappedQuerySpec<T> {

            private final ResultSetExtractor<List<T>> resultSetExtractor;

            public IndexedParamMappedQuerySpec(ResultSetExtractor<List<T>> resultSetExtractor) {
                this.resultSetExtractor = resultSetExtractor;
            }

            @Override
            public List<T> list() {
                JdbcLogUtil.logSql(EnumRWType.read, sqlParam.getSql(), sqlParam.getParams());
                return classicOps.query(sqlParam.getSql(), this.resultSetExtractor, sqlParam.getParams());
            }
            @Override
            public  Page<T> page(Pageable pageable){
                String sql = sqlParam.getSql();
                SqlParam countSqlParam= CrudUtil.countSql(sqlId, sqlParam,namedParams, dataSourceWrapper.getDialect());
                JdbcLogUtil.logSql(EnumRWType.read,countSqlParam);
                Long total = classicOps.query(countSqlParam.getSql(), new ScalarMapper<>(Long.class),
                        countSqlParam.getParams());
                if(null==total) total=0L;
                int pageNum = Math.max(pageable.getPageNumber(), 0);
                int firstEntityIndex = pageable.getPageSize() * pageNum;
                sql = dataSourceWrapper.getDialect().getPageSql(sql, firstEntityIndex, pageable.getPageSize());
                List<T> entities = classicOps.query(sql,this.resultSetExtractor, sqlParam.getParams());
                if(null==entities) entities=new ArrayList<>();
                return new PageImpl<>(entities, pageable, total);
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
