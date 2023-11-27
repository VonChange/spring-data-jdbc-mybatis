package com.vonchange.jdbc.abstractjdbc.template;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MyJdbcTemplate extends JdbcTemplate {
    public MyJdbcTemplate() {
    }

    /**
     * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
     * <p>
     * Note: This will not trigger initialization of the exception translator.
     * 
     * @param dataSource the JDBC DataSource to obtain connections from
     */
    public MyJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    protected <T> int insert(final PreparedStatementCreator psc, final PreparedStatementSetter pss,
            final ResultSetExtractor<T> rse) throws DataAccessException {

        logger.debug("Executing prepared SQL update");
        return execute(psc, new PreparedStatementCallback<Integer>() {

            public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
                try {
                    if (pss != null) {
                        pss.setValues(ps);
                    }
                    int result = ps.executeUpdate();
                    ResultSet resultSet = ps.getGeneratedKeys();
                    rse.extractData(resultSet);
                    /*
                     * if (logger.isDebugEnabled()) {
                     * logger.debug("generatedKeys : " + generatedKeys);
                     * }
                     */
                    return result;
                } finally {
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer) pss).cleanupParameters();
                    }
                }
            }
        });
    }

    private <T> int insert(String sql, List<String> columnReturn, PreparedStatementSetter pss,
            ResultSetExtractor<T> rse) throws DataAccessException {
        return insert(new InsertPreparedStatementCreator(sql, columnReturn), pss, rse);
    }

    public <T> int insert(String sql, List<String> columnReturn, ResultSetExtractor<T> rse, Object... params) {
        return insert(sql, columnReturn, newArgPreparedStatementSetter(params), rse);
    }

    private static class InsertPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

        private final String sql;
        // private final List<String> columnReturn;

        public InsertPreparedStatementCreator(String sql, List<String> columnReturn) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
            // this.columnReturn = columnReturn;
        }

        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            /*
             * if(null==columnReturn||columnReturn.isEmpty()){
             * return con.prepareStatement(this.sql, Statement.RETURN_GENERATED_KEYS);
             * }
             */
            return con.prepareStatement(this.sql, Statement.RETURN_GENERATED_KEYS);
            // return con.prepareStatement(this.sql, columnReturn.toArray(new String[0]));
        }

        public String getSql() {
            return this.sql;
        }
    }

    private int fetchSizeBigData = 0;

    public int getFetchSizeBigData() {
        return fetchSizeBigData;
    }

    public void setFetchSizeBigData(int fetchSizeBigData) {
        this.fetchSizeBigData = fetchSizeBigData;
    }

    public <T> T queryBigData(String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException {
        return queryBigData(sql, newArgPreparedStatementSetter(args), rse);
    }

    public <T> T queryBigData(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse)
            throws DataAccessException {
        int fetchSize = getFetchSizeBigData();
        return query(new BigDataPreparedStatementCreator(sql, fetchSize), pss, rse);
    }

    private static class BigDataPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

        private final String sql;
        private final int fetchSize;

        public BigDataPreparedStatementCreator(String sql, int fetchSize) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
            this.fetchSize = fetchSize;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setFetchSize(this.fetchSize);
            ps.setFetchDirection(ResultSet.FETCH_REVERSE);
            return ps;
        }

        @Override
        public String getSql() {
            return this.sql;
        }
    }
}
