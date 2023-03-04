package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.ha.MockDataSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
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

    @Test
    public void testCreateSelectorWithProperties() {
        String props = "druid.ha.random.checkingIntervalSeconds=60;";
        props += "druid.ha.random.recoveryIntervalSeconds=120;";
        props += "druid.ha.random.validationSleepSeconds=10;";
        props += "druid.ha.random.blacklistThreshold=5;";
        dataSource.setConnectionProperties(props);

        RandomDataSourceSelector selector = new RandomDataSourceSelector(dataSource);
        selector.init();
        assertEquals(60, selector.getCheckingIntervalSeconds());
        assertEquals(60, selector.getValidateThread().getCheckingIntervalSeconds());

        assertEquals(120, selector.getRecoveryIntervalSeconds());
        assertEquals(120, selector.getRecoverThread().getRecoverIntervalSeconds());

        assertEquals(10, selector.getValidationSleepSeconds());
        assertEquals(10, selector.getValidateThread().getValidationSleepSeconds());
        assertEquals(10, selector.getRecoverThread().getValidationSleepSeconds());

        assertEquals(5, selector.getBlacklistThreshold());
        assertEquals(5, selector.getValidateThread().getBlacklistThreshold());
    }
}
