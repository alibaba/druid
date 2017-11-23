package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAlterViewTest extends TestCase {
    
    public void test_if() throws Exception {
        String sql = "alter view view_name rename to new_view_name;";
        Assert.assertEquals("ALTER VIEW view_name RENAME TO new_view_name;", SQLUtils.formatOdps(sql));
    }
}
