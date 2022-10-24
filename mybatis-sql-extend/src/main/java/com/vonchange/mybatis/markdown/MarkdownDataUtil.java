package com.vonchange.mybatis.markdown;


import com.vonchange.common.util.StringUtils;
import com.vonchange.mybatis.exception.MybatisMinRuntimeException;


public class MarkdownDataUtil{
    private MarkdownDataUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static final String COUNTFLAG = "Count";
    public static String getSql(MarkdownDTO markdownDTO,String id) {
        String sql =markdownDTO.getContentMap().get(id);
        if(StringUtils.isBlank(sql)&&!StringUtils.endsWith(id, COUNTFLAG)){
            throw  new MybatisMinRuntimeException(markdownDTO.getId()+" can not find id:"+id);
        }
        /* 支持[@sql */
        return MarkdownUtil.getSqlSpinner(markdownDTO,sql);
    }
}
