package com.alibaba.druid.bvt.pool.exception;

import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.test.util.OracleMockDriver;
import com.alibaba.druid.util.JdbcUtils;

public class OracleExceptionSorter_userDefined_1 extends PoolTestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());

        dataSource = new DruidDataSource();

        dataSource.setExceptionSorter(new OracleExceptionSorter());

        dataSource.setDriver(new OracleMockDriver());
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    @Override
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);

        super.tearDown();
    }

    public void test_userDefinedErrorCodes() throws Exception {
        dataSource.init();
        
        dataSource.setConnectionProperties("druid.oracle.fatalErrorCodes=1,2,3,a,");
        
        OracleExceptionSorter sorter = (OracleExceptionSorter) dataSource.getExceptionSorter();
        Assert.assertEquals(3, sorter.getFatalErrorCodes().size());
        Assert.assertTrue(sorter.getFatalErrorCodes().contains(1));
        Assert.assertTrue(sorter.getFatalErrorCodes().contains(2));
        Assert.assertTrue(sorter.getFatalErrorCodes().contains(3));

        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("xx", "xx", 1)));
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("xx", "xx", 2)));
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("xx", "xx", 3)));
        Assert.assertFalse(sorter.isExceptionFatal(new SQLException("xx", "xx", 4)));
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("xx", "xx", 28)));
    }
}
