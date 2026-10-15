package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest21 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "create table sales (f1 bigint)";
        assertEquals("CREATE TABLE sales ("
                + "\n\tf1 BIGINT"
                + "\n)", SQLUtils.formatOdps(sql));
    }
}
