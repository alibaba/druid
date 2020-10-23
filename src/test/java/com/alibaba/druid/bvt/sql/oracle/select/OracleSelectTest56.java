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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest56 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "select "
                + "\nAA.ID,"
                + "\nAA.CODE,"
                + "\nAA.TYPE,"
                + "\nAA.STATUS,"
                + "\nAA.EMPLOYEENAME,"
                + "\nAA.CREATORNAME,"
                + "\nAA.OPERATIONTYPE,"
                + "\nAA.CREATEDATE,"
                + "\nAA.REMARK,"
                + "\nW.NAME,"
                + "\nDD.DESC"
                + "\nfrom "
                + "\na AA,"
                + "\nw W,"
                + "\nd DD"
                + "\nwhere "
                + "\nAA.employeeNo IN ("
                + "\nSELECT employeeno FROM employeeauditor ea WHERE auditorno = 1 GROUP BY employeeno"
                + "\nUNION ALL"
                + "\nSELECT 1 FROM dual "
                + "\n) and"
                + "\nAA.WNO = W.WNO(+) and"
                + "\nAA.DEPTNO = DD.DEPTNO(+)"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(4, visitor.getTables().size());

        Assert.assertEquals(18, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT AA.ID, AA.CODE, AA.TYPE, AA.STATUS, AA.EMPLOYEENAME\n" +
                    "\t, AA.CREATORNAME, AA.OPERATIONTYPE, AA.CREATEDATE, AA.REMARK, W.NAME\n" +
                    "\t, DD.DESC\n" +
                    "FROM a AA, w W, d DD\n" +
                    "WHERE AA.employeeNo IN (\n" +
                    "\t\tSELECT employeeno\n" +
                    "\t\tFROM employeeauditor ea\n" +
                    "\t\tWHERE auditorno = 1\n" +
                    "\t\tGROUP BY employeeno\n" +
                    "\t\tUNION ALL\n" +
                    "\t\tSELECT 1\n" +
                    "\t\tFROM dual\n" +
                    "\t)\n" +
                    "\tAND AA.WNO = W.WNO(+)\n" +
                    "\tAND AA.DEPTNO = DD.DEPTNO(+)", text);
        }

        {
            String text = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            assertEquals("select AA.ID, AA.CODE, AA.TYPE, AA.STATUS, AA.EMPLOYEENAME\n" +
                    "\t, AA.CREATORNAME, AA.OPERATIONTYPE, AA.CREATEDATE, AA.REMARK, W.NAME\n" +
                    "\t, DD.DESC\n" +
                    "from a AA, w W, d DD\n" +
                    "where AA.employeeNo in (\n" +
                    "\t\tselect employeeno\n" +
                    "\t\tfrom employeeauditor ea\n" +
                    "\t\twhere auditorno = 1\n" +
                    "\t\tgroup by employeeno\n" +
                    "\t\tunion all\n" +
                    "\t\tselect 1\n" +
                    "\t\tfrom dual\n" +
                    "\t)\n" +
                    "\tand AA.WNO = W.WNO(+)\n" +
                    "\tand AA.DEPTNO = DD.DEPTNO(+)", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
