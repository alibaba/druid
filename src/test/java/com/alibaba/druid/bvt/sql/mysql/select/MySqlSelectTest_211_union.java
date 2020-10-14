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

public class MySqlSelectTest_211_union extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select count(*) from ((select k from linxi3 except select id from linxi1 where id =1) union select k from linxi50);";

        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT count(*)\n" +
                        "FROM (\n" +
                        "\t(SELECT k\n" +
                        "\tFROM linxi3\n" +
                        "\tEXCEPT\n" +
                        "\tSELECT id\n" +
                        "\tFROM linxi1\n" +
                        "\tWHERE id = 1)\n" +
                        "\tUNION\n" +
                        "\tSELECT k\n" +
                        "\tFROM linxi50\n" +
                        ");", //
                stmt.toString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(3, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());


    }

}
