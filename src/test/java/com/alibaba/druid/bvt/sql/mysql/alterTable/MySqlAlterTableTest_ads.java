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
package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterDatabaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterDatabaseSetOption;
import junit.framework.TestCase;

public class MySqlAlterTableTest_ads extends TestCase {

    public void test_alter_1() throws Exception {
        String sql = "ALTER TABLE grant_db.grant_table ADD INDEX user_id_index HashMap (grant_c1)\n";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER TABLE grant_db.grant_table\n" + "\tADD INDEX user_id_index HASHMAP (grant_c1)", output);
    }

    public void test_alter_2() throws Exception {
        String sql = "ALTER TABLE grant_db.grant_table  clustered by()\n";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER TABLE grant_db.grant_table\n" + "\tCLUSTERED BY ()", output);
    }

    public void test_alter_3() throws Exception {
        String sql = "ALTER TABLE grant_db.grant_table  subpartition_available_partition_num=10\n";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER TABLE grant_db.grant_table\n" + "\tSUBPARTITION_AVAILABLE_PARTITION_NUM = 10", output);
    }

    public void test_alter_4() throws Exception {
        String sql = "ALTER TABLEGROUP grant_db.group1  minRedundancy=10 k2=v2 executeTimeout=2000\n";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER TABLEGROUP grant_db.group1 minRedundancy = 10 k2 = v2 executeTimeout = 2000", output);
    }

    public void test_alter_5() throws Exception {
        String sql = "ALTER TABLE schema1.table1 ADD COLUMN col1 varchar default '10' not null primary key";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER TABLE schema1.table1\n" + "\tADD COLUMN col1 varchar NOT NULL PRIMARY KEY DEFAULT '10'", output);
    }

    public void test_alter_6() throws Exception {
        String sql = "alter database test_db set ecu_count=2";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER DATABASE test_db SET ecu_count = 2", output);
    }

    public void test_alter_7() throws Exception {
        String sql = "ALTER TABLE grant_db.grant_table ADD INDEX user_id_index HashMap (grant_c1)";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER TABLE grant_db.grant_table\n" + "\tADD INDEX user_id_index HASHMAP (grant_c1)", output);
    }

    public void test_alter_8() throws Exception {
        String sql = "ALTER TABLE grant_db.grant_table ADD column col2 varchar default '10' not null primary key";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER TABLE grant_db.grant_table\n"
                     + "\tADD COLUMN col2 varchar NOT NULL PRIMARY KEY DEFAULT '10'", output);
    }

    public void test_alter_9() throws Exception {
        String sql = "alter database test_db set ecu_count=2";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER DATABASE test_db SET ecu_count = 2", output);
    }

    public void test_alter_10() throws Exception {
        String sql = "alter database ads_cd_pre2 set resource_type='ecu' ecu_type=c8 ecu_count=2 modify_resource_type=true";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("ALTER DATABASE ads_cd_pre2 SET resource_type = 'ecu', ecu_type = c8, ecu_count = 2, modify_resource_type = true", output);

        SQLExpr modify_resource_type = ((MySqlAlterDatabaseSetOption) ((SQLAlterDatabaseStatement) stmt).getItem()).getOption(
                "modify_resource_type");

        assertEquals("true", modify_resource_type.toString());
    }
}
