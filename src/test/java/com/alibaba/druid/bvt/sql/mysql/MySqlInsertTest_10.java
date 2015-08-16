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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class MySqlInsertTest_10 extends MysqlTest {

    public void test_parseCompleteValues_false() throws Exception {
        String sql = "insert into t(a,b) values ('a1','b1'),('a2','b2'),('a3','b3'),('a4','b4');";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        parser.setParseCompleteValues(false);
        parser.setParseValuesSize(3);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;

        Assert.assertEquals(3, insertStmt.getValuesList().size());
        Assert.assertEquals(2, insertStmt.getValues().getValues().size());
        Assert.assertEquals(2, insertStmt.getColumns().size());
        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String formatSql = "INSERT INTO t (a, b)"//
                           + "\nVALUES ('a1', 'b1'),"//
                           + "\n\t('a2', 'b2'),"//
                           + "\n\t('a3', 'b3')";
        Assert.assertEquals(formatSql, SQLUtils.toMySqlString(insertStmt));
    }

    public void test_parseCompleteValues_true() throws Exception {
        String sql = "insert into t(a,b) values ('a1','b1'),('a2','b2'),('a3','b3'),('a4','b4');";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        parser.setParseCompleteValues(true);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;

        Assert.assertEquals(4, insertStmt.getValuesList().size());
        Assert.assertEquals(2, insertStmt.getValues().getValues().size());
        Assert.assertEquals(2, insertStmt.getColumns().size());
        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String formatSql = "INSERT INTO t (a, b)"//
                           + "\nVALUES ('a1', 'b1'),"//
                           + "\n\t('a2', 'b2'),"//
                           + "\n\t('a3', 'b3'),"//
                           + "\n\t('a4', 'b4')";
        Assert.assertEquals(formatSql, SQLUtils.toMySqlString(insertStmt));
    }
}
