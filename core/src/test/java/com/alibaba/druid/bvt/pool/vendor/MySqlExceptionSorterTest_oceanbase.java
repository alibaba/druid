package com.alibaba.druid.bvt.pool.vendor;

import static org.junit.Assert.*;


import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;


import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;


public class MySqlExceptionSorterTest_oceanbase extends PoolTestCase {
    public void test_true() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        SQLException exception = new SQLException("", "", -9000);
        assertTrue(sorter.isExceptionFatal(exception));
    }

    public void test_true_1() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        assertFalse(sorter.isExceptionFatal(new SQLException("", "", -10000)));
    }

    public void test_false() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        assertFalse(sorter.isExceptionFatal(new SQLException("", "", -10001)));
    }

    public void test_false_1() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        assertTrue(sorter.isExceptionFatal(new SQLException("", "", -8000)));
        assertFalse(sorter.isExceptionFatal(new SQLException("", "", -9100)));
    }
}
