package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest18 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "create table t (f1 string comment \"xxx\")";
        assertEquals("CREATE TABLE t ("
                + "\n\tf1 STRING COMMENT 'xxx'"
                + "\n)", SQLUtils.formatOdps(sql));
    }

    @Test
    public void test_column_comment_2() throws Exception {
        String sql = "create table t (f1 string comment \"xxx's\")";
        assertEquals("CREATE TABLE t ("
                + "\n\tf1 STRING COMMENT 'xxx\\'s'"
                + "\n)", SQLUtils.formatOdps(sql));
    }
}
