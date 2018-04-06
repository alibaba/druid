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
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class DeleteSyntaxTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "DELETE FROM somelog WHERE user = 'jcole' ORDER BY timestamp_column LIMIT 1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLStatement stmt = stmtList.get(0);

        assertEquals("DELETE FROM somelog\nWHERE user = 'jcole'\nORDER BY timestamp_column\nLIMIT 1;", SQLUtils.toMySqlString(stmt));
    }

    public void test_1() throws Exception {
        String sql = "DELETE t1 FROM t1 LEFT JOIN t2 ON t1.id=t2.id WHERE t2.id IS NULL;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLStatement stmt = stmtList.get(0);

        assertEquals("DELETE t1" + //
                            "\nFROM t1" + //
                            "\n\tLEFT JOIN t2 ON t1.id = t2.id" + //
                            "\nWHERE t2.id IS NULL;", SQLUtils.toMySqlString(stmt));
    }

    public void test_2() throws Exception {
        String sql = "DELETE a1, a2 FROM t1 AS a1 INNER JOIN t2 AS a2 WHERE a1.id=a2.id";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLStatement stmt = stmtList.get(0);

        assertEquals("DELETE a1, a2\nFROM t1 a1" + //
                            "\n\tINNER JOIN t2 a2" + //
                            "\nWHERE a1.id = a2.id", SQLUtils.toMySqlString(stmt));
    }

    public void test_3() throws Exception {
        String sql = "DELETE FROM a1, a2 USING t1 AS a1 INNER JOIN t2 AS a2 WHERE a1.id=a2.id";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLStatement stmt = stmtList.get(0);

        Assert.assertEquals("DELETE FROM a1, a2\n" +
                "USING t1 a1\n" +
                "\tINNER JOIN t2 a2\n" +
                "WHERE a1.id = a2.id", SQLUtils.toMySqlString(stmt));
        Assert.assertEquals("delete from a1, a2\n" +
                "using t1 a1\n" +
                "\tinner join t2 a2\n" +
                "where a1.id = a2.id", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

    public void test_4() throws Exception {
        String sql = "DELETE LOW_PRIORITY QUICK IGNORE FROM T";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLStatement stmt = stmtList.get(0);

        Assert.assertEquals("DELETE LOW_PRIORITY QUICK IGNORE FROM T", SQLUtils.toMySqlString(stmt));
        Assert.assertEquals("delete low_priority quick ignore from T", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

}
