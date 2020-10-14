/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;


public class MySqlSelectTest_101_binary extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "\n" +
                "SELECT 1 AS one FROM `projects` WHERE  `projects`.`name` = BINARY 'test11111111111' AND `projects`.`namespace_id` = 61  LIMIT 1;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        SQLBinaryOpExpr where = (SQLBinaryOpExpr) stmt.getSelect().getQueryBlock().getWhere();
        SQLBinaryOpExpr left = (SQLBinaryOpExpr) where.getLeft();
        SQLBinaryOpExpr right = (SQLBinaryOpExpr) where.getRight();

        assertEquals("`projects`.`name` = BINARY 'test11111111111'", SQLUtils.toMySqlString(left));
        assertEquals("`projects`.`namespace_id` = 61", SQLUtils.toMySqlString(right));

        assertEquals("SELECT 1 AS one\n" +
                "FROM `projects`\n" +
                "WHERE `projects`.`name` = BINARY 'test11111111111'\n" +
                "\tAND `projects`.`namespace_id` = 61\n" +
                "LIMIT 1;", stmt.toString());
    }

}