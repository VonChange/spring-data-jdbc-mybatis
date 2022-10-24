package com.vonchange.nine.demo.config;


import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.spring.data.mybatis.mini.repository.ReadDataSources;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class DBConfig implements InitializingBean {
    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceRead")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource readDataSource() {
        return DataSourceBuilder.create().build();
    }


    //多数据源 定义 key 为你要设置@DataSourceKey的值 不建议多数据源 最好分服务
    @Bean
    public DataSourceWrapper readDataSourceWrapper(@Qualifier("dataSourceRead") DataSource dataSource) {
        return new DataSourceWrapper(dataSource,"dataSourceRead");
    }
    //自定义 读库数据源 不自定义默认所有你设置的数据源
    //@Bean
    public ReadDataSources initReadDataSources(){
        return new ReadDataSources() {
            @Override
            public DataSource[] allReadDataSources() {
                return new DataSource[]{mainDataSource(),mainDataSource(),readDataSource()};
            }
        };
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        /*DataSource dataSource=mainDataSource();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        DataSourceUtils.releaseConnection(connection,dataSource);*/
    }

}