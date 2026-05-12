package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest25 {
    @Test
    public void test_drop_function() throws Exception {
        String sql = "-- xxx"
                + "\n -- yyy"
                + "\ndrop function if exists mytables;";
        assertEquals("-- xxx"
                + "\n-- yyy"
                + "\nDROP FUNCTION IF EXISTS mytables;", SQLUtils.formatOdps(sql));
    }
}
