package com.alibaba.druid.bvt.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.springframework.jdbc.support.nativejdbc.Jdbc4NativeJdbcExtractor;

import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.DruidJdbcExtractor;

public class Jdbc4ExtractorTest extends TestCase {

    public void test_spring() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        try {
            Jdbc4NativeJdbcExtractor extractor = new Jdbc4NativeJdbcExtractor();

            dataSource.setUrl("jdbc:mock:xx1");
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(true, extractor.getNativeConnection(conn) instanceof MockConnection);

            Statement stmt = conn.createStatement();
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(stmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(stmt) instanceof MockStatement);

            stmt.close();

            PreparedStatement preStmt = conn.prepareStatement("select 1");
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(preStmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(preStmt) instanceof MockPreparedStatement);
            Assert.assertEquals(true, extractor.getNativePreparedStatement(preStmt) instanceof MockPreparedStatement);
            ResultSet rs = preStmt.executeQuery();
            Assert.assertEquals(true, extractor.getNativeResultSet(rs) instanceof MockResultSet);
            rs.close();
            
            preStmt.close();

            PreparedStatement callStmt = conn.prepareCall("select 1");
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(callStmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(callStmt) instanceof MockCallableStatement);
            Assert.assertEquals(true, extractor.getNativePreparedStatement(callStmt) instanceof MockCallableStatement);
            callStmt.close();

            conn.close();
        } finally {
            dataSource.close();
        }
    }

    public void test_spring_filter() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        try {
            DruidJdbcExtractor extractor = new DruidJdbcExtractor();

            dataSource.setUrl("jdbc:mock:xx1");
            dataSource.setFilters("stat");
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(true, extractor.getNativeConnection(conn) instanceof MockConnection);

            Statement stmt = conn.createStatement();
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(stmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(stmt) instanceof MockStatement);

            stmt.close();

            PreparedStatement preStmt = conn.prepareStatement("select 1");
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(preStmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(preStmt) instanceof MockPreparedStatement);
            Assert.assertEquals(true, extractor.getNativePreparedStatement(preStmt) instanceof MockPreparedStatement);
            ResultSet rs = preStmt.executeQuery();
            Assert.assertEquals(true, extractor.getNativeResultSet(rs) instanceof MockResultSet);
            rs.close();
            
            preStmt.close();

            PreparedStatement callStmt = conn.prepareCall("select 1");
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(callStmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(callStmt) instanceof MockCallableStatement);
            Assert.assertEquals(true, extractor.getNativePreparedStatement(callStmt) instanceof MockCallableStatement);
            callStmt.close();

            conn.close();
        } finally {
            dataSource.close();
        }
    }
}
