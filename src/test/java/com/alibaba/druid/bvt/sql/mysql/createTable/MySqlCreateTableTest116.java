package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTableTest116 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS `Employee` (\n" +
                "   id int(10)" +
                " ) broadcast ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_bin";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE IF NOT EXISTS `Employee` (\n" +
                "\tid int(10)\n" +
                ") BROADCAST ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", stmt.toString());


    }

    public void test_1() throws Exception {
        String sql = "CREATE TABLE `t1` (\n" +
                "  `id` int NOT NULL,\n" +
                "  `e_id` varchar(45) COLLATE utf8_bin NOT NULL,\n" +
                "  `m_id` varchar(45) COLLATE utf8_bin NOT NULL,\n" +
                "  `m_id1` varchar(45) COLLATE utf8_bin NOT NULL,\n" +
                "  `m_id2` varchar(45) COLLATE utf8_bin NOT NULL,\n" +
                "  `name` varchar(256) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `addr` varchar(45) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `index_e_id` (`e_id`),\n" +
                "  index `index_m_id` (`m_id`),\n" +
                "  KEY `index_m1_id` (m_id1(10)),\n" +
                "  index `index_m2_id` (m_id2(30))\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE `t1` (\n" +
                "\t`id` int NOT NULL,\n" +
                "\t`e_id` varchar(45) COLLATE utf8_bin NOT NULL,\n" +
                "\t`m_id` varchar(45) COLLATE utf8_bin NOT NULL,\n" +
                "\t`m_id1` varchar(45) COLLATE utf8_bin NOT NULL,\n" +
                "\t`m_id2` varchar(45) COLLATE utf8_bin NOT NULL,\n" +
                "\t`name` varchar(256) COLLATE utf8_bin DEFAULT NULL,\n" +
                "\t`addr` varchar(45) COLLATE utf8_bin DEFAULT NULL,\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tKEY `index_e_id` (`e_id`),\n" +
                "\tINDEX `index_m_id`(`m_id`),\n" +
                "\tKEY `index_m1_id` (m_id1(10)),\n" +
                "\tINDEX `index_m2_id`(m_id2(30))\n" +
                ") ENGINE = InnoDB CHARSET = utf8 COLLATE = utf8_bin", stmt.toString());


    }

}