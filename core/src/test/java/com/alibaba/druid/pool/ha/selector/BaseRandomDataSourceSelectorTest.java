package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.pool.ha.MockDataSource;
import org.junit.After;
import org.junit.Before;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class BaseRandomDataSourceSelectorTest {
    protected Map<String, DataSource> dataSourceMap;
    protected HighAvailableDataSource dataSource;

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
        dataSource.destroy();
        dataSource = null;
    }
}
