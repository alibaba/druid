package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest7 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "--这里是注释"
                + "\nselect * from table1;"
                + "\nselect * from table2;;";
        assertEquals("-- 这里是注释"
                + "\nSELECT *"
                + "\nFROM table1;"
                + "\n"
                + "\nSELECT *"
                + "\nFROM table2;", SQLUtils.formatOdps(sql));
    }
}
