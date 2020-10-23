package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest140 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE foo AS SELECT * FROM t WITH NO DATA";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE foo\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM t\n" +
                "WITH NO DATA", stmt.toString());

        assertEquals("create table foo\n" +
                "as\n" +
                "select *\n" +
                "from t\n" +
                "with no data", stmt.toLowerCaseString());

    }





}