package com.vonchange.jdbc.mybatis;

import com.vonchange.common.util.StringPool;
import com.vonchange.jdbc.model.SqlParam;
import com.vonchange.jdbc.util.JdbcUtil;
import com.vonchange.jdbc.util.MybatisTpl;
import com.vonchange.mybatis.dialect.Dialect;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class MybatisJdbcTemplate extends NamedParameterJdbcTemplate {
    public MybatisJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    public MybatisJdbcTemplate(JdbcOperations classicJdbcTemplate) {
        super(classicJdbcTemplate);
    }

    protected PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource,
                                                                   @Nullable Consumer<PreparedStatementCreatorFactory> customizer) {
        Map<String,Object> param =toMap(paramSource);
        SqlParam sqlParam = genSqlParam(sql,param);
        if(null==sqlParam){
            return getPreparedStatementCreatorOriginal(sql,paramSource,customizer);
        }
        List<SqlParameter> declaredParameters = buildSqlParameterList(sqlParam.getParams());
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlParam.getSql(),declaredParameters);
        if (customizer != null) {
            customizer.accept(pscf);
        }
        Object[] paramObj=sqlParam.getParams().toArray();
        return pscf.newPreparedStatementCreator(paramObj);
    }
    protected PreparedStatementCreator getPreparedStatementCreatorOriginal(String sql, SqlParameterSource paramSource,
                                                                           @Nullable Consumer<PreparedStatementCreatorFactory> customizer) {

        ParsedSql parsedSql = getParsedSql(sql);
        PreparedStatementCreatorFactory pscf = getPreparedStatementCreatorFactory(parsedSql, paramSource);
        if (customizer != null) {
            customizer.accept(pscf);
        }
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
        return pscf.newPreparedStatementCreator(params);
    }
    protected abstract Dialect dialect();
    private  SqlParam genSqlParam(String sql,Map<String,Object> param){
        if(!sql.contains(StringPool.SPACE)){
            return MybatisTpl.generate("sql."+sql,param,dialect());
        }
        if(sql.contains("[@")){//#{ spel 也有的 ||sql.contains("#{"
            return MybatisTpl.generate(sql,sql,param,dialect());
        }
        if(sql.contains("#{")){
           if(!sql.contains(":#{")){
               return MybatisTpl.generate(sql,sql,param,dialect());
           }
        }
        return null;
    }
    public static List<SqlParameter> buildSqlParameterList(Collection<?> param) {
        List<SqlParameter> params = new ArrayList<>(param.size());
        for (Object value : param) {
            if(null==value){
                params.add(new SqlParameter(JdbcUtils.TYPE_UNKNOWN));
                continue;
            }
            params.add(new SqlParameter(JdbcUtil.sqlTypeFor(value.getClass())));
        }
        return params;
    }
    public static List<SqlParameter> buildSqlParameterListX(SqlParameterSource paramSource) {
        String[] paramNames = paramSource.getParameterNames();
        if (paramNames == null) {
            return new ArrayList<>();
        }
        List<SqlParameter> params = new ArrayList<>(paramNames.length);
        for (String paramName : paramNames) {
            params.add(new SqlParameter(
                    paramName, paramSource.getSqlType(paramName), paramSource.getTypeName(paramName)));
        }
        return params;
    }

    private Map<String,Object> toMap( SqlParameterSource paramSource){
        String[] parameterNames = paramSource.getParameterNames();
        Map<String,Object> map = new HashMap<>();
        if(null==parameterNames){
            return map;
        }
        for (String paramName : parameterNames) {
            map.put(paramName,paramSource.getValue(paramName));
        }
        return map;
    }



}
