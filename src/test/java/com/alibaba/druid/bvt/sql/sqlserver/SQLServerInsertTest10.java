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
package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class SQLServerInsertTest10 extends TestCase {

    public void test_0() throws Exception {
        String sql = "INSERT INTO DataDB..TBL_TEST_7 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        parser.setParseCompleteValues(false);
        parser.setParseValuesSize(3);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLServerInsertStatement insertStmt = (SQLServerInsertStatement) stmt;

        assertEquals(1, insertStmt.getValuesList().size());
        assertEquals(33, insertStmt.getValues().getValues().size());
        assertEquals(0, insertStmt.getColumns().size());
        assertEquals(1, statementList.size());

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

        String formatSql = "INSERT INTO DataDB..TBL_TEST_7\n" +
                "VALUES (?, ?, ?, ?, ?\n" +
                "\t, ?, ?, ?, ?, ?\n" +
                "\t, ?, ?, ?, ?, ?\n" +
                "\t, ?, ?, ?, ?, ?\n" +
                "\t, ?, ?, ?, ?, ?\n" +
                "\t, ?, ?, ?, ?, ?\n" +
                "\t, ?, ?, ?)";
        assertEquals(formatSql, SQLUtils.toSQLServerString(insertStmt));
    }
}
