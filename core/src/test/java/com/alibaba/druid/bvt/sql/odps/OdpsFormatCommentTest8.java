package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest8 {
    @Test
    public void test_column_comment() throws Exception {
        String sql = "select * from table1;--comment1 goes here"
                + "\n"
                + "\nselect * from table2;;select * from table3;"
                + "\n--comment2 goes here";
        assertEquals("SELECT *\n" +
                "FROM table1;-- comment1 goes here\n" +
                "\n" +
                "SELECT *\n" +
                "FROM table2;\n" +
                "\n" +
                "SELECT *\n" +
                "FROM table3;\n" +
                "\n" +
                "-- comment2 goes here", SQLUtils.formatOdps(sql));

        assertEquals("select *\n" +
                "from table1;-- comment1 goes here\n" +
                "\n" +
                "select *\n" +
                "from table2;\n" +
                "\n" +
                "select *\n" +
                "from table3;\n" +
                "\n" +
                "-- comment2 goes here", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
