package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallSQLException;
import junit.framework.TestCase;

public class WallSQLExceptionTest extends TestCase {
    public void test_wall() throws Exception {
        WallSQLException ex = new WallSQLException("", new RuntimeException());
        assertEquals("", ex.getMessage());
        assertNotNull(ex.getCause());
    }
}
