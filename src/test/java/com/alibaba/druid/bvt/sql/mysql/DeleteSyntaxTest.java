/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class DeleteSyntaxTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "DELETE FROM somelog WHERE user = 'jcole' ORDER BY timestamp_column LIMIT 1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("DELETE FROM somelog\nWHERE user = 'jcole'\nORDER BY timestamp_column\nLIMIT 1;", text);
    }

    public void test_1() throws Exception {
        String sql = "DELETE t1 FROM t1 LEFT JOIN t2 ON t1.id=t2.id WHERE t2.id IS NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("DELETE t1" + //
                            "\nFROM t1" + //
                            "\n\tLEFT JOIN t2 ON t1.id = t2.id" + //
                            "\nWHERE t2.id IS NULL;", text);
    }

    public void test_2() throws Exception {
        String sql = "DELETE a1, a2 FROM t1 AS a1 INNER JOIN t2 AS a2 WHERE a1.id=a2.id";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("DELETE a1, a2\nFROM t1 a1" + //
                            "\n\tINNER JOIN t2 a2" + //
                            "\nWHERE a1.id = a2.id;", text);
    }

    public void test_3() throws Exception {
        String sql = "DELETE FROM a1, a2 USING t1 AS a1 INNER JOIN t2 AS a2 WHERE a1.id=a2.id";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("DELETE FROM a1, a2 USING (t1) AS a1" + //
                            "\n\tINNER JOIN t2 a2\nWHERE a1.id = a2.id;", text);
    }

    public void test_4() throws Exception {
        String sql = "DELETE LOW_PRIORITY QUICK IGNORE FROM T";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("DELETE LOW_PRIORITY QUICK IGNORE FROM T;", text);
    }

    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();

        for (SQLStatement stmt : stmtList) {
            stmt.accept(new MySqlOutputVisitor(out));
            out.append(";");
        }

        return out.toString();
    }
}
