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
package com.alibaba.druid.bvt.sql.oracle.create;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTableTest39 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE customers_demo ("
        + "  customer_id number(6),"
        + "  cust_first_name varchar2(20),"
        + "  cust_last_name varchar2(20),"
        + "  credit_limit number(9,2))"
        + "PARTITION BY RANGE (credit_limit)"
        + "INTERVAL (1000)"
        + "(PARTITION p1 VALUES LESS THAN (5001));";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE customers_demo (\n" +
                        "\tcustomer_id number(6),\n" +
                        "\tcust_first_name varchar2(20),\n" +
                        "\tcust_last_name varchar2(20),\n" +
                        "\tcredit_limit number(9, 2)\n" +
                        ")\n" +
                        "PARTITION BY RANGE (credit_limit) INTERVAL (1000) (\n" +
                        "\tPARTITION p1 VALUES LESS THAN (5001)\n" +
                        ");",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(4, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("customers_demo", "customer_id")));
    }
}
