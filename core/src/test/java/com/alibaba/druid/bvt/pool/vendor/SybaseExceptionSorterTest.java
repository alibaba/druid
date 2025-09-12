package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.pool.vendor.SybaseExceptionSorter;

import java.sql.SQLException;

public class SybaseExceptionSorterTest extends PoolTestCase {
    public void test_false() throws Exception {
        SybaseExceptionSorter sorter = new SybaseExceptionSorter();
        assertFalse(sorter.isExceptionFatal(new SQLException()));
    }

    public void test_false_2() throws Exception {
        SybaseExceptionSorter sorter = new SybaseExceptionSorter();
        assertFalse(sorter.isExceptionFatal(new SQLException("xxx")));
    }

    public void test_true() throws Exception {
        SybaseExceptionSorter sorter = new SybaseExceptionSorter();
        assertTrue(sorter.isExceptionFatal(new SQLException("JZ0C0")));
    }

    public void test_true_1() throws Exception {
        SybaseExceptionSorter sorter = new SybaseExceptionSorter();
        assertTrue(sorter.isExceptionFatal(new SQLException("JZ0C1")));
    }
}
