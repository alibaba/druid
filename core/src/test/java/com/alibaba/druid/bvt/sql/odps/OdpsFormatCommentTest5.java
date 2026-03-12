package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest5 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "select *"
                + "\nfrom t -- xxxx"//
                + "\nwhere id > 0;";
        assertEquals("SELECT *"
                + "\nFROM t -- xxxx"
                + "\nWHERE id > 0;", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_column_comment_as() throws Exception {
        String sql = "select *"
                + "\nfrom xxxx a-- xxxx"//
                + "\nwhere id > 0;";
        assertEquals("SELECT *"
                + "\nFROM xxxx a -- xxxx"
                + "\nWHERE id > 0;", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_column_comment_subquery() throws Exception {
        String sql = "select *"
                + "\nfrom ("
                + "\n-- comment_xxx"
                + "\nselect * from t"
                + "\n) a;";
        assertEquals("SELECT *"
                + "\nFROM ("
                + "\n\t-- comment_xxx"
                + "\n\tSELECT *"
                + "\n\tFROM t"
                + "\n) a;", SQLUtils.formatOdps(sql));
    }
}
