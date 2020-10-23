/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;
import org.junit.Test;

public class MySqlCreateTableTest54 extends MysqlTest {

    @Test
    public void test_UNSIGNED_ZEROFILL() throws Exception {
        String sql = "CREATE TABLE t1 (year YEAR(4), month INT(2) UNSIGNED ZEROFILL, day INT(2) UNSIGNED ZEROFILL);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE TABLE t1 (" + "\n\tyear YEAR(4)," + "\n\tmonth INT(2) UNSIGNED ZEROFILL,"
                            + "\n\tday INT(2) UNSIGNED ZEROFILL" + "\n)", output);

    }

    @Test
    public void test_FOREIGN_KEY() throws Exception {
        String sql = "CREATE TABLE `t_activity_node` ("
                     + "\n`id` bigint(20) NOT NULL,"
                     + "\n`sellerId` bigint(20) DEFAULT NULL,"
                     + "\n`canvas_id` bigint(20) NOT NULL COMMENT '画布ID',"
                     + "\n`view_node_id` bigint(20) NOT NULL COMMENT '对应显示的节点id',"
                     + "\n`activity_type` int(11) NOT NULL COMMENT '活动类型',"
                     + "\n`node_type` int(11) NOT NULL COMMENT '节点类型',"
                     + "\n`node_title` varchar(200) NOT NULL COMMENT '节点标题',"
                     + "\n`status` int(11) NOT NULL DEFAULT '0' COMMENT '页面的显示状态',"
                     + "\n`update_status` int(11) DEFAULT NULL COMMENT '节点创建后的修改状态',"
                     + "\n`execute_status` int(11) DEFAULT NULL COMMENT '节点当前的执行状态',"
                     + "\n`start_time` datetime DEFAULT NULL COMMENT '该节点活动的开始时间',"
                     + "\n`end_time` datetime DEFAULT NULL COMMENT '该节点活动的结束时间',"
                     + "\n`activity_start_time` datetime DEFAULT NULL COMMENT '营销活动的开始时间',"
                     + "\n`activity_end_time` datetime DEFAULT NULL COMMENT '营销活动的结束时间',"
                     + "\n`report_start_time` datetime DEFAULT NULL COMMENT '该节点活动效果报告的开始时间',"
                     + "\n`report_end_time` datetime DEFAULT NULL COMMENT '该节点活动效果报告的结束时间',"
                     + "\n`cron_rule` varchar(100) DEFAULT NULL COMMENT '周期性营销的时间表达式',"
                     + "\n`split_rule` varchar(1000) DEFAULT NULL COMMENT '节点拆分规则',"
                     + "\n`search_json` varchar(5000) DEFAULT NULL COMMENT '索引查找的条件字符串',"
                     + "\n`filter_json` varchar(1000) DEFAULT NULL COMMENT '各种过滤条件的设置',"
                     + "\n`buyer_count` int(11) DEFAULT NULL COMMENT '营销的会员数量',"
                     + "\n`gmt_modified` datetime NOT NULL COMMENT '活动最后修改时间',"
                     + "\n`gmt_create` datetime NOT NULL COMMENT '活动创建时间',"
                     + "\nPRIMARY KEY (`id`),"
                     + "\nKEY `canvas_id` (`canvas_id`),"
                     + "\nKEY `sid_ty_time` (`sellerId`,`node_type`,`start_time`),"
                     + "\nCONSTRAINT `t_activity_node_ibfk_1` FOREIGN KEY (`canvas_id`) REFERENCES `t_activity_canvas` (`id`) ON DELETE CASCADE"
                     + "\n) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(24, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE `t_activity_node` ("
                                    + "\n\t`id` bigint(20) NOT NULL,"
                                    + "\n\t`sellerId` bigint(20) DEFAULT NULL,"
                                    + "\n\t`canvas_id` bigint(20) NOT NULL COMMENT '画布ID',"
                                    + "\n\t`view_node_id` bigint(20) NOT NULL COMMENT '对应显示的节点id',"
                                    + "\n\t`activity_type` int(11) NOT NULL COMMENT '活动类型',"
                                    + "\n\t`node_type` int(11) NOT NULL COMMENT '节点类型',"
                                    + "\n\t`node_title` varchar(200) NOT NULL COMMENT '节点标题',"
                                    + "\n\t`status` int(11) NOT NULL DEFAULT '0' COMMENT '页面的显示状态',"
                                    + "\n\t`update_status` int(11) DEFAULT NULL COMMENT '节点创建后的修改状态',"
                                    + "\n\t`execute_status` int(11) DEFAULT NULL COMMENT '节点当前的执行状态',"
                                    + "\n\t`start_time` datetime DEFAULT NULL COMMENT '该节点活动的开始时间',"
                                    + "\n\t`end_time` datetime DEFAULT NULL COMMENT '该节点活动的结束时间',"
                                    + "\n\t`activity_start_time` datetime DEFAULT NULL COMMENT '营销活动的开始时间',"
                                    + "\n\t`activity_end_time` datetime DEFAULT NULL COMMENT '营销活动的结束时间',"
                                    + "\n\t`report_start_time` datetime DEFAULT NULL COMMENT '该节点活动效果报告的开始时间',"
                                    + "\n\t`report_end_time` datetime DEFAULT NULL COMMENT '该节点活动效果报告的结束时间',"
                                    + "\n\t`cron_rule` varchar(100) DEFAULT NULL COMMENT '周期性营销的时间表达式',"
                                    + "\n\t`split_rule` varchar(1000) DEFAULT NULL COMMENT '节点拆分规则',"
                                    + "\n\t`search_json` varchar(5000) DEFAULT NULL COMMENT '索引查找的条件字符串',"
                                    + "\n\t`filter_json` varchar(1000) DEFAULT NULL COMMENT '各种过滤条件的设置',"
                                    + "\n\t`buyer_count` int(11) DEFAULT NULL COMMENT '营销的会员数量',"
                                    + "\n\t`gmt_modified` datetime NOT NULL COMMENT '活动最后修改时间',"
                                    + "\n\t`gmt_create` datetime NOT NULL COMMENT '活动创建时间',"
                                    + "\n\tPRIMARY KEY (`id`),"
                                    + "\n\tKEY `canvas_id` (`canvas_id`),"
                                    + "\n\tKEY `sid_ty_time` (`sellerId`, `node_type`, `start_time`),"
                                    + "\n\tCONSTRAINT `t_activity_node_ibfk_1` FOREIGN KEY (`canvas_id`) REFERENCES `t_activity_canvas` (`id`) ON DELETE CASCADE"
                                    + "\n) ENGINE = InnoDB CHARSET = utf8", output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table `t_activity_node` ("
                                    + "\n\t`id` bigint(20) not null,"
                                    + "\n\t`sellerId` bigint(20) default null,"
                                    + "\n\t`canvas_id` bigint(20) not null comment '画布ID',"
                                    + "\n\t`view_node_id` bigint(20) not null comment '对应显示的节点id',"
                                    + "\n\t`activity_type` int(11) not null comment '活动类型',"
                                    + "\n\t`node_type` int(11) not null comment '节点类型',"
                                    + "\n\t`node_title` varchar(200) not null comment '节点标题',"
                                    + "\n\t`status` int(11) not null default '0' comment '页面的显示状态',"
                                    + "\n\t`update_status` int(11) default null comment '节点创建后的修改状态',"
                                    + "\n\t`execute_status` int(11) default null comment '节点当前的执行状态',"
                                    + "\n\t`start_time` datetime default null comment '该节点活动的开始时间',"
                                    + "\n\t`end_time` datetime default null comment '该节点活动的结束时间',"
                                    + "\n\t`activity_start_time` datetime default null comment '营销活动的开始时间',"
                                    + "\n\t`activity_end_time` datetime default null comment '营销活动的结束时间',"
                                    + "\n\t`report_start_time` datetime default null comment '该节点活动效果报告的开始时间',"
                                    + "\n\t`report_end_time` datetime default null comment '该节点活动效果报告的结束时间',"
                                    + "\n\t`cron_rule` varchar(100) default null comment '周期性营销的时间表达式',"
                                    + "\n\t`split_rule` varchar(1000) default null comment '节点拆分规则',"
                                    + "\n\t`search_json` varchar(5000) default null comment '索引查找的条件字符串',"
                                    + "\n\t`filter_json` varchar(1000) default null comment '各种过滤条件的设置',"
                                    + "\n\t`buyer_count` int(11) default null comment '营销的会员数量',"
                                    + "\n\t`gmt_modified` datetime not null comment '活动最后修改时间',"
                                    + "\n\t`gmt_create` datetime not null comment '活动创建时间',"
                                    + "\n\tprimary key (`id`),"
                                    + "\n\tkey `canvas_id` (`canvas_id`),"
                                    + "\n\tkey `sid_ty_time` (`sellerId`, `node_type`, `start_time`),"
                                    + "\n\tconstraint `t_activity_node_ibfk_1` foreign key (`canvas_id`) references `t_activity_canvas` (`id`) on delete cascade"
                                    + "\n) engine = InnoDB charset = utf8", output);
        }
    }

}
