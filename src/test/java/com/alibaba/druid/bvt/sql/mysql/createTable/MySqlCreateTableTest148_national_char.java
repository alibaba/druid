package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlCreateTableTest148_national_char
 * @description
 *
 * [NATIONAL] CHAR[(M)] [CHARACTER SET charset_name] [COLLATE collation_name]
 * [NATIONAL] VARCHAR(M) [CHARACTER SET charset_name] [COLLATE collation_name]
 *
 * @Author zzy
 * @Date 2019-05-14 17:25
 */
public class MySqlCreateTableTest148_national_char extends TestCase {

    public void test_0() throws Exception {
        String sql = "create temporary table if not exists `tb_kxipe` (\n" +
                "\t col_vttevt national char(128),\n" +
                "\t col_wqq national varchar(128)\n" +
                ") comment 'comment' ";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TEMPORARY TABLE IF NOT EXISTS `tb_kxipe` (\n" +
                "\tcol_vttevt national char(128),\n" +
                "\tcol_wqq national varchar(128)\n" +
                ") COMMENT 'comment'", stmt.toString());

        assertEquals("create temporary table if not exists `tb_kxipe` (\n" +
                "\tcol_vttevt national char(128),\n" +
                "\tcol_wqq national varchar(128)\n" +
                ") comment 'comment'", stmt.toLowerCaseString());
    }

}
