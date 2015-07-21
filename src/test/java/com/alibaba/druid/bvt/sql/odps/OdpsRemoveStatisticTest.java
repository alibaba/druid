package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsRemoveStatisticTest extends TestCase {
    public void test_table_count() throws Exception {
        String sql = "remove statistic tbl_shop table_count;";
        Assert.assertEquals("REMOVE STATISTIC tbl_shop TABLE_COUNT;", SQLUtils.formatOdps(sql));
    }
    
    public void test_null_value() throws Exception {
        String sql = "remove statistic tbl_shop null_value shop_name;";
        Assert.assertEquals("REMOVE STATISTIC tbl_shop NULL_VALUE shop_name;", SQLUtils.formatOdps(sql));
    }
    
    public void test_column_sum() throws Exception {
        String sql = "remove statistic tbl_shop column_sum shop_name;";
        Assert.assertEquals("REMOVE STATISTIC tbl_shop COLUMN_SUM shop_name;", SQLUtils.formatOdps(sql));
    }
    
    public void test_column_max() throws Exception {
        String sql = "remove statistic tbl_shop column_max shop_name;";
        Assert.assertEquals("REMOVE STATISTIC tbl_shop COLUMN_MAX shop_name;", SQLUtils.formatOdps(sql));
    }
    
    public void test_column_min() throws Exception {
        String sql = "remove statistic tbl_shop column_min shop_name;";
        Assert.assertEquals("REMOVE STATISTIC tbl_shop COLUMN_MIN shop_name;", SQLUtils.formatOdps(sql));
    }
    
    public void test_expression_condition() throws Exception {
        String sql = "remove statistic tbl_shop expression_condition tbl_shop='hangzhou';";
        Assert.assertEquals("REMOVE STATISTIC tbl_shop EXPRESSION_CONDITION tbl_shop = 'hangzhou';", SQLUtils.formatOdps(sql));
    }
}
