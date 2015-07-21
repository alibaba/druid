package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest5 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select *" //
                + "\nfrom t -- xxxx"//
                + "\nwhere id > 0;"
                ;//
        Assert.assertEquals("SELECT *" //
                + "\nFROM t -- xxxx" //
                + "\nWHERE id > 0;", SQLUtils.formatOdps(sql));
    }

    public void test_column_comment_as() throws Exception {
        String sql = "select *" //
                + "\nfrom xxxx a-- xxxx"//
                + "\nwhere id > 0;"
                ;//
        Assert.assertEquals("SELECT *" //
                + "\nFROM xxxx a -- xxxx" //
                + "\nWHERE id > 0;", SQLUtils.formatOdps(sql));
    }
    

    public void test_column_comment_subquery() throws Exception {
        String sql = "select *" //
                + "\nfrom (" //
                + "\n-- comment_xxx" //
                + "\nselect * from t" //
                + "\n) a;"
                ;//
        Assert.assertEquals("SELECT *" //
                + "\nFROM (" //
                + "\n\t-- comment_xxx" //
                + "\n\tSELECT *" //
                + "\n\tFROM t" //
                + "\n) a;", SQLUtils.formatOdps(sql));
    }
}
