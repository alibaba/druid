package com.alibaba.druid.filter.url;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for HostAndPortValidatorThread
 *
 * @author DigitalSonic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:com/alibaba/druid/filter/url/dataSource.xml")
public class HostAndPortValidatorThreadTest {
    @Autowired
    private DruidDataSource dataSource;

    @Test
    public void testValidateFail() throws Exception {
        assertEquals(0, HostAndPortHolder.getInstance().getBlacklistSize());
        dataSource.setValidationQuery("select xxx from foo");
        Thread.sleep(20 * 1000);
        assertTrue(HostAndPortHolder.getInstance().getBlacklistSize() > 0);
        assertTrue(dataSource.isTestOnBorrow());
        assertTrue(dataSource.isTestOnReturn());

        dataSource.setValidationQuery(null);
        Thread.sleep(20 * 1000);
        assertEquals(0, HostAndPortHolder.getInstance().getBlacklistSize());
        assertFalse(dataSource.isTestOnBorrow());
        assertFalse(dataSource.isTestOnReturn());
    }
}