package com.vonchange.jdbc.abstractjdbc.util.markdown;


import com.vonchange.jdbc.abstractjdbc.config.ConstantJdbc;
import com.vonchange.mybatis.common.util.StringUtils;
import com.vonchange.mybatis.tpl.exception.MybatisMinRuntimeException;


public class MarkdownDataUtil{
    private MarkdownDataUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getSql(MarkdownDTO markdownDTO,String id) {
        String sql =markdownDTO.getContentMap().get(id);
        if(StringUtils.isBlank(sql)&&!StringUtils.endsWith(id, ConstantJdbc.COUNTFLAG)){
            throw  new MybatisMinRuntimeException(markdownDTO.getId()+" can not find id:"+id);
        }
        /* 支持[@sql */
        return MarkdownUtil.getSqlSpinner(markdownDTO,sql);
    }
}
