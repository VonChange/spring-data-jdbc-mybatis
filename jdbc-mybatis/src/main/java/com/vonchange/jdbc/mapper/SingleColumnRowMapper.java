package com.vonchange.jdbc.mapper;

import com.vonchange.common.util.ConvertUtil;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SingleColumnRowMapper<T> implements RowMapper<T> {
    @Nullable
    private Class<?> targetType;

    public SingleColumnRowMapper(Class<T> targetType) {
        setRequiredType(targetType);
    }


    /**
     * Set the type that each result object is expected to match.
     * <p>If not specified, the column value will be exposed as
     * returned by the JDBC driver.
     */
    public void setRequiredType(Class<T> targetType) {
        this.targetType = ClassUtils.resolvePrimitiveIfNecessary(targetType);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        if (nrOfColumns != 1) {
            throw new IncorrectResultSetColumnCountException(1, nrOfColumns);
        }
        Object result = JdbcUtils.getResultSetValue(rs, 1);
        return ConvertUtil.toObject(result,targetType);
    }
}
