package com.vonchange.jdbc.mapper;

import com.vonchange.common.util.ClazzUtils;
import com.vonchange.common.util.ConvertUtil;
import com.vonchange.jdbc.abstractjdbc.core.CrudUtil;
import com.vonchange.jdbc.abstractjdbc.util.ConvertMap;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class BeanRowMapper<T> implements RowMapper<T> {
    @Nullable
    private Class<T> mappedClass;
    public BeanRowMapper(Class<T> mappedClass) {
        this.mappedClass=mappedClass;
    }
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        if(ClazzUtils.isBaseType(mappedClass)){
           return ConvertUtil.toObject(JdbcUtils.getResultSetValue(rs, 1),mappedClass);
        }
        if(mappedClass == Map.class){
            return (T)CrudUtil.toOrmMap(CrudUtil.rowToMap(rs,null));
        }
        return ConvertMap.toBean(CrudUtil.rowToMap(rs,null),mappedClass);
    }


}
