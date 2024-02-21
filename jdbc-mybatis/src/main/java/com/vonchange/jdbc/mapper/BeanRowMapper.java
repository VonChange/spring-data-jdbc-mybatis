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


public class BeanRowMapper<T> implements RowMapper<T> {
    @Nullable
    private Class<T> mappedClass;
    public BeanRowMapper(Class<T> mappedClass) {
        this.mappedClass=mappedClass;
    }
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        boolean base=false;
        if(ClazzUtils.isBaseType(mappedClass)){
            base=true;
        }
        return base? ConvertUtil.toObject(JdbcUtils.getResultSetValue(rs, 1),mappedClass)
                : ConvertMap.toBean(CrudUtil.rowToMap(rs,null),mappedClass);
    }


}
