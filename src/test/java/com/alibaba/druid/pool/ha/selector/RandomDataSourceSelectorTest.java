package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.pool.ha.MockDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class RandomDataSourceSelectorTest {
    private Map<String, DataSource> dataSourceMap;
    private HighAvailableDataSource dataSource;

    @Before
    public void setUp() {
        dataSourceMap = new HashMap<String, DataSource>();
        for (int i = 0; i < 10; i++) {
            dataSourceMap.put(Integer.toString(i), new MockDataSource(Integer.toString(i)));
        }
        dataSource = new HighAvailableDataSource();
        dataSource.setDataSourceMap(dataSourceMap);
    }

    @After
    public void tearDown() {
        dataSourceMap = null;
        dataSource = null;
    }

    @Test
    public void testRandomGet() throws Exception {
        RandomDataSourceSelector selector = new RandomDataSourceSelector(dataSource);
        int[] count = new int[10];

        for (int i = 0; i < 100; i++) {
            MockDataSource dataSource = (MockDataSource) selector.get();
            count[Integer.parseInt(dataSource.getName())]++;
        }

        for (int i : count) {
            assertTrue(i > 0);
        }
    }

    @Test
    public void testOneDataSourceFail() {

    }

}