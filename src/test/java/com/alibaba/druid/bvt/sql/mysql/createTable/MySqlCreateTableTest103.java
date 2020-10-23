package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest103 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `procs_priv` (\n" +
                "  `Host` char(60) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "  `Db` char(64) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "  `User` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "  `Routine_name` char(64) CHARACTER SET utf8 NOT NULL DEFAULT '',\n" +
                "  `Routine_type` enum('FUNCTION','PROCEDURE') COLLATE utf8_bin NOT NULL,\n" +
                "  `Grantor` char(93) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "  `Proc_priv` set('Execute','Alter Routine','Grant') CHARACTER SET utf8 NOT NULL DEFAULT '',\n" +
                "  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "  PRIMARY KEY (`Host`,`Db`,`User`,`Routine_name`,`Routine_type`),\n" +
                "  KEY `Grantor` (`Grantor`)\n" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Procedure privileges'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(10, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE `procs_priv` (\n" +
                "\t`Host` char(60) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "\t`Db` char(64) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "\t`User` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "\t`Routine_name` char(64) CHARACTER SET utf8 NOT NULL DEFAULT '',\n" +
                "\t`Routine_type` enum('FUNCTION', 'PROCEDURE') COLLATE utf8_bin NOT NULL,\n" +
                "\t`Grantor` char(93) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "\t`Proc_priv` set('Execute', 'Alter Routine', 'Grant') CHARACTER SET utf8 NOT NULL DEFAULT '',\n" +
                "\t`Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "\tPRIMARY KEY (`Host`, `Db`, `User`, `Routine_name`, `Routine_type`),\n" +
                "\tKEY `Grantor` (`Grantor`)\n" +
                ") ENGINE = MyISAM CHARSET = utf8 COLLATE = utf8_bin COMMENT 'Procedure privileges'", stmt.toString());

        assertEquals("CREATE TABLE `procs_priv` (\n" +
                "\t`Host` char(60) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "\t`Db` char(64) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "\t`User` char(32) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "\t`Routine_name` char(64) CHARACTER SET utf8 NOT NULL DEFAULT '',\n" +
                "\t`Routine_type` enum('FUNCTION', 'PROCEDURE') COLLATE utf8_bin NOT NULL,\n" +
                "\t`Grantor` char(93) COLLATE utf8_bin NOT NULL DEFAULT '',\n" +
                "\t`Proc_priv` set('Execute', 'Alter Routine', 'Grant') CHARACTER SET utf8 NOT NULL DEFAULT '',\n" +
                "\t`Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "\tPRIMARY KEY (`Host`, `Db`, `User`, `Routine_name`, `Routine_type`),\n" +
                "\tKEY `Grantor` (`Grantor`)\n" +
                ") ENGINE = MyISAM CHARSET = utf8 COLLATE = utf8_bin COMMENT 'Procedure privileges'", stmt.clone().toString());
    }
}