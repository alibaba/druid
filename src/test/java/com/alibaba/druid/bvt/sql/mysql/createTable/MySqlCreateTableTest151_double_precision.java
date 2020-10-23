package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlCreateTableTest151_double_precision
 * @description
 * @Author zzy
 * @Date 2019-05-15 14:07
 */
public class MySqlCreateTableTest151_double_precision extends TestCase {

    public void test_0() {
        String sql = "create temporary table tb_etaqf (\n" +
                "\t `col_mcdw` double precision(10,2)\n" +
                ")";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TEMPORARY TABLE tb_etaqf (\n" +
                "\t`col_mcdw` double precision(10, 2)\n" +
                ")", stmt.toString());

        assertEquals("create temporary table tb_etaqf (\n" +
                "\t`col_mcdw` double precision(10, 2)\n" +
                ")", stmt.toLowerCaseString());
    }

}
