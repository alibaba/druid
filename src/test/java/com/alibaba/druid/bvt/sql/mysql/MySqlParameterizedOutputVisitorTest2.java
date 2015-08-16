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
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;

public class MySqlParameterizedOutputVisitorTest2 extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM T WHERE ID = ?";
        for (int i = 0; i < 10000; ++i) {
            sql += " OR ID = ?";
        }

        validate(sql, "SELECT *\nFROM T\nWHERE ID = ?");
        validateOracle(sql, "SELECT *\nFROM T\nWHERE ID = ?");
    }

    void validate(String sql, String expect) {

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        MySqlParameterizedOutputVisitor visitor = new MySqlParameterizedOutputVisitor(out);
        statemen.accept(visitor);

        Assert.assertEquals(expect, out.toString());
    }

    void validateOracle(String sql, String expect) {

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        StringBuilder out = new StringBuilder();
        OracleParameterizedOutputVisitor visitor = new OracleParameterizedOutputVisitor(out, false);
        statemen.accept(visitor);

        Assert.assertEquals(expect, out.toString());
    }
}
