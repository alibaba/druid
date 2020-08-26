package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.pool.ha.MockDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class NamedDataSourceSelectorTest {
    private Map<String, DataSource> dataSourceMap;
    private HighAvailableDataSource dataSource;

    @Before
    public void setUp() {
        dataSourceMap = new HashMap<String, DataSource>();
        dataSourceMap.put("foo", new MockDataSource("foo"));
        dataSourceMap.put("bar", new MockDataSource("bar"));
        dataSource = new HighAvailableDataSource();
        dataSource.setDataSourceMap(dataSourceMap);
    }

    @After
    public void tearDown() {
        dataSourceMap = null;
        dataSource.destroy();
        dataSource = null;
    }

    @Test
    public void testEmptyMap() {
        dataSourceMap.clear();
        NamedDataSourceSelector selector = new NamedDataSourceSelector(null);
        assertNull(selector.get());
        selector = new NamedDataSourceSelector(dataSource);
        assertNull(selector.get());
    }

    @Test
    public void testOnlyOne() {
        dataSourceMap.remove("foo");
        NamedDataSourceSelector selector = new NamedDataSourceSelector(dataSource);
        for (int i = 0; i < 50; i++) {
            assertEquals("bar", ((MockDataSource)selector.get()).getName());
        }
    }

    @Test
    public void testGetByName() throws Exception {
        NamedDataSourceSelector selector = new NamedDataSourceSelector(dataSource);

        assertNull(selector.get());
        selector.setTarget("foo");
        assertEquals("foo", ((MockDataSource)selector.get()).getName());
        selector.resetDataSourceName();
        selector.setDefaultName("bar");
        assertEquals("bar", ((MockDataSource)selector.get()).getName());
    }
}