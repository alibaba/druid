/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class MySqlSelectTest_316_json_table
        extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SELECT *\n" +
                "FROM\n" +
                "JSON_TABLE(\n" +
                "  '[ {\"c1\": null} ]',\n" +
                "  '$[*]' COLUMNS( c1 INT PATH '$.c1' ERROR ON ERROR )\n" +
                ") as jt;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        System.out.println(stmt.toString());

        assertEquals("SELECT *\n" +
                "FROM JSON_TABLE('[ {\"c1\": null} ]' '$[*]'\n" +
                "\tCOLUMNS (\n" +
                "\t\tc1 INT PATH '$.c1' DEFAULT ERROR ON ERROR\n" +
                "\t)\n" +
                ") jt;", stmt.toString());

        assertEquals("select *\n" +
                "from json_table('[ {\"c1\": null} ]' '$[*]'\n" +
                "\tcolumns (\n" +
                "\t\tc1 INT path '$.c1' default ERROR on error\n" +
                "\t)\n" +
                ") jt;", stmt.toLowerCaseString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }

    public void test_1() throws Exception {
        String sql = "SELECT *\n" +
                "FROM\n" +
                "JSON_TABLE(\n" +
                "  '[{\"a\":\"3\"},{\"a\":2},{\"b\":1},{\"a\":0},{\"a\":[1,2]}]',\n" +
                "  \"$[*]\"\n" +
                "  COLUMNS(\n" +
                "    rowid FOR ORDINALITY,\n" +
                "    ac VARCHAR(100) PATH \"$.a\" DEFAULT '111' ON EMPTY DEFAULT '999' ON ERROR,\n" +
                "    aj JSON PATH \"$.a\" DEFAULT '{\"x\": 333}' ON EMPTY,\n" +
                "    bx INT EXISTS PATH \"$.b\"\n" +
                "  )\n" +
                ") AS tt;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        System.out.println(stmt.toString());

        assertEquals("SELECT *\n" +
                "FROM JSON_TABLE('[{\"a\":\"3\"},{\"a\":2},{\"b\":1},{\"a\":0},{\"a\":[1,2]}]' '$[*]'\n" +
                "\tCOLUMNS (\n" +
                "\t\trowid, \n" +
                "\t\tac VARCHAR(100) PATH '$.a' DEFAULT '111' ON EMPTY DEFAULT '999' ON ERROR, \n" +
                "\t\taj JSON PATH '$.a' DEFAULT '{\"x\": 333}' ON EMPTY, \n" +
                "\t\tbx INT EXISTS PATH '$.b'\n" +
                "\t)\n" +
                ") tt;", stmt.toString());

        assertEquals("select *\n" +
                "from json_table('[{\"a\":\"3\"},{\"a\":2},{\"b\":1},{\"a\":0},{\"a\":[1,2]}]' '$[*]'\n" +
                "\tcolumns (\n" +
                "\t\trowid, \n" +
                "\t\tac VARCHAR(100) path '$.a' default '111' on empty default '999' on error, \n" +
                "\t\taj JSON path '$.a' default '{\"x\": 333}' on empty, \n" +
                "\t\tbx INT exists path '$.b'\n" +
                "\t)\n" +
                ") tt;", stmt.toLowerCaseString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }

    public void test_2() throws Exception {
        String sql = "SELECT *\n" +
                "FROM\n" +
                "  JSON_TABLE(\n" +
                "    '[{\"a\": 1, \"b\": [11,111]}, {\"a\": 2, \"b\": [22,222]}]',\n" +
                "    '$[*]' COLUMNS(\n" +
                "        a INT PATH '$.a',\n" +
                "        NESTED PATH '$.b[*]' COLUMNS (b1 INT PATH '$'),\n" +
                "        NESTED PATH '$.b[*]' COLUMNS (b2 INT PATH '$')\n" +
                "    )\n" +
                ") AS jt;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        System.out.println(stmt.toString());

        assertEquals("SELECT *\n" +
                "FROM JSON_TABLE('[{\"a\": 1, \"b\": [11,111]}, {\"a\": 2, \"b\": [22,222]}]' '$[*]'\n" +
                "\tCOLUMNS (\n" +
                "\t\ta INT PATH '$.a', \n" +
                "\t\tNESTED PATH '$.b[*]' COLUMNS (b1 INT PATH '$'), \n" +
                "\t\tNESTED PATH '$.b[*]' COLUMNS (b2 INT PATH '$')\n" +
                "\t)\n" +
                ") jt;", stmt.toString());

        assertEquals("select *\n" +
                "from json_table('[{\"a\": 1, \"b\": [11,111]}, {\"a\": 2, \"b\": [22,222]}]' '$[*]'\n" +
                "\tcolumns (\n" +
                "\t\ta INT path '$.a', \n" +
                "\t\tNESTED path '$.b[*]' columns (b1 INT path '$'), \n" +
                "\t\tNESTED path '$.b[*]' columns (b2 INT path '$')\n" +
                "\t)\n" +
                ") jt;", stmt.toLowerCaseString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }

    public void test_3() throws Exception {
        String sql = "SELECT *\n" +
                "FROM\n" +
                "  JSON_TABLE(\n" +
                "    '[{\"a\": \"a_val\",\n" +
                "      \"b\": [{\"c\": \"c_val\", \"l\": [1,2]}]},\n" +
                "    {\"a\": \"a_val\",\n" +
                "      \"b\": [{\"c\": \"c_val\",\"l\": [11]}, {\"c\": \"c_val\", \"l\": [22]}]}]',\n" +
                "    '$[*]' COLUMNS(\n" +
                "      top_ord FOR ORDINALITY,\n" +
                "      apath VARCHAR(10) PATH '$.a',\n" +
                "      NESTED PATH '$.b[*]' COLUMNS (\n" +
                "        bpath VARCHAR(10) PATH '$.c',\n" +
                "        ord FOR ORDINALITY,\n" +
                "        NESTED PATH '$.l[*]' COLUMNS (lpath varchar(10) PATH '$')\n" +
                "        )\n" +
                "    )\n" +
                ") as jt;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        System.out.println(stmt.toString());

        assertEquals("SELECT *\n" +
                "FROM JSON_TABLE('[{\"a\": \"a_val\",\n" +
                "      \"b\": [{\"c\": \"c_val\", \"l\": [1,2]}]},\n" +
                "    {\"a\": \"a_val\",\n" +
                "      \"b\": [{\"c\": \"c_val\",\"l\": [11]}, {\"c\": \"c_val\", \"l\": [22]}]}]' '$[*]'\n" +
                "\tCOLUMNS (\n" +
                "\t\ttop_ord, \n" +
                "\t\tapath VARCHAR(10) PATH '$.a', \n" +
                "\t\tNESTED PATH '$.b[*]' COLUMNS (bpath VARCHAR(10) PATH '$.c', ord, NESTED PATH '$.l[*]' COLUMNS (lpath varchar(10) PATH '$'))\n" +
                "\t)\n" +
                ") jt;", stmt.toString());

        assertEquals("select *\n" +
                "from json_table('[{\"a\": \"a_val\",\n" +
                "      \"b\": [{\"c\": \"c_val\", \"l\": [1,2]}]},\n" +
                "    {\"a\": \"a_val\",\n" +
                "      \"b\": [{\"c\": \"c_val\",\"l\": [11]}, {\"c\": \"c_val\", \"l\": [22]}]}]' '$[*]'\n" +
                "\tcolumns (\n" +
                "\t\ttop_ord, \n" +
                "\t\tapath VARCHAR(10) path '$.a', \n" +
                "\t\tNESTED path '$.b[*]' columns (bpath VARCHAR(10) path '$.c', ord, NESTED path '$.l[*]' columns (lpath varchar(10) path '$'))\n" +
                "\t)\n" +
                ") jt;", stmt.toLowerCaseString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }
}