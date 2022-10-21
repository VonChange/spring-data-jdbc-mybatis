/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.vonchange.jdbc.abstractjdbc.count;


import com.vonchange.mybatis.common.util.StringUtils;
import com.vonchange.jsqlparser.expression.Alias;
import com.vonchange.jsqlparser.expression.Expression;
import com.vonchange.jsqlparser.expression.Function;
import com.vonchange.jsqlparser.parser.CCJSqlParserUtil;
import com.vonchange.jsqlparser.schema.Column;
import com.vonchange.jsqlparser.statement.Statement;
import com.vonchange.jsqlparser.statement.select.FromItem;
import com.vonchange.jsqlparser.statement.select.Join;
import com.vonchange.jsqlparser.statement.select.LateralSubSelect;
import com.vonchange.jsqlparser.statement.select.OrderByElement;
import com.vonchange.jsqlparser.statement.select.PlainSelect;
import com.vonchange.jsqlparser.statement.select.Select;
import com.vonchange.jsqlparser.statement.select.SelectBody;
import com.vonchange.jsqlparser.statement.select.SelectExpressionItem;
import com.vonchange.jsqlparser.statement.select.SelectItem;
import com.vonchange.jsqlparser.statement.select.SetOperationList;
import com.vonchange.jsqlparser.statement.select.SubJoin;
import com.vonchange.jsqlparser.statement.select.SubSelect;
import com.vonchange.jsqlparser.statement.select.ValuesList;
import com.vonchange.jsqlparser.statement.select.WithItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * sql解析类，提供更智能的count查询sql
 *
 * @author liuzh
 */
public class CountSqlParser {
    private static final Logger log = LoggerFactory.getLogger(CountSqlParser.class);
    public static final String KEEP_ORDERBY = "/*keep orderby*/";
    private static final Alias TABLE_ALIAS;
    private static final String KEEP_SMART = "/*rx*/";
    //<editor-fold desc="聚合函数">
    private final Set<String> skipFunctions = Collections.synchronizedSet(new HashSet<String>());
    private final Set<String> falseFunctions = Collections.synchronizedSet(new HashSet<String>());

    /**
     * 聚合函数，以下列函数开头的都认为是聚合函数
     */
    private static final Set<String> AGGREGATE_FUNCTIONS = new HashSet<>(Arrays.asList(
            ("APPROX_COUNT_DISTINCT," +
            "ARRAY_AGG," +
            "AVG," +
            "BIT_," +
            //"BIT_AND," +
            //"BIT_OR," +
            //"BIT_XOR," +
            "BOOL_," +
            //"BOOL_AND," +
            //"BOOL_OR," +
            "CHECKSUM_AGG," +
            "COLLECT," +
            "CORR," +
            //"CORR_," +
            //"CORRELATION," +
            "COUNT," +
            //"COUNT_BIG," +
            "COVAR," +
            //"COVAR_POP," +
            //"COVAR_SAMP," +
            //"COVARIANCE," +
            //"COVARIANCE_SAMP," +
            "CUME_DIST," +
            "DENSE_RANK," +
            "EVERY," +
            "FIRST," +
            "GROUP," +
            //"GROUP_CONCAT," +
            //"GROUP_ID," +
            //"GROUPING," +
            //"GROUPING," +
            //"GROUPING_ID," +
            "JSON_," +
            //"JSON_AGG," +
            //"JSON_ARRAYAGG," +
            //"JSON_OBJECT_AGG," +
            //"JSON_OBJECTAGG," +
            //"JSONB_AGG," +
            //"JSONB_OBJECT_AGG," +
            "LAST," +
            "LISTAGG," +
            "MAX," +
            "MEDIAN," +
            "MIN," +
            "PERCENT_," +
            //"PERCENT_RANK," +
            //"PERCENTILE_CONT," +
            //"PERCENTILE_DISC," +
            "RANK," +
            "REGR_," +
            "SELECTIVITY," +
            "STATS_," +
            //"STATS_BINOMIAL_TEST," +
            //"STATS_CROSSTAB," +
            //"STATS_F_TEST," +
            //"STATS_KS_TEST," +
            //"STATS_MODE," +
            //"STATS_MW_TEST," +
            //"STATS_ONE_WAY_ANOVA," +
            //"STATS_T_TEST_*," +
            //"STATS_WSR_TEST," +
            "STD," +
            //"STDDEV," +
            //"STDDEV_POP," +
            //"STDDEV_SAMP," +
            //"STDDEV_SAMP," +
            //"STDEV," +
            //"STDEVP," +
            "STRING_AGG," +
            "SUM," +
            "SYS_OP_ZONE_ID," +
            "SYS_XMLAGG," +
            "VAR," +
            //"VAR_POP," +
            //"VAR_SAMP," +
            //"VARIANCE," +
            //"VARIANCE_SAMP," +
            //"VARP," +
            "XMLAGG").split(",")));
    //</editor-fold>

