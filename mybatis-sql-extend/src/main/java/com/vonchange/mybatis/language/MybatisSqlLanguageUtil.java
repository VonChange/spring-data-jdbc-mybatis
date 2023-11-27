package com.vonchange.mybatis.language;

import com.vonchange.common.util.StringUtils;
import com.vonchange.mybatis.dialect.Dialect;
import com.vonchange.mybatis.markdown.MarkdownUtil;
import com.vonchange.mybatis.sql.DynamicSql;


public class MybatisSqlLanguageUtil {
    /**
     *
     * @param prePackage 前缀报名 比如mapper
     * @param script sqlInXml
     * @param dialect 方言
     */
    public static String sqlInXml(String prePackage,String script, Dialect dialect){
        String sqlInXml = script;
        if (script.startsWith("@")) {
            long a=System.currentTimeMillis();
            String sqlId = script.substring(1);
            sqlInXml = MarkdownUtil.getSql(null==prePackage?sqlId:(prePackage+"."+sqlId));
            sqlInXml = DynamicSql.dynamicSql(sqlInXml, dialect);
            sqlInXml = sqlInXml.trim();
            if (sqlInXml.contains("</")) {
                sqlInXml = "<script>" + sqlInXml + "</script>";
                sqlInXml = StringUtils.replaceEach(sqlInXml, new String[]{" > ", " < ", " >= ", " <= ", " <> "},
                        new String[]{" &gt; ", " &lt; ", " &gt;= ", " &lt;= ", " &lt;&gt; "});
            }
            System.err.println("sqlInXml time"+(System.currentTimeMillis()-a));
        }
        return sqlInXml;
    }

}
