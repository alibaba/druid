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


public class MySqlSelectTest_293_operator_precedence extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT ALL + + ( + col1 ) \n" +
                "FROM random_aggregates_23_tab2 AS cor0 \n" +
                "WHERE NOT ( + + col2 ) + CAST( NULL AS SIGNED ) IS NOT NULL";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT ALL +(+(+col1))\n" +
                "FROM random_aggregates_23_tab2 cor0\n" +
                "WHERE NOT +(+col2) + CAST(NULL AS SIGNED) IS NOT NULL", stmt.toString());

        System.out.println(stmt.toString());
    }


    public void test_1() throws Exception {
        String sql = "SELECT - CAST( 73 AS SIGNED ) + 60 AS col0";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT -CAST(73 AS SIGNED) + 60 AS col0", stmt.toString());

        System.out.println(stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT + 81 * 58 + - 33 DIV + CASE 58 WHEN - 15 + + 31 THEN + 95 WHEN - CAST( - NULLIF ( + 49, - 18 ) AS SIGNED ) + + 33 THEN - - 23 + + 54 ELSE 53 END DIV - + 77 DIV 49 AS col1 ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT (+81) * 58 + -33 DIV (+CASE 58\n" +
                "\t\tWHEN -15 + +31 THEN +95\n" +
                "\t\tWHEN -CAST(-NULLIF(+49, -18) AS SIGNED) + +33 THEN --23 + +54\n" +
                "\t\tELSE 53\n" +
                "\tEND) DIV (-(+77)) DIV 49 AS col1", stmt.toString());

        System.out.println(stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "SELECT DISTINCT CASE + 27 \n" +
                "\tWHEN - ( MIN( + - 75 ) ) / - COUNT( * ) * ( - - 52 ) - - COUNT( * ) - - 36 / + + 56 * - 24 * - 2 THEN 64 \n" +
                "\tWHEN + + MIN( + 9 ) + ( - 76 ) + COUNT( * ) + - 15 + + 25 + - + ( - + 79 ) * 28 THEN NULL \n" +
                "\tWHEN - + 88 THEN + 28 \n" +
                "\tELSE - 89 + + - 29 \n" +
                "END ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT DISTINCT CASE +27\n" +
                "\t\tWHEN (-MIN(+-75)) / -COUNT(*) * --52 - -COUNT(*) - -36 / +(+56) * -24 * -2 THEN 64\n" +
                "\t\tWHEN +(+MIN(+9)) + -76 + COUNT(*) + -15 + +25 + (-(+(-(+79)))) * 28 THEN NULL\n" +
                "\t\tWHEN -(+88) THEN +28\n" +
                "\t\tELSE -89 + +-29\n" +
                "\tEND", stmt.toString());

        System.out.println(stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "SELECT 1 \n" +
                "FROM (select 1 col0, 2 col1, 3 col2) x \n" +
                "WHERE NOT ( - ( + col2 ) * + + col0 ) = col0";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 1\n" +
                "FROM (\n" +
                "\tSELECT 1 AS col0, 2 AS col1, 3 AS col2\n" +
                ") x\n" +
                "WHERE NOT (-(+col2)) * +(+col0) = col0", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "SELECT 78 DIV + - 73 * - 86 DIV 61 AS col0";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT 78 DIV +-73 * -86 DIV 61 AS col0", stmt.toString());

        System.out.println(stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "SELECT DISTINCT 81 DIV + 73 * - 85 DIV + + 50 AS col1";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT DISTINCT 81 DIV +73 * -85 DIV +(+50) AS col1", stmt.toString());

        System.out.println(stmt.toString());
    }

    public void test_7() throws Exception {
        String sql = "SELECT + CASE WHEN 33 NOT BETWEEN - + 16 AND ( + COUNT( * ) + COUNT( * ) / - COALESCE ( - 27, ( - MAX( ALL 41 ) ) / 24 * - - 95 - - 80 + - COUNT( * ) * CAST( NULL AS DECIMAL ) / + 76 - - + 74 * - 49 + - - 25 ) * 89 * - NULLIF ( - - SUM( DISTINCT + 57 ), COUNT( * ) ) - 29 - + MAX( - - 43 ) - + + MAX( DISTINCT + 90 ) + CASE - + 26 WHEN NULLIF ( 5, + 58 * MIN( 67 ) + COUNT( * ) * 8 ) * 47 + CAST( NULL AS SIGNED ) THEN 57 WHEN + 45 THEN NULL ELSE CAST( 35 AS SIGNED ) * 56 END * CAST( NULL AS SIGNED ) ) THEN NULL WHEN NOT + ( - 10 ) / 42 IS NULL THEN + 93 ELSE 54 END * + 36";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT (+CASE \n" +
                "\t\tWHEN 33 NOT BETWEEN (-(+16)) AND (+COUNT(*) + COUNT(*) / -COALESCE(-27, (-MAX(ALL 41)) / 24 * --95 - -80 + (-COUNT(*)) * CAST(NULL AS DECIMAL) / +76 - (-(+74)) * -49 + --25) * 89 * -NULLIF(-(-SUM(DISTINCT +57)), COUNT(*)) - 29 - +MAX(--43) - +(+MAX(DISTINCT +90)) + CASE -(+26)\n" +
                "\t\t\t\t\tWHEN NULLIF(5, (+58) * MIN(67) + COUNT(*) * 8) * 47 + CAST(NULL AS SIGNED) THEN 57\n" +
                "\t\t\t\t\tWHEN +45 THEN NULL\n" +
                "\t\t\t\t\tELSE CAST(35 AS SIGNED) * 56\n" +
                "\t\t\t\tEND * CAST(NULL AS SIGNED))\n" +
                "\t\tTHEN NULL\n" +
                "\t\tWHEN NOT (+-10) / 42 IS NULL THEN +93\n" +
                "\t\tELSE 54\n" +
                "\tEND) * +36", stmt.toString());

        System.out.println(stmt.toString());
    }

}