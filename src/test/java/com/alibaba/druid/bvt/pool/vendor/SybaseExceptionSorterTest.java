package com.alibaba.druid.bvt.pool.vendor;

import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.vendor.SybaseExceptionSorter;

public class SybaseExceptionSorterTest extends PoolTestCase {

    public void test_false() throws Exception {
        SybaseExceptionSorter sorter = new SybaseExceptionSorter();
        Assert.assertFalse(sorter.isExceptionFatal(new SQLException()));
    }
    
    public void test_false_2() throws Exception {
        SybaseExceptionSorter sorter = new SybaseExceptionSorter();
        Assert.assertFalse(sorter.isExceptionFatal(new SQLException("xxx")));
    }
    
    public void test_true() throws Exception {
        SybaseExceptionSorter sorter = new SybaseExceptionSorter();
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("JZ0C0")));
    }
    
    public void test_true_1() throws Exception {
        SybaseExceptionSorter sorter = new SybaseExceptionSorter();
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("JZ0C1")));
    }
}
