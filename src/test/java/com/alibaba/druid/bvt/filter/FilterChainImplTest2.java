package com.alibaba.druid.bvt.filter;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Collections;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class FilterChainImplTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat,log4j,wall,encoding");
        dataSource.getProxyFilters().add(new FilterAdapter() {} );
        dataSource.setDbType("mysql");

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }
    
    public void test_getURL() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getURL(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getURL_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getURL("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getString() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getString(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getString_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getString("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getBoolean() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertFalse(stmt.getBoolean(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getBoolean_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertFalse(stmt.getBoolean("1"));
        
        stmt.close();
        conn.close();
    }
    

    public void test_getByte() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertEquals(0, stmt.getByte(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getByte_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertEquals(0, stmt.getByte("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getShort() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertEquals(0, stmt.getShort(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getShort_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertEquals(0, stmt.getShort("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getInt() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertEquals(0, stmt.getInt(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getInt_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertEquals(0, stmt.getInt("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getLong() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertEquals(0, stmt.getLong(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getLong_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertEquals(0, stmt.getLong("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getFloat() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertTrue(0F == stmt.getFloat(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getFloat_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertTrue(0F == stmt.getFloat("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getDouble() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertTrue(0D == stmt.getDouble(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getDouble_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertTrue(0D == stmt.getDouble("1"));
        
        stmt.close();
        conn.close();
    }
    
    
    public void test_getBytes() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getBytes(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getBytes_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getBytes("1"));
        
        stmt.close();
        conn.close();
    }
    
    
    public void test_getDate() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getDate(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getDate_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getDate("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getTime() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getTime(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getTime_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getTime("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getTimestamp() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getTimestamp(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getTimestamp_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getTimestamp("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getBigDecimal() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getBigDecimal(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getBigDecimal_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getBigDecimal("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getRef() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getRef(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getRef_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getRef("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getBlob() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getBlob(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getBlob_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getBlob("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getArray() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getArray(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getArray_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getArray("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getDate_2() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getDate(1, null));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getDate_3() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getDate("1", null));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getTime_2() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getTime(1, null));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getTime_3() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getTime("1", null));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getTimestamp_2() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getTimestamp(1, null));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getTimestamp_3() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getTimestamp("1", null));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getRowId() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getRowId(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getRowId_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getRowId("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getNClob() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getNClob(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getNClob_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getNClob("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getSQLXML() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getSQLXML(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getSQLXML_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getSQLXML("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getNString() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getNString(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getNString_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getNString("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getNCharacterStream() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getNCharacterStream(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getNCharacterStream_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getNCharacterStream("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getCharacterStream() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getCharacterStream(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getCharacterStream_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getCharacterStream("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getObject() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getObject(1));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getObject_1() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getObject("1"));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getObject_2() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getObject(1, Collections.<String, Class<?>>emptyMap()));
        
        stmt.close();
        conn.close();
    }
    
    public void test_getObject_3() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select ?");
        stmt.registerOutParameter(1, Types.VARCHAR);
        
        Assert.assertNull(stmt.getObject("1", Collections.<String, Class<?>>emptyMap()));
        
        stmt.close();
        conn.close();
    }
}
