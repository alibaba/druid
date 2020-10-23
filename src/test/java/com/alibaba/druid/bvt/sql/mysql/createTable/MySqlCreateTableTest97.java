package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest97 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `gsp_order` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `spare_num` int(11) NOT NULL COMMENT '????',\n" +
                "  `coupon` decimal(11,2) NOT NULL DEFAULT '0.00' COMMENT '????',\n" +
                "  `remark` varchar(255) DEFAULT NULL COMMENT '??',\n" +
                "  `creator_id` int(11) NOT NULL COMMENT '???ID',\n" +
                "  `last_operator` varchar(16) DEFAULT NULL COMMENT '???????',\n" +
                "  `createtime` datetime NOT NULL COMMENT '????',\n" +
                "  `updatetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????????',\n" +
                "  `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '?????0????1?????',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `creator_id` (`creator_id`),\n" +
                "  KEY `id` (`id`,`is_del`),\n" +
                "  KEY `order_id` (`order_id`),\n" +
                "  CONSTRAINT `gsp_order2_details_ibfk_2` FOREIGN KEY (`creator_id`) REFERENCES `gsp_sys_users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=84608 DEFAULT CHARSET=utf8";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(14, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE `gsp_order` (\n" +
                "\t`id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "\t`spare_num` int(11) NOT NULL COMMENT '????',\n" +
                "\t`coupon` decimal(11, 2) NOT NULL DEFAULT '0.00' COMMENT '????',\n" +
                "\t`remark` varchar(255) DEFAULT NULL COMMENT '??',\n" +
                "\t`creator_id` int(11) NOT NULL COMMENT '???ID',\n" +
                "\t`last_operator` varchar(16) DEFAULT NULL COMMENT '???????',\n" +
                "\t`createtime` datetime NOT NULL COMMENT '????',\n" +
                "\t`updatetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????????',\n" +
                "\t`is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '?????0????1?????',\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tKEY `creator_id` (`creator_id`),\n" +
                "\tKEY `id` (`id`, `is_del`),\n" +
                "\tKEY `order_id` (`order_id`),\n" +
                "\tCONSTRAINT `gsp_order2_details_ibfk_2` FOREIGN KEY (`creator_id`) REFERENCES `gsp_sys_users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 84608 CHARSET = utf8", stmt.toString());
    }
}