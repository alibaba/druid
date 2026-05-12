package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest30 {
    @Test
    public void test_drop_function() throws Exception {
        String sql = "--啊实打实大啊实打实大"
                + "\nCREATE TABLE xxx ("
                + "\n  aa STRING,"
                + "\n  asdasd STRING,"
                + "\n  asasd STRING"
                + "\n);";
        assertEquals("-- 啊实打实大啊实打实大"
                + "\nCREATE TABLE xxx ("
                + "\n\taa STRING,"
                + "\n\tasdasd STRING,"
                + "\n\tasasd STRING"
                + "\n);", SQLUtils.formatOdps(sql));
    }
}
