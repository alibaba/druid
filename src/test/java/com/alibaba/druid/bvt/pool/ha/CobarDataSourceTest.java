package com.alibaba.druid.bvt.pool.ha;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.ha.cobar.CobarConfigLoader;
import com.alibaba.druid.pool.ha.cobar.CobarDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CobarDataSourceTest extends TestCase {

    public void test_cobarDataSource() throws Exception {
        String url = "jdbc:cobar://127.0.0.1:8080/druid";

        final JSONObject config = new JSONObject();

        JSONArray cobarList = new JSONArray();

        JSONObject cobarA = new JSONObject();
        cobarA.put("ip", "mock");
        cobarA.put("port", 80);
        cobarA.put("schema", "cobarA");
        cobarA.put("weight", 1);

        cobarList.add(cobarA);

        JSONObject cobarB = new JSONObject();
        cobarB.put("ip", "mock");
        cobarB.put("port", 81);
        cobarB.put("schema", "cobarB");
        cobarB.put("weight", 1);

        cobarList.add(cobarB);

        String mockUrlA = "jdbc:mock://mock:80/cobarA";
        String mockUrlB = "jdbc:mock://mock:81/cobarB";

        config.put("cobarList", cobarList);

        final CobarDataSource dataSource = new CobarDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername("test");
        dataSource.setPassword("");
        dataSource.setMaxWait(1);
        dataSource.setConfigLoadPeriodMillis(3);

        CobarConfigLoader configLoader = new CobarConfigLoader(dataSource) {

            public void load() throws SQLException {
                responseMessage = config.toJSONString();

                handleResponseMessage();
            }

            protected String createJdbcUrl(String ip, int port, String schema) {
                return "jdbc:mock://" + ip + ":" + port + "/" + schema;
            }
        };
        dataSource.setConfigLoader(configLoader);

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1");
            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }

        cobarList.remove(cobarB);
        Assert.assertEquals(1, cobarList.size());
        Thread.sleep(dataSource.getConfigLoadPeriodMillis() * 2);
        for (int i = 0; i < 100; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            Assert.assertNotNull(mockConn);
            Assert.assertEquals(mockUrlA, mockConn.getUrl());

            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }
        
        cobarList.add(cobarB);
        Assert.assertEquals(2, cobarList.size());
        cobarList.remove(cobarA);
        Assert.assertEquals(1, cobarList.size());
        Thread.sleep(dataSource.getConfigLoadPeriodMillis() * 2);
        for (int i = 0; i < 100; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            Assert.assertNotNull(mockConn);
            Assert.assertEquals(mockUrlB, mockConn.getUrl());

            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }
    }

}
