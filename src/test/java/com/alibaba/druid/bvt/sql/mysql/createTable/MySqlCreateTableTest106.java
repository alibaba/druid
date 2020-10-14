package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest106 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `procs_priv` (\n" +
                "  `Routine_type` enum('FUNCTION','PROCEDURE') COLLATE utf8_bin NOT NULL,\n" +
                "  PRIMARY KEY (`Host`,`Db`,`User`,`Routine_name`,`Routine_type`),\n" +
                "  KEY `Grantor` (`Grantor`)\n" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Procedure privileges'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(3, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE `procs_priv` (\n" +
                "\t`Routine_type` enum('FUNCTION', 'PROCEDURE') COLLATE utf8_bin NOT NULL,\n" +
                "\tPRIMARY KEY (`Host`, `Db`, `User`, `Routine_name`, `Routine_type`),\n" +
                "\tKEY `Grantor` (`Grantor`)\n" +
                ") ENGINE = MyISAM CHARSET = utf8 COLLATE = utf8_bin COMMENT 'Procedure privileges'", stmt.toString());
    }
}