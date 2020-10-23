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

import java.util.List;

public class MySqlSelectTest_mtr
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT 1 FROM ((SELECT 2) LIMIT 1) AS res; ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 1\n" +
                "FROM (\n" +
                "\tSELECT 2\n" +
                "\tLIMIT 1\n" +
                ") res;", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT if(1, NULL, (SELECT min('hello')));  ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT if(1, NULL, (\n" +
                "\t\tSELECT min('hello')\n" +
                "\t));", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT (SELECT COUNT(DISTINCT func_group_t1_36.b)) FROM func_group_t1_36 GROUP BY func_group_t1_36.a;  ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT (\n" +
                "\t\tSELECT COUNT(DISTINCT func_group_t1_36.b)\n" +
                "\t)\n" +
                "FROM func_group_t1_36\n" +
                "GROUP BY func_group_t1_36.a;", stmt.toString());
    }

    public void test_utf32_0() throws Exception {
        String sql = "select _utf32 0x000000610000006200000063, _utf32 X'0000042100000422' ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 'abc', 'СТ'", stmt.toString());
    }

    public void test_utf32_1() throws Exception {
        String sql = "SELECT * FROM ctype_utf32_uca_t1_3 WHERE c LIKE _utf32 0x0000039C00000025 COLLATE utf32_unicode_ci;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM ctype_utf32_uca_t1_3\n" +
                "WHERE c LIKE 'Μ%' COLLATE utf32_unicode_ci;", stmt.toString());
    }

    public void test_utf32_2() throws Exception {
        String sql = "SELECT hex(RPAD(_utf32 X'0420',10,_utf32 X'0000042100000422'));";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT hex(RPAD('Р', 10, 'СТ'));", stmt.toString());
    }

    public void test_utf8_0() throws Exception {
        String sql = "select _utf8 0xD0B0D0B2D0B2, _utf8 X'D0B0D0B2D0B2', _utf8 'D0B0D0B2D0B2'";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 'авв', 'авв', _utf8 'D0B0D0B2D0B2'", stmt.toString());
    }

    public void test_utf8_1() throws Exception {
        String sql = "select length(_utf8 0xD0B0D0B2D0B2), length(_utf8 X'D0B0D0B2D0B2'), length(_utf8 'D0B0D0B2D0B2')";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT length('авв'), length('авв')\n" +
                "\t, length(_utf8 'D0B0D0B2D0B2')", stmt.toString());
    }

    public void test_utf8_2() throws Exception {
        String sql = "select length(_utf8mb4 0xD0B1), bit_length(_utf8mb4 0xD0B1), char_length(_utf8mb4 0xD0B1);";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT length('б'), bit_length('б')\n" +
                "\t, char_length('б');", stmt.toString());
    }

    public void test_utf16_0() throws Exception {
        String sql = "select _utf16 0x039C0025, _utf16 X'039C0025', _utf16 '039C0025'";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT _utf16 x'039C0025', _utf16 x'039C0025', _utf16 x'3033394330303235'", stmt.toString());
    }

    public void test_utf16_1() throws Exception {
        String sql = "select length(_utf16 0x039C0025), length(_utf16 X'039C0025')";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT length('Μ%'), length('Μ%')", stmt.toString());
    }

    public void test_ucs2_0() throws Exception {
        String sql = "select _ucs2 0x00e400e50068, _ucs2 X'00e400e50068', _ucs2 'ab'";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT _utf16 x'00e400e50068', _utf16 x'00e400e50068', _utf16 x'6162'", stmt.toString());
    }

    public void test_gbk_0() throws Exception {
        String sql = "select _gbk 0xA14041, _gbk X'A14041'";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT '\uE4C6A', '\uE4C6A'", stmt.toString());
    }

    public void test_gbk_1() throws Exception {
        String sql = "select length(_gbk 0xA14041), length(_gbk X'A14041')";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT length('\uE4C6A'), length('\uE4C6A')", stmt.toString());
    }

    public void test_big5_0() throws Exception {
        String sql = "select _big5 0xA4A4B5D8A5C1B0EA, _big5 X'A4A4B5D8A5C1B0EA', _big5 'A4A4B5D8A5C1B0EA'";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT '中華民國', '中華民國', _big5 'A4A4B5D8A5C1B0EA'", stmt.toString());
    }

    public void test_big5_1() throws Exception {
        String sql = "select length(_big5 0xA4A4B5D8A5C1B0EA), length(_big5 X'A4A4B5D8A5C1B0EA'), length(_big5 'A4A4B5D8A5C1B0EA')";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT length('中華民國'), length('中華民國')\n" +
                "\t, length(_big5 'A4A4B5D8A5C1B0EA')", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "select {fn length(\"hello\")}, { date \"1997-10-20\" };  ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT length('hello'), DATE '1997-10-20';", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "select 'a' union select concat('a', -'3');";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 'a'\n" +
                "UNION\n" +
                "SELECT concat('a', -'3');", stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "select * from fulltext_t1_0 where MATCH(a,b) AGAINST (\"collections\" WITH QUERY EXPANSION);\n" +
                "select * from fulltext_t1_0 where MATCH(a,b) AGAINST (\"indexes\" WITH QUERY EXPANSION);\n" +
                "select * from fulltext_t1_0 where MATCH(a,b) AGAINST (\"indexes collections\" WITH QUERY EXPANSION);\n" +
                "SELECT COUNT(*) FROM basic_articles_0         WHERE MATCH (title,body)         AGAINST ('database' WITH QUERY EXPANSION);\n" +
                "SELECT * FROM basic_articles_0         WHERE MATCH (title,body)         AGAINST ('test' WITH QUERY EXPANSION);\n" +
                "SELECT id1 FROM misc_1_t1_0 WHERE MATCH (a1,b1) AGAINST ('tutorial' WITH QUERY EXPANSION) ORDER BY id1;\n" +
                "SELECT id2 FROM misc_1_t2_1 WHERE MATCH (a2,b2) AGAINST ('tutorial' WITH QUERY EXPANSION) ORDER BY id2;\n" +
                "select * from multiple_index_t1_0 where MATCH(b) AGAINST (\"DataBase\" WITH QUERY EXPANSION);\n" +
                "select * from multiple_index_t1_0 where MATCH(a) AGAINST (\"Security\" WITH QUERY EXPANSION);\n" +
                "SELECT id FROM misc_t1_2      WHERE MATCH (a,b)      AGAINST (NULL WITH QUERY EXPANSION);\n" +
                "SELECT COUNT(*) FROM misc_t1_2 WHERE MATCH (a,b) AGAINST ('database' WITH QUERY EXPANSION);\n" +
                "SELECT * FROM misc_1_t1_14 WHERE MATCH (a1,b1) AGAINST ('tutorial' WITH QUERY EXPANSION) ORDER BY id1;\n" +
                "SELECT * FROM misc_1_t2_15 WHERE MATCH (a2,b2) AGAINST ('tutorial' WITH QUERY EXPANSION) ORDER BY id2;\n" +
                "SELECT * FROM misc_1_t1_14 WHERE MATCH (a1,b1) AGAINST ('root' WITH QUERY EXPANSION) ;\n" +
                "SELECT * FROM misc_1_t2_15 WHERE MATCH (a2,b2) AGAINST ('root' WITH QUERY EXPANSION) ;";

        List<SQLStatement> stmts = SQLUtils
                .parseStatements(sql, DbType.mysql);

        StringBuilder builder = new StringBuilder();
        for (SQLStatement stmt : stmts) {
            builder.append(stmt.toString()).append("\n\n");
        }

        assertEquals("SELECT *\n" +
                "FROM fulltext_t1_0\n" +
                "WHERE MATCH (a, b) AGAINST ('collections' WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT *\n" +
                "FROM fulltext_t1_0\n" +
                "WHERE MATCH (a, b) AGAINST ('indexes' WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT *\n" +
                "FROM fulltext_t1_0\n" +
                "WHERE MATCH (a, b) AGAINST ('indexes collections' WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT COUNT(*)\n" +
                "FROM basic_articles_0\n" +
                "WHERE MATCH (title, body) AGAINST ('database' WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT *\n" +
                "FROM basic_articles_0\n" +
                "WHERE MATCH (title, body) AGAINST ('test' WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT id1\n" +
                "FROM misc_1_t1_0\n" +
                "WHERE MATCH (a1, b1) AGAINST ('tutorial' WITH QUERY EXPANSION)\n" +
                "ORDER BY id1;\n" +
                "\n" +
                "SELECT id2\n" +
                "FROM misc_1_t2_1\n" +
                "WHERE MATCH (a2, b2) AGAINST ('tutorial' WITH QUERY EXPANSION)\n" +
                "ORDER BY id2;\n" +
                "\n" +
                "SELECT *\n" +
                "FROM multiple_index_t1_0\n" +
                "WHERE MATCH (b) AGAINST ('DataBase' WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT *\n" +
                "FROM multiple_index_t1_0\n" +
                "WHERE MATCH (a) AGAINST ('Security' WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT id\n" +
                "FROM misc_t1_2\n" +
                "WHERE MATCH (a, b) AGAINST (NULL WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT COUNT(*)\n" +
                "FROM misc_t1_2\n" +
                "WHERE MATCH (a, b) AGAINST ('database' WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT *\n" +
                "FROM misc_1_t1_14\n" +
                "WHERE MATCH (a1, b1) AGAINST ('tutorial' WITH QUERY EXPANSION)\n" +
                "ORDER BY id1;\n" +
                "\n" +
                "SELECT *\n" +
                "FROM misc_1_t2_15\n" +
                "WHERE MATCH (a2, b2) AGAINST ('tutorial' WITH QUERY EXPANSION)\n" +
                "ORDER BY id2;\n" +
                "\n" +
                "SELECT *\n" +
                "FROM misc_1_t1_14\n" +
                "WHERE MATCH (a1, b1) AGAINST ('root' WITH QUERY EXPANSION);\n" +
                "\n" +
                "SELECT *\n" +
                "FROM misc_1_t2_15\n" +
                "WHERE MATCH (a2, b2) AGAINST ('root' WITH QUERY EXPANSION);\n" +
                "\n", builder.toString());
    }

    public void test_7() throws Exception {
        String sql = "select sql_buffer_result distinct distinct a from gis_split_inf_t_0 where a =1  ;\n" +
                "SELECT SQL_BIG_RESULT DISTINCT f1 FROM ctype_uca_t1_14;\n" +
                "select sql_big_result distinct myisam_t1_17.a from myisam_t1_17,myisam_t2_18 order by myisam_t2_18.a;\n" +
                "select sql_big_result distinct myisam_t1_17.a from myisam_t1_17,myisam_t2_18;\n" +
                "select SQL_CALC_FOUND_ROWS distinct b from select_found_t1_0 limit 1;\n" +
                "SELECT SQL_CALC_FOUND_ROWS DISTINCT email FROM select_found_t2_3 LEFT JOIN select_found_t1_2  ON kid = select_found_t2_3.id WHERE select_found_t1_2.id IS NULL order by email LIMIT 10;\n" +
                "SELECT SQL_CALC_FOUND_ROWS DISTINCT email FROM select_found_t2_3 LEFT JOIN select_found_t1_2  ON kid = select_found_t2_3.id WHERE select_found_t1_2.id IS NULL LIMIT 10;\n" +
                "SELECT SQL_CALC_FOUND_ROWS DISTINCT 'a' FROM select_found_t1_10 GROUP BY b LIMIT 2;";

        List<SQLStatement> stmts = SQLUtils
                .parseStatements(sql, DbType.mysql);

        StringBuilder builder = new StringBuilder();
        for (SQLStatement stmt : stmts) {
            builder.append(stmt.toString()).append("\n\n");
        }

        assertEquals("SELECT DISTINCT SQL_BUFFER_RESULT a\n" +
                "FROM gis_split_inf_t_0\n" +
                "WHERE a = 1;\n" +
                "\n" +
                "SELECT DISTINCT SQL_BIG_RESULT f1\n" +
                "FROM ctype_uca_t1_14;\n" +
                "\n" +
                "SELECT DISTINCT SQL_BIG_RESULT myisam_t1_17.a\n" +
                "FROM myisam_t1_17, myisam_t2_18\n" +
                "ORDER BY myisam_t2_18.a;\n" +
                "\n" +
                "SELECT DISTINCT SQL_BIG_RESULT myisam_t1_17.a\n" +
                "FROM myisam_t1_17, myisam_t2_18;\n" +
                "\n" +
                "SELECT DISTINCT SQL_CALC_FOUND_ROWS b\n" +
                "FROM select_found_t1_0\n" +
                "LIMIT 1;\n" +
                "\n" +
                "SELECT DISTINCT SQL_CALC_FOUND_ROWS email\n" +
                "FROM select_found_t2_3\n" +
                "\tLEFT JOIN select_found_t1_2 ON kid = select_found_t2_3.id\n" +
                "WHERE select_found_t1_2.id IS NULL\n" +
                "ORDER BY email\n" +
                "LIMIT 10;\n" +
                "\n" +
                "SELECT DISTINCT SQL_CALC_FOUND_ROWS email\n" +
                "FROM select_found_t2_3\n" +
                "\tLEFT JOIN select_found_t1_2 ON kid = select_found_t2_3.id\n" +
                "WHERE select_found_t1_2.id IS NULL\n" +
                "LIMIT 10;\n" +
                "\n" +
                "SELECT DISTINCT SQL_CALC_FOUND_ROWS 'a'\n" +
                "FROM select_found_t1_10\n" +
                "GROUP BY b\n" +
                "LIMIT 2;\n" +
                "\n", builder.toString());
    }

    public void test_8() throws Exception {
        String sql = "SELECT COUNT(*) FROM   (SELECT * FROM 16k_t1_30 FORCE INDEX (idx,PRIMARY)      WHERE a BETWEEN 2 AND 7 OR pk=1000000) AS t;";

        List<SQLStatement> stmts = SQLUtils
                .parseStatements(sql, DbType.mysql);

        StringBuilder builder = new StringBuilder();
        for (SQLStatement stmt : stmts) {
            builder.append(stmt.toString()).append("\n\n");
        }

        assertEquals("SELECT COUNT(*)\n" +
                "FROM (\n" +
                "\tSELECT *\n" +
                "\tFROM 16k_t1_30 FORCE INDEX (idx, PRIMARY)\n" +
                "\tWHERE a BETWEEN 2 AND 7\n" +
                "\t\tOR pk = 1000000\n" +
                ") t;\n" +
                "\n", builder.toString());
    }

    public void test_9() throws Exception {
        String sql = "select 1 + /*!00000 2 */ + 3 /*!99999 noise*/ + 4;\n" +
                "SELECT 100 + /*  + shouldn't fail */ 1 AS result;\n" +
                "SELECT 100 + /*  ! shouldn't fail */ 1 AS result;\n" +
                "SELECT 1 FROM /*+ regular commentary, not a hint! */ opt_hints_t1_14;\n" +
                "SELECT 1 FROM /*+ #1 */ opt_hints_t1_14 WHERE /*+ #2 */ 1 /*+ #3 */;\n" +
                "SELECT /*+ NO_ICP() */ 1   FROM /*+ regular commentary, not a hint! */ opt_hints_t1_14;\n" +
//                "select 2 as expected, /*!01000/**/*/ 2 as result;\n" +
//                "select 1 as expected, /*!99998/**/*/ 1 as result;\n" +
                "select 3 as expected, /*!01000 1 + */ 2 as result;\n" +
                "select 2 as expected, /*!99990 1 + */ 2 as result;\n" +
//                "select 7 as expected, /*!01000 1 + /* 8 + */ 2 + */ 4 as result;\n" +
//                "select 8 as expected, /*!99998 1 + /* 2 + */ 4 + */ 8 as result;\n" +
                "select 7 as expected, /*!01000 1 + /*!99998 8 + */ 2 + */ 4 as result;\n" +
                "select 4 as expected, /*!99998 1 + /*!99998 8 + */ 2 + */ 4 as result;\n" +
                "select 4 as expected, /*!99998 1 + /*!01000 8 + */ 2 + */ 4 as result;";

        List<SQLStatement> stmts = SQLUtils
                .parseStatements(sql, DbType.mysql);

        StringBuilder builder = new StringBuilder();
        for (SQLStatement stmt : stmts) {
            builder.append(stmt.toString()).append("\n\n");
        }

        assertEquals("SELECT 1 + +3 + 4;\n" +
                "\n" +
                "SELECT 100 + /*  + shouldn't fail */\n" +
                "\t1 AS result;\n" +
                "\n" +
                "SELECT 100 + /*  ! shouldn't fail */\n" +
                "\t1 AS result;\n" +
                "\n" +
                "SELECT 1\n" +
                "FROM opt_hints_t1_14;\n" +
                "\n" +
                "SELECT 1\n" +
                "FROM opt_hints_t1_14\n" +
                "WHERE 1;\n" +
                "\n" +
                "SELECT /*+ NO_ICP() */ 1\n" +
                "FROM opt_hints_t1_14;\n" +
                "\n" +
//                "SELECT 2 AS expected, 2 AS result;\n" +
//                "\n" +
//                "SELECT 1 AS expected, 1 AS result;\n" +
//                "\n" +
                "SELECT 3 AS expected, 2 AS result;\n" +
                "\n" +
                "SELECT 2 AS expected, 2 AS result;\n" +
                "\n" +
//                "SELECT 7 AS expected, 4 AS result;\n" +
//                "\n" +
//                "SELECT 8 AS expected, 8 AS result;\n" +
//                "\n" +
                "SELECT 7 AS expected, 4 AS result;\n" +
                "\n" +
                "SELECT 4 AS expected, 4 AS result;\n" +
                "\n" +
                "SELECT 4 AS expected, 4 AS result;\n" +
                "\n", builder.toString());
    }

    public void test_10() throws Exception {
        String sql = "select create_1ea10_7.1a20,1e+ 1e+10 from create_1ea10_7;";

        List<SQLStatement> stmts = SQLUtils
                .parseStatements(sql, DbType.mysql);

        StringBuilder builder = new StringBuilder();
        for (SQLStatement stmt : stmts) {
            builder.append(stmt.toString()).append("\n\n");
        }

        assertEquals("SELECT create_1ea10_7.1a20, 1e + 1e+10\n" +
                "FROM create_1ea10_7;\n" +
                "\n", builder.toString());
    }

    public void test_11() throws Exception {
        String sql = "select join_outer_t1_0.*,join_outer_t2_1.* from { oj join_outer_t2_1 left outer join join_outer_t1_0 on (join_outer_t1_0.a=join_outer_t2_1.a) };\n" +
                "select join_outer_t1_0.*,join_outer_t2_1.* from join_outer_t1_0 as t0,{ oj join_outer_t2_1 left outer join join_outer_t1_0 on (join_outer_t1_0.a=join_outer_t2_1.a) } WHERE t0.a=2;\n" +
                "select {fn length(\"hello\")}, { date \"1997-10-20\" };\n" +
                "SELECT parser_t1_58.* FROM parser_t1_58 AS t0, { OJ parser_t2_59 INNER JOIN parser_t1_58 ON (parser_t1_58.a1=parser_t2_59.a1) } WHERE t0.a3=2;\n" +
                "SELECT parser_t1_58.*,parser_t2_59.* FROM { OJ ((parser_t1_58 INNER JOIN parser_t2_59 ON (parser_t1_58.a1=parser_t2_59.a2)) LEFT OUTER JOIN parser_t3_60 ON parser_t3_60.a3=parser_t2_59.a1)};\n" +
                "SELECT parser_t1_58.*,parser_t2_59.* FROM { OJ ((parser_t1_58 LEFT OUTER JOIN parser_t2_59 ON parser_t1_58.a3=parser_t2_59.a2) INNER JOIN parser_t3_60 ON (parser_t3_60.a1=parser_t2_59.a2))};\n" +
                "SELECT parser_t1_58.*,parser_t2_59.* FROM { OJ (parser_t1_58 LEFT OUTER JOIN parser_t2_59 ON parser_t1_58.a1=parser_t2_59.a2) CROSS JOIN parser_t3_60 ON (parser_t3_60.a2=parser_t2_59.a3)};\n" +
                "SELECT * FROM {oj parser_t1_58 LEFT OUTER JOIN parser_t2_59 ON parser_t1_58.a1=parser_t2_59.a3} WHERE parser_t1_58.a2 > 10;";

        List<SQLStatement> stmts = SQLUtils
                .parseStatements(sql, DbType.mysql);

        StringBuilder builder = new StringBuilder();
        for (SQLStatement stmt : stmts) {
            builder.append(stmt.toString()).append("\n\n");
        }

        assertEquals("SELECT join_outer_t1_0.*, join_outer_t2_1.*\n" +
                "FROM join_outer_t2_1\n" +
                "\tLEFT JOIN join_outer_t1_0 ON join_outer_t1_0.a = join_outer_t2_1.a;\n" +
                "\n" +
                "SELECT join_outer_t1_0.*, join_outer_t2_1.*\n" +
                "FROM (join_outer_t1_0 t0, join_outer_t2_1)\n" +
                "\tLEFT JOIN join_outer_t1_0 ON join_outer_t1_0.a = join_outer_t2_1.a\n" +
                "WHERE t0.a = 2;\n" +
                "\n" +
                "SELECT length('hello'), DATE '1997-10-20';\n" +
                "\n" +
                "SELECT parser_t1_58.*\n" +
                "FROM (parser_t1_58 t0, parser_t2_59)\n" +
                "\tINNER JOIN parser_t1_58 ON parser_t1_58.a1 = parser_t2_59.a1\n" +
                "WHERE t0.a3 = 2;\n" +
                "\n" +
                "SELECT parser_t1_58.*, parser_t2_59.*\n" +
                "FROM parser_t1_58\n" +
                "\tINNER JOIN parser_t2_59 ON parser_t1_58.a1 = parser_t2_59.a2\n" +
                "\tLEFT JOIN parser_t3_60 ON parser_t3_60.a3 = parser_t2_59.a1;\n" +
                "\n" +
                "SELECT parser_t1_58.*, parser_t2_59.*\n" +
                "FROM parser_t1_58\n" +
                "\tLEFT JOIN parser_t2_59 ON parser_t1_58.a3 = parser_t2_59.a2\n" +
                "\tINNER JOIN parser_t3_60 ON parser_t3_60.a1 = parser_t2_59.a2;\n" +
                "\n" +
                "SELECT parser_t1_58.*, parser_t2_59.*\n" +
                "FROM parser_t1_58\n" +
                "\tLEFT JOIN parser_t2_59 ON parser_t1_58.a1 = parser_t2_59.a2\n" +
                "\tCROSS JOIN parser_t3_60 ON parser_t3_60.a2 = parser_t2_59.a3;\n" +
                "\n" +
                "SELECT *\n" +
                "FROM parser_t1_58\n" +
                "\tLEFT JOIN parser_t2_59 ON parser_t1_58.a1 = parser_t2_59.a3\n" +
                "WHERE parser_t1_58.a2 > 10;\n" +
                "\n", builder.toString());
    }
}