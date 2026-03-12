package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest19 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "set xxx=aaa;--ssss"
                + "\nset yyy=123;";
        assertEquals("SET xxx = aaa;-- ssss"
                + "\nSET yyy = 123;", SQLUtils.formatOdps(sql));
    }
}
