package com.vonchange.jdbc.mybatis.core.config;

import com.vonchange.jdbc.model.DataSourceWrapper;

public class ConfigInfo {
    private String location;
    private String method;
    private Class<?> domainType;
    private String repositoryName;
    private DataSourceWrapper dataSourceWrapper;


    public String getRepositoryName() {
        return repositoryName;
    }

    public DataSourceWrapper getDataSourceWrapper() {
        return dataSourceWrapper;
    }

    public void setDataSourceWrapper(DataSourceWrapper dataSourceWrapper) {
        this.dataSourceWrapper = dataSourceWrapper;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?> getDomainType() {
        return domainType;
    }

    public void setDomainType(Class<?> domainType) {
        this.domainType = domainType;
    }
}
