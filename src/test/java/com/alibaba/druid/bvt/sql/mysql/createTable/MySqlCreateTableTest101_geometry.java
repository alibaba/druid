package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest101_geometry extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE geom (g GEOMETRY NOT NULL);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(1, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE geom (\n" +
                "\tg GEOMETRY NOT NULL\n" +
                ");", stmt.toString());
    }
}