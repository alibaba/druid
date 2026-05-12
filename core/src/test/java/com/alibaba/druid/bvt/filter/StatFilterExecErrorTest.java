package com.alibaba.druid.bvt.filter;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class StatFilterExecErrorTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.getProxyFilters().add(new FilterAdapter() {
            @Override
            public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                    throws SQLException {
                throw new SQLException();
            }

            @Override
            public ResultSetProxy preparedStatement_executeQuery(FilterChain chain, PreparedStatementProxy statement)
                    throws SQLException {
                throw new SQLException();
            }
        });

        dataSource.init();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    @Test
    public void test_stat() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        assertEquals(0, sqlStat.getReadStringLength());

        try {
            stmt.executeQuery();
        } catch (SQLException eror) {
        } finally {
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }

        assertEquals(1, sqlStat.getErrorCount());
        assertEquals(0, sqlStat.getRunningCount());

        sqlStat.reset();
    }
}
