package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlCreateTableTest102 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "CREATE TABLE `xktv_perf_6519` (\n" +
                "  `kd` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `gmt_create` datetime NOT NULL COMMENT '创建时间',\n" +
                "  `time` datetime NOT NULL COMMENT '统计的时间',\n" +
                "  `scope` int(11) NOT NULL COMMENT '时间粒度',\n" +
                "  `knstance_id` varchar(128) DEFAULT NULL COMMENT '实例id',\n" +
                "  `kp` varchar(16) NOT NULL COMMENT 'ip',\n" +
                "  `port` int(11) NOT NULL COMMENT 'port',\n" +
                "  `db` varchar(42) NOT NULL COMMENT 'db',\n" +
                "  `hashcode` varchar(32) NOT NULL COMMENT 'hashcode',\n" +
                "  `sql_type` varchar(6) NOT NULL COMMENT 'sql_type',\n" +
                "  `logical_table` varchar(128) DEFAULT NULL COMMENT '逻辑表名',\n" +
                "  `physical_table` varchar(128) DEFAULT NULL COMMENT '物理表名',\n" +
                "  `request_count` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT '执行次数',\n" +
                "  `rows_examined` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rows_examined',\n" +
                "  `rows_sent` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rows_sent',\n" +
                "  `rows_affected` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rows_affected',\n" +
                "  `knnodb_pages_read` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'innodb_pages_read',\n" +
                "  `knnodb_pages_io_read` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'innodb_pages_io_read',\n" +
                "  `rt_count` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rt_count',\n" +
                "  `query_time` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'query_time',\n" +
                "  `wait_time` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'wait_time',\n" +
                "  `lock_time` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'lock_time',\n" +
                "  `rt_1ms_count` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rt_1ms_count',\n" +
                "  `rt_2ms_count` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rt_2ms_count',\n" +
                "  `rt_3ms_count` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rt_3ms_count',\n" +
                "  `rt_10ms_count` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rt_10ms_count',\n" +
                "  `rt_100ms_count` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rt_100ms_count',\n" +
                "  `rt_1s_count` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rt_1s_count',\n" +
                "  `rt_gt1s_count` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'rt_gt1s_count',\n" +
                "  `avg_90_rt` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'avg_90_rt',\n" +
                "  `avg_95_rt` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'avg_95_rt',\n" +
                "  `avg_99_rt` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'avg_99_rt',\n" +
                "  `avg_rt` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'avg_rt',\n" +
                "  `val_ext_c0` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c0',\n" +
                "  `val_ext_c1` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c1',\n" +
                "  `val_ext_c2` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c2',\n" +
                "  `val_ext_c3` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c3',\n" +
                "  `val_ext_c4` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c4',\n" +
                "  `val_ext_c5` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c5',\n" +
                "  `val_ext_c6` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c6',\n" +
                "  `val_ext_c7` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c7',\n" +
                "  `val_ext_c8` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c8',\n" +
                "  `val_ext_c9` decimal(42,20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c9',\n" +
                "  `str_ext_c0` varchar(128) DEFAULT NULL COMMENT 'str_ext_c0',\n" +
                "  `str_ext_c1` varchar(128) DEFAULT NULL COMMENT 'str_ext_c1',\n" +
                "  `str_ext_c2` varchar(128) DEFAULT NULL COMMENT 'str_ext_c2',\n" +
                "  `str_ext_c3` varchar(128) DEFAULT NULL COMMENT 'str_ext_c3',\n" +
                "  `str_ext_c4` varchar(128) DEFAULT NULL COMMENT 'str_ext_c4',\n" +
                "  `str_ext_c5` varchar(128) DEFAULT NULL COMMENT 'str_ext_c5',\n" +
                "  `str_ext_c6` varchar(128) DEFAULT NULL COMMENT 'str_ext_c6',\n" +
                "  `str_ext_c7` varchar(128) DEFAULT NULL COMMENT 'str_ext_c7',\n" +
                "  `str_ext_c8` varchar(128) DEFAULT NULL COMMENT 'str_ext_c8',\n" +
                "  `str_ext_c9` varchar(128) DEFAULT NULL COMMENT 'str_ext_c9',\n" +
                "  `str_ext_c10` varchar(128) DEFAULT NULL COMMENT 'str_ext_c10',\n" +
                "  `str_ext_c11` varchar(128) DEFAULT NULL COMMENT 'str_ext_c11',\n" +
                "  `str_ext_c12` varchar(128) DEFAULT NULL COMMENT 'str_ext_c12',\n" +
                "  `str_ext_c13` varchar(128) DEFAULT NULL COMMENT 'str_ext_c13',\n" +
                "  `str_ext_c14` varchar(128) DEFAULT NULL COMMENT 'str_ext_c14',\n" +
                "  PRIMARY KEY (`kd`) COMMENT 'index_key_ins_perf_6519',\n" +
                "  KEY `kdx_time` (`time`) COMMENT 'index_perf_time_6519',\n" +
                "  KEY `kdx_instance_time` (`kp`,`port`,`scope`,`time`) COMMENT 'index_ins_perf_inst_time_6519',\n" +
                "  KEY `kdx_instance_hash` (`kp`,`port`,`hashcode`,`scope`,`time`) COMMENT 'index_ins_perf_hash_6519',\n" +
                "  KEY `kdx_instance_all_dim` (`kp`,`port`,`db`,`logical_table`,`physical_table`,`sql_type`,`hashcode`,`scope`,`time`) COMMENT 'index_ins_perf_all_6519'\n" +
                ") ENGINE=XENGINE AUTO_INCREMENT=339181584 DEFAULT CHARSET=utf8mb4 COMMENT='sql_ins_perf'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(63, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE `xktv_perf_6519` (\n" +
                "\t`kd` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "\t`gmt_create` datetime NOT NULL COMMENT '创建时间',\n" +
                "\t`time` datetime NOT NULL COMMENT '统计的时间',\n" +
                "\t`scope` int(11) NOT NULL COMMENT '时间粒度',\n" +
                "\t`knstance_id` varchar(128) DEFAULT NULL COMMENT '实例id',\n" +
                "\t`kp` varchar(16) NOT NULL COMMENT 'ip',\n" +
                "\t`port` int(11) NOT NULL COMMENT 'port',\n" +
                "\t`db` varchar(42) NOT NULL COMMENT 'db',\n" +
                "\t`hashcode` varchar(32) NOT NULL COMMENT 'hashcode',\n" +
                "\t`sql_type` varchar(6) NOT NULL COMMENT 'sql_type',\n" +
                "\t`logical_table` varchar(128) DEFAULT NULL COMMENT '逻辑表名',\n" +
                "\t`physical_table` varchar(128) DEFAULT NULL COMMENT '物理表名',\n" +
                "\t`request_count` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT '执行次数',\n" +
                "\t`rows_examined` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rows_examined',\n" +
                "\t`rows_sent` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rows_sent',\n" +
                "\t`rows_affected` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rows_affected',\n" +
                "\t`knnodb_pages_read` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'innodb_pages_read',\n" +
                "\t`knnodb_pages_io_read` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'innodb_pages_io_read',\n" +
                "\t`rt_count` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rt_count',\n" +
                "\t`query_time` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'query_time',\n" +
                "\t`wait_time` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'wait_time',\n" +
                "\t`lock_time` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'lock_time',\n" +
                "\t`rt_1ms_count` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rt_1ms_count',\n" +
                "\t`rt_2ms_count` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rt_2ms_count',\n" +
                "\t`rt_3ms_count` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rt_3ms_count',\n" +
                "\t`rt_10ms_count` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rt_10ms_count',\n" +
                "\t`rt_100ms_count` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rt_100ms_count',\n" +
                "\t`rt_1s_count` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rt_1s_count',\n" +
                "\t`rt_gt1s_count` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'rt_gt1s_count',\n" +
                "\t`avg_90_rt` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'avg_90_rt',\n" +
                "\t`avg_95_rt` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'avg_95_rt',\n" +
                "\t`avg_99_rt` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'avg_99_rt',\n" +
                "\t`avg_rt` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'avg_rt',\n" +
                "\t`val_ext_c0` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c0',\n" +
                "\t`val_ext_c1` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c1',\n" +
                "\t`val_ext_c2` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c2',\n" +
                "\t`val_ext_c3` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c3',\n" +
                "\t`val_ext_c4` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c4',\n" +
                "\t`val_ext_c5` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c5',\n" +
                "\t`val_ext_c6` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c6',\n" +
                "\t`val_ext_c7` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c7',\n" +
                "\t`val_ext_c8` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c8',\n" +
                "\t`val_ext_c9` decimal(42, 20) DEFAULT '0.00000000000000000000' COMMENT 'val_ext_c9',\n" +
                "\t`str_ext_c0` varchar(128) DEFAULT NULL COMMENT 'str_ext_c0',\n" +
                "\t`str_ext_c1` varchar(128) DEFAULT NULL COMMENT 'str_ext_c1',\n" +
                "\t`str_ext_c2` varchar(128) DEFAULT NULL COMMENT 'str_ext_c2',\n" +
                "\t`str_ext_c3` varchar(128) DEFAULT NULL COMMENT 'str_ext_c3',\n" +
                "\t`str_ext_c4` varchar(128) DEFAULT NULL COMMENT 'str_ext_c4',\n" +
                "\t`str_ext_c5` varchar(128) DEFAULT NULL COMMENT 'str_ext_c5',\n" +
                "\t`str_ext_c6` varchar(128) DEFAULT NULL COMMENT 'str_ext_c6',\n" +
                "\t`str_ext_c7` varchar(128) DEFAULT NULL COMMENT 'str_ext_c7',\n" +
                "\t`str_ext_c8` varchar(128) DEFAULT NULL COMMENT 'str_ext_c8',\n" +
                "\t`str_ext_c9` varchar(128) DEFAULT NULL COMMENT 'str_ext_c9',\n" +
                "\t`str_ext_c10` varchar(128) DEFAULT NULL COMMENT 'str_ext_c10',\n" +
                "\t`str_ext_c11` varchar(128) DEFAULT NULL COMMENT 'str_ext_c11',\n" +
                "\t`str_ext_c12` varchar(128) DEFAULT NULL COMMENT 'str_ext_c12',\n" +
                "\t`str_ext_c13` varchar(128) DEFAULT NULL COMMENT 'str_ext_c13',\n" +
                "\t`str_ext_c14` varchar(128) DEFAULT NULL COMMENT 'str_ext_c14',\n" +
                "\tPRIMARY KEY (`kd`) COMMENT 'index_key_ins_perf_6519',\n" +
                "\tKEY `kdx_time` (`time`) COMMENT 'index_perf_time_6519',\n" +
                "\tKEY `kdx_instance_time` (`kp`, `port`, `scope`, `time`) COMMENT 'index_ins_perf_inst_time_6519',\n" +
                "\tKEY `kdx_instance_hash` (`kp`, `port`, `hashcode`, `scope`, `time`) COMMENT 'index_ins_perf_hash_6519',\n" +
                "\tKEY `kdx_instance_all_dim` (`kp`, `port`, `db`, `logical_table`, `physical_table`, `sql_type`, `hashcode`, `scope`, `time`) COMMENT 'index_ins_perf_all_6519'\n" +
                ") ENGINE = XENGINE AUTO_INCREMENT = 339181584 CHARSET = utf8mb4 COMMENT 'sql_ins_perf'", stmt.toString());
    }
}
