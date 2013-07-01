package com.alibaba.druid.bvt.pool;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;

public class PreparedStatementProxyImplTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setInitialSize(1);
        dataSource.setFilters("log4j");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_setObject() throws Exception {
        String sql = "insert t values(?, ?, ?, ?, ?,  ?, ?, ?, ?, ?)";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setObject(1, (byte) 1);
        stmt.setObject(2, (short) 1);
        stmt.setObject(3, (int) 1);
        stmt.setObject(4, (long) 1);
        stmt.setObject(5, (float) 1);

        stmt.setObject(6, (double) 1);
        stmt.setObject(7, new BigDecimal(1));
        stmt.setObject(8, true);
        stmt.setObject(9, "xxx");
        stmt.setObject(10, new java.sql.Date(System.currentTimeMillis()));

        stmt.setObject(11, new java.util.Date(System.currentTimeMillis()));
        stmt.setObject(12, new java.sql.Timestamp(System.currentTimeMillis()));
        stmt.setObject(13, new java.sql.Time(System.currentTimeMillis()));

        stmt.execute();

        PreparedStatementProxy stmtProxy = stmt.unwrap(PreparedStatementProxy.class);
        Assert.assertNotNull(stmtProxy);

        Assert.assertEquals(Types.TINYINT, stmtProxy.getParameter(0).getSqlType());
        Assert.assertEquals(Types.SMALLINT, stmtProxy.getParameter(1).getSqlType());
        Assert.assertEquals(Types.INTEGER, stmtProxy.getParameter(2).getSqlType());
        Assert.assertEquals(Types.BIGINT, stmtProxy.getParameter(3).getSqlType());
        Assert.assertEquals(Types.FLOAT, stmtProxy.getParameter(4).getSqlType());

        Assert.assertEquals(Types.DOUBLE, stmtProxy.getParameter(5).getSqlType());
        Assert.assertEquals(Types.DECIMAL, stmtProxy.getParameter(6).getSqlType());
        Assert.assertEquals(Types.BOOLEAN, stmtProxy.getParameter(7).getSqlType());
        Assert.assertEquals(Types.VARCHAR, stmtProxy.getParameter(8).getSqlType());
        Assert.assertEquals(Types.DATE, stmtProxy.getParameter(9).getSqlType());

        Assert.assertEquals(Types.DATE, stmtProxy.getParameter(10).getSqlType());
        Assert.assertEquals(Types.TIMESTAMP, stmtProxy.getParameter(11).getSqlType());
        Assert.assertEquals(Types.TIME, stmtProxy.getParameter(12).getSqlType());

        stmt.close();

        conn.close();
    }
}
