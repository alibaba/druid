package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlCreateTableTest144_three_backtick
 * @description
 * @Author zzy
 * @Date 2019-05-09 14:51
 */
public class MySqlCreateTableTest144_three_backtick extends MysqlTest {

    public void test_0() throws Exception {

        String sql = "CREATE TABLE `dbn_product_album_info` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `position` int(11) DEFAULT '9999',\n" +
                "  `album_id` bigint(20) DEFAULT NULL,\n" +
                "  `product_id` bigint(20) DEFAULT NULL,\n" +
                "  `goods_id` bigint(20) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `FKDECFEBDE7B82325` (`product_id`),\n" +
                "  KEY `FKDECFEBD949942C0` (`album_id`),\n" +
                "  KEY ```album_id``` (`album_id`) USING BTREE,\n" +
                "  CONSTRAINT `FKDECFEBD949942C0` FOREIGN KEY (`album_id`) REFERENCES `dbn_product_album` (`id`),\n" +
                "  CONSTRAINT `FKDECFEBDE7B82325` FOREIGN KEY (`product_id`) REFERENCES `dbn_products` (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=317607 DEFAULT CHARSET=utf8 COLLATE=utf8_bin\n";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE `dbn_product_album_info` (\n" +
                "\t`id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "\t`position` int(11) DEFAULT '9999',\n" +
                "\t`album_id` bigint(20) DEFAULT NULL,\n" +
                "\t`product_id` bigint(20) DEFAULT NULL,\n" +
                "\t`goods_id` bigint(20) DEFAULT NULL,\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tKEY `FKDECFEBDE7B82325` (`product_id`),\n" +
                "\tKEY `FKDECFEBD949942C0` (`album_id`),\n" +
                "\tKEY ```album_id``` USING BTREE (`album_id`),\n" +
                "\tCONSTRAINT `FKDECFEBD949942C0` FOREIGN KEY (`album_id`) REFERENCES `dbn_product_album` (`id`),\n" +
                "\tCONSTRAINT `FKDECFEBDE7B82325` FOREIGN KEY (`product_id`) REFERENCES `dbn_products` (`id`)\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 317607 CHARSET = utf8 COLLATE = utf8_bin", stmt.toString());

        assertEquals("create table `dbn_product_album_info` (\n" +
                "\t`id` bigint(20) not null auto_increment,\n" +
                "\t`position` int(11) default '9999',\n" +
                "\t`album_id` bigint(20) default null,\n" +
                "\t`product_id` bigint(20) default null,\n" +
                "\t`goods_id` bigint(20) default null,\n" +
                "\tprimary key (`id`),\n" +
                "\tkey `FKDECFEBDE7B82325` (`product_id`),\n" +
                "\tkey `FKDECFEBD949942C0` (`album_id`),\n" +
                "\tkey ```album_id``` using BTREE (`album_id`),\n" +
                "\tconstraint `FKDECFEBD949942C0` foreign key (`album_id`) references `dbn_product_album` (`id`),\n" +
                "\tconstraint `FKDECFEBDE7B82325` foreign key (`product_id`) references `dbn_products` (`id`)\n" +
                ") engine = InnoDB auto_increment = 317607 charset = utf8 collate = utf8_bin", stmt.toLowerCaseString());


    }

}
