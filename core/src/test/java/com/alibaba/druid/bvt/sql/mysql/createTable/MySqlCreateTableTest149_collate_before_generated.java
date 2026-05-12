package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @version 1.0
 * @ClassName MySqlCreateTableTest149_collate_before_generated
 * @description | data_type
 * [COLLATE collation_name]
 * [GENERATED ALWAYS] AS (expr)
 * [VIRTUAL | STORED] [NOT NULL | NULL]
 * [UNIQUE [KEY]] [[PRIMARY] KEY]
 * [COMMENT 'string']
 * [reference_definition]
 * @Author zzy
 * @Date 2019-05-14 17:41
 */
public class MySqlCreateTableTest149_collate_before_generated {
    @Test
    public void test_0() {
        String sql = "create temporary table `tb_dhma` (col_oxqagw int collate utf8_unicode_ci generated always as ( 1+2 ))";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TEMPORARY TABLE `tb_dhma` (\n" +
                "\tcol_oxqagw int GENERATED ALWAYS AS (1 + 2) COLLATE utf8_unicode_ci\n" +
                ")", stmt.toString());

        assertEquals("create temporary table `tb_dhma` (\n" +
                "\tcol_oxqagw int generated always as (1 + 2) collate utf8_unicode_ci\n" +
                ")", stmt.toLowerCaseString());
    }
}
