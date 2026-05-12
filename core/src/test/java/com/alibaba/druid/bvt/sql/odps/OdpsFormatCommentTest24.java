package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest24 {
    @Test
    public void test_drop_view() throws Exception {
        String sql = "-- xxx"
                + "\n -- yyy"
                + "\ndrop view if exists mytables;";
        assertEquals("-- xxx"
                + "\n-- yyy"
                + "\nDROP VIEW IF EXISTS mytables;", SQLUtils.formatOdps(sql));
    }
}
