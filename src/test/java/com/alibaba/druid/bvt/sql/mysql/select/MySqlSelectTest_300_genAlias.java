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
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class MySqlSelectTest_300_genAlias
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT `current_date`(), `current_timestamp`(), `current_time`(), `localtime`(), `localtimestamp`()";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SelectItemGenerateAlias);

        assertEquals("SELECT `current_date`() AS ```current_date``()`, `current_timestamp`() AS ```current_timestamp``()`, `current_time`() AS ```current_time``()`, `localtime`() AS ```localtime``()`, `localtimestamp`() AS ```localtimestamp``()`", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "select count(*)  from `aa_lineitem`";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SelectItemGenerateAlias);

        assertEquals("SELECT count(*) AS `count(*)`\n" +
                "FROM `aa_lineitem`", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "DELETE FROM t1\n" +
                "WHERE s11 > ANY\n" +
                " (SELECT COUNT(*) /* no hint */ FROM t2\n" +
                "  WHERE NOT EXISTS\n" +
                "   (SELECT * FROM t3\n" +
                "    WHERE ROW(5*t2.s1,77)=\n" +
                "     (SELECT 50,11*s1 FROM t4 UNION SELECT 50,77 FROM\n" +
                "      (SELECT * FROM t5) AS t5)));";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SelectItemGenerateAlias);

        assertEquals("DELETE FROM t1\n" +
                "WHERE s11 > ANY (\n" +
                "\t\tSELECT COUNT(*) AS `COUNT(*)`\n" +
                "\t\tFROM t2\n" +
                "\t\tWHERE NOT EXISTS (\n" +
                "\t\t\tSELECT *\n" +
                "\t\t\tFROM t3\n" +
                "\t\t\tWHERE ROW(5 * t2.s1, 77) = (\n" +
                "\t\t\t\tSELECT 50, 11 * s1 AS `11*s1`\n" +
                "\t\t\t\tFROM t4\n" +
                "\t\t\t\tUNION\n" +
                "\t\t\t\tSELECT 50, 77\n" +
                "\t\t\t\tFROM (\n" +
                "\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\tFROM t5\n" +
                "\t\t\t\t) t5\n" +
                "\t\t\t)\n" +
                "\t\t)\n" +
                "\t);", stmt.toString());
    }
}