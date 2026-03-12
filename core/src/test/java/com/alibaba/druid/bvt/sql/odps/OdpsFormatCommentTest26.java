package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest26 {
    @Test
    public void test_drop_function() throws Exception {
        String sql = "create table t as select * from dual;";
        assertEquals("CREATE TABLE t"
                + "\nAS"
                + "\nSELECT *"
                + "\nFROM dual;", SQLUtils.formatOdps(sql));
    }
}
