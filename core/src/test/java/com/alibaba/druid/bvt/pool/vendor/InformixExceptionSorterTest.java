package com.alibaba.druid.bvt.pool.vendor;

import static org.junit.Assert.*;


import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;


import com.alibaba.druid.pool.vendor.InformixExceptionSorter;

public class InformixExceptionSorterTest extends PoolTestCase {
    public void test_informix() throws Exception {
        InformixExceptionSorter sorter = new InformixExceptionSorter();
        assertEquals(false, sorter.isExceptionFatal(new SQLException()));

        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -710)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79716)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79730)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79734)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79735)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79736)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79756)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79757)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79758)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79759)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79760)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79788)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79811)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79812)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79836)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79837)));
        assertEquals(true, sorter.isExceptionFatal(new SQLException("", "", -79879)));
        assertEquals(false, sorter.isExceptionFatal(new SQLException("", "", 100)));
    }
}
