package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest2 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "--[Subject -]"
                + "\n--[Author  -高铁/035139]"//
                + "\n--[Created -2015-06-10 13:19:18]"//
                + "\n--[Update ]"//
                + "\nset odps.sql.mapper.split.size=2048;"//
                + "\nselect * from dual;";
        assertEquals("-- [Subject -]"
                + "\n-- [Author  -高铁/035139]"
                + "\n-- [Created -2015-06-10 13:19:18]"
                + "\n-- [Update ]"
                + "\nSET odps.sql.mapper.split.size = 2048;"
                + "\n"
                + "\nSELECT *"
                + "\nFROM dual;", SQLUtils.formatOdps(sql));
    }
}
