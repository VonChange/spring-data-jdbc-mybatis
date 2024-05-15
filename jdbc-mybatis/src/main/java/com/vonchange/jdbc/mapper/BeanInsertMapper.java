package com.vonchange.jdbc.mapper;

import com.vonchange.common.util.UtilAll;
import com.vonchange.common.util.exception.ErrorMsg;
import com.vonchange.jdbc.core.CrudUtil;
import com.vonchange.jdbc.model.EntityInfo;
import com.vonchange.jdbc.util.ConvertMap;
import com.vonchange.jdbc.util.EntityUtil;
import com.vonchange.mybatis.exception.EnumJdbcErrorCode;
import com.vonchange.mybatis.exception.JdbcMybatisRuntimeException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class BeanInsertMapper<T> implements ResultSetExtractor<Void> {
    private final List<T> entities;


    public BeanInsertMapper(List<T> entities) {
        this.entities = entities;
    }
    public BeanInsertMapper(T entity) {
        this.entities = Collections.singletonList(entity);
    }


    @Override
    public Void extractData(ResultSet rs) throws SQLException {
        int i=0;
        while (rs.next()){
            toBean(rs,entities.get(i));
            i++;
        }
        return null;
    }

    private void toBean(ResultSet rs, T entity) throws SQLException {
        EntityInfo entityInfo = EntityUtil.getEntityInfo(entity.getClass());
        String idFieldName = entityInfo.getIdFieldName();
        if(UtilAll.UString.isBlank(idFieldName)){
            throw  new JdbcMybatisRuntimeException(EnumJdbcErrorCode.NeedIdAnnotation,
                    ErrorMsg.builder().message("need @Id in your entity"));
        }
        ConvertMap.toBean(CrudUtil.rowToMap(entityInfo,rs,idFieldName),entity);
    }
}
