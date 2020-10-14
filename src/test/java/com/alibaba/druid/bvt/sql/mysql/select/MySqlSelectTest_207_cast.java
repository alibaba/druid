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
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlSelectTest_207_cast extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT  a.key_label as keyLabel,\n" +
                "            a.key_type_cd as keyType,\n" +
                "            a.key_subtp_cd as keySubType,\n" +
                "            a.key_size as keySize,\n" +
                "\n" +
                "            cast(b.token_no as unsigned int) as tokenNo,\n" +
                "            b.token_label as tokenLabel\n" +
                "    FROM  kky_prof a, khm_ltoken b\n" +
                "    WHERE   a.key_label is not null\n" +
                "     AND  b.com_id = a.com_id\n" +
                "     AND  a.key_stats_cd = 'Y'";


        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT a.key_label AS keyLabel, a.key_type_cd AS keyType, a.key_subtp_cd AS keySubType, a.key_size AS keySize, CAST(b.token_no AS unsigned int) AS tokenNo\n" +
                        "\t, b.token_label AS tokenLabel\n" +
                        "FROM kky_prof a, khm_ltoken b\n" +
                        "WHERE a.key_label IS NOT NULL\n" +
                        "\tAND b.com_id = a.com_id\n" +
                        "\tAND a.key_stats_cd = 'Y'", //
                stmt.toString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(2, visitor.getTables().size());
        assertEquals(9, visitor.getColumns().size());
        assertEquals(4, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());


    }

}
