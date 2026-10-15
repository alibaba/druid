package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest17 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "set xxx=aaa;--ssss";
        assertEquals("SET xxx = aaa;-- ssss", SQLUtils.formatOdps(sql));
    }
}
