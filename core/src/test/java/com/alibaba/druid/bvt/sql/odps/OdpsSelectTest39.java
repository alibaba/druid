package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OdpsSelectTest39 {
    @Test
    public void test_where_parenthesized_boolean_precedence() {
        String sql = "select * from a where (b or c) and d and e";

        assertEquals("SELECT *\n"
                + "FROM a\n"
                + "WHERE (b\n"
                + "\t\tOR c)\n"
                + "\tAND d\n"
                + "\tAND e", SQLUtils.formatOdps(sql));

        assertEquals("select *\n"
                + "from a\n"
                + "where (b\n"
                + "\t\tor c)\n"
                + "\tand d\n"
                + "\tand e", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

    @Test
    public void test_where_right_parenthesized_boolean_precedence() {
        String sql = "select * from a where b and (c or d) and e";

        assertEquals("SELECT *\n"
                + "FROM a\n"
                + "WHERE b\n"
                + "\tAND (c\n"
                + "\t\tOR d)\n"
                + "\tAND e", SQLUtils.formatOdps(sql));

        assertEquals("select *\n"
                + "from a\n"
                + "where b\n"
                + "\tand (c\n"
                + "\t\tor d)\n"
                + "\tand e", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
