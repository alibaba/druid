package com.alibaba.druid.bvt.filter;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.trace.TraceFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class TraceFilterTest extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;
    private TraceFilter filter;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("trace");
        
        filter = (TraceFilter) dataSource.getProxyFilters().get(0);
        JdbcStatContext statContext = new JdbcStatContext();
        statContext.setTraceEnable(true);
        JdbcStatManager.getInstance().setStatContext(statContext);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
        JdbcStatManager.getInstance().setStatContext(null);
    }

    public void test_exuecute() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("SELECT 1");
        stmt.close();
        conn.close();
    }
    
    public void test_exuecuteQuery() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1");
        rs.next();
        rs.close();
        stmt.close();
        conn.close();
    }
    
    public void test_preExuecuteQuery() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT ?");
        stmt.setInt(1, 123);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();
        conn.close();
    }
    
    public void test_set() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT ?");
        stmt.setInt(1, 123);
        stmt.setArray(1, null);
        stmt.setAsciiStream(1, null);
        stmt.setAsciiStream(1, null, 0);
        stmt.setAsciiStream(1, null, 0L);
        stmt.setBigDecimal(1, null);
        stmt.setBinaryStream(1, null);
        stmt.setBinaryStream(1, null, 0);
        stmt.setBinaryStream(1, null, 0L);
        stmt.setBlob(1, (Blob) null);
        stmt.setBlob(1, (InputStream) null);
        stmt.setBlob(1, (InputStream) null, 0);
        stmt.setBoolean(1, true);
        stmt.setByte(1, (byte) 12);
        stmt.setBytes(1, null);
        
        stmt.setCharacterStream(1, null);
        stmt.setCharacterStream(1, null, 0);
        stmt.setCharacterStream(1, null, 0L);
        
        stmt.setClob(1, (Clob) null);
        stmt.setClob(1, (Reader) null);
        stmt.setClob(1, (Reader) null, 1);
        
        stmt.setDate(1, null);
        stmt.setDate(1, null, null);
        
        stmt.setDouble(1, 1D);
        stmt.setFloat(1, 1F);
        stmt.setLong(1, 1L);
        
        stmt.setNCharacterStream(1, null);
        stmt.setNCharacterStream(1, null, 0);
        
        stmt.setNClob(1, (NClob) null);
        stmt.setNClob(1, (Reader) null);
        stmt.setNClob(1, (Reader) null, 1);
        
        stmt.setNull(1, Types.INTEGER);
        stmt.setNull(1, Types.INTEGER, "int");
        stmt.setObject(1, null);
        stmt.setObject(1, null, Types.INTEGER);
        stmt.setObject(1, null, Types.INTEGER, 2);
        
        stmt.setRef(1, null);
        stmt.setRowId(1, null);
        stmt.setShort(1, (short) 1);
        stmt.setSQLXML(1, null);
        stmt.setString(1, null);
        stmt.setTime(1, null);
        stmt.setTime(1, null, null);
        stmt.setTimestamp(1, null);
        stmt.setTimestamp(1, null, null);
        stmt.setUnicodeStream(1, null, 0);
        stmt.setURL(1, null);
        
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.previous();
        rs.next();
        rs.close();
        stmt.close();
        conn.close();
    }
}
