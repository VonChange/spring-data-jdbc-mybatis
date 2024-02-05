package com.vonchange.nine.demo.config;


import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;
import com.vonchange.mybatis.dialect.H2Dialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class DBConfig {
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

    //@DataSourceKey
    @Bean
    public DataSourceWrapper readDataSourceWrapper(@Qualifier("dataSourceRead") DataSource dataSource) {
        return new DataSourceWrapper(dataSource,"dataSourceRead");
    }


}