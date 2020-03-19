package org.springframework.data.mybatis.mini.jdbc.repository.config;

import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;

public class ConfigInfo {
    private String location;
    private String method;
    private Class<?> type;
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

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
