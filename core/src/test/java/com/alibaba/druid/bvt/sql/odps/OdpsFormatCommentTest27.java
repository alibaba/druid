package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest27 {
    @Test
    public void test_drop_function() throws Exception {
        String sql = "select split(val, ',')[1] from dual";
        assertEquals("SELECT SPLIT(val, ',')[1]"
                + "\nFROM dual", SQLUtils.formatOdps(sql));
    }
}
