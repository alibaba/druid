package com.alibaba.druid.bvt.bug;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by wenshao on 14/08/2017.
 */
public class Issue1898 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "SELECT 0 bSomething";
        String formatted = com.alibaba.druid.sql.SQLUtils.format(sql, "mysql");
//        assertNotEquals("SELECT b'Something'", formatted);
        assertEquals("SELECT 0 AS bSomething", formatted);
    }
}
