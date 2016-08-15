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
package com.alibaba.druid.bvt.sql.sqlserver;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;

public class SQLServerInsertTest5 extends TestCase {

    public void test_parseCompleteValues_false() throws Exception {
        String sql = "insert into t(a,b) values ('a1','b1'),('a2','b2'),('a3','b3'),('a4','b4');";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        parser.setParseCompleteValues(false);
        parser.setParseValuesSize(3);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLServerInsertStatement insertStmt = (SQLServerInsertStatement) stmt;

        Assert.assertEquals(3, insertStmt.getValuesList().size());
        Assert.assertEquals(2, insertStmt.getValues().getValues().size());
        Assert.assertEquals(2, insertStmt.getColumns().size());
        Assert.assertEquals(1, statementList.size());

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

        String formatSql = "INSERT INTO t"//
                           + "\n\t(a, b)"//
                           + "\nVALUES" + "\n('a1', 'b1'),"//
                           + "\n('a2', 'b2'),"//
                           + "\n('a3', 'b3')";
        Assert.assertEquals(formatSql, SQLUtils.toSQLServerString(insertStmt));
    }

    public void test_parseCompleteValues_true() throws Exception {
        String sql = "insert into t(a,b) values ('a1','b1'),('a2','b2'),('a3','b3'),('a4','b4');";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        parser.setParseCompleteValues(true);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLServerInsertStatement insertStmt = (SQLServerInsertStatement) stmt;

        Assert.assertEquals(4, insertStmt.getValuesList().size());
        Assert.assertEquals(2, insertStmt.getValues().getValues().size());
        Assert.assertEquals(2, insertStmt.getColumns().size());
        Assert.assertEquals(1, statementList.size());

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

        String formatSql = "INSERT INTO t"//
                           + "\n\t(a, b)"//
                           + "\nVALUES"//
                           + "\n('a1', 'b1'),"//
                           + "\n('a2', 'b2'),"//
                           + "\n('a3', 'b3'),"//
                           + "\n('a4', 'b4')";
        Assert.assertEquals(formatSql, SQLUtils.toSQLServerString(insertStmt));
    }
}
