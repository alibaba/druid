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
package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class OdpsCreateTableTest6_array extends TestCase {

    public void test_select() throws Exception {
        // 1095288847322
        String sql = "CREATE TABLE src_thrift\n" +
                "(\n" +
                "    aint bigint,\n" +
                "    astring string,\n" +
                "    lint ARRAY<bigint>,\n" +
                "    lstring ARRAY<STRING>,\n" +
                "    --lintString ARRAY<INTSTRING>\n" +
                "    mStringString MAP<STRING, STRING>\n" +
                "    --attribute Map<String,Map<String,Map<String,PropValueUnion>>>,\n" +
                "    --unionField1 PropValueUnion,\n" +
                "    --unionField2 PropValueUnion,\n" +
                "    -- unionField3 PropValueUnion\n" +
                ");";//



        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        assertEquals("CREATE TABLE src_thrift (\n" +
                "\taint BIGINT,\n" +
                "\tastring STRING,\n" +
                "\tlint ARRAY<BIGINT>,\n" +
                "\tlstring ARRAY<STRING>,\n" +
                "\tmStringString MAP<STRING, STRING>\n" +
                ");", stmt.toString());

        assertEquals("create table src_thrift (\n" +
                "\taint bigint,\n" +
                "\tastring string,\n" +
                "\tlint array<bigint>,\n" +
                "\tlstring array<string>, -- lintString ARRAY<INTSTRING>\n" +
                "\tmStringString map<string, string> -- attribute Map<String,Map<String,Map<String,PropValueUnion>>>,\n" +
                "\t-- unionField1 PropValueUnion,\n" +
                "\t-- unionField2 PropValueUnion,\n" +
                "\t-- unionField3 PropValueUnion\n" +
                ");", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

//        System.out.println(SQLUtils.formatOdps(sql));

//        assertTrue(visitor.getColumns().contains(new Column("abc", "name")));
    }


}
