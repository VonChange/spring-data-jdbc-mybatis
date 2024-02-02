package com.vonchange.jdbc.mybatis.repository;

import com.vonchange.jdbc.abstractjdbc.config.ConstantJdbc;
import com.vonchange.jdbc.abstractjdbc.core.AbstractJdbcCore;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.dialect.MySQLDialect;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JdbcRepositorySpringImpl extends AbstractJdbcCore {
    private static final Logger log = LoggerFactory.getLogger(JdbcRepositorySpringImpl.class);
    private DataSource[] dataSources;
    private DataSource dataSource;
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

    public JdbcRepositorySpringImpl(DataSource... dataSources) {
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

    @Override
    public DataSourceWrapper getReadDataSource() {
        if (null == dataSources || dataSources.length == 0) {
            throw new JdbcMybatisRuntimeException("no dataSource");
        }
        return new DataSourceWrapper(dataSource, ConstantJdbc.DataSourceDefault);
    }

    @Override
    protected DataSourceWrapper getWriteDataSource() {
        if (null == dataSources || dataSources.length == 0) {
            throw new JdbcMybatisRuntimeException("no dataSource");
        }
        return new DataSourceWrapper(dataSource, ConstantJdbc.DataSourceDefault);
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

}
