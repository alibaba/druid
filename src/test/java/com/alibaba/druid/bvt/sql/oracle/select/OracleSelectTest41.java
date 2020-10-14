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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest41 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "WITH RESULTVIEW AS" + //
                " (" + //
                "  SELECT" + //
                "   AA.USERID," + //
                "    DECODE(AA.USERTYPE," + //
                "           '1'," + //
                "           (SELECT ORG_TABLE.ORGNAME" + //
                "              FROM ORG_TABLE" + //
                "             WHERE ORG_TABLE.ORGID = AA.BELONGORG)," + //
                "           '2'," + //
                "           (SELECT CUST_TABLE.CUSTOMERNAME" + //
                "              FROM CUST_TABLE" + //
                "             WHERE CUST_TABLE.CUSTOMERID = AA.BELONGORG)) ORGNAME" + //
                "    FROM AA" + //
                "    LEFT JOIN AAPWD" + //
                "      ON AA.USERID = AAPWD.USERID" + //
                "   WHERE AA.BELONGORG IN" + //
                "         (1,2,3)" + //
                "   ORDER BY AA.USERID DESC" + //
                "  )" + //
                "SELECT *" + //
                "  FROM (SELECT RESULTVIEW.*, ROWNUM AS RESULTNUMS FROM RESULTVIEW)" + //
                " WHERE RESULTNUMS > 1" + //
                "   AND RESULTNUMS <= 10"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(4, visitor.getTables().size());

        Assert.assertEquals(8, visitor.getColumns().size());

         assertTrue(visitor.containsColumn("AA", "USERID"));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
