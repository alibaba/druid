package com.alibaba.druid.bvt.proxy.filter;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLXML;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

public class ClobTest extends TestCase {

    public void test_clob() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setFilters("stat");
        dataSource.setUrl("jdbc:mock:");

        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT NULL");

        Assert.assertTrue(rs.next());
        
        {
            Clob x = rs.getClob(1);
            Assert.assertNull(x);
        }

        {
            NClob x = rs.getNClob(1);
            Assert.assertNull(x);
        }

        {
            Blob x = rs.getBlob(1);
            Assert.assertNull(x);
        }

        {
            SQLXML x = rs.getSQLXML(1);
            Assert.assertNull(x);
        }

        {
            String x = rs.getString(1);
            Assert.assertNull(x);
        }

        Assert.assertNull(rs.getRowId(1));
        Assert.assertNull(rs.getBigDecimal(1));
        Assert.assertNull(rs.getObject(1));

        rs.close();
        stmt.close();
        conn.close();
    }
}
