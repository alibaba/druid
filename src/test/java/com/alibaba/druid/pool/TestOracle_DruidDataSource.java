package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.TreeMap;

import junit.framework.TestCase;

import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class TestOracle_DruidDataSource extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        user = "alibaba";
        password = "ccbuauto";
    }

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setFilters("stat");
        dataSource.setExceptionSoter(OracleExceptionSorter.class.getName());

        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
        rs.next();
        rs.close();
        stmt.close();
        conn.close();
        
        for (Object item : JdbcStatManager.getInstance().getDataSourceList().values()) {
            String text = JSON.toJSONString(item, SerializerFeature.UseISO8601DateFormat);
            System.out.println(JSON.toJSONString(JSON.parseObject(text, TreeMap.class), true));
        }
        
        for (Object item : JdbcStatManager.getInstance().getSqlList().values()) {
            String text = JSON.toJSONString(item, SerializerFeature.UseISO8601DateFormat);
            System.out.println(JSON.toJSONString(JSON.parseObject(text, TreeMap.class), true));
        }
    }
}
