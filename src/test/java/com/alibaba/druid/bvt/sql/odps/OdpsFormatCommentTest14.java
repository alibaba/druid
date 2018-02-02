package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest14 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select * from (" //
                + "select 1 from t1"
                + "\n --c_0"
                + "\n union all "
                + "\n --c_1" //
                + "\nselect 2 from t2" //
                + "\n --c_2"
                + "\n union all "
                + "\n --c_3" //
                + "\nselect 3 from t3" //
                + ") xx";
        Assert.assertEquals("SELECT *"
                + "\nFROM ("
                + "\n\tSELECT 1"
                + "\n\tFROM t1 -- c_0"
                + "\n\tUNION ALL"
                + "\n\t-- c_1"
                + "\n\tSELECT 2"
                + "\n\tFROM t2 -- c_2"
                + "\n\tUNION ALL"
                + "\n\t-- c_3"
                + "\n\tSELECT 3"
                + "\n\tFROM t3"
                + "\n) xx", SQLUtils.formatOdps(sql));
    }

   
}
