package com.vonchange.jdbc.template;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

    public <T> int[] batchInsert(String sql, List<Object[]> batchArgs,List<String> columnReturn,final ResultSetExtractor<T> rse) throws DataAccessException {
        return batchInsert(sql, batchArgs, new int[0],columnReturn,rse);
    }
    public <T> int[] batchInsert(String sql, List<Object[]> batchArgs, final int[] argTypes,List<String> columnReturn,final ResultSetExtractor<T> rse) throws DataAccessException {
        if (batchArgs.isEmpty()) {
            return new int[0];
        }

        return batchInsert(
                new InsertPreparedStatementCreator(sql,columnReturn),
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Object[] values = batchArgs.get(i);
                        int colIndex = 0;
                        for (Object value : values) {
                            colIndex++;
                            if (value instanceof SqlParameterValue) {
                                SqlParameterValue paramValue = (SqlParameterValue) value;
                                StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
                            }
                            else {
                                int colType;
                                if (argTypes.length < colIndex) {
                                    colType = SqlTypeValue.TYPE_UNKNOWN;
                                }
                                else {
                                    colType = argTypes[colIndex - 1];
                                }
                                StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
                            }
                        }
                    }
                    @Override
                    public int getBatchSize() {
                        return batchArgs.size();
                    }
                },rse);
    }

    public <T> int[] batchInsert(PreparedStatementCreator psc,  final BatchPreparedStatementSetter pss,ResultSetExtractor<T> rse) throws DataAccessException {

        int[] result = execute(psc, (PreparedStatementCallback<int[]>) ps -> {
            try {
                int batchSize = pss.getBatchSize();
                InterruptibleBatchPreparedStatementSetter ipss =
                        (pss instanceof InterruptibleBatchPreparedStatementSetter ?
                                (InterruptibleBatchPreparedStatementSetter) pss : null);
                if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
                    for (int i = 0; i < batchSize; i++) {
                        pss.setValues(ps, i);
                        if (ipss != null && ipss.isBatchExhausted(i)) {
                            break;
                        }
                        ps.addBatch();
                    }
                    int[] num= ps.executeBatch();
                    ResultSet resultSet = ps.getGeneratedKeys();
                    rse.extractData(resultSet);
                    return num;
                }
                else {
                    List<Integer> rowsAffected = new ArrayList<>();
                    for (int i = 0; i < batchSize; i++) {
                        pss.setValues(ps, i);
                        if (ipss != null && ipss.isBatchExhausted(i)) {
                            break;
                        }
                        rowsAffected.add(ps.executeUpdate());
                    }
                    int[] rowsAffectedArray = new int[rowsAffected.size()];
                    for (int i = 0; i < rowsAffectedArray.length; i++) {
                        rowsAffectedArray[i] = rowsAffected.get(i);
                    }
                    return rowsAffectedArray;
                }
            }
            finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer) pss).cleanupParameters();
                }
            }
        });

        Assert.state(result != null, "No result array");
        return result;
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