    static {
        TABLE_ALIAS = new Alias("table_count");
        TABLE_ALIAS.setUseAs(false);
    }

    /**
     * 添加到聚合函数，可以是逗号隔开的多个函数前缀
     *
     * @param functions
     */
    public static void addAggregateFunctions(String functions){
        if(StringUtils.isNotBlank(functions)){
            String[] funs = functions.split(",");
            for (int i = 0; i < funs.length; i++) {
                AGGREGATE_FUNCTIONS.add(funs[i].toUpperCase());
            }
        }
    }

    /**
     * 获取智能的countSql
     *
     * @param sql
     */
    public String getSmartCountSql(String sql) {
        return getSmartCountSql(sql, "0");
    }
    private String generateMyCountSql(String sql) {
        String lowerSql = sql.toLowerCase();
        int begin = lowerSql.indexOf("from ");
        int guoIndex = lowerSql.lastIndexOf(')');
        int end = lowerSql.length();
        int orderIndex=0;
        if (guoIndex > 0) {
            orderIndex=sql.lastIndexOf("order ",guoIndex);
            //sql.substring(guoIndex).lastIndexOf("order ");
            if(orderIndex>0){
                end = guoIndex + orderIndex;
            }
        }else{
            orderIndex=sql.lastIndexOf("order ");
            if(orderIndex>0){
                end = guoIndex + orderIndex;
            }
        }
        String result = sql.substring(begin, end);
        return StringUtils.format("select count(1)  {0} ", result);
    }
    /**
     * 获取智能的countSql
     *
     * @param sql
     * @param name 列名，默认 0
     */
    public String getSmartCountSql(String sql, String name) {
        //解析SQL
        Statement stmt = null;
        //特殊sql不需要去掉order by时，使用注释前缀
        if(sql.contains(KEEP_ORDERBY)){
            return getSimpleCountSql(sql, name);
        }
        boolean keepSmart=false;
        if(sql.contains(KEEP_SMART)){
            keepSmart=true;
        }
        if(keepSmart){
            try{
                return generateMyCountSql(sql);
            }catch (Exception e){
                return getSimpleCountSql(sql);
            }
        }
        try {
            stmt = CCJSqlParserUtil.parse(sql);
        } catch (Exception e) {
            log.debug("无法解析 使用一般方法返回count语句");
            //无法解析的用一般方法返回count语句
            return getSimpleCountSql(sql, name);
        }
        Select select = (Select) stmt;
        SelectBody selectBody = select.getSelectBody();
        try {
            //处理body-去order by
            processSelectBody(selectBody);
        } catch (Exception e) {
            log.debug("当sql包含group by时 不去除order by");
            //当 sql 包含 group by 时，不去除 order by
            return getSimpleCountSql(sql, name);
        }
        //处理with-去order by
        processWithItemsList(select.getWithItemsList());
        //处理为count查询
        sqlToCount(select, name);
        return select.toString();
    }

    /**
     * 获取普通的Count-sql
     *
     * @param sql 原查询sql
     * @return 返回count查询sql
     */
    public String getSimpleCountSql(final String sql) {
        return getSimpleCountSql(sql, "0");
    }

    /**
     * 获取普通的Count-sql
     *
     * @param sql 原查询sql
     * @return 返回count查询sql
     */
    public String getSimpleCountSql(final String sql, String name) {
        StringBuilder stringBuilder = new StringBuilder(sql.length() + 40);
        stringBuilder.append("select count(");
        stringBuilder.append(name);
        stringBuilder.append(") from (");
        stringBuilder.append(sql);
        stringBuilder.append(") tmp_count");
        return stringBuilder.toString();
    }

