package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest95 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `f_product` (\n" +
                "  `id` int(200) NOT NULL AUTO_INCREMENT,\n" +
                " `type` enum('market','land','enterprise') CHARACTER SET utf8 DEFAULT NULL COMMENT 'comment',\n" +
                " PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1DEFAULT CHARSET=utf8mb4";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(3, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE `f_product` (\n" +
                "\t`id` int(200) NOT NULL AUTO_INCREMENT,\n" +
                "\t`type` enum('market', 'land', 'enterprise') CHARACTER SET utf8 DEFAULT NULL COMMENT 'comment',\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 1DEFAULT CHARSET = utf8mb4", stmt.toString());
    }
}