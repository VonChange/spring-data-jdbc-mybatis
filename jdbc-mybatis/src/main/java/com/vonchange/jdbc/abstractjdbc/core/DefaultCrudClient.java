package com.vonchange.jdbc.abstractjdbc.core;

import com.vonchange.common.util.StringPool;
import com.vonchange.jdbc.abstractjdbc.config.Constants;
import com.vonchange.jdbc.abstractjdbc.handler.BeanInsertHandler;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.jdbc.abstractjdbc.template.MyJdbcTemplate;
import com.vonchange.jdbc.abstractjdbc.util.ConvertMap;
import com.vonchange.jdbc.mapper.BeanRowMapper;
import com.vonchange.jdbc.mapper.SingleColumnRowMapper;
import com.vonchange.mybatis.tpl.MybatisTpl;
import com.vonchange.mybatis.tpl.model.SqlWithParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

    private final Map<Class<?>, RowMapper<?>> rowMapperCache = new ConcurrentHashMap<>();

    public DefaultCrudClient(DataSourceWrapper dataSource) {
        MyJdbcTemplate myJdbcTemplate = new MyJdbcTemplate(dataSource.getDataSource());
        myJdbcTemplate.setFetchSizeBigData(dataSource.getDialect().getBigDataFetchSize());
        myJdbcTemplate.setFetchSize(dataSource.getDialect().getFetchSize());
        this.classicOps=myJdbcTemplate;
    }


    public final <T> int insert(T entity) {
        SqlWithParam sqlParameter = CrudUtil.generateInsertSql(entity,false);
        JdbcLogUtil.logSql(Constants.EnumRWType.write, sqlParameter.getSql(), sqlParameter.getParams());
        return classicOps.insert(sqlParameter.getSql(),sqlParameter.getColumnReturns(), new BeanInsertHandler<>(entity), sqlParameter.getParams());
    }
    public final <T> int insert(List<T> entities) {
        if(CollectionUtils.isEmpty(entities)){
            return 0;
        }
        SqlWithParam sqlParameter = CrudUtil.generateInsertSql(entities.get(0),true);
        String sql = sqlParameter.getSql();
         List<List<Object[]>> list=  CrudUtil.insertBatchParam(entities,true,-1);
         int num=0;
        for (List<Object[]> objects : list) {
            num+=classicOps.batchUpdate(sql,objects).length;
        }
        return num;
    }
    public final <T> int update(T entity) {
        SqlWithParam sqlParameter = CrudUtil.generateUpdateEntitySql(entity, false);
        JdbcLogUtil.logSql(Constants.EnumRWType.write, sqlParameter.getSql(), sqlParameter.getParams());
        return classicOps.update(sqlParameter.getSql(), sqlParameter.getParams());
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
            //validateIndexedParamValue(value);
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
        public <T> MappedQuerySpec<T> query(Class<T> mappedClass){
            SqlWithParam sqlWithParam=getSqlParameter(mappedClass);
            this.sql=sqlWithParam.getSql();// @TODO 顺序问题
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
                    return MybatisTpl.generate("mybatis_sql",sqlId,namedParams);
                }
                SqlWithParam sqlWithParam = new SqlWithParam();
                sqlWithParam.setSql(sqlId);
                sqlWithParam.setParams(this.indexedParams.toArray());
                return sqlWithParam;
            }
            if(sqlId.contains(StringPool.DOT)){
                return CrudUtil.getSqlParameter(sqlId,namedParams);
            }
            return CrudUtil.nameQuery(sqlId,mappedClass,this.indexedParams);
        }


        public <T> MappedQuerySpec<T> query(RowMapper<T> rowMapper) {
            return new IndexedParamMappedQuerySpec<>(rowMapper);
        }
        @Override
        public int update() {
            return  classicOps.update(sql,this.indexedParams.toArray());
        }
        public <T> int updateBatch(List<T> entities) {
            if(CollectionUtils.isEmpty(entities)){
                return 0;
            }
            T entity = entities.get(0);
            Map<String, Object> map = ConvertMap.toMap(entity);
            SqlWithParam sqlParameter = CrudUtil.getSqlParameter(sqlId,map);
            String sql = sqlParameter.getSql();
            List<List<Object[]>> list= CrudUtil.batchUpdateParam(entities,sqlParameter.getPropertyNames(),-1);
            int num=0;
            for (List<Object[]> objects : list) {
                num+=classicOps.batchUpdate(sql,objects).length;
            }
            return  num;
        }
       /* private PreparedStatementCreator statementCreatorForIndexedParams() {
            return new PreparedStatementCreatorFactory(this.sql).newPreparedStatementCreator(this.indexedParams);
        }*/
        private class IndexedParamMappedQuerySpec<T> implements MappedQuerySpec<T> {

            private final RowMapper<T> rowMapper;

            public IndexedParamMappedQuerySpec(RowMapper<T> rowMapper) {
                this.rowMapper = rowMapper;
            }

            @Override
            public List<T> list() {
                JdbcLogUtil.logSql(Constants.EnumRWType.read,sql,indexedParams.toArray());
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
