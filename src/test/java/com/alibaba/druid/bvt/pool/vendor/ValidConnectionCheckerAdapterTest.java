package com.alibaba.druid.bvt.pool.vendor;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;


public class ValidConnectionCheckerAdapterTest extends TestCase {
    public void test_adapter() throws Exception {
        ValidConnectionCheckerAdapter adapter = new ValidConnectionCheckerAdapter();
        adapter.configFromProperties(System.getProperties());
        Assert.assertTrue(adapter.isValidConnection(null, null, 10));
    }
}
