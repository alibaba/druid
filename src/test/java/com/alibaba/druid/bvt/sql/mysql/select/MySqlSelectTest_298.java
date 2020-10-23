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
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;

public class MySqlSelectTest_298
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT 1 FROM ((SELECT 2) ORDER BY 1) AS res";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 1\n" +
                "FROM (\n" +
                "\tSELECT 2\n" +
                "\tORDER BY 1\n" +
                ") res", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT 100 + /* + shouldn't fail */ 1 AS result;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 100 + 1 AS result;", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "select _latin1 0x4B regexp _latin1 '[[:upper:]]' COLLATE latin1_bin;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT _latin1 '4B' REGEXP _latin1 '[[:upper:]]' COLLATE latin1_bin;", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select cast('-10a' as signed integer);";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT CAST('-10a' AS signed integer);", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "select 2 as expected, /*!99990 1 + */ 2 as result; ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 2 AS expected, 2 AS result;", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "select 2 as expected, /*!99990 1 + */ 2 as result; ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 2 AS expected, 2 AS result;", stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "SELECT 1 FROM /*+ regular commentary, not a hint! */ opt_hints_t1_14;  ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 1\n" +
                "FROM opt_hints_t1_14;", stmt.toString());
    }

    public void test_7() throws Exception {
        String sql = "SELECT /*+ NO_ICP() */ 1 FROM /*+ regular commentary, not a hint! */ opt_hints_t1_14;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT /*+ NO_ICP() */ 1\n" +
                "FROM opt_hints_t1_14;", stmt.toString());
    }

    public void test_8() throws Exception {
        String sql = "select group_concat('x') UNION ALL select 1;  ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT group_concat('x')\n" +
                "UNION ALL\n" +
                "SELECT 1;", stmt.toString());
    }

    public void test_9() throws Exception {
        String sql = "select hex(convert(_ujis 0x8FABF841 using ucs2));  ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT hex(convert(_ujis '8FABF841' USING ucs2));", stmt.toString());
    }

    public void test_10() throws Exception {
        String sql = "select locate(_ujis 0xa2a1,_ujis 0xa1a2a1a3); ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT locate(_ujis 'a2a1', _ujis 'a1a2a1a3');", stmt.toString());
    }

    public void test_11() throws Exception {
        String sql = "SELECT HEX(RIGHT(_utf16le 0x00D800DC7FD8FFDF, 1));";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT HEX(RIGHT(_utf16le '00D800DC7FD8FFDF', 1));", stmt.toString());
    }

    public void test_x1() throws Exception {
        String sql = "`default`.`row_number`() OVER (PARTITION BY `field` ORDER BY `field` ASC)";

        SQLExprParser parser = SQLParserUtils.createExprParser(sql, DbType.mysql);
        parser.config(SQLParserFeature.KeepNameQuotes, true);
        parser.config(SQLParserFeature.PipesAsConcat, true);
        parser.config(SQLParserFeature.EnableSQLBinaryOpExprGroup, true);
        SQLExpr expr = parser.expr();

        if (parser.getLexer().token() != com.alibaba.druid.sql.parser.Token.EOF) {
            throw new ParserException("illegal sql expr : " + sql + ", " + parser.getLexer().info());
        }
    }

    public void test_x2() throws Exception {
        String sql = "SELECT 123 UNION SELECT 123 UNION ALL SELECT 123";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT 123\n" +
                "UNION\n" +
                "SELECT 123\n" +
                "UNION ALL\n" +
                "SELECT 123", stmt.toString());
    }

    public void test_x3() throws Exception {
        String sql = "SELECT 101 UNION SELECT 102 UNION DISTINCT SELECT 103";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT 101\n" +
                "UNION\n" +
                "SELECT 102\n" +
                "UNION DISTINCT\n" +
                "SELECT 103", stmt.toString());
    }

    public void test_x4() throws Exception {
        String sql = "SELECT 123 UNION DISTINCT SELECT 123 UNION ALL SELECT 123";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT 123\n" +
                "UNION DISTINCT\n" +
                "SELECT 123\n" +
                "UNION ALL\n" +
                "SELECT 123", stmt.toString());
    }

    public void test_x5() throws Exception {
        String sql = "SELECT 123 UNION DISTINCT SELECT 123 UNION SELECT 123";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT 123\n" +
                "UNION DISTINCT\n" +
                "SELECT 123\n" +
                "UNION\n" +
                "SELECT 123", stmt.toString());
    }

    public void test_x6() throws Exception {
        String sql = "SELECT 123 UNION ALL SELECT 123 UNION SELECT 123";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT 123\n" +
                "UNION ALL\n" +
                "SELECT 123\n" +
                "UNION\n" +
                "SELECT 123", stmt.toString());
    }

    public void test_x7() throws Exception {
        String sql = "SELECT id from test4dmp.test where id BETWEEN 1 and 10 union all  select id from test4dmp.student where id BETWEEN 5 and 12 union all select cid from test4dmp.course where cid BETWEEN 18 and 19   order by id";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT id\n" +
                "FROM test4dmp.test\n" +
                "WHERE id BETWEEN 1 AND 10\n" +
                "UNION ALL\n" +
                "SELECT id\n" +
                "FROM test4dmp.student\n" +
                "WHERE id BETWEEN 5 AND 12\n" +
                "UNION ALL\n" +
                "SELECT cid\n" +
                "FROM test4dmp.course\n" +
                "WHERE cid BETWEEN 18 AND 19\n" +
                "ORDER BY id", stmt.toString());
    }

    public void test_x8() throws Exception {
        String sql = "SELECT id from test4dmp.test where id BETWEEN 1 and 10 union all  select id from test4dmp.student where id BETWEEN 5 and 12 union all select cid from test4dmp.course where cid BETWEEN 18 and 19   order by id";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT id\n" +
                "FROM test4dmp.test\n" +
                "WHERE id BETWEEN 1 AND 10\n" +
                "UNION ALL\n" +
                "SELECT id\n" +
                "FROM test4dmp.student\n" +
                "WHERE id BETWEEN 5 AND 12\n" +
                "UNION ALL\n" +
                "SELECT cid\n" +
                "FROM test4dmp.course\n" +
                "WHERE cid BETWEEN 18 AND 19\n" +
                "ORDER BY id", stmt.toString());


        SQLStatement stmt2 = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT id\n" +
                "FROM test4dmp.test\n" +
                "WHERE id BETWEEN 1 AND 10\n" +
                "UNION ALL\n" +
                "SELECT id\n" +
                "FROM test4dmp.student\n" +
                "WHERE id BETWEEN 5 AND 12\n" +
                "UNION ALL\n" +
                "SELECT cid\n" +
                "FROM test4dmp.course\n" +
                "WHERE cid BETWEEN 18 AND 19\n" +
                "ORDER BY id", stmt2.toString());
    }
}