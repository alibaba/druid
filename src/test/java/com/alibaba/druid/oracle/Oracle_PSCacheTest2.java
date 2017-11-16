package com.alibaba.druid.oracle;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.benckmark.proxy.BenchmarkExecutor;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.OracleUtils;

import java.sql.*;

/**
 * Created by wenshao on 23/07/2017.
 */
public class Oracle_PSCacheTest2 extends DbTestCase {
    public Oracle_PSCacheTest2() {
        super("pool_config/oracle_db_sonar.properties");
    }

    Connection connDDL;

    protected void setUp() throws Exception {
        connDDL = DriverManager.getConnection("jdbc:oracle:thin:@47.94.57.164:1521:prod11g", "ecs_user", "adam123456");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(connDDL);
    }

    public void test_oracle() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@47.94.57.164:1521:prod11g", "ecs_user", "adam123456");

        PreparedStatement pstmt = null;
        String sql = "select * from t1 where f0 = ?";

        pstmt = conn.prepareStatement(sql);
        executeQuery(pstmt);

        OracleUtils.enterImplicitCache(pstmt);
        OracleUtils.exitImplicitCacheToActive(pstmt);
        executeQuery(pstmt);

        addColumn();

        pstmt.close();
        pstmt = conn.prepareStatement(sql);

        executeQuery(pstmt);
        dropColumn();

        JdbcUtils.close(conn);

    }

    private void executeQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = null;
        try {
            pstmt.clearParameters();
            pstmt.setString(1, "3");
            rs = pstmt.executeQuery();
            JdbcUtils.printResultSet(rs);
        } finally {
            JdbcUtils.close(rs);
        }
    }

    void addColumn() throws Exception {
        Statement stmt = null;
        stmt = connDDL.createStatement();
        stmt.execute("alter table t1 add (f1 varchar2(20))");
        stmt.close();
    }

    void dropColumn() throws Exception {
        Statement stmt = null;
        stmt = connDDL.createStatement();
        stmt.execute("alter table t1 drop column f1");
        stmt.close();
    }
}
