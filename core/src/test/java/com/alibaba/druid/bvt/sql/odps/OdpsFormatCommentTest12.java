package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest12 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "select * from t --abc";
        assertEquals("SELECT *"
                + "\nFROM t -- abc", SQLUtils.formatOdps(sql));
    }
}
