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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;

public class MySqlParameterizedOutputVisitorTest extends TestCase {

    public void test_0() throws Exception {
        validate("SELECT * FROM T WHERE ID IN (?, ?, ?)", "SELECT *\nFROM T\nWHERE ID IN (?)");
        validate("SELECT * FROM T WHERE ID = 5", "SELECT *\nFROM T\nWHERE ID = ?");
        validate("SELECT * FROM T WHERE 1 = 0 AND ID = 5", "SELECT *\nFROM T\nWHERE 1 = 0\n\tAND ID = ?");
        validate("SELECT * FROM T WHERE ID = ? OR ID = ?", "SELECT *\nFROM T\nWHERE ID = ?");
        validate("SELECT * FROM T WHERE A.ID = ? OR A.ID = ?", "SELECT *\nFROM T\nWHERE A.ID = ?");
        validate("SELECT * FROM T WHERE 1 = 0 OR a.id = ? OR a.id = ? OR a.id = ? OR a.id = ?",
                 "SELECT *\nFROM T\nWHERE 1 = 0\n\tOR a.id = ?");
        validateOracle("SELECT * FROM T WHERE 1 = 0 OR a.id = ? OR a.id = ? OR a.id = ? OR a.id = ?",
                       "SELECT *\nFROM T\nWHERE 1 = 0\n\tOR a.id = ?");
        validateOracle("SELECT * FROM T WHERE A.ID = ? OR A.ID = ?", "SELECT *\nFROM T\nWHERE A.ID = ?");
        validate("INSERT INTO T (F1, F2) VALUES(?, ?), (?, ?), (?, ?)", "INSERT INTO T (F1, F2)\nVALUES (?, ?)");
        validate("update net_device d, sys_user u set d.resp_user_id=u.id where d.resp_user_login_name=u.username and d.id in (42354)", //
                 "UPDATE net_device d, sys_user u\nSET d.resp_user_id = u.id\nWHERE d.resp_user_login_name = u.username\n\tAND d.id IN (?)");

        // System.out.println("SELECT * FORM A WHERE ID = (##)".replaceAll("##", "?"));
    }

    void validate(String sql, String expect) {

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        MySqlParameterizedOutputVisitor visitor = new MySqlParameterizedOutputVisitor(out);
        stmt.accept(visitor);
        
        Assert.assertTrue(visitor.getReplaceCount() > 0);

        Assert.assertEquals(expect, out.toString());
    }

    void validateOracle(String sql, String expect) {

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        OracleParameterizedOutputVisitor visitor = new OracleParameterizedOutputVisitor(out, false);
        stmt.accept(visitor);
        
        Assert.assertTrue(visitor.getReplaceCount() > 0);

        Assert.assertEquals(expect, out.toString());
    }
}
