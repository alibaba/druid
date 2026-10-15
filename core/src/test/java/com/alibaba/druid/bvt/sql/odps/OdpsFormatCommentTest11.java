package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest11 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "select * from t where -- comment_xx"
                + "\n f1 > 0";
        assertEquals("SELECT *"
                + "\nFROM t"
                + "\nWHERE -- comment_xx"
                + "\nf1 > 0", SQLUtils.formatOdps(sql));
    }
}
