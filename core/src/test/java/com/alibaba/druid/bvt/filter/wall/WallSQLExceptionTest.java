package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallSQLException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallSQLExceptionTest {
    @Test
    public void test_wall() throws Exception {
        WallSQLException ex = new WallSQLException("", new RuntimeException());
        assertEquals("", ex.getMessage());
        assertNotNull(ex.getCause());
    }
}
