package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTableTest138 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `t_push_branch3` (\n" +
                "  `id` varchar(50) NOT NULL COMMENT '主键id',\n" +
                "  `waybill_no` varchar(50) DEFAULT NULL COMMENT '运单号',\n" +
                "  `bill_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '开单时间',\n" +
                "  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  PRIMARY KEY (`id`,`create_time`),\n" +
                "  KEY `index_waybill_no` (`waybill_no`),\n" +
                "  KEY `index_create_time` (`create_time`)\n" +
                ") ENGINE=InnoDB COMMENT='网点剔除表'\n" +
                "PARTITION BY RANGE  COLUMNS(create_time)\n" +
                "(PARTITION P201812 VALUES LESS THAN ('2019-01-01') ENGINE = InnoDB,\n" +
                " PARTITION P201901 VALUES LESS THAN ('2019-02-01') ENGINE = InnoDB,\n" +
                " PARTITION P201902 VALUES LESS THAN ('2019-03-01') ENGINE = InnoDB,\n" +
                " PARTITION P201903 VALUES LESS THAN ('2019-04-01') ENGINE = InnoDB,\n" +
                " PARTITION P201904 VALUES LESS THAN ('2019-05-01') ENGINE = InnoDB,\n" +
                " PARTITION P201905 VALUES LESS THAN ('2019-06-01') ENGINE = InnoDB,\n" +
                " PARTITION P201906 VALUES LESS THAN ('2019-07-01') ENGINE = InnoDB,\n" +
                " PARTITION P201907 VALUES LESS THAN ('2019-08-01') ENGINE = InnoDB,\n" +
                " PARTITION P201908 VALUES LESS THAN ('2019-09-01') ENGINE = InnoDB,\n" +
                " PARTITION P201909 VALUES LESS THAN ('2019-10-01') ENGINE = InnoDB,\n" +
                " PARTITION P201910 VALUES LESS THAN ('2019-11-01') ENGINE = InnoDB,\n" +
                " PARTITION P201911 VALUES LESS THAN ('2019-12-01') ENGINE = InnoDB,\n" +
                " PARTITION P201912 VALUES LESS THAN ('2020-01-01') ENGINE = InnoDB,\n" +
                " PARTITION PMAX VALUES LESS THAN (MAXVALUE) ENGINE = InnoDB);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("CREATE TABLE `t_push_branch3` (\n" +
                "\t`id` varchar(50) NOT NULL COMMENT '主键id',\n" +
                "\t`waybill_no` varchar(50) DEFAULT NULL COMMENT '运单号',\n" +
                "\t`bill_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '开单时间',\n" +
                "\t`create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "\tPRIMARY KEY (`id`, `create_time`),\n" +
                "\tKEY `index_waybill_no` (`waybill_no`),\n" +
                "\tKEY `index_create_time` (`create_time`)\n" +
                ") ENGINE = InnoDB COMMENT '网点剔除表'\n" +
                "PARTITION BY RANGE COLUMNS (create_time) (\n" +
                "\tPARTITION P201812 VALUES LESS THAN ('2019-01-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201901 VALUES LESS THAN ('2019-02-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201902 VALUES LESS THAN ('2019-03-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201903 VALUES LESS THAN ('2019-04-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201904 VALUES LESS THAN ('2019-05-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201905 VALUES LESS THAN ('2019-06-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201906 VALUES LESS THAN ('2019-07-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201907 VALUES LESS THAN ('2019-08-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201908 VALUES LESS THAN ('2019-09-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201909 VALUES LESS THAN ('2019-10-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201910 VALUES LESS THAN ('2019-11-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201911 VALUES LESS THAN ('2019-12-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION P201912 VALUES LESS THAN ('2020-01-01')\n" +
                "\t\tSTORAGE ENGINE InnoDB,\n" +
                "\tPARTITION PMAX VALUES LESS THAN MAXVALUE\n" +
                "\t\tSTORAGE ENGINE InnoDB\n" +
                ");", stmt.toString());

        assertEquals("create table `t_push_branch3` (\n" +
                "\t`id` varchar(50) not null comment '主键id',\n" +
                "\t`waybill_no` varchar(50) default null comment '运单号',\n" +
                "\t`bill_time` datetime default null on update current_timestamp comment '开单时间',\n" +
                "\t`create_time` datetime not null on update current_timestamp comment '创建时间',\n" +
                "\tprimary key (`id`, `create_time`),\n" +
                "\tkey `index_waybill_no` (`waybill_no`),\n" +
                "\tkey `index_create_time` (`create_time`)\n" +
                ") engine = InnoDB comment '网点剔除表'\n" +
                "partition by range columns (create_time) (\n" +
                "\tpartition P201812 values less than ('2019-01-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201901 values less than ('2019-02-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201902 values less than ('2019-03-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201903 values less than ('2019-04-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201904 values less than ('2019-05-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201905 values less than ('2019-06-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201906 values less than ('2019-07-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201907 values less than ('2019-08-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201908 values less than ('2019-09-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201909 values less than ('2019-10-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201910 values less than ('2019-11-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201911 values less than ('2019-12-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition P201912 values less than ('2020-01-01')\n" +
                "\t\tstorage engine InnoDB,\n" +
                "\tpartition PMAX values less than maxvalue\n" +
                "\t\tstorage engine InnoDB\n" +
                ");", stmt.toLowerCaseString());

    }





}