package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class GroupingSetsTest extends TestCase {
    public void test_groupingSets() throws Exception {
        String sql = "SELECT brand, size, sum(sales) FROM items_sold GROUP BY GROUPING SETS ((brand), (size), ());";

        String result = SQLUtils.format(sql, (DbType) null);

        Assert.assertEquals("SELECT brand, size, sum(sales)"
                + "\nFROM items_sold"
                + "\nGROUP BY GROUPING SETS ((brand), (size), ());", result);
    }

    public void test_groupingSetsHasComma() throws Exception {
        String sql = "SELECT brand, size, sum(sales) FROM items_sold GROUP BY brand, size, GROUPING SETS ((brand), (size), ());";

        String result = SQLUtils.format(sql, (DbType) null);

        Assert.assertEquals("SELECT brand, size, sum(sales)\n" +
                "FROM items_sold\n" +
                "GROUP BY brand, size,\n" +
                "\tGROUPING SETS ((brand), (size), ());", result);
    }

    public void test_groupingSetsNoComma() throws Exception {
        String sql = "SELECT brand, size, sum(sales) FROM items_sold GROUP BY brand, size GROUPING SETS ((brand), (size), ());";

        String result = SQLUtils.format(sql, (DbType) null);

        Assert.assertEquals("SELECT brand, size, sum(sales)\n" +
                "FROM items_sold\n" +
                "GROUP BY brand, size\n" +
                "\tGROUPING SETS ((brand), (size), ());", result);
    }
}
