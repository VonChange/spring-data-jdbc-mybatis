package com.vonchange.jdbc.mybatis.core.config;

import com.vonchange.jdbc.abstractjdbc.model.DataSourceWrapper;

public class DataSourceWrapperHelperImpl implements DataSourceWrapperHelper{
    private DataSourceWrapper[] dataSourceWrappers;
    public DataSourceWrapperHelperImpl(DataSourceWrapper... dataSourceWrappers){
        this.dataSourceWrappers=dataSourceWrappers;
    }
    @Override
    public DataSourceWrapper getDataSourceWrapperByKey(String key){
        for (DataSourceWrapper dataSourceWrapper: dataSourceWrappers) {
            if(key.equals(dataSourceWrapper.getKey())){
                return dataSourceWrapper;
            }
        }
        return null;
    }
}
