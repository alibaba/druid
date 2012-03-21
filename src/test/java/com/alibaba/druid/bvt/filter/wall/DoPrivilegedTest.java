package com.alibaba.druid.bvt.filter.wall;

import java.security.PrivilegedAction;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallUtils;

public class DoPrivilegedTest extends TestCase {

    public void test_0() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("select @@version_compile_os"));
    }

    public void test_1() throws Exception {
        WallProvider.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                Assert.assertTrue(WallUtils.isValidateMySql("select @@version_compile_os"));
                return null;
            }
        });

    }
}
