package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlCreateTableTest145_tail_hint
 * @description
 * @Author zzy
 * @Date 2019-05-09 20:08
 */
public class MySqlCreateTableTest145_tail_hint extends MysqlTest {

    public void test_0() throws Exception {

        String sql = "CREATE TABLE `log_fake` (\n" +
                "  `id` varchar(37) NOT NULL COMMENT 'uuid',\n" +
                "  `merchant_id` varchar(37) DEFAULT NULL COMMENT '商户id',\n" +
                "  `type` int(11) NOT NULL COMMENT 'type字段决定payload如何解析',\n" +
                "  `payload` blob COMMENT 'payload存放以JSON格式编码的系统事件，例如订单成功支付。',\n" +
                "  `processed` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否已经处理（下游业务逻辑完成运算并更新数据或者同步到持久化的队列）',\n" +
                "  `partition` int(10) unsigned NOT NULL COMMENT '分区ID(0-999)。多线程方式处理日志记录的时候，防止同一条记录被重复处理。',\n" +
                "  `action_id` varchar(37) DEFAULT NULL COMMENT '外部业务id 比如提现记录id',\n" +
                "  `ctime` bigint(20) DEFAULT NULL,\n" +
                "  `mtime` bigint(20) DEFAULT NULL,\n" +
                "  `version` bigint(20) unsigned NOT NULL,\n" +
                "  `deleted` tinyint(1) NOT NULL DEFAULT '0',\n" +
                "  KEY `id` (`id`),\n" +
                "  KEY `log_processed` (`processed`),\n" +
                "  KEY `log_ctime` (`ctime`),\n" +
                "  KEY `log_merchant_id_ctime` (`merchant_id`,`ctime`),\n" +
                "  KEY `log_action_id` (`action_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='日志记录用于驱动下游逻辑（余额更新）和外部系统的数据同步（积分）。'\n" +
                "/*!50100 PARTITION BY RANGE (`ctime`)\n" +
                "(PARTITION p201804 VALUES LESS THAN (1525104000000) ENGINE = InnoDB,\n" +
                " PARTITION p201805 VALUES LESS THAN (1527782400000) ENGINE = InnoDB,\n" +
                " PARTITION p201806 VALUES LESS THAN (1530374400000) ENGINE = InnoDB,\n" +
                " PARTITION p201807 VALUES LESS THAN (1533052800000) ENGINE = InnoDB,\n" +
                " PARTITION p201808 VALUES LESS THAN (1535731200000) ENGINE = InnoDB,\n" +
                " PARTITION p201809 VALUES LESS THAN (1538323200000) ENGINE = InnoDB,\n" +
                " PARTITION p201810 VALUES LESS THAN (1541001600000) ENGINE = InnoDB,\n" +
                " PARTITION p201811 VALUES LESS THAN (1543593600000) ENGINE = InnoDB,\n" +
                " PARTITION p201812 VALUES LESS THAN (1546272000000) ENGINE = InnoDB,\n" +
                " PARTITION pmax VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */ dbpartition by hash(`merchant_id`)";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE `log_fake` (\n" +
                "\t`id` varchar(37) NOT NULL COMMENT 'uuid',\n" +
                "\t`merchant_id` varchar(37) DEFAULT NULL COMMENT '商户id',\n" +
                "\t`type` int(11) NOT NULL COMMENT 'type字段决定payload如何解析',\n" +
                "\t`payload` blob COMMENT 'payload存放以JSON格式编码的系统事件，例如订单成功支付。',\n" +
                "\t`processed` tinyint(1) UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否已经处理（下游业务逻辑完成运算并更新数据或者同步到持久化的队列）',\n" +
                "\t`partition` int(10) UNSIGNED NOT NULL COMMENT '分区ID(0-999)。多线程方式处理日志记录的时候，防止同一条记录被重复处理。',\n" +
                "\t`action_id` varchar(37) DEFAULT NULL COMMENT '外部业务id 比如提现记录id',\n" +
                "\t`ctime` bigint(20) DEFAULT NULL,\n" +
                "\t`mtime` bigint(20) DEFAULT NULL,\n" +
                "\t`version` bigint(20) UNSIGNED NOT NULL,\n" +
                "\t`deleted` tinyint(1) NOT NULL DEFAULT '0',\n" +
                "\tKEY `id` (`id`),\n" +
                "\tKEY `log_processed` (`processed`),\n" +
                "\tKEY `log_ctime` (`ctime`),\n" +
                "\tKEY `log_merchant_id_ctime` (`merchant_id`, `ctime`),\n" +
                "\tKEY `log_action_id` (`action_id`)\n" +
                ") ENGINE = InnoDB CHARSET = utf8 COMMENT '日志记录用于驱动下游逻辑（余额更新）和外部系统的数据同步（积分）。'\n" +
                "DBPARTITION BY hash(`merchant_id`) /*!50100 PARTITION BY RANGE (`ctime`)\n" +
                "(PARTITION p201804 VALUES LESS THAN (1525104000000) ENGINE = InnoDB,\n" +
                " PARTITION p201805 VALUES LESS THAN (1527782400000) ENGINE = InnoDB,\n" +
                " PARTITION p201806 VALUES LESS THAN (1530374400000) ENGINE = InnoDB,\n" +
                " PARTITION p201807 VALUES LESS THAN (1533052800000) ENGINE = InnoDB,\n" +
                " PARTITION p201808 VALUES LESS THAN (1535731200000) ENGINE = InnoDB,\n" +
                " PARTITION p201809 VALUES LESS THAN (1538323200000) ENGINE = InnoDB,\n" +
                " PARTITION p201810 VALUES LESS THAN (1541001600000) ENGINE = InnoDB,\n" +
                " PARTITION p201811 VALUES LESS THAN (1543593600000) ENGINE = InnoDB,\n" +
                " PARTITION p201812 VALUES LESS THAN (1546272000000) ENGINE = InnoDB,\n" +
                " PARTITION pmax VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */", stmt.toString());

        assertEquals("create table `log_fake` (\n" +
                "\t`id` varchar(37) not null comment 'uuid',\n" +
                "\t`merchant_id` varchar(37) default null comment '商户id',\n" +
                "\t`type` int(11) not null comment 'type字段决定payload如何解析',\n" +
                "\t`payload` blob comment 'payload存放以JSON格式编码的系统事件，例如订单成功支付。',\n" +
                "\t`processed` tinyint(1) unsigned not null default '0' comment '是否已经处理（下游业务逻辑完成运算并更新数据或者同步到持久化的队列）',\n" +
                "\t`partition` int(10) unsigned not null comment '分区ID(0-999)。多线程方式处理日志记录的时候，防止同一条记录被重复处理。',\n" +
                "\t`action_id` varchar(37) default null comment '外部业务id 比如提现记录id',\n" +
                "\t`ctime` bigint(20) default null,\n" +
                "\t`mtime` bigint(20) default null,\n" +
                "\t`version` bigint(20) unsigned not null,\n" +
                "\t`deleted` tinyint(1) not null default '0',\n" +
                "\tkey `id` (`id`),\n" +
                "\tkey `log_processed` (`processed`),\n" +
                "\tkey `log_ctime` (`ctime`),\n" +
                "\tkey `log_merchant_id_ctime` (`merchant_id`, `ctime`),\n" +
                "\tkey `log_action_id` (`action_id`)\n" +
                ") engine = InnoDB charset = utf8 comment '日志记录用于驱动下游逻辑（余额更新）和外部系统的数据同步（积分）。'\n" +
                "dbpartition by hash(`merchant_id`) /*!50100 PARTITION BY RANGE (`ctime`)\n" +
                "(PARTITION p201804 VALUES LESS THAN (1525104000000) ENGINE = InnoDB,\n" +
                " PARTITION p201805 VALUES LESS THAN (1527782400000) ENGINE = InnoDB,\n" +
                " PARTITION p201806 VALUES LESS THAN (1530374400000) ENGINE = InnoDB,\n" +
                " PARTITION p201807 VALUES LESS THAN (1533052800000) ENGINE = InnoDB,\n" +
                " PARTITION p201808 VALUES LESS THAN (1535731200000) ENGINE = InnoDB,\n" +
                " PARTITION p201809 VALUES LESS THAN (1538323200000) ENGINE = InnoDB,\n" +
                " PARTITION p201810 VALUES LESS THAN (1541001600000) ENGINE = InnoDB,\n" +
                " PARTITION p201811 VALUES LESS THAN (1543593600000) ENGINE = InnoDB,\n" +
                " PARTITION p201812 VALUES LESS THAN (1546272000000) ENGINE = InnoDB,\n" +
                " PARTITION pmax VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */", stmt.toLowerCaseString());

    }

}
