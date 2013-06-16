package com.alibaba.druid.bvt.utils;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.util.jdbc.LocalResultSet;

public class LocalResultSetTest extends TestCase {

    public void test_0() throws Exception {
        LocalResultSet rs = new LocalResultSet(null);
        rs.getRows().add(new Object[1]);
        rs.getRows().add(new Object[1]);
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.next());
        Assert.assertFalse(rs.next());
        Assert.assertTrue(rs.previous());
        Assert.assertFalse(rs.previous());
        rs.getInt(1);
        Assert.assertTrue(rs.wasNull());
        rs.updateObject(1, 2);
        Assert.assertEquals(2, rs.getInt(1));
        Assert.assertFalse(rs.wasNull());
        rs.close();
    }
}
