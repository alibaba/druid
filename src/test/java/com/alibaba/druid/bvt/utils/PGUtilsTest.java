package com.alibaba.druid.bvt.utils;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.util.PGUtils;

public class PGUtilsTest extends TestCase {

    public void test_error() throws Exception {
        new PGUtils();

        Exception error = null;
        try {
            PGUtils.createXAConnection(null);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
