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
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class MySqlSelectTest_295
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from teacher_text a join teacher b on a.id=b.id  where row(a.id,'B') not in (select a.id,a.coid from community a where coid ='B') order by 1;";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SupportUnicodeCodePoint);

        assertEquals("SELECT *\n" +
                "FROM teacher_text a\n" +
                "\tJOIN teacher b ON a.id = b.id\n" +
                "WHERE row(a.id, 'B') NOT IN (\n" +
                "\tSELECT a.id, a.coid\n" +
                "\tFROM community a\n" +
                "\tWHERE coid = 'B'\n" +
                ")\n" +
                "ORDER BY 1;", stmt.toString());

        SQLExpr expr = SQLUtils.toSQLExpr("CAST(ROW (`id_0_3`, `coid`) AS row(bigint,varchar))", DbType.mysql);
        assertEquals("CAST(ROW(`id_0_3`, `coid`) AS ROW(bigint,varchar))", SQLUtils.toMySqlString(expr));
    }

    public void test_1() throws Exception {
        String sql = "SELECT \"$operator$HASH_CODE\"(ARRAY[1, 2])";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SupportUnicodeCodePoint);

        assertEquals("SELECT $operator$HASH_CODE(ARRAY[1, 2])", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "alter table ddlDb.addCluKey add CLUSTERING key ck1(c1)";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SupportUnicodeCodePoint);

        assertEquals("ALTER TABLE ddlDb.addCluKey\n" +
                "\tADD CLUSTERING KEY ck1 (c1)", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select cast(row(1,row(2,3)) as row(varchar, row(integer)));";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql, SQLParserFeature.SupportUnicodeCodePoint);

        assertEquals("SELECT CAST(row(1, row(2, 3)) AS ROW(varchar,ROW(integer)));", stmt.toString());
    }



}