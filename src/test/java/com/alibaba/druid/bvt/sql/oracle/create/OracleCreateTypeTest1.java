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

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;

import java.util.List;

public class OracleCreateTypeTest1 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE OR REPLACE TYPE BODY \"STRCAT_TYPE\" is\n" +
                        "  static function ODCIAggregateInitialize(cs_ctx IN OUT strcat_type) return number\n" +
                        "  is\n" +
                        "  begin\n" +
                        "      cs_ctx := strcat_type( null );\n" +
                        "      return ODCIConst.Success;\n" +
                        "  end;"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        assertEquals(0, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("CREATE OR REPLACE TYPE BODY \"STRCAT_TYPE\" IS\n" +
                    "\tSTATIC FUNCTION ODCIAggregateInitialize (cs_ctx IN OUT strcat_type) RETURN number\n" +
                    "\tIS\n" +
                    "\tBEGIN\n" +
                    "\t\tcs_ctx := strcat_type(NULL);\n" +
                    "\t\tRETURN ODCIConst.Success;\n" +
                    "\tEND;\n" +
                    "END", text);
        }

        {
            String text = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            assertEquals("create or replace type body \"STRCAT_TYPE\" is\n" +
                    "\tstatic function ODCIAggregateInitialize (cs_ctx in out strcat_type) return number\n" +
                    "\tis\n" +
                    "\tbegin\n" +
                    "\t\tcs_ctx := strcat_type(null);\n" +
                    "\t\treturn ODCIConst.Success;\n" +
                    "\tend;\n" +
                    "END", text);
        }
        // assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
