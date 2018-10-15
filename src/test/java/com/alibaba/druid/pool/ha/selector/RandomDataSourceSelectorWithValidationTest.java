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
        highAvailableDataSource.init();
    }

    @After
    public void tearDown() {
        highAvailableDataSource = null;
    }

    @Test
    public void testOneDataSourceFailAndRecover() throws Exception {
        ((RandomDataSourceSelector) highAvailableDataSource.getDataSourceSelector()).getValidateThread().setSleepSeconds(3);
        ((RandomDataSourceSelector) highAvailableDataSource.getDataSourceSelector()).getRecoverThread().setSleepSeconds(3);

        Thread.sleep(30 * 1000);

        DruidDataSource dataSource = (DruidDataSource) highAvailableDataSource.getDataSourceMap().get("foo");
        dataSource.setValidationQuery("select xxx from yyy");
        Thread.sleep(10 * 1000);
        assertTrue(dataSource.isTestOnReturn());
        for (int i = 0; i < 100; i++) {
            assertNotEquals(dataSource, highAvailableDataSource.getDataSourceSelector().get());
        }

        dataSource.setValidationQuery(null);
        Thread.sleep(4 * 1000);
        assertFalse(dataSource.isTestOnReturn());
        int count = 0;
        for (int i = 0; i < 100; i++) {
            if (dataSource == highAvailableDataSource.getDataSourceSelector().get()) {
                count++;
            }
        }
        assertTrue(count > 0);
    }

    @Test
    public void testAllDataSourceFail() throws Exception {
        ((RandomDataSourceSelector) highAvailableDataSource.getDataSourceSelector()).getValidateThread().setSleepSeconds(3);
        ((RandomDataSourceSelector) highAvailableDataSource.getDataSourceSelector()).getRecoverThread().setSleepSeconds(3);

        Thread.sleep(30 * 1000);

        DruidDataSource foo = (DruidDataSource) highAvailableDataSource.getDataSourceMap().get("foo");
        DruidDataSource bar = (DruidDataSource) highAvailableDataSource.getDataSourceMap().get("bar");
        foo.setValidationQuery("select xxx from yyy");
        bar.setValidationQuery("select xxx from yyy");

        Thread.sleep(10 * 1000);
        assertTrue(foo.isTestOnReturn());
        assertTrue(bar.isTestOnReturn());

        int[] count = new int[2];
        for (int i = 0; i < 100; i++) {
            DataSource dataSource = highAvailableDataSource.getDataSourceSelector().get();
            if (foo == dataSource) {
                count[0]++;
            } else if (bar == dataSource) {
                count[1]++;
            }
        }
        assertTrue(count[0] > 0);
        assertTrue(count[1] > 0);
    }
}
