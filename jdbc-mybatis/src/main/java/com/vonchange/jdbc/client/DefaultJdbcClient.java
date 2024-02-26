package com.vonchange.jdbc.client;

import com.vonchange.common.util.StringPool;
import com.vonchange.jdbc.config.EnumRWType;
import com.vonchange.jdbc.config.EnumSqlRead;
import com.vonchange.jdbc.core.CrudUtil;
import com.vonchange.jdbc.core.JdbcLogUtil;
import com.vonchange.jdbc.core.MyJdbcTemplate;
import com.vonchange.jdbc.mapper.BeanMapper;
import com.vonchange.jdbc.mapper.ScalarMapper;
import com.vonchange.jdbc.model.DataSourceWrapper;
import com.vonchange.jdbc.model.SqlParam;
import com.vonchange.jdbc.util.MybatisTpl;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultJdbcClient implements JdbcClient{
    private static final Logger log = LoggerFactory.getLogger(DefaultJdbcClient.class);
    private final MyJdbcTemplate classicOps;
    private  DataSourceWrapper dataSourceWrapper;
    public DefaultJdbcClient(DataSourceWrapper dataSource) {
        this.dataSourceWrapper=dataSource;
        MyJdbcTemplate myJdbcTemplate = new MyJdbcTemplate(dataSource.getDataSource());
        myJdbcTemplate.setFetchSizeBigData(dataSource.getDialect().getBigDataFetchSize());
        myJdbcTemplate.setFetchSize(dataSource.getDialect().getFetchSize());
        this.classicOps=myJdbcTemplate;
    }
    @Override
    public StatementSpec sql(String sql) {
        return new DefaultStatementSpec(sql);
    }
    private class DefaultStatementSpec implements StatementSpec {
        private final String sql;


        private final List<Object> indexedParams = new ArrayList<>();
        private final Map<String,Object> namedParams = new HashMap<>();
        private SqlParam sqlParam;

        public DefaultStatementSpec(String sql) {
            this.sql = sql;
        }
        @Override
        public StatementSpec param(@Nullable Object value) {
            //validateIndexedParamValue(value);
            this.indexedParams.add(value);
            return this;
        }
        @Override
        public StatementSpec params(Object... values) {
            Collections.addAll(this.indexedParams, values);
            return this;
        }
        @Override
        public StatementSpec params(List<?> values) {
            this.indexedParams.addAll(values);
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
            this.sqlParam =getSqlParameter(this.sql);
            return query(new BeanMapper<>(mappedClass));
        }
        private <T> SqlParam getSqlParameter(String sql){
            if(!sql.contains(StringPool.SPACE)){
                throw new JdbcMybatisRuntimeException("{} error",sql);
            }
            if(sql.contains("[@")||sql.contains("#{")){
                SqlParam sqlParam = MybatisTpl.generate("mybatis_sql",sql,namedParams,dataSourceWrapper.getDialect());
                sqlParam.setSqlRead(EnumSqlRead.mybatis);
                return sqlParam;
            }
            SqlParam sqlParam = new SqlParam(sql,this.indexedParams);
            sqlParam.setSqlRead(EnumSqlRead.sql);
            return sqlParam;
        }


        public <T> MappedQuerySpec<T> query(ResultSetExtractor<List<T>> resultSetExtractor) {
            return new IndexedParamMappedQuerySpec<>(resultSetExtractor);
        }
        @Override
        public int update() {
            // update only EnumSqlRead.markdown
            SqlParam sqlParameter = CrudUtil.getSqlParameter(sql,this.namedParams,dataSourceWrapper.getDialect());
            return  classicOps.update(sqlParameter.getSql(),sqlParameter.getParams());
        }


        private class IndexedParamMappedQuerySpec<T> implements MappedQuerySpec<T> {

            private final ResultSetExtractor<List<T>> resultSetExtractor;

            public IndexedParamMappedQuerySpec(ResultSetExtractor<List<T>> resultSetExtractor) {
                this.resultSetExtractor = resultSetExtractor;
            }

            @Override
            public List<T> list() {
                JdbcLogUtil.logSql(EnumRWType.read, sqlParam);
                return classicOps.query(sqlParam.getSql(), this.resultSetExtractor, sqlParam.getParams());
            }
            @Override
            public Page<T> page(Pageable pageable){
                Long total = classicOps.query(CrudUtil.generateMyCountSql(sqlParam.getSql()), new ScalarMapper<>(Long.class),
                        sqlParam.getParams());
                if(null==total) total=0L;
                int pageNum = Math.max(pageable.getPageNumber(), 0);
                int firstEntityIndex = pageable.getPageSize() * pageNum;
                String sqlPage = dataSourceWrapper.getDialect().getPageSql(sqlParam.getSql(), firstEntityIndex, pageable.getPageSize());
                List<T> entities = classicOps.query(sqlPage,this.resultSetExtractor, sqlParam.getParams());
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
                    log.warn("{} result list size more than 1,actual size {}",sql,list.size());
                }
                return list.get(0);
            }
        }
    }
}
