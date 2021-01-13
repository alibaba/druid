package com.alibaba.druid.pool.ha.selector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class RandomDataSourceSelectorSingleDataSourceTest {
    private final static Log LOG = LogFactory
            .getLog(RandomDataSourceSelectorSingleDataSourceTest.class);

    private HighAvailableDataSource highAvailableDataSource;

    @Before
    public void setUp() throws Exception {
        highAvailableDataSource = new HighAvailableDataSource();
        String file = "/com/alibaba/druid/pool/ha/ha-with-prefix-datasource.properties";
        highAvailableDataSource.setDataSourceFile(file);
        highAvailableDataSource.setPropertyPrefix("prefix3");
        highAvailableDataSource.setMaxActive(2);
        highAvailableDataSource.setMaxWait(500);
        highAvailableDataSource.setInitialSize(2);
        initSelector(highAvailableDataSource);
        highAvailableDataSource.init();
    }

    @After
    public void tearDown() {
        highAvailableDataSource.destroy();
        highAvailableDataSource = null;
    }

    @Test
    public void testGetWhileBlacklistIsEmpty() {
        RandomDataSourceSelector selector = (RandomDataSourceSelector) highAvailableDataSource.getDataSourceSelector();
        DruidDataSource dataSource = (DruidDataSource) highAvailableDataSource.getAvailableDataSourceMap()
                .get("prefix3.foo");
        assertNotNull(selector.get());
        assertFalse(dataSource.isTestOnReturn());
    }

    @Test
    public void testGetWhileBlacklisted() throws Exception {
        RandomDataSourceSelector selector = (RandomDataSourceSelector) highAvailableDataSource.getDataSourceSelector();
        DruidDataSource dataSource = (DruidDataSource) highAvailableDataSource.getAvailableDataSourceMap()
                .get("prefix3.foo");
        dataSource.setValidationQuery("select xxx from yyy");

        Thread.sleep(10 * 1000);
        assertTrue(dataSource.isTestOnReturn());
        for (int i = 0; i < 5; i++) {
            assertNotNull(selector.get());
            Thread.sleep(1000);
        }
    }

    @Test
    public void testGetAfterRecovering() throws Exception {
        RandomDataSourceSelector selector = (RandomDataSourceSelector) highAvailableDataSource.getDataSourceSelector();
        DruidDataSource dataSource = (DruidDataSource) highAvailableDataSource.getAvailableDataSourceMap()
                .get("prefix3.foo");
        dataSource.setValidationQuery("select xxx from yyy");

        Thread.sleep(10 * 1000);
        assertTrue(dataSource.isTestOnReturn());
        assertNotNull(selector.get());
        Thread.sleep(10 * 1000);
        assertTrue(dataSource.getPoolingCount() == 0);

        dataSource.setValidationQuery("values current_timestamp");
        Thread.sleep(10 * 1000);
        assertFalse(dataSource.isTestOnReturn());
        assertNotNull(selector.get());
        assertTrue(dataSource.getPoolingCount() > 0);
    }

    private RandomDataSourceSelector initSelector(HighAvailableDataSource dataSource) {
        RandomDataSourceSelector selector = new RandomDataSourceSelector(dataSource);
        RandomDataSourceValidateThread validateThread = new RandomDataSourceValidateThread(selector);
        RandomDataSourceRecoverThread recoverThread = new RandomDataSourceRecoverThread(selector);
        validateThread.setCheckingIntervalSeconds(3);
        recoverThread.setRecoverIntervalSeconds(3);
        selector.setValidateThread(validateThread);
        selector.setRecoverThread(recoverThread);
        selector.init();
        dataSource.setDataSourceSelector(selector);
        return selector;
    }
}
