package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest93 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `dc_job_instance` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `code` varchar(45) NOT NULL,\n" +
                "  `name` varchar(512) DEFAULT NULL,\n" +
                "  `cluster_code` varchar(64) DEFAULT NULL,\n" +
                "  `engine_inst_code` varchar(45) DEFAULT NULL,\n" +
                "  `dependent_code` varchar(1024) DEFAULT NULL COMMENT '依赖上一个实例code',\n" +
                "  `concurrent_rely` varchar(1024) DEFAULT NULL COMMENT '并发依赖',\n" +
                "  `job_text` longtext,\n" +
                "  `project_code` varchar(45) DEFAULT NULL COMMENT '项目code',\n" +
                "  `job_name` varchar(128) DEFAULT NULL COMMENT 'job名称',\n" +
                "  `job_code` varchar(45) DEFAULT NULL,\n" +
                "  `job_id` int(11) DEFAULT NULL,\n" +
                "  `job_type` varchar(45) DEFAULT NULL,\n" +
                "  `type` varchar(45) NOT NULL DEFAULT 'test' COMMENT '开发实例：dev\\n调度实例：schedule\\n测试实例：test\\n补数据实例：supplement',\n" +
                "  `version` int(11) NOT NULL DEFAULT '0',\n" +
                "  `status` int(11) NOT NULL DEFAULT '0' COMMENT '1：等待运行，3：等待资源，5：运行中，7：运行失败，9：运行成功， 11：暂停， 13：终止',\n" +
                "  `schedule_start` datetime DEFAULT NULL COMMENT '调度开始时间',\n" +
                "  `run_start` datetime DEFAULT NULL COMMENT '运行开始时间',\n" +
                "  `run_end` datetime DEFAULT NULL COMMENT '运行结束时间',\n" +
                "  `owner` varchar(64) NOT NULL,\n" +
                "  `bizdate` varchar(64) DEFAULT NULL COMMENT '业务日期：yyyy-MM-dd\\n',\n" +
                "  `rundate` varchar(64) DEFAULT NULL COMMENT '运行日期：yyyy-MM-dd',\n" +
                "  `run_mode` varchar(45) DEFAULT NULL COMMENT '运行模式：jobserver, thriftserver, phoenix、tidb',\n" +
                "  `runtimes` int(16) DEFAULT '0' COMMENT '运行时间，单位秒',\n" +
                "  `res_cu` decimal(20,5) DEFAULT '0.00000' COMMENT '计算消耗资源，单位cu',\n" +
                "  `retry_count` int(11) DEFAULT '0',\n" +
                "  `failure_count` int(11) DEFAULT NULL COMMENT '失败次数',\n" +
                "  `last_notify_time` datetime DEFAULT NULL COMMENT '最新一次告警通知时间',\n" +
                "  `alarm_count` int(11) DEFAULT '0' COMMENT '告警次数',\n" +
                "  `error_type` varchar(45) DEFAULT NULL COMMENT '系统异常：sys\\n作业异常：job',\n" +
                "  `spark_application_id` varchar(128) DEFAULT 'spark app id',\n" +
                "  `spark_group_id` varchar(128) DEFAULT NULL COMMENT 'Spark group id',\n" +
                "  `spark_web_url` varchar(256) DEFAULT NULL,\n" +
                "  `gmt_created` datetime NOT NULL,\n" +
                "  `gmt_modified` datetime DEFAULT NULL,\n" +
                "  `creater` varchar(45) NOT NULL,\n" +
                "  `modifier` varchar(45) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `code_UNIQUE` (`code`),\n" +
                "  KEY `project_code_index` (`project_code`),\n" +
                "  KEY `job_code_index` (`job_code`),\n" +
                "  KEY `type_index` (`type`),\n" +
                "  KEY `spark_app_id_index` (`spark_application_id`),\n" +
                "  FULLTEXT KEY `ft_job_text` (`job_text`,`spark_application_id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=23185 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(44, stmt.getTableElementList().size());
    }
}