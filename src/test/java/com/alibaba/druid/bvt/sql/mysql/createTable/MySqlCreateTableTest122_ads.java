package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest122_ads extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE cache_table_1 OPTIONS(cache=true) AS /*+ engine=MPP */ SELECT * FROM test_realtime1 LIMIT 200;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE cache_table_1\n" +
                "OPTIONS (cache = true)\n" +
                "AS\n" +
                "/*+ engine=MPP */\n" +
                "SELECT *\n" +
                "FROM test_realtime1\n" +
                "LIMIT 200;", stmt.toString());

    }



}