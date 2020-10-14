package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.ParserException;

public class MySqlCreateTableTest108_error extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table t (\n" +
                "f0 int,\n" +
                "delete int" +
                ");";

        Exception error = null;
        try {
            SQLUtils.parseStatements(sql, DbType.mysql);
        } catch (ParserException ex) {
            error = ex;
        }
        assertNotNull(error);
        assertEquals("illegal name, pos 31, line 3, column 2, token DELETE", error.getMessage());
    }
}