package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest8 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select * from table1;--comment1 goes here" //
                + "\n" //
                + "\nselect * from table2;;select * from table3;" //
                + "\n--comment2 goes here";//
        Assert.assertEquals("SELECT *\n" +
                "FROM table1;-- comment1 goes here\n" +
                "\n" +
                "SELECT *\n" +
                "FROM table2;\n" +
                "\n" +
                "SELECT *\n" +
                "FROM table3;\n" +
                "\n" +
                "-- comment2 goes here", SQLUtils.formatOdps(sql));

        Assert.assertEquals("select *\n" +
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
