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
package com.alibaba.druid.bvt.sql.mysql.update;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.wall.WallUtils;

import java.util.List;

public class MySqlUpdateTest_17 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "update security_group_ip_count set ip_count=GREATEST(ip_count-?, 0), gmt_modified=now() where group_id=? ";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        // assertEquals(2, visitor.getConditions().size());

        assertTrue(visitor.containsTable("security_group_ip_count"));

        assertTrue(visitor.getColumns().contains(new Column("security_group_ip_count", "ip_count")));

        assertEquals("UPDATE security_group_ip_count\n" +
                        "SET ip_count = GREATEST(ip_count - ?, 0), gmt_modified = now()\n" +
                        "WHERE group_id = ?", //
                stmt.toString());
        assertEquals("update security_group_ip_count\n" +
                        "set ip_count = GREATEST(ip_count - ?, 0), gmt_modified = now()\n" +
                        "where group_id = ?", //
                stmt.toLowerCaseString());


        assertTrue(WallUtils.isValidateMySql(sql));

        {
            SQLUpdateStatement update = (SQLUpdateStatement) stmt;
            SQLExpr where = update.getWhere();
            assertEquals("group_id = ?", where.toString());
        }

    }
}
