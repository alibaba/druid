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
package com.alibaba.druid.sql.mysql.demo;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

/**
 * @author guoyukun gyk001@gmail.com
 */
public class MysqlBackQuoteTest extends TestCase {

    public void test_demo_0() throws Exception {
        String sql = "SELECT `a` as a, `table` as t from `d`";
        String expectSql = "SELECT a AS a, table AS t\n" + "FROM d;";

        // parser得到AST
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList(); //

        // 将AST通过visitor输出
        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            // System.out.println(stmt);
            if (stmt instanceof SQLSelectStatement) {
                SQLSelectStatement ss = (SQLSelectStatement) stmt;

                SQLSelectQuery ssq = ss.getSelect().getQuery();
                List<SQLSelectItem> items = ((SQLSelectQueryBlock) ssq).getSelectList();
                Assert.assertNotNull(items);
                Assert.assertEquals(2, items.size());
                Assert.assertEquals("a", items.get(0).getAlias());
                Assert.assertEquals("t", items.get(1).getAlias());
            }
            stmt.accept(visitor);
            out.append(";");
        }

        System.out.println(out.toString());
        Assert.assertEquals(expectSql, out.toString());
    }
}
