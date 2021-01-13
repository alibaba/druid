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
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTypeTest10 extends OracleTest {

    public void test_types() throws Exception {
        String sql = "CREATE OR REPLACE type body connstrBycomma is\n" +
                "      static function ODCIAggregateInitialize(sctx IN OUT connstrBycomma)\t\n" +
                "      return number is\n" +
                "      begin\n" +
                "        sctx := connstrBycomma('',',');\n" +
                "        return ODCIConst.Success;\n" +
                "      end;\n" +
                "      member function ODCIAggregateIterate(self IN OUT connstrBycomma, value IN VARCHAR2) return number is\n" +
                "      begin\n" +
                "        if self.currentstr is null then\n" +
                "          self.currentstr := value;\n" +
                "        else\n" +
                "          self.currentstr := self.currentstr ||currentseprator || value;\n" +
                "        end if;\n" +
                "        return ODCIConst.Success;\n" +
                "      end;\n" +
                "      member function ODCIAggregateTerminate(self IN connstrBycomma, returnValue OUT VARCHAR2, flags IN number) return number is\n" +
                "      begin\n" +
                "        returnValue := self.currentstr;\n" +
                "        return ODCIConst.Success;\n" +
                "      end;\n" +
                "      member function ODCIAggregateMerge(self IN OUT connstrBycomma, ctx2 IN connstrBycomma) return number is\n" +
                "      begin\n" +
                "        if ctx2.currentstr is null then\n" +
                "          self.currentstr := self.currentstr;\n" +
                "        elsif self.currentstr is null then\n" +
                "          self.currentstr := ctx2.currentstr;\n" +
                "        else\n" +
                "          self.currentstr := self.currentstr || currentseprator || ctx2.currentstr;\n" +
                "        end if;\n" +
                "        return ODCIConst.Success;\n" +
                "      end;\n" +
                "      end;";

        System.out.println(sql);


        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE TYPE BODY connstrBycomma IS\n" +
                        "\tSTATIC FUNCTION ODCIAggregateInitialize (sctx IN OUT connstrBycomma) RETURN number\n" +
                        "\tIS\n" +
                        "\tBEGIN\n" +
                        "\t\tsctx := connstrBycomma(NULL, ',');\n" +
                        "\t\tRETURN ODCIConst.Success;\n" +
                        "\tEND;\n" +
                        "\tMEMBER FUNCTION ODCIAggregateIterate (self IN OUT connstrBycomma, value IN VARCHAR2) RETURN number\n" +
                        "\tIS\n" +
                        "\tBEGIN\n" +
                        "\t\tIF self.currentstr IS NULL THEN\n" +
                        "\t\t\tself.currentstr := value;\n" +
                        "\t\tELSE\n" +
                        "\t\t\tself.currentstr := self.currentstr || currentseprator || value;\n" +
                        "\t\tEND IF;\n" +
                        "\t\tRETURN ODCIConst.Success;\n" +
                        "\tEND;\n" +
                        "\tMEMBER FUNCTION ODCIAggregateTerminate (self IN connstrBycomma, returnValue OUT VARCHAR2, flags IN number) RETURN number\n" +
                        "\tIS\n" +
                        "\tBEGIN\n" +
                        "\t\treturnValue := self.currentstr;\n" +
                        "\t\tRETURN ODCIConst.Success;\n" +
                        "\tEND;\n" +
                        "\tMEMBER FUNCTION ODCIAggregateMerge (self IN OUT connstrBycomma, ctx2 IN connstrBycomma) RETURN number\n" +
                        "\tIS\n" +
                        "\tBEGIN\n" +
                        "\t\tIF ctx2.currentstr IS NULL THEN\n" +
                        "\t\t\tself.currentstr := self.currentstr;\n" +
                        "\t\tELSE IF self.currentstr IS NULL THEN\n" +
                        "\t\t\tself.currentstr := ctx2.currentstr;\n" +
                        "\t\tELSE\n" +
                        "\t\t\tself.currentstr := self.currentstr || currentseprator || ctx2.currentstr;\n" +
                        "\t\tEND IF;\n" +
                        "\t\tRETURN ODCIConst.Success;\n" +
                        "\tEND;\n" +
                        "END",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        assertEquals(0, visitor.getColumns().size());

//        assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_total")));
    }
}
