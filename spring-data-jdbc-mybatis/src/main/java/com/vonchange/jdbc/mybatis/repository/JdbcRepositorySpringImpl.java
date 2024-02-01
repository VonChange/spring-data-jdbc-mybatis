package com.vonchange.jdbc.mybatis.repository;

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
    private static final Random RANDOM = new Random();
    private DataSource[] dataSources;
    private DataSource dataSource;
    private DataSourceInSql dataSourceInSql;
    private ReadDataSources readDataSources;
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

    @Autowired(required = false)
    public void setDataSourceInSql(DataSourceInSql dataSourceInSql) {
        this.dataSourceInSql = dataSourceInSql;
    }

    @Autowired(required = false)
    public void setReadDataSources(ReadDataSources readDataSources) {
        this.readDataSources = readDataSources;
    }

    @Override
    public DataSourceWrapper getReadDataSource() {
        DataSource[] dataSourcesRead;
        if (null != readDataSources && null != readDataSources.allReadDataSources()) {
            dataSourcesRead = readDataSources.allReadDataSources();
        } else {
            dataSourcesRead = dataSources;
        }
        if (null == dataSourcesRead || dataSourcesRead.length == 0) {
            throw new JdbcMybatisRuntimeException("no dataSource");
        }
        List<DataSourceWrapper> dataSourceWrapperList = new ArrayList<>();
        int i = 0;
        for (DataSource dataSource : dataSourcesRead) {
            dataSourceWrapperList.add(new DataSourceWrapper(dataSource, DATA_SOURCE_NAME + ((i == 0) ? "" : i)));
            i++;
        }
        int random = RANDOM.nextInt(dataSourceWrapperList.size());
        log.debug("dataSource read random {}", random);
        return dataSourceWrapperList.get(random);
    }

    @Override
    protected DataSourceWrapper getWriteDataSource() {
        if (null == dataSources || dataSources.length == 0) {
            throw new JdbcMybatisRuntimeException("no dataSource");
        }
        return new DataSourceWrapper(dataSource, DATA_SOURCE_NAME);
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
