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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;


public class MySqlSelectTest_233 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select f1 \n" +
                "from t1\n" +
                "union all\n" +
                "select f2\n" +
                "from t2";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleMysqlStatement(sql);

        SQLUnionQuery query = (SQLUnionQuery) stmt.getSelect().getQuery();
        SQLSelectQueryBlock left = (SQLSelectQueryBlock) query.getLeft();
        left.addOrderBy(new SQLIdentifierExpr("f1"));

        SQLSelectQueryBlock right = (SQLSelectQueryBlock) query.getRight();
        right.addOrderBy(new SQLIdentifierExpr("f1"));

        assertEquals("SELECT f1\n" +
                "FROM t1\n" +
                "ORDER BY f1\n" +
                "UNION ALL\n" +
                "(SELECT f2\n" +
                "FROM t2\n" +
                "ORDER BY f1)", stmt.toString());
    }



}