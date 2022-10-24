package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.ha.MockDataSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class StickyRandomDataSourceSelectorTest extends BaseRandomDataSourceSelectorTest {
    @Test
    public void testGetSameDataSourceInFiveSeconds() throws InterruptedException {
        StickyRandomDataSourceSelector selector = new StickyRandomDataSourceSelector(dataSource);
        int lastHashCode = selector.get().hashCode();
        for (int i = 0; i < 4; i++) {
            assertEquals(lastHashCode, selector.get().hashCode());
            Thread.sleep(1000);
        }
    }

    @Test
    public void testGetDifferentDataSource() throws InterruptedException {
        StickyRandomDataSourceSelector selector = new StickyRandomDataSourceSelector(dataSource);
        selector.setExpireSeconds(1);
        int lastHashCode = selector.get().hashCode();
        boolean hasDifferentDataSource = false;
        for (int i = 0; i < 20; i++) {
            hasDifferentDataSource = selector.get().hashCode() != lastHashCode;
            Thread.sleep(500);
        }
        assertTrue(hasDifferentDataSource);
    }

    @Test
    public void testIgnoreForbiddenDataSource() throws InterruptedException {
        MockDataSource normal = new MockDataSource("Normal");
        MockDataSource forbidden = new MockDataSource("Forbidden");
        dataSourceMap.put("N", normal);
        dataSourceMap.put("F", forbidden);
        dataSource.setDataSourceMap(dataSourceMap);

        StickyRandomDataSourceSelector selector = new StickyRandomDataSourceSelector(dataSource);
        selector.setExpireSeconds(1);
        selector.getBlacklist().add(forbidden);

        for (int i = 0; i < 20; i++) {
            assertNotEquals(forbidden, selector.get());
            Thread.sleep(500);
        }
    }
}
