package com.vonchange.spring.data.mybatis.mini.repository;

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.jdbc.springjdbc.repository.AbstractJbdcRepositoryMysql;
import com.vonchange.mybatis.tpl.exception.MybatisMinRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public    class JdbcRepositorySpringDataImpl extends AbstractJbdcRepositoryMysql implements JdbcRepository {
    private static final Logger log = LoggerFactory.getLogger(JdbcRepositorySpringDataImpl.class);
    private static final Random RANDOM = new Random();
    private DataSource[] dataSources;
    private DataSourceInSql dataSourceInSql;
    private ReadDataSources readDataSources;
    @Value("${mybatis-mini.isReadAllScopeOpen:false}")
    private boolean isReadAllScopeOpen;
    @Value("${mybatis-mini.batchSize:5000}")
    private int batchSize;
    @Value("${mybatis-mini.logWrite:false}")
    private boolean logWrite;
    @Value("${mybatis-mini.logRead:false}")
    private boolean logRead;
    @Value("${mybatis-mini.logFullSql:false}")
    private boolean logFullSql;

    private static final String   DATA_SOURCE_NAME="dataSource";
    public JdbcRepositorySpringDataImpl(DataSource... dataSources){
        this.dataSources=dataSources;
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
        if(null!=readDataSources&&null!=readDataSources.allReadDataSources()){
            dataSourcesRead=readDataSources.allReadDataSources();
        }else{
            dataSourcesRead=dataSources;
        }
        if(null==dataSourcesRead||dataSourcesRead.length==0){
            throw new MybatisMinRuntimeException("no dataSource");
        }
        List<DataSourceWrapper> dataSourceWrapperList = new ArrayList<>();
        int i=0;
        for (DataSource dataSource: dataSourcesRead) {
            dataSourceWrapperList.add(new DataSourceWrapper(dataSource,DATA_SOURCE_NAME+((i==0)?"":i)));
            i++;
        }
        int random =RANDOM.nextInt(dataSourceWrapperList.size());
        log.debug("dataSource read random {}",random);
        return dataSourceWrapperList.get(random);
    }

    @Override
    protected DataSourceWrapper getWriteDataSource() {
        if(null==dataSources||dataSources.length==0){
            throw new MybatisMinRuntimeException("no dataSource");
        }
        return new DataSourceWrapper(dataSources[0],DATA_SOURCE_NAME);
    }

    @Override
    protected boolean needInitEntityInfo() {
        return false;
    }
    @Override
    protected DataSourceWrapper getDataSourceFromSql(String sql){
        if(null==dataSourceInSql){
            return null;
        }
        return dataSourceInSql.getDataSourceFromSql(sql);
    }
    @Override
    protected int batchSize() {
        return batchSize;
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
    protected boolean  readAllScopeOpen(){
        return isReadAllScopeOpen;
    }
}
