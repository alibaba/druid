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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlSelectTest_206_union extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT uid FROM (select uid, cid, category, empid from comb_opportunity_sales_relation_online where uid = 1723876714657374)  \n" +
                "union SELECT uid FROM (select uid, cid, category, empid from comb_opportunity_sales_relation_offline where uid = 1723876714657374) ";

        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT uid\n" +
                        "FROM (\n" +
                        "\tSELECT uid, cid, category, empid\n" +
                        "\tFROM comb_opportunity_sales_relation_online\n" +
                        "\tWHERE uid = 1723876714657374\n" +
                        ")\n" +
                        "UNION\n" +
                        "SELECT uid\n" +
                        "FROM (\n" +
                        "\tSELECT uid, cid, category, empid\n" +
                        "\tFROM comb_opportunity_sales_relation_offline\n" +
                        "\tWHERE uid = 1723876714657374\n" +
                        ")", //
                stmt.toString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(2, visitor.getTables().size());
        assertEquals(8, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());


    }

}
