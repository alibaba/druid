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

public class OracleCreateTableTest38 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE list_customers "
        + "   ( customer_id             NUMBER(6)"
        + "   , cust_first_name         VARCHAR2(20) "
        + "   , cust_last_name          VARCHAR2(20)"
        + "   , cust_address            CUST_ADDRESS_TYP"
        + "   , nls_territory           VARCHAR2(30)"
        + "   , cust_email              VARCHAR2(40))"
        + "   PARTITION BY LIST (nls_territory) ("
        + "   PARTITION asia VALUES ('CHINA', 'THAILAND'),"
        + "   PARTITION europe VALUES ('GERMANY', 'ITALY', 'SWITZERLAND'),"
        + "   PARTITION west VALUES ('AMERICA'),"
        + "   PARTITION east VALUES ('INDIA'),"
        + "   PARTITION rest VALUES (DEFAULT));";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE list_customers (\n" +
                        "\tcustomer_id NUMBER(6),\n" +
                        "\tcust_first_name VARCHAR2(20),\n" +
                        "\tcust_last_name VARCHAR2(20),\n" +
                        "\tcust_address CUST_ADDRESS_TYP,\n" +
                        "\tnls_territory VARCHAR2(30),\n" +
                        "\tcust_email VARCHAR2(40)\n" +
                        ")\n" +
                        "PARTITION BY LIST (nls_territory) (\n" +
                        "\tPARTITION asia VALUES ('CHINA', 'THAILAND'), \n" +
                        "\tPARTITION europe VALUES ('GERMANY', 'ITALY', 'SWITZERLAND'), \n" +
                        "\tPARTITION west VALUES ('AMERICA'), \n" +
                        "\tPARTITION east VALUES ('INDIA'), \n" +
                        "\tPARTITION rest VALUES (DEFAULT)\n" +
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

        Assert.assertEquals(6, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("list_customers", "customer_id")));
    }
}
