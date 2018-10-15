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

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class MySqlCreateTableTest66 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "create table hp_db.g20_relationship_communication_daily(                   "
                + " a_iden_string    varchar,"
                + " b_iden_string    varchar,"
                + " counter          bigint,"
                + " durationtime     bigint"
                + ") "
                + "\nPARTITION BY HASH KEY(a_iden_string) PARTITION NUM 100"
                + "\nSUBPARTITION BY LIST(bdt bigint)"
                + "\nSUBPARTITION OPTIONS(available_Partition_Num=90)"
                + "\nTABLEGROUP g20_test_group;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE hp_db.g20_relationship_communication_daily ("
                    + "\n\ta_iden_string varchar,"
                    + "\n\tb_iden_string varchar,"
                    + "\n\tcounter bigint,"
                    + "\n\tdurationtime bigint"
                    + "\n)" 
                    + "\nPARTITION BY HASH KEY(a_iden_string) PARTITION NUM 100"
                    + "\nSUBPARTITION BY LIST (bdt bigint)"
                    + "\nSUBPARTITION OPTIONS (available_Partition_Num = 90)"
                    + "\nTABLEGROUP g20_test_group", output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table hp_db.g20_relationship_communication_daily ("
                    + "\n\ta_iden_string varchar,"
                    + "\n\tb_iden_string varchar,"
                    + "\n\tcounter bigint,"
                    + "\n\tdurationtime bigint"
                    + "\n)" 
                    + "\npartition by hash key(a_iden_string) partition num 100"
                    + "\nsubpartition by list (bdt bigint)"
                    + "\nsubpartition options (available_Partition_Num = 90)"
                    + "\ntablegroup g20_test_group", output);
        }
    }
}
