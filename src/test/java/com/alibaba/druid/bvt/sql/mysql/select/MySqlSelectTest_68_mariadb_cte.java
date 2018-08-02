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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_68_mariadb_cte extends MysqlTest {

    public void test_0() throws Exception {
        String sql =  "WITH RECURSIVE ancestors AS (\n" +
                "    SELECT *\n" +
                "    FROM org\n" +
                "    WHERE 1=1\n" +
                "    AND deleted = 1\n" +
                "    UNION\n" +
                "    SELECT f.*\n" +
                "    FROM org AS f, ancestors AS a\n" +
                "    WHERE\n" +
                "    f.id = a.parent_id\n" +
                "    )\n" +
                "    SELECT * FROM ancestors;";

        System.out.println(sql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL, SQLParserFeature.OptimizedForParameterized);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("WITH RECURSIVE ancestors AS (\n" +
                            "\t\tSELECT *\n" +
                            "\t\tFROM org\n" +
                            "\t\tWHERE 1 = 1\n" +
                            "\t\t\tAND deleted = 1\n" +
                            "\t\tUNION\n" +
                            "\t\tSELECT f.*\n" +
                            "\t\tFROM org f, ancestors a\n" +
                            "\t\tWHERE f.id = a.parent_id\n" +
                            "\t)\n" +
                            "SELECT *\n" +
                            "FROM ancestors;", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("with recursive ancestors as (\n" +
                            "\t\tselect *\n" +
                            "\t\tfrom org\n" +
                            "\t\twhere 1 = 1\n" +
                            "\t\t\tand deleted = 1\n" +
                            "\t\tunion\n" +
                            "\t\tselect f.*\n" +
                            "\t\tfrom org f, ancestors a\n" +
                            "\t\twhere f.id = a.parent_id\n" +
                            "\t)\n" +
                            "select *\n" +
                            "from ancestors;", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("WITH RECURSIVE ancestors AS (\n" +
                            "\t\tSELECT *\n" +
                            "\t\tFROM org\n" +
                            "\t\tWHERE 1 = 1\n" +
                            "\t\t\tAND deleted = ?\n" +
                            "\t\tUNION\n" +
                            "\t\tSELECT f.*\n" +
                            "\t\tFROM org f, ancestors a\n" +
                            "\t\tWHERE f.id = a.parent_id\n" +
                            "\t)\n" +
                            "SELECT *\n" +
                            "FROM ancestors;", //
                    output);
        }
    }
}
