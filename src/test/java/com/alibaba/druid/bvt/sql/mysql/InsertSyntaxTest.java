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

public class InsertSyntaxTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "INSERT INTO tbl_name () VALUES();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("INSERT INTO tbl_name\nVALUES ();", text);
    }

    public void test_1() throws Exception {
        String sql = "INSERT INTO tbl_name (col1,col2) VALUES(15,col1*2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("INSERT INTO tbl_name (col1, col2)\nVALUES (15, col1 * 2);", text);
    }

    public void test_2() throws Exception {
        String sql = "INSERT INTO tbl_name (col1,col2) VALUES(col2*2,15);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("INSERT INTO tbl_name (col1, col2)\nVALUES (col2 * 2, 15);", text);
    }

    public void test_3() throws Exception {
        String sql = "INSERT INTO tbl_name (a,b,c) VALUES(1,2,3),(4,5,6),(7,8,9);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("INSERT INTO tbl_name (a, b, c)\nVALUES (1, 2, 3)," + //
                            "\n\t(4, 5, 6)," + //
                            "\n\t(7, 8, 9);", text);
    }

    public void test_4() throws Exception {
        String sql = "INSERT INTO tbl_name (a,b,c) VALUES(1,2,3,4,5,6,7,8,9);;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("INSERT INTO tbl_name (a, b, c)" + //
                            "\nVALUES (1, 2, 3, 4, 5" + //
                            "\n\t, 6, 7, 8, 9);", text);
    }

    public void test_5() throws Exception {
        String sql = "INSERT LOW_PRIORITY DELAYED HIGH_PRIORITY IGNORE INTO tbl_name (a,b,c) VALUES(1,2,3);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("INSERT LOW_PRIORITY DELAYED HIGH_PRIORITY IGNORE INTO tbl_name (a, b, c)\nVALUES (1, 2, 3);",
                            text);
    }

    public void test_6() throws Exception {
        String sql = "INSERT tbl_name (a,b,c) VALUES(1,2,3) ON DUPLICATE KEY UPDATE c=c+1;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("INSERT INTO tbl_name (a, b, c)" + //
                            "\nVALUES (1, 2, 3)" + //
                            "\nON DUPLICATE KEY UPDATE c = c + 1;", text);
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
