package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.ha.MockDataSource;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RandomDataSourceSelectorTest extends BaseRandomDataSourceSelectorTest {
    @Test
    public void testRandomGet() {
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
}