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
package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlParameterizedOutputVisitorTest_5 extends MySQLParameterizedTest {

    public void test_0() throws Exception {
        String sql = "ALTER TABLE `action_plans` ADD `kee` varchar(100)";
        assertEquals("ALTER TABLE `action_plans`\n" +
                "\tADD COLUMN `kee` varchar(100)", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }

    public void test_1() throws Exception {
        String sql = "CREATE TABLE `snapshot_data` (`id` int(11) DEFAULT NULL auto_increment PRIMARY KEY, `snapshot_id` integer, `resource_id` integer, `snapshot_data` mediumtext, `data_type` varchar(50), `created_at` datetime, `updated_at` datetime) ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_bin";
        assertEquals("CREATE TABLE `snapshot_data` (\n" +
                "\t`id` int(11) PRIMARY KEY DEFAULT NULL AUTO_INCREMENT,\n" +
                "\t`snapshot_id` integer,\n" +
                "\t`resource_id` integer,\n" +
                "\t`snapshot_data` mediumtext,\n" +
                "\t`data_type` varchar(50),\n" +
                "\t`created_at` datetime,\n" +
                "\t`updated_at` datetime\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }

    public void test_2() throws Exception {
        String sql = "ALTER TABLE `active_rule_changes` ADD `username` varchar(200)";
        assertEquals("ALTER TABLE `active_rule_changes`\n" +
                "\tADD COLUMN `username` varchar(200)", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }

    public void test_3() throws Exception {
        String sql = "CREATE TABLE `issue_changes` (`id` int(11) DEFAULT NULL auto_increment PRIMARY KEY, `kee` varchar(50), `issue_key` varchar(50) NOT NULL, `user_login` varchar(40), `change_type` varchar(20), `change_data` mediumtext, `created_at` datetime, `updated_at` datetime) ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_bin";
        assertEquals("CREATE TABLE `issue_changes` (\n" +
                "\t`id` int(11) PRIMARY KEY DEFAULT NULL AUTO_INCREMENT,\n" +
                "\t`kee` varchar(50),\n" +
                "\t`issue_key` varchar(50) NOT NULL,\n" +
                "\t`user_login` varchar(40),\n" +
                "\t`change_type` varchar(20),\n" +
                "\t`change_data` mediumtext,\n" +
                "\t`created_at` datetime,\n" +
                "\t`updated_at` datetime\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin", ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));
    }
}
