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
import com.alibaba.druid.sql.dialect.oracle.parser.OracleLexer;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest86_comment extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "/*sqlId=9f0szhacj63ag*/SELECT /*+rule*/ SYS_XMLGEN(VALUE(KU$), XMLFORMAT.createFormat2('TABLE_T', '7')), KU$.OBJ_NUM FROM SYS.KU$_HTABLE_VIEW KU$ WHERE NOT (BITAND (KU$.PROPERTY,8192)=8192) AND  NOT BITAND(KU$.SCHEMA_OBJ.FLAGS,128)!=0 AND  KU$.SCHEMA_OBJ.NAME=:NAME1 AND  KU$.SCHEMA_OBJ.OWNER_NAME=:SCHEMA2"; //

        System.out.println(sql);

        OracleLexer lexer = new OracleLexer(sql);
        lexer.config(SQLParserFeature.SkipComments, false);
        lexer.nextToken();
        String comment = lexer.stringVal();
        assertEquals("/*sqlId=9f0szhacj63ag*/", comment);

        OracleStatementParser parser = new OracleStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("/*sqlId=9f0szhacj63ag*/\n" +
                    "SELECT /*+rule*/ SYS_XMLGEN(VALUE(KU$), XMLFORMAT.createFormat2('TABLE_T', '7'))\n" +
                    "\t, KU$.OBJ_NUM\n" +
                    "FROM SYS.KU$_HTABLE_VIEW KU$\n" +
                    "WHERE (NOT BITAND(KU$.PROPERTY, 8192) = 8192)\n" +
                    "\tAND (NOT BITAND(KU$.SCHEMA_OBJ.FLAGS, 128) != 0)\n" +
                    "\tAND KU$.SCHEMA_OBJ.NAME = :NAME1\n" +
                    "\tAND KU$.SCHEMA_OBJ.OWNER_NAME = :SCHEMA2", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(8, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

//        Assert.assertTrue(visitor.containsTable("sup_registration"));
//        Assert.assertTrue(visitor.containsTable("sup_task"));
//        Assert.assertTrue(visitor.containsTable("sys_org"));
//
//         Assert.assertTrue(visitor.containsColumn("sup_task", "orgid"));
//         Assert.assertTrue(visitor.containsColumn("sup_task", "orgid"));
//
    }
}
