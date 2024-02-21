package com.vonchange.mybatis.language;

import com.vonchange.common.util.MarkdownUtil;
import com.vonchange.common.util.UtilAll;
import com.vonchange.mybatis.dialect.Dialect;

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
            String sqlId = script.substring(1);
            sqlInXml = MarkdownUtil.getContent(null==prePackage?sqlId:(prePackage+"."+sqlId));
            sqlInXml = DynamicSql.dynamicSql(sqlInXml);
            sqlInXml = sqlInXml.trim();
            if (sqlInXml.contains("</")) {
                sqlInXml = "<script>" + sqlInXml + "</script>";
                sqlInXml = UtilAll.UString.replaceEach(sqlInXml, new String[]{" > ", " < ", " >= ", " <= ", " <> "},
                        new String[]{" &gt; ", " &lt; ", " &gt;= ", " &lt;= ", " &lt;&gt; "});
            }
        }
        return sqlInXml;
    }

}
