package com.alibaba.druid.bvt.pool.vendor;

import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.vendor.InformixExceptionSorter;

public class InformixExceptionSorterTest extends PoolTestCase {

    public void test_informix() throws Exception {
        InformixExceptionSorter sorter = new InformixExceptionSorter();
        Assert.assertEquals(false, sorter.isExceptionFatal(new SQLException()));

        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -710)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79716)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79730)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79734)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79735)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79736)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79756)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79757)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79758)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79759)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79760)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79788)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79811)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79812)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79836)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79837)));
        Assert.assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79879)));
        Assert.assertEquals(false, sorter.isExceptionFatal(new SQLException("", "", 100)));
    }
}
