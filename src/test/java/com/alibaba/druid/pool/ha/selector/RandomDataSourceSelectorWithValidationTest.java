package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RandomDataSourceSelectorWithValidationTest {
    private HighAvailableDataSource highAvailableDataSource;

    @Before
    public void setUp() throws Exception {
        highAvailableDataSource = new HighAvailableDataSource();
        String file = "/com/alibaba/druid/pool/ha/ha-datasource.properties";
        highAvailableDataSource.setDataSourceFile(file);
        initSelector(highAvailableDataSource);
        highAvailableDataSource.init();
    }

    @After
    public void tearDown() {
        highAvailableDataSource.destroy();
        highAvailableDataSource = null;
    }

    @Test
    public void testOneDataSourceFailAndRecover() throws Exception {
        RandomDataSourceSelector selector = (RandomDataSourceSelector) highAvailableDataSource.getDataSourceSelector();

        DruidDataSource dataSource = (DruidDataSource) highAvailableDataSource.getAvailableDataSourceMap().get("foo");
        dataSource.setValidationQuery("select xxx from yyy");
        Thread.sleep(10 * 1000);
        assertTrue(dataSource.isTestOnReturn());
        for (int i = 0; i < 100; i++) {
            assertNotEquals(dataSource, selector.get());
        }
        dataSource.setValidationQuery(null);
        Thread.sleep(4 * 1000);
        assertFalse(dataSource.isTestOnReturn());
        int count = 0;
        for (int i = 0; i < 100; i++) {
            if (dataSource == selector.get()) {
                count++;
            }
        }
        assertTrue(count > 0);
    }

    @Test
    public void testAllDataSourceFail() throws Exception {
        RandomDataSourceSelector selector = (RandomDataSourceSelector) highAvailableDataSource.getDataSourceSelector();

        DruidDataSource foo = (DruidDataSource) highAvailableDataSource.getAvailableDataSourceMap().get("foo");
        DruidDataSource bar = (DruidDataSource) highAvailableDataSource.getAvailableDataSourceMap().get("bar");
        foo.setValidationQuery("select xxx from yyy");
        bar.setValidationQuery("select xxx from yyy");

        Thread.sleep(6 * 1000);
        assertTrue(foo.isTestOnReturn());
        assertTrue(bar.isTestOnReturn());

        int[] count = new int[2];
        for (int i = 0; i < 100; i++) {
            DataSource dataSource = selector.get();
            if (foo == dataSource) {
                count[0]++;
            } else if (bar == dataSource) {
                count[1]++;
            }
        }
        assertTrue(count[0] > 0);
        assertTrue(count[1] > 0);
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
