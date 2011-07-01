package com.alibaba.druid.pool;

import java.sql.Connection;
import java.util.TreeMap;

import javax.management.openmbean.TabularData;

import junit.framework.TestCase;

import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class TestOracle extends TestCase {

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
        conn.close();
        
        TabularData dataSourcesList = JdbcStatManager.getInstance().getDataSourceList();
        for (Object item : dataSourcesList.values()) {
            String text = JSON.toJSONString(item, SerializerFeature.UseISO8601DateFormat);
            System.out.println(JSON.toJSONString(JSON.parseObject(text, TreeMap.class), true));
        }
    }
}
