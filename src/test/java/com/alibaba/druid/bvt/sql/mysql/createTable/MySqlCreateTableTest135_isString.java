package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest135_isString extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table t(\n" +
                "f1 varchar(50), \n" +
                "f2 string, \n" +
                "f3 clob, \n" +
                "f4 nclob, \n" +
                "f5 nvarchar(50), \n" +
                "f6 text \n" +
                ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        for (SQLColumnDefinition item : stmt.getColumnDefinitions()) {
            assertTrue(item.getDataType().isString());
        }


    }




}