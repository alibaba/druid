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

public class OracleSelectTest96_pivot extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "  select *\n" +
                        " from s join d using(c)\n" +
                        " pivot\n" +
                        " (\n" +
                        " max(c_c_p) as max_ccp\n" +
                        " , max(d_c_p) max_dcp\n" +
                        " , max(d_x_p) dxp\n" +
                        " , count(1) cnt\n" +
                        " for (i, p) in\n" +
                        " (\n" +
                        " (1,1) as one_one,\n" +
                        " (1,2) as one_two,\n" +
                        " (1,3) as one_three,\n" +
                        " (2,1) as two_one,\n" +
                        " (2,2) as two_two,\n" +
                        " (2,3) as two_three\n" +
                        " )\n" +
                        " )\n" +
                        " where d_t = 'p'\n"; //

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT *\n" +
                    "FROM s\n" +
                    "\tJOIN d USING (c)\n" +
                    "PIVOT (max(c_c_p) AS max_ccp, max(d_c_p) AS max_dcp, max(d_x_p) AS dxp, count(1) AS cnt FOR (i, p) IN ((1, 1) AS one_one, (1, 2) AS one_two, (1, 3) AS one_three, (2, 1) AS two_one, (2, 2) AS two_two, (2, 3) AS two_three))\n" +
                    "WHERE d_t = 'p'", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(3, visitor.getConditions().size());
        assertEquals(1, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }


}
