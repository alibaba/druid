package com.alibaba.druid.bvt.filter;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import com.alibaba.druid.proxy.jdbc.PreparedStatementProxyImpl;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.mock.MockNClob;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.util.JdbcUtils;

public class FilterChainImplTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat,log4j,wall,encoding");
        dataSource.getProxyFilters().add(new FilterAdapter() {
        });
        dataSource.setDbType("mysql");

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_size() {
        Assert.assertEquals(dataSource.getProxyFilters().size(), new FilterChainImpl(dataSource).getFilterSize());
    }

    public void test_unwrap() throws Exception {
        Assert.assertNull(new FilterChainImpl(dataSource).unwrap(null, null));
    }

    public void test_unwrap_5() throws Exception {
        Assert.assertNull(new FilterChainImpl(dataSource).wrap((ConnectionProxy) dataSource.getConnection().getConnection(),
                                                               (Clob) null));
    }

    public void test_unwrap_6() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertTrue(new FilterChainImpl(dataSource).wrap((ConnectionProxy) dataSource.getConnection().getConnection(),
                                                               new MockNClob()) instanceof NClob);
        conn.close();
    }

    public void test_unwrap_8() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertTrue(new FilterChainImpl(dataSource).wrap((ConnectionProxy) dataSource.getConnection().getConnection(),
                                                               (Clob) new MockNClob()) instanceof NClob);
        conn.close();
    }

    public void test_unwrap_7() throws Exception {
        Assert.assertNull(new FilterChainImpl(dataSource).wrap((ConnectionProxy) dataSource.getConnection().getConnection(),
                                                               (NClob) null));
    }

    public void test_unwrap_9() throws Exception {
        Assert.assertNull(new FilterChainImpl(dataSource).wrap((StatementProxy) null, (NClob) null));
    }

    public void test_getUnicodeStream() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getUnicodeStream(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getUnicodeStream_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getUnicodeStream("1"));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getRef() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getRef(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getRef_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getRef("1"));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getArray() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getArray(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getArray_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getArray("1"));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getURL() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getURL(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getURL_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getURL("1"));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getRowId() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getRowId(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getRowId_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getRowId("1"));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getNClob() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getNClob(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getNClob_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getNClob("1"));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getSQLXML() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getSQLXML(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getSQLXML_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getSQLXML("1"));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getNString() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getNString(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getNString_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getNString("1"));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getNCharacterStream() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getNCharacterStream(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getNCharacterStream_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getNCharacterStream("1"));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getObject() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getObject(1));

        rs.close();
        stmt.close();
        conn.close();

    }

    public void test_getObject_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        Assert.assertNull(rs.getObject("1"));

        rs.close();
        stmt.close();
        conn.close();

    }
}
