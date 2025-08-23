package com.alibaba.druid.bvt.pool.exception;

import static org.junit.Assert.*;


import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;


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
        assertEquals(3, sorter.getFatalErrorCodes().size());
        assertTrue(sorter.getFatalErrorCodes().contains(1));
        assertTrue(sorter.getFatalErrorCodes().contains(2));
        assertTrue(sorter.getFatalErrorCodes().contains(3));

        assertTrue(sorter.isExceptionFatal(new SQLException("xx", "xx", 1)));
        assertTrue(sorter.isExceptionFatal(new SQLException("xx", "xx", 2)));
        assertTrue(sorter.isExceptionFatal(new SQLException("xx", "xx", 3)));
        assertFalse(sorter.isExceptionFatal(new SQLException("xx", "xx", 4)));
        assertTrue(sorter.isExceptionFatal(new SQLException("xx", "xx", 28)));
    }
}
