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
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest97_wmsys_wmconat extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT NVL(TO_CHAR(WMSYS.WM_CONCAT(NVL(O.CHILD_ITEM_CODE, O.ITEM_CODE))), ?)\n" +
                        "FROM ECC_CPR.CCG_GTS_OSG3A_V O\n" +
                        "WHERE O.ENABLED_FLAG = ?\n" +
                        "\tAND NVL(O.PRICE, ?) = ?\n" +
                        "\tAND O.CONTRACT_HEADER_ID = :B1"; //

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) stmt.getSelect().getQueryBlock().getSelectList().get(0).getExpr();
        SQLMethodInvokeExpr param0 = (SQLMethodInvokeExpr) expr.getArguments().get(0);
        assertTrue(param0.getArguments().get(0)
                instanceof SQLAggregateExpr);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT NVL(TO_CHAR(WMSYS.WM_CONCAT(NVL(O.CHILD_ITEM_CODE, O.ITEM_CODE))), ?)\n" +
                    "FROM ECC_CPR.CCG_GTS_OSG3A_V O\n" +
                    "WHERE O.ENABLED_FLAG = ?\n" +
                    "\tAND NVL(O.PRICE, ?) = ?\n" +
                    "\tAND O.CONTRACT_HEADER_ID = :B1", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }

    public void test_1() throws Exception {
        String sql = //
                "SELECT NVL(TO_CHAR(WMSYS.WM_CONCAT(NVL(O.CHILD_ITEM_CODE, O.ITEM_CODE))), ?)\n" +
                        "FROM ECC_CPR.CCG_GTS_OSG3A_V O\n" +
                        "WHERE O.ENABLED_FLAG = ?\n" +
                        "\tAND NVL(O.PRICE, ?) = ?\n" +
                        "\tAND O.CONTRACT_HEADER_ID = :B1"; //

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SQLSelectQueryBlock queryBlock = stmt.getSelect()
                .getQueryBlock();

        SQLMethodInvokeExpr nvl = (SQLMethodInvokeExpr) queryBlock.getSelectList()
                .get(0).getExpr();
        SQLMethodInvokeExpr toChar =(SQLMethodInvokeExpr) nvl.getArguments().get(0);
        assertTrue(toChar.getArguments().get(0) instanceof SQLAggregateExpr);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT NVL(TO_CHAR(WMSYS.WM_CONCAT(NVL(O.CHILD_ITEM_CODE, O.ITEM_CODE))), ?)\n" +
                    "FROM ECC_CPR.CCG_GTS_OSG3A_V O\n" +
                    "WHERE O.ENABLED_FLAG = ?\n" +
                    "\tAND NVL(O.PRICE, ?) = ?\n" +
                    "\tAND O.CONTRACT_HEADER_ID = :B1", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }
}
