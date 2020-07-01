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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;


public class MySqlSelectTest_241 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select job_names.job_name from cover_rage_job_org as org RIGHT JOIN (\n" +
                " select job_names.job_name as job_name\n" +
                "    from (\n" +
                "    SELECT '1' as job_name\n" +
                ") job_name_all) as job_names on job_names.job_name = org.job_name";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("SELECT job_names.job_name\n" +
                "FROM cover_rage_job_org org\n" +
                "\tRIGHT JOIN (\n" +
                "\t\tSELECT job_names.job_name AS job_name\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT '1' AS job_name\n" +
                "\t\t) job_name_all\n" +
                "\t) job_names\n" +
                "\tON job_names.job_name = org.job_name", stmt.toString());


        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
    }



}