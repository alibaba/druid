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
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import junit.framework.TestCase;

/**
 * @author lizongbo
 */
public class MySqlOrderingExprTest extends TestCase {

    public void test_order_column_0() throws Exception {
        String sql = "SELECT task_id, task_name FROM table_task WHERE task_count > 20  AND (task_date = '20230306'   OR task_date = '20230307') GROUP BY task_id ASC";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        SQLSelectStatement sss = (SQLSelectStatement) stmt;
        MySqlSelectQueryBlock sqb = (MySqlSelectQueryBlock) sss.getSelect().getQueryBlock();
        SQLSelectGroupByClause so = sqb.getGroupBy();
        String groupByColumnName = so.getItems().get(0).toString();
        System.out.println(groupByColumnName);
        assertEquals("task_id ASC", groupByColumnName);
    }

}
