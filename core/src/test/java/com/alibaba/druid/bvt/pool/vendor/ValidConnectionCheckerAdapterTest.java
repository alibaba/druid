package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import static org.junit.*;

import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;


public class ValidConnectionCheckerAdapterTest extends PoolTestCase {
    public void test_adapter() throws Exception {
        ValidConnectionCheckerAdapter adapter = new ValidConnectionCheckerAdapter();
        adapter.configFromProperties(System.getProperties());
        assertTrue(adapter.isValidConnection(null, null, 10));
    }
}
