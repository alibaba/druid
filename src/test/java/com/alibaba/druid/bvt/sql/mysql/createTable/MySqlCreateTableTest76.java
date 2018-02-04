/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;
import org.junit.Test;

public class MySqlCreateTableTest76 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE `sys_msg_entry_0320` (\n" +
                "  `provider_dsp_name` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '消息提供者的显示名，提供给一些需要统一发送名称的系统消息',\n" +
                "  `template_data` varchar(2048) NOT NULL /*!50616 COLUMN_FORMAT COMPRESSED */ COMMENT '模板渲染时使用的数据,key-value对',\n" +
                "  `template_merge_data` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '对于需要合并的消息，被合并的参数放在此处理',\n" +
                "  `expiration_date` datetime NOT NULL COMMENT '失效日期，精度到天,查询和任务处理时使用',\n" +
                "  `merge_key` varchar(128) DEFAULT NULL COMMENT '消息合并主键，合并成功的消息，不参与计数',\n" +
                "  `out_id` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '外部关联源的id',\n" +
                "  `number_expiration_date` datetime DEFAULT NULL COMMENT '计数过期时间',\n" +
                "  `hidden` int(11) DEFAULT '0' COMMENT '标记是否在吊顶展示，1表示隐藏，0表示展示',\n" +
                "  `popup` tinyint(3) unsigned DEFAULT '0' COMMENT '表示消息提醒方式:0-数字提醒，1-layer，2-popup',\n" +
                "  `tag_id` bigint(20) unsigned DEFAULT NULL COMMENT 'TAG标志',\n" +
                "  `attribute` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '消息的一些属性',\n" +
                "  `original_msg_ids` varchar(1024) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '原始消息ID，多个id用半角逗号隔开',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `idx_expiration_date` (`expiration_date`),\n" +
                "  KEY `k_rid_hidd_cid` (`receiver_id`,`hidden`,`cat_id`,`expiration_date`),\n" +
                "  KEY `k_rid_aid_tid` (`receiver_id`,`app_id`,`type_id`,`expiration_date`),\n" +
                "  KEY `k_rid_stat_popup` (`receiver_id`,`status`,`popup`,`expiration_date`),\n" +
                "  KEY `k_tid` (`tag_id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=167279613030 DEFAULT CHARSET=gbk COMMENT='消息实体表'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        Column column = visitor.getColumn("sys_msg_entry_0320", "provider_dsp_name");
        Assert.assertNotNull(column);
        Assert.assertEquals("varchar", column.getDataType());

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE `sys_msg_entry_0320` (\n" +
                    "\t`provider_dsp_name` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '消息提供者的显示名，提供给一些需要统一发送名称的系统消息',\n" +
                    "\t`template_data` varchar(2048) NOT NULL /*!50616 COLUMN_FORMAT COMPRESSED */ COMMENT '模板渲染时使用的数据,key-value对',\n" +
                    "\t`template_merge_data` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '对于需要合并的消息，被合并的参数放在此处理',\n" +
                    "\t`expiration_date` datetime NOT NULL COMMENT '失效日期，精度到天,查询和任务处理时使用',\n" +
                    "\t`merge_key` varchar(128) DEFAULT NULL COMMENT '消息合并主键，合并成功的消息，不参与计数',\n" +
                    "\t`out_id` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '外部关联源的id',\n" +
                    "\t`number_expiration_date` datetime DEFAULT NULL COMMENT '计数过期时间',\n" +
                    "\t`hidden` int(11) DEFAULT '0' COMMENT '标记是否在吊顶展示，1表示隐藏，0表示展示',\n" +
                    "\t`popup` tinyint(3) UNSIGNED DEFAULT '0' COMMENT '表示消息提醒方式:0-数字提醒，1-layer，2-popup',\n" +
                    "\t`tag_id` bigint(20) UNSIGNED DEFAULT NULL COMMENT 'TAG标志',\n" +
                    "\t`attribute` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '消息的一些属性',\n" +
                    "\t`original_msg_ids` varchar(1024) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT '原始消息ID，多个id用半角逗号隔开',\n" +
                    "\tPRIMARY KEY (`id`),\n" +
                    "\tKEY `idx_expiration_date` (`expiration_date`),\n" +
                    "\tKEY `k_rid_hidd_cid` (`receiver_id`, `hidden`, `cat_id`, `expiration_date`),\n" +
                    "\tKEY `k_rid_aid_tid` (`receiver_id`, `app_id`, `type_id`, `expiration_date`),\n" +
                    "\tKEY `k_rid_stat_popup` (`receiver_id`, `status`, `popup`, `expiration_date`),\n" +
                    "\tKEY `k_tid` (`tag_id`)\n" +
                    ") ENGINE = InnoDB AUTO_INCREMENT = 167279613030 CHARSET = gbk COMMENT '消息实体表'", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table `sys_msg_entry_0320` (\n" +
                    "\t`provider_dsp_name` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment '消息提供者的显示名，提供给一些需要统一发送名称的系统消息',\n" +
                    "\t`template_data` varchar(2048) not null /*!50616 COLUMN_FORMAT COMPRESSED */ comment '模板渲染时使用的数据,key-value对',\n" +
                    "\t`template_merge_data` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment '对于需要合并的消息，被合并的参数放在此处理',\n" +
                    "\t`expiration_date` datetime not null comment '失效日期，精度到天,查询和任务处理时使用',\n" +
                    "\t`merge_key` varchar(128) default null comment '消息合并主键，合并成功的消息，不参与计数',\n" +
                    "\t`out_id` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment '外部关联源的id',\n" +
                    "\t`number_expiration_date` datetime default null comment '计数过期时间',\n" +
                    "\t`hidden` int(11) default '0' comment '标记是否在吊顶展示，1表示隐藏，0表示展示',\n" +
                    "\t`popup` tinyint(3) unsigned default '0' comment '表示消息提醒方式:0-数字提醒，1-layer，2-popup',\n" +
                    "\t`tag_id` bigint(20) unsigned default null comment 'TAG标志',\n" +
                    "\t`attribute` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment '消息的一些属性',\n" +
                    "\t`original_msg_ids` varchar(1024) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment '原始消息ID，多个id用半角逗号隔开',\n" +
                    "\tprimary key (`id`),\n" +
                    "\tkey `idx_expiration_date` (`expiration_date`),\n" +
                    "\tkey `k_rid_hidd_cid` (`receiver_id`, `hidden`, `cat_id`, `expiration_date`),\n" +
                    "\tkey `k_rid_aid_tid` (`receiver_id`, `app_id`, `type_id`, `expiration_date`),\n" +
                    "\tkey `k_rid_stat_popup` (`receiver_id`, `status`, `popup`, `expiration_date`),\n" +
                    "\tkey `k_tid` (`tag_id`)\n" +
                    ") engine = InnoDB auto_increment = 167279613030 charset = gbk comment '消息实体表'", output);
        }
    }
}
