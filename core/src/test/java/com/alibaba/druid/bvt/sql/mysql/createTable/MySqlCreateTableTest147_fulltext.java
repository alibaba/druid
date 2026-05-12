package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @version 1.0
 * @ClassName MySqlCreateTableTest147_fulltext
 * @description | {FULLTEXT|SPATIAL} [INDEX|KEY] [index_name] (key_part,...)
 * [index_option] ...
 * [INDEX|KEY] [index_name]都可以省略
 * @Author zzy
 * @Date 2019-05-14 16:19
 */
public class MySqlCreateTableTest147_fulltext {
    @Test
    public void test_0() throws Exception {
        String sql = "create table tt (b varchar(128), fulltext (b));";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE tt (\n" +
                "\tb varchar(128),\n" +
                "\tFULLTEXT INDEX(b)\n" +
                ");", stmt.toString());

        assertEquals("create table tt (\n" +
                "\tb varchar(128),\n" +
                "\tfulltext index(b)\n" +
                ");", stmt.toLowerCaseString());
    }
}
