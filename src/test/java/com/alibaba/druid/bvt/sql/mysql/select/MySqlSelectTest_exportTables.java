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
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_exportTables extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select *\n" +
                "from my_table\n" +
                "where level between 10-5 and 10+5\n" +
                "order by -ABS(10 - level) desc\n" +
                "limit 0,100";

        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        assertEquals(1, statementList.size());

        StringBuffer out = new StringBuffer();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        visitor.setExportTables(true);

        stmt.accept(visitor);


        assertEquals("SELECT *\n" +
                        "FROM my_table\n" +
                        "WHERE level BETWEEN 10 - 5 AND 10 + 5\n" +
                        "ORDER BY -ABS(10 - level) DESC\n" +
                        "LIMIT 0, 100", //
                            out.toString());

        assertNotNull(visitor.getTables());
        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.getTables().contains("my_table"));
    }

    public void test_1() throws Exception {
        String sql = "SELECT * FROM table1 INNER JOIN table2 ON table1.id = table2.id;";


        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        assertEquals(1, statementList.size());

        StringBuffer out = new StringBuffer();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        visitor.setExportTables(true);

        stmt.accept(visitor);


        assertEquals("SELECT *\n" +
                        "FROM table1\n" +
                        "\tINNER JOIN table2 ON table1.id = table2.id;", //
                out.toString());

        assertNotNull(visitor.getTables());
        assertEquals(2, visitor.getTables().size());
        assertEquals("[table1, table2]", visitor.getTables().toString());
    }
    
}
