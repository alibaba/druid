package com.alibaba.druid.filter.url;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import org.apache.derby.impl.jdbc.EmbedConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:com/alibaba/druid/filter/url/dataSource.xml")
public class JdbcUrlHostAndPortReplaceFilterTest {
    @Autowired
    private DruidDataSource dataSource;

    @Test
    public void testConnectWithDifferentPlaceholderValue() throws Exception {
        int[] count = new int[] { 0,0 };
        List<Connection> connectionList = new ArrayList<Connection>();

        for (int i = 0; i < 10; i++) {
            DruidPooledConnection conn = dataSource.getConnection();
            EmbedConnection embed = ((EmbedConnection)((ConnectionProxyImpl)conn.getConnection()).getRawObject());
            String connName = embed.toString();

            if (connName.contains("foo")) {
                count[0]++;
            } else if (connName.contains("bar")) {
                count[1]++;
            } else {
                fail("NOT foo or bar");
            }
            connectionList.add(conn);
        }
        assertTrue(count[0] > 0 && count[1] >0);
        for (Connection c : connectionList) {
            c.close();
        }
    }

}