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

public class ControlFlowFunctionsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT CASE 1 WHEN 1 THEN 'one' WHEN 2 THEN 'two' ELSE 'more' END;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CASE 1 WHEN 1 THEN 'one' WHEN 2 THEN 'two' ELSE 'more' END;", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT IF(1>2,2,3);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IF(1 > 2, 2, 3);", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT IF(1<2,'yes','no');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IF(1 < 2, 'yes', 'no');", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT IF(STRCMP('test','test1'),'no','yes');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IF(STRCMP('test', 'test1'), 'no', 'yes');", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT IFNULL(1,0);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IFNULL(1, 0);", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT IFNULL(1/0,'yes');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT IFNULL(1 / 0, 'yes');", text);
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
