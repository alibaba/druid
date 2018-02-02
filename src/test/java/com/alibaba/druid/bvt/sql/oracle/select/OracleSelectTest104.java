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
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest104 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT\n" +
                        "  /* DS_SVC */\n" +
                        "  /*+ cursor_sharing_exact dynamic_sampling(0) no_sql_tune no_monitoring optimizer_features_enable(default) */\n" +
                        "  SUM(C1)\n" +
                        "FROM\n" +
                        "  (SELECT\n" +
                        "    /*+ qb_name(\"innerQuery\")  */\n" +
                        "    1 AS C1\n" +
                        "  FROM SYS.\"X$KZSPR\" \"X$KZSPR\"\n" +
                        "  WHERE (\"X$KZSPR\".\"INST_ID\"  =USERENV('INSTANCE'))\n" +
                        "  AND ((-\"X$KZSPR\".\"KZSPRPRV\")=(-45)\n" +
                        "  OR (  -\"X$KZSPR\".\"KZSPRPRV\")=(-47)\n" +
                        "  OR (  -\"X$KZSPR\".\"KZSPRPRV\")=(-48)\n" +
                        "  OR (  -\"X$KZSPR\".\"KZSPRPRV\")=(-49)\n" +
                        "  OR (  -\"X$KZSPR\".\"KZSPRPRV\")=(-50))\n" +
                        "  ) innerQuery";

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

//        SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) stmt.getSelect().getQueryBlock().getSelectList().get(0).getExpr();
//        SQLMethodInvokeExpr param0 = (SQLMethodInvokeExpr) expr.getParameters().get(0);
//        assertTrue(param0.getParameters().get(0)
//                instanceof SQLAggregateExpr);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT /* DS_SVC */ /*+ cursor_sharing_exact dynamic_sampling(0) no_sql_tune no_monitoring optimizer_features_enable(default) */ SUM(C1)\n" +
                    "FROM (\n" +
                    "\tSELECT /*+ qb_name(\"innerQuery\")  */ 1 AS C1\n" +
                    "\tFROM SYS.\"X$KZSPR\" \"X$KZSPR\"\n" +
                    "\tWHERE \"X$KZSPR\".\"INST_ID\" = USERENV('INSTANCE')\n" +
                    "\t\tAND (-\"X$KZSPR\".\"KZSPRPRV\" = -45\n" +
                    "\t\t\tOR -\"X$KZSPR\".\"KZSPRPRV\" = -47\n" +
                    "\t\t\tOR -\"X$KZSPR\".\"KZSPRPRV\" = -48\n" +
                    "\t\t\tOR -\"X$KZSPR\".\"KZSPRPRV\" = -49\n" +
                    "\t\t\tOR -\"X$KZSPR\".\"KZSPRPRV\" = -50)\n" +
                    ") innerQuery", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(2, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }

   
}
