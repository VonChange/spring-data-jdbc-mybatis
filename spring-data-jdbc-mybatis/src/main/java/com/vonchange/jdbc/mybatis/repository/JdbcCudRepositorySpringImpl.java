package com.vonchange.jdbc.mybatis.repository;

import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.jdbc.springjdbc.repository.AbstractJbdcRepositoryMysql;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.dialect.MySQLDialect;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

public class JdbcCudRepositorySpringImpl extends AbstractJbdcRepositoryMysql {
    private static final Logger log = LoggerFactory.getLogger(JdbcCudRepositorySpringImpl.class);

    private DataSource[] dataSources;
    private DataSource dataSource;
    private DataSourceInSql dataSourceInSql;
    @Value("${jdbc-mybatis.isReadAllScopeOpen:false}")
    private boolean isReadAllScopeOpen;

    @Value("${jdbc-mybatis.logWrite:false}")
    private boolean logWrite;
    @Value("${jdbc-mybatis.logRead:false}")
    private boolean logRead;
    @Value("${jdbc-mybatis.logFullSql:false}")
    private boolean logFullSql;
    @Value("${jdbc-mybatis.dialect:}")
    private String dialect;

    @Autowired
    public void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String DATA_SOURCE_NAME = "dataSource";

    public JdbcCudRepositorySpringImpl(DataSource... dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    protected Dialect getDefaultDialect() {
        if ("".equals(dialect)) {
            return new MySQLDialect();
        }
        try {
            return (Dialect) Class.forName(dialect).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("no dialect {}", dialect, e);
        }
        return new MySQLDialect();
    }

    @Autowired(required = false)
    public void setDataSourceInSql(DataSourceInSql dataSourceInSql) {
        this.dataSourceInSql = dataSourceInSql;
    }

    @Override
    public DataSourceWrapper getReadDataSource() {
        return new DataSourceWrapper(dataSource, DATA_SOURCE_NAME);
    }

    @Override
    protected DataSourceWrapper getWriteDataSource() {
        if (null == dataSources || dataSources.length == 0) {
            throw new JdbcMybatisRuntimeException("no dataSource");
        }
        return new DataSourceWrapper(dataSource, DATA_SOURCE_NAME);
    }

    @Override
    protected DataSourceWrapper getDataSourceFromSql(String sql) {
        if (null == dataSourceInSql) {
            return null;
        }
        return dataSourceInSql.getDataSourceFromSql(sql);
    }


    @Override
    protected boolean logRead() {
        return logRead;
    }

    @Override
    protected boolean logWrite() {
        return logWrite;
    }

    @Override
    protected boolean logFullSql() {
        return logFullSql;
    }

    @Override
    protected boolean readAllScopeOpen() {
        return isReadAllScopeOpen;
    }

}
