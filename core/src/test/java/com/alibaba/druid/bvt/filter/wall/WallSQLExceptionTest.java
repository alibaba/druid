package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import junit.framework.TestCase;


import com.alibaba.druid.wall.WallSQLException;


public class WallSQLExceptionTest extends TestCase {
    public void test_wall() throws Exception {
        WallSQLException ex = new WallSQLException("", new RuntimeException());
        assertEquals("", ex.getMessage());
        assertNotNull(ex.getCause());
    }
}
