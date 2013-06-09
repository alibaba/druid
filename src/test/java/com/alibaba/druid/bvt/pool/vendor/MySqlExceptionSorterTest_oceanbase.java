package com.alibaba.druid.bvt.pool.vendor;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;


public class MySqlExceptionSorterTest_oceanbase extends TestCase {
    public void test_true() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("", "", -9000)));
    }
    
    public void test_true_1() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("", "", -10000)));
    }
    
    public void test_false() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertFalse(sorter.isExceptionFatal(new SQLException("", "", -10001)));
    }
    
    public void test_false_1() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertFalse(sorter.isExceptionFatal(new SQLException("", "", -8000)));
    }
}
