package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLTableLike;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;


public class MySqlCreateTableTest160 extends MysqlTest {

    public void test_0() throws Exception {
        //for ADB
        String sql = "CREATE TABLE IF NOT EXISTS bar (LIKE a INCLUDING PROPERTIES)";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertTrue(stmt.getTableElementList().get(0) instanceof SQLTableLike);
        assertEquals("CREATE TABLE IF NOT EXISTS bar (\n" +
                "\tLIKE a INCLUDING PROPERTIES\n" +
                ")", stmt.toString());
    }

    public void test_1() throws Exception {
        //for ADB
        String sql = "CREATE TABLE IF NOT EXISTS bar2 (c TIMESTAMP, LIKE bar, d DATE)";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertTrue(stmt.getTableElementList().get(1) instanceof SQLTableLike);
        assertEquals("CREATE TABLE IF NOT EXISTS bar2 (\n" +
                "\tc TIMESTAMP,\n" +
                "\tLIKE bar,\n" +
                "\td DATE\n" +
                ")", stmt.toString());
    }
}