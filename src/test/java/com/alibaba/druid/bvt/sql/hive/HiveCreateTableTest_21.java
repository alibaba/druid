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

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class HiveCreateTableTest_21 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "create table if not exists aliyun_cdm.test_903_table  (\n" +
                        "col1 TINYINT,col2 SMALLINT,col3 INT,col4 BIGINT,col5 BOOLEAN\n" +
                        ",col6 FLOAT,col7 DOUBLE,col8 DOUBLE PRECISION,col9 STRING,col10 BINARY\n" +
                        ",col11 TIMESTAMP,col12 DECIMAL,col13 DECIMAL(10,2),col13 DATE,col14 VARCHAR,col15 CHAR\n" +
                        ",col16 ARRAY<STRING>,col17 MAP<STRING,INT>,col18 STRUCT<col19:STRING comment 'column19'>,col20 UNIONTYPE<STRING,INT>\n" +
                        ") clustered by (PAR1,PAR2) INTO 32 BUCKETS\n"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE TABLE IF NOT EXISTS aliyun_cdm.test_903_table (\n" +
                    "\tcol1 TINYINT,\n" +
                    "\tcol2 SMALLINT,\n" +
                    "\tcol3 INT,\n" +
                    "\tcol4 BIGINT,\n" +
                    "\tcol5 BOOLEAN,\n" +
                    "\tcol6 FLOAT,\n" +
                    "\tcol7 DOUBLE,\n" +
                    "\tcol8 DOUBLE PRECISION,\n" +
                    "\tcol9 STRING,\n" +
                    "\tcol10 BINARY,\n" +
                    "\tcol11 TIMESTAMP,\n" +
                    "\tcol12 DECIMAL,\n" +
                    "\tcol13 DECIMAL(10, 2),\n" +
                    "\tcol13 DATE,\n" +
                    "\tcol14 VARCHAR,\n" +
                    "\tcol15 CHAR,\n" +
                    "\tcol16 ARRAY<STRING>,\n" +
                    "\tcol17 MAP<STRING, INT>,\n" +
                    "\tcol18 STRUCT<col19:STRING>,\n" +
                    "\tcol20 UNIONTYPE<STRING, INT>\n" +
                    ")\n" +
                    "CLUSTERED BY (PAR1,PAR2)\n" +
                    "INTO 32 BUCKETS", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(19, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("aliyun_cdm.test_903_table"));

    }
}
