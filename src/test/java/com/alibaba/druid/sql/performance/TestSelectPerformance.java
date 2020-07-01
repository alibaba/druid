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
package com.alibaba.druid.sql.performance;

import java.text.NumberFormat;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class TestSelectPerformance extends TestCase {

    private final int COUNT = 1000 * 1000;
    private String    sql   = "SELECT      distinct a.id \"id\",    a.col \"col\",     a.position \"position\",     a.panel_id \"panelId\"    FROM     (select * from view_position_info) a LEFT JOIN db1.view_portal b ON a.panel_id = b.panel_id       LEFT JOIN (select * from view_portal_panel) c  ON a.panel_id = c.panel_id    WHERE     b.user_id = ? and     ((b.is_grid='y' and c.param_name='is_hidden' and c.param_value='false') or      b.is_grid  != 'y') and b.user_id in (select user_id from table1 where id = 1)    ORDER BY    a.col ASC, a.position ASC";

    public void test_simple() throws Exception {
        for (int i = 0; i < 5; ++i) {
            f();
        }
    }

    private void f() throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; ++i) {
        	List<SQLStatement> statementList =  new SQLStatementParser(sql).parseStatementList();
            
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            statementList.get(0).accept(visitor);
            // stmtList.toString();
        }
        long time = System.currentTimeMillis() - start;
        System.out.println(NumberFormat.getInstance().format(time));
    }
}
