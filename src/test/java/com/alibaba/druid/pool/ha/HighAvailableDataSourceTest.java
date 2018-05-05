package com.alibaba.druid.pool.ha;

import com.alibaba.druid.mock.MockConnection;
import org.junit.Test;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HighAvailableDataSourceTest {
    @Test
    public void testByNameGet() throws Exception {
        HighAvailableDataSource haDataSource = new HighAvailableDataSource();
        Map<String, DataSource> map = new HashMap<String, DataSource>();
        map.put("foo", new MockDataSource("foo"));
        map.put("bar", new MockDataSource("bar"));
        haDataSource.setDataSourceMap(map);
        haDataSource.setSelector("byname");
        haDataSource.init();
        haDataSource.setTargetDataSource("foo");
        assertNotNull(haDataSource.getConnection());
    }

    @Test
    public void testRandomGet() throws Exception {
        HighAvailableDataSource haDataSource = new HighAvailableDataSource();
        Map<String, DataSource> map = new HashMap<String, DataSource>();
        map.put("foo", new MockDataSource("foo"));
        map.put("bar", new MockDataSource("bar"));
        int[] count = new int[2];

        haDataSource.setDataSourceMap(map);
        for (int i = 0; i < 100; i++) {
            String name = ((MockConnection)haDataSource.getConnection()).getUrl();
            if (name.equalsIgnoreCase("foo")) {
                count[0]++;
            } else if (name.equalsIgnoreCase("bar")) {
                count[1]++;
            }
        }
        assertTrue(count[0] > 0);
        assertTrue(count[1] > 0);
    }
}