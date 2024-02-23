
package com.vonchange.jdbc.mapper;

import com.vonchange.jdbc.config.EnumMappedClass;
import com.vonchange.jdbc.util.ConvertMap;
import com.vonchange.mybatis.tpl.EntityUtil;
import com.vonchange.mybatis.tpl.model.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <core>ResultSetHandler</core> implementation that converts a
 * <core>ResultSet</core> into a <core>List</core> of beans. This class is
 * thread safe.
 *
 * @param <T> the target processor type
 */
public class BeanMapper<T> implements ResultSetExtractor<List<T>> {

    private static final Logger log = LoggerFactory.getLogger(BeanMapper.class);

    private final Class<? extends T> mappedClass;

    public BeanMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    @Override
    public List<T> extractData(ResultSet rs) throws SQLException {
        EnumMappedClass enumMappedClass = ConvertMap.enumMappedClass(mappedClass);
        EntityInfo entityInfo = null;
        if(enumMappedClass.equals(EnumMappedClass.bean)){
            entityInfo = EntityUtil.getEntityInfo(mappedClass);
        }
        List<T> results = new ArrayList<>();
        while (rs.next()){
            results.add(ConvertMap.toMappedClass(rs,mappedClass,enumMappedClass,entityInfo));
        }
        return results;
    }
}
