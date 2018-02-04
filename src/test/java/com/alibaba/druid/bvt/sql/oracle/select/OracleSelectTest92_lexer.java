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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest92_lexer extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "select * from dual where 1 < > 2 and 1 ! = 2 and 1 ^ /*aaa */ = 2"; //

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
                    "FROM dual\n" +
                    "WHERE 1 <> 2\n" +
                    "\tAND 1 != 2\n" +
                    "\tAND 1 ^= 2", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }


}
