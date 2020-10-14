package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest100 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS ttable\n" +
                "(\n" +
                "`id` BIGINT(20) NOT NULL AUTO_INCREMENT,\n" +
                "`queue_id` varchar(20) NOT NULL DEFAULT '-1',\n" +
                "`status` TINYINT(4) NOT NULL DEFAULT '1',\n" +
                "`geometry` geometry not null,\n" +
                "CONSTRAINT PRIMARY KEY (`id`),\n" +
                "CONSTRAINT UNIQUE KEY `uk_queue_id` USING BTREE (`queue_id`) KEY_BLOCK_SIZE=10,\n" +
                "FULLTEXT KEY `ft_status` (`queue_id`),\n" +
                "spatial index `spatial` (`geometry`),\n" +
                "CONSTRAINT FOREIGN KEY `fk_test`(`queue_id`) REFERENCES `test`(`id`)\n" +
                ") ENGINE=INNODB";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(9, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE IF NOT EXISTS ttable (\n" +
                "\t`id` BIGINT(20) NOT NULL AUTO_INCREMENT,\n" +
                "\t`queue_id` varchar(20) NOT NULL DEFAULT '-1',\n" +
                "\t`status` TINYINT(4) NOT NULL DEFAULT '1',\n" +
                "\t`geometry` geometry NOT NULL,\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE KEY `uk_queue_id` USING BTREE (`queue_id`) KEY_BLOCK_SIZE = 10,\n" +
                "\tFULLTEXT KEY `ft_status` (`queue_id`),\n" +
                "\tSPATIAL INDEX `spatial`(`geometry`),\n" +
                "\tCONSTRAINT FOREIGN KEY `fk_test` (`queue_id`) REFERENCES `test` (`id`)\n" +
                ") ENGINE = INNODB", stmt.toString());
    }
}