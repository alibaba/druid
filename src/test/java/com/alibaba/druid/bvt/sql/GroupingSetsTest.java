package com.alibaba.druid.bvt.sql;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class GroupingSetsTest extends TestCase {
    public void test_groupingSets() throws Exception {
        String sql = "SELECT brand, size, sum(sales) FROM items_sold GROUP BY GROUPING SETS ((brand), (size), ());";
        
        String result = SQLUtils.format(sql, null);
        
        Assert.assertEquals("SELECT brand, size, SUM(sales)"
                + "\nFROM items_sold"
                + "\nGROUP BY GROUPING SETS ((brand), (size), ());", result);
    }
}
