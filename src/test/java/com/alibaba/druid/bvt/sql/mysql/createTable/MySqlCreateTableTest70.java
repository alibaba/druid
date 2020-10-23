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
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;
import org.junit.Test;

public class MySqlCreateTableTest70 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE `app_customer_license` ("
                + "  `id`  bigint(20) NOT NULL AUTO_INCREMENT ,"
                + "  `created_by`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,"
                + "  `created_date`  datetime NOT NULL ,"
                + "  `last_modified_by`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,"
                + "  `last_modified_date`  datetime NULL DEFAULT NULL ,"
                + "  `version`  bigint(20) NOT NULL ,"
                + "  `device_id`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,"
                + "  `customer_info`  bigint(20) NULL DEFAULT NULL ,"
                + "  PRIMARY KEY (`id`),"
                + "  FOREIGN KEY (`customer_info`) REFERENCES `app_customer_info` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,"
                + "  INDEX `fk_app_customer_info_id` (`customer_info`) USING BTREE,"
                + "  UNIQUE `idx_app_customer_license_deviceId` (`device_id`) USING BTREE"
                + ")"
                + "ENGINE=InnoDB "
                + "DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci "
                + "AUTO_INCREMENT=1 "
                + "ROW_FORMAT=DYNAMIC "
                + ";;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        Column column = visitor.getColumn("app_customer_license", "version");
        Assert.assertNotNull(column);
        Assert.assertEquals("bigint", column.getDataType());

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE `app_customer_license` ("
                    + "\n\t`id` bigint(20) NOT NULL AUTO_INCREMENT,"
                    + "\n\t`created_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,"
                    + "\n\t`created_date` datetime NOT NULL,"
                    + "\n\t`last_modified_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,"
                    + "\n\t`last_modified_date` datetime NULL DEFAULT NULL,"
                    + "\n\t`version` bigint(20) NOT NULL,"
                    + "\n\t`device_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,"
                    + "\n\t`customer_info` bigint(20) NULL DEFAULT NULL,"
                    + "\n\tPRIMARY KEY (`id`),"
                    + "\n\tFOREIGN KEY (`customer_info`) REFERENCES `app_customer_info` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,"
                    + "\n\tINDEX `fk_app_customer_info_id` USING BTREE(`customer_info`),"
                    + "\n\tUNIQUE `idx_app_customer_license_deviceId` USING BTREE (`device_id`)"
                    + "\n) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci AUTO_INCREMENT = 1 ROW_FORMAT = DYNAMIC", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table `app_customer_license` ("
                    + "\n\t`id` bigint(20) not null auto_increment,"
                    + "\n\t`created_by` varchar(50) character set utf8 collate utf8_general_ci not null,"
                    + "\n\t`created_date` datetime not null,"
                    + "\n\t`last_modified_by` varchar(50) character set utf8 collate utf8_general_ci null default null,"
                    + "\n\t`last_modified_date` datetime null default null,"
                    + "\n\t`version` bigint(20) not null,"
                    + "\n\t`device_id` varchar(20) character set utf8 collate utf8_general_ci not null,"
                    + "\n\t`customer_info` bigint(20) null default null,"
                    + "\n\tprimary key (`id`),"
                    + "\n\tforeign key (`customer_info`) references `app_customer_info` (`id`) on delete restrict on update restrict,"
                    + "\n\tindex `fk_app_customer_info_id` using BTREE(`customer_info`),"
                    + "\n\tunique `idx_app_customer_license_deviceId` using BTREE (`device_id`)"
                    + "\n) engine = InnoDB character set = utf8 collate = utf8_general_ci auto_increment = 1 row_format = DYNAMIC", output);
        }
    }
}
