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
package com.alibaba.druid.bvt.sql.oracle.createTable;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTableTest70 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "  create table month_part (c1 number,c3 date)\n" +
                "partition by range(c3)\n" +
                "interval(numtoyminterval (1,'month'))\n" +
                "(partition part1 values less than (to_date('2010-01-01','YYYY-MM-DD')),\n" +
                "partition part2 values less than (to_date('2010-02-01','YYYY-MM-DD'))\n" +
                ") ";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());
//
        assertEquals("CREATE TABLE month_part (\n" +
                        "\tc1 number,\n" +
                        "\tc3 date\n" +
                        ")\n" +
                        "PARTITION BY RANGE (c3) INTERVAL (numtoyminterval(1, 'month')) (\n" +
                        "\tPARTITION part1 VALUES LESS THAN (to_date('2010-01-01', 'YYYY-MM-DD')),\n" +
                        "\tPARTITION part2 VALUES LESS THAN (to_date('2010-02-01', 'YYYY-MM-DD'))\n" +
                        ")",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));
//
//        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
//        stmt.accept(visitor);
//
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
//
//        assertEquals(1, visitor.getTables().size());
//
//        assertEquals(3, visitor.getColumns().size());
//
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("JWGZPT.A", "XM")));
    }
}
