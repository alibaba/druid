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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlSelectTest_210_union extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT count(*)\n" +
                "FROM (\n" +
                "  (SELECT user_id\n" +
                "     FROM user_info_offline\n" +
                "     WHERE create_time > '2018-01-01 00:00:00'\n" +
                "     EXCEPT\n" +
                "     SELECT user_id FROM user_info_online\n" +
                "  )\n" +
                "\n" +
                "      UNION\n" +
                "\n" +
                "  SELECT coalesce(a.user_id, b.user_id) AS user_id\n" +
                "      FROM user_info_online a LEFT JOIN user_info_offline b ON a.user_id = b.user_id\n" +
                "      WHERE ((a.create_time > '2018-01-01 00:00:00') OR\n" +
                "             (a.create_time IS NULL AND b.create_time > '2018-01-01 00:00:00')\n" +
                "      )\n" +
                ")";

        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT count(*)\n" +
                        "FROM (\n" +
                        "\t(SELECT user_id\n" +
                        "\tFROM user_info_offline\n" +
                        "\tWHERE create_time > '2018-01-01 00:00:00'\n" +
                        "\tEXCEPT\n" +
                        "\tSELECT user_id\n" +
                        "\tFROM user_info_online)\n" +
                        "\tUNION\n" +
                        "\tSELECT coalesce(a.user_id, b.user_id) AS user_id\n" +
                        "\tFROM user_info_online a\n" +
                        "\t\tLEFT JOIN user_info_offline b ON a.user_id = b.user_id\n" +
                        "\tWHERE a.create_time > '2018-01-01 00:00:00'\n" +
                        "\t\tOR (a.create_time IS NULL\n" +
                        "\t\t\tAND b.create_time > '2018-01-01 00:00:00')\n" +
                        ")", //
                stmt.toString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(2, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(5, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());


    }

}
