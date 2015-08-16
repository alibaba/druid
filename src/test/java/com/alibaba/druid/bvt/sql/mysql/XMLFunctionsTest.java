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

public class XMLFunctionsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SET @xml = '<a><b>X</b><b>Y</b></a>';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SET @xml = '<a><b>X</b><b>Y</b></a>';", text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT @i, ExtractValue(@xml, '//b[$@i]');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT @i, ExtractValue(@xml, '//b[$@i]');", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT @j, ExtractValue(@xml, '//b[$@j]');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT @j, ExtractValue(@xml, '//b[$@j]');", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT @k, ExtractValue(@xml, '//b[$@k]');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT @k, ExtractValue(@xml, '//b[$@k]');", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT ExtractValue('<a><b/></a>', '/a/b');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ExtractValue('<a><b/></a>', '/a/b');", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT UpdateXML('<a><b>ccc</b><d></d></a>', '/a', '<e>fff</e>') AS val1";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT UpdateXML('<a><b>ccc</b><d></d></a>', '/a', '<e>fff</e>') AS val1;", text);
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
