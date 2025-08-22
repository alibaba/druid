package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import static org.junit.*;

import com.alibaba.druid.wall.WallSQLException;


public class WallSQLExceptionTest extends TestCase {
    public void test_wall() throws Exception {
        WallSQLException ex = new WallSQLException("", new RuntimeException());
        assertEquals("", ex.getMessage());
        assertNotNull(ex.getCause());
    }
}
