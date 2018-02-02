package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest96_set extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table test1(id int, name set('a', 'b', 'c') not null default 'a');";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(2, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE test1 (\n" +
                "\tid int,\n" +
                "\tname set('a', 'b', 'c') NOT NULL DEFAULT 'a'\n" +
                ");", stmt.toString());
    }
}