    /**
     * 将sql转换为count查询
     *
     * @param select
     */
    public void sqlToCount(Select select, String name) {
        SelectBody selectBody = select.getSelectBody();
        // 是否能简化count查询
        List<SelectItem> COUNT_ITEM = new ArrayList<>();
        COUNT_ITEM.add(new SelectExpressionItem(new Column("count(" + name +")")));
        if (selectBody instanceof PlainSelect && isSimpleCount((PlainSelect) selectBody)) {
            ((PlainSelect) selectBody).setSelectItems(COUNT_ITEM);
        } else {
            PlainSelect plainSelect = new PlainSelect();
            SubSelect subSelect = new SubSelect();
            subSelect.setSelectBody(selectBody);
            subSelect.setAlias(TABLE_ALIAS);
            plainSelect.setFromItem(subSelect);
            plainSelect.setSelectItems(COUNT_ITEM);
            select.setSelectBody(plainSelect);
        }
    }

    /**
     * 是否可以用简单的count查询方式
     *
     * @param select
     */
    public boolean isSimpleCount(PlainSelect select) {
        //包含group by的时候不可以
        if (select.getGroupBy() != null) {
            return false;
        }
        //包含distinct的时候不可以
        if (select.getDistinct() != null) {
            return false;
        }
        for (SelectItem item : select.getSelectItems()) {
            //select列中包含参数的时候不可以，否则会引起参数个数错误
            if (item.toString().contains("?")) {
                return false;
            }
            //如果查询列中包含函数，也不可以，函数可能会聚合列
            if (item instanceof SelectExpressionItem) {
                Expression expression = ((SelectExpressionItem) item).getExpression();
                if (expression instanceof Function) {
                    String name = ((Function) expression).getName();
                    if (name != null) {
                        String NAME = name.toUpperCase();
                        if(skipFunctions.contains(NAME)){
                            //go on
                        } else if(falseFunctions.contains(NAME)){
                            return false;
                        } else {
                            for (String aggregateFunction : AGGREGATE_FUNCTIONS) {
                                if(NAME.startsWith(aggregateFunction)){
                                    falseFunctions.add(NAME);
                                    return false;
                                }
                            }
                            skipFunctions.add(NAME);
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 处理selectBody去除Order by
     *
     * @param selectBody
     */
    public void processSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                processSelectBody(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && !operationList.getSelects().isEmpty()) {
                List<SelectBody> plainSelects = operationList.getSelects();
                for (SelectBody plainSelect : plainSelects) {
                    processSelectBody(plainSelect);
                }
            }
            if (!orderByHashParameters(operationList.getOrderByElements())) {
                operationList.setOrderByElements(null);
            }
        }
    }

    /**
     * 处理PlainSelect类型的selectBody
     *
     * @param plainSelect
     */
    public void processPlainSelect(PlainSelect plainSelect) {
        if (!orderByHashParameters(plainSelect.getOrderByElements())) {
            plainSelect.setOrderByElements(null);
        }
        if (plainSelect.getFromItem() != null) {
            processFromItem(plainSelect.getFromItem());
        }
        if (plainSelect.getJoins() != null &&! plainSelect.getJoins().isEmpty()) {
            List<Join> joins = plainSelect.getJoins();
            for (Join join : joins) {
                if (join.getRightItem() != null) {
                    processFromItem(join.getRightItem());
                }
            }
        }
    }

    /**
     * 处理WithItem
     *
     * @param withItemsList
     */
    public void processWithItemsList(List<WithItem> withItemsList) {
        if (withItemsList != null && !withItemsList.isEmpty()) {
            for (WithItem item : withItemsList) {
                processSelectBody(item.getSelectBody());
            }
        }
    }

    /**
     * 处理子查询
     *
     * @param fromItem
     */
    public void processFromItem(FromItem fromItem) {
        if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (subJoin.getJoinList() != null && !subJoin.getJoinList().isEmpty()) {
                for (Join join : subJoin.getJoinList()) {
                    if (join.getRightItem() != null) {
                        processFromItem(join.getRightItem());
                    }
                }
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {

        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }
        //Table时不用处理
    }

    /**
     * 判断Orderby是否包含参数，有参数的不能去
     *
     * @param orderByElements
     */
    public boolean orderByHashParameters(List<OrderByElement> orderByElements) {
        if (orderByElements == null) {
            return false;
        }
        for (OrderByElement orderByElement : orderByElements) {
            if (orderByElement.toString().contains("?")) {
                return true;
            }
        }
        return false;
    }
}
