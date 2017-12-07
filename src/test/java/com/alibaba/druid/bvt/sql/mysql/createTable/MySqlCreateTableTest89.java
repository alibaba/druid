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
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class MySqlCreateTableTest89 extends MysqlTest {

    public void test_one() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS `test_table_normal`(  \n" +
                "scoreID INTEGER NOT NULL PRIMARY KEY,  \n" +
                "stuID     INTEGER NOT NULL,  \n" +
                "KEMUID     INTEGER NOT NULL,  \n" +
                "score     FLOAT,  \n" +
                "FOREIGN KEY SCORE_ID_FK (stuID) REFERENCES students (stuid),  \n" +
                "CONSTRAINT CHK_SCORE_ZIP CHECK (SCORE > 0)  \n" +
                ");  ";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.KeepComments);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        visitor.containsTable("t_share_like_info");
//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        System.out.println(stmt);

        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("CREATE TABLE IF NOT EXISTS `test_table_normal` (\n" +
                    "\tscoreID INTEGER NOT NULL PRIMARY KEY,\n" +
                    "\tstuID INTEGER NOT NULL,\n" +
                    "\tKEMUID INTEGER NOT NULL,\n" +
                    "\tscore FLOAT,\n" +
                    "\tFOREIGN KEY SCORE_ID_FK (stuID) REFERENCES students (stuid),\n" +
                    "\tCONSTRAINT CHK_SCORE_ZIP CHECK (SCORE > 0)\n" +
                    ")", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("create table if not exists `test_table_normal` (\n" +
                    "\tscoreID INTEGER not null primary key,\n" +
                    "\tstuID INTEGER not null,\n" +
                    "\tKEMUID INTEGER not null,\n" +
                    "\tscore FLOAT,\n" +
                    "\tforeign key SCORE_ID_FK (stuID) references students (stuid),\n" +
                    "\tconstraint CHK_SCORE_ZIP check (SCORE > 0)\n" +
                    ")", output);
        }
    }
}
