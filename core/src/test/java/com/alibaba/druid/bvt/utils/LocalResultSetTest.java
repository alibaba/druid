package com.alibaba.druid.bvt.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


import com.alibaba.druid.util.jdbc.LocalResultSet;

public class LocalResultSetTest extends TestCase {
    public void test_0() throws Exception {
        LocalResultSet rs = new LocalResultSet(null);
        rs.getRows().add(new Object[1]);
        rs.getRows().add(new Object[1]);
        assertTrue(rs.next());
        assertTrue(rs.next());
        assertFalse(rs.next());
        assertTrue(rs.previous());
        assertFalse(rs.previous());
        rs.getInt(1);
        assertTrue(rs.wasNull());
        rs.updateObject(1, 2);
        assertEquals(2, rs.getInt(1));
        assertFalse(rs.wasNull());
        rs.close();
    }
}
