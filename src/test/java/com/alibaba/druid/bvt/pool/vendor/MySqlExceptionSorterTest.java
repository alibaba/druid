package com.alibaba.druid.bvt.pool.vendor;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;


public class MySqlExceptionSorterTest extends TestCase {
    public void test_true() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("", "", 1040)));
    }
    
    public void test_true_1() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("", "", 1042)));
    }
    
    public void test_true_2() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertTrue(sorter.isExceptionFatal(new SQLException("", "08xx", 0)));
    }
    
    public void test_false_2() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertFalse(sorter.isExceptionFatal(new SQLException("", null, 0)));
    }
    
    public void test_false() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertFalse(sorter.isExceptionFatal(new SQLException("", "", -10001)));
    }
    
    public void test_false_1() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertFalse(sorter.isExceptionFatal(new SQLException("", "", -8000)));
    }
    
    public void test_true_3() throws Exception {
        MySqlExceptionSorter sorter = new MySqlExceptionSorter();
        Assert.assertTrue(sorter.isExceptionFatal(new com.mysql.jdbc.CommunicationsException(null, 0, 0, null)));
    }
}
