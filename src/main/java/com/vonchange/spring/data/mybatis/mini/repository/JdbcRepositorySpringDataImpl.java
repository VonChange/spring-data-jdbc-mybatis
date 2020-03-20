package com.vonchange.spring.data.mybatis.mini.repository;

import com.vonchange.jdbc.abstractjdbc.core.JdbcRepository;
import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.jdbc.springjdbc.repository.AbstractJbdcRepositoryMysql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public    class JdbcRepositorySpringDataImpl extends AbstractJbdcRepositoryMysql implements JdbcRepository {
    private static final Logger log = LoggerFactory.getLogger(JdbcRepositorySpringDataImpl.class);
    private static final Random RANDOM = new Random();
    private DataSource[] dataSources;
    @Value("${mybatis-mini.isReadExcludePrimary:false}")
    private boolean isReadExcludePrimary;
    @Value("${mybatis-mini.batchSize:5000}")
    private int batchSize;

    public JdbcRepositorySpringDataImpl(DataSource... dataSources){
        this.dataSources=dataSources;
    }

    @Override
    public DataSourceWrapper getReadDataSource() {
        if(dataSources.length==1){
            return  new DataSourceWrapper(dataSources[0],"dataSource");
        }
        List<DataSourceWrapper> dataSourceWrapperList = new ArrayList<>();
        int i=0;
        for (DataSource dataSource: dataSources) {
            if(isReadExcludePrimary&&i==0){
                i++;
                continue;
            }
            dataSourceWrapperList.add(new DataSourceWrapper(dataSource,"dataSource"+((i==0)?"":i)));
            i++;
        }
        int random =RANDOM.nextInt(dataSourceWrapperList.size());
        log.debug("dataSource read random {}",random);
        return dataSourceWrapperList.get(random);
    }

    @Override
    protected DataSourceWrapper getWriteDataSource() {
        return new DataSourceWrapper(dataSources[0],"dataSource");
    }

    @Override
    protected boolean needInitEntityInfo() {
        return false;
    }

    @Override
    protected int batchSize() {
        return batchSize;
    }

}
