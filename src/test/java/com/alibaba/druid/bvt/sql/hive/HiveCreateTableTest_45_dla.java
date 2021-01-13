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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;

public class HiveCreateTableTest_45_dla extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE EXTERNAL TABLE IF NOT EXISTS `bfbf8508d0a59644fe2003c7506f3676`.`device_group_relation` ( `device_name` VARCHAR NOT NULL COMMENT 'name,?meta device??', `gmt_create` TIMESTAMP NOT NULL COMMENT '????', `gmt_modified` TIMESTAMP NOT NULL COMMENT '????', `group_id` VARCHAR NOT NULL COMMENT 'group id', `group_relation_status` VARCHAR NOT NULL COMMENT 'inital 0, 1 isDeleted', `group_type` VARCHAR NOT NULL COMMENT 'group type, \"ISOLATION\", \"\"', `id` BIGINT NOT NULL COMMENT '??', `iot_id` VARCHAR NOT NULL COMMENT 'iot_id', `product_key` VARCHAR NOT NULL COMMENT 'product key', `tenant_id` VARCHAR NULL COMMENT 'rbac tenant_id' ) TBLPROPERTIES ( 'TABLE_MAPPING' = `device_group_relation` )\n";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive, SQLParserFeature.KeepComments);

        SQLStatement stmt = statementList.get(0);
        assertEquals("CREATE EXTERNAL TABLE IF NOT EXISTS `bfbf8508d0a59644fe2003c7506f3676`.`device_group_relation` (\n" +
                "\t`device_name` VARCHAR NOT NULL COMMENT 'name,?meta device??',\n" +
                "\t`gmt_create` TIMESTAMP NOT NULL COMMENT '????',\n" +
                "\t`gmt_modified` TIMESTAMP NOT NULL COMMENT '????',\n" +
                "\t`group_id` VARCHAR NOT NULL COMMENT 'group id',\n" +
                "\t`group_relation_status` VARCHAR NOT NULL COMMENT 'inital 0, 1 isDeleted',\n" +
                "\t`group_type` VARCHAR NOT NULL COMMENT 'group type, \"ISOLATION\", \"\"',\n" +
                "\t`id` BIGINT NOT NULL COMMENT '??',\n" +
                "\t`iot_id` VARCHAR NOT NULL COMMENT 'iot_id',\n" +
                "\t`product_key` VARCHAR NOT NULL COMMENT 'product key',\n" +
                "\t`tenant_id` VARCHAR NULL COMMENT 'rbac tenant_id'\n" +
                ")\n" +
                "TBLPROPERTIES (\n" +
                "\t'TABLE_MAPPING' = `device_group_relation`\n" +
                ")", stmt.toString());
    }

}
