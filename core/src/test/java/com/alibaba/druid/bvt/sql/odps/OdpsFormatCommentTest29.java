package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest29 {
    @Test
    public void test_drop_function() throws Exception {
        String sql = "create table xxxx001(   --测试"
                + "\ncol string,  --测试2"
                + "\ncol2 string  --测试3"
                + "\n)";
        assertEquals("CREATE TABLE xxxx001 ( -- 测试"
                + "\n\tcol STRING, -- 测试2"
                + "\n\tcol2 STRING -- 测试3"
                + "\n)", SQLUtils.formatOdps(sql));
    }
}
