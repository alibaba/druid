package com.alibaba.druid.bvt.pool.exception;

import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.vendor.OracleExceptionSorter;

public class OracleExceptionSorter_userDefined extends PoolTestCase {

    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty("druid.oracle.fatalErrorCodes", "1,2,3,a,");
    }

    protected void tearDown() throws Exception {
        System.clearProperty("druid.oracle.fatalErrorCodes");

        super.tearDown();
    }

    public void test_userDefinedErrorCodes() throws Exception {
        OracleExceptionSorter sorter = new OracleExceptionSorter();
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
