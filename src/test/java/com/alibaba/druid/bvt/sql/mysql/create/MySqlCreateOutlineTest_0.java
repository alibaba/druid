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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import junit.framework.TestCase;

import java.util.List;

public class MySqlCreateOutlineTest_0 extends TestCase {

    public void test_0() throws Exception {
        String sql = "create outline t2 on select ? to select /*+TDDL:slave()*/ * from ms10 where c1=?;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals("CREATE OUTLINE t2 ON SELECT ? TO SELECT /*+TDDL:slave()*/ *\n" +
                        "FROM ms10\n" +
                        "WHERE c1 = ?;", //
                SQLUtils.toMySqlString(stmt));

        assertEquals("create outline t2 on select ? to select /*+TDDL:slave()*/ *\n" +
                        "from ms10\n" +
                        "where c1 = ?;", //
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(2, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("ms10")));

        assertTrue(visitor.getColumns().contains(new Column("ms10", "c1")));
        assertTrue(visitor.getColumns().contains(new Column("ms10", "*")));
//        assertTrue(visitor.getColumns().contains(new Column("t2", "*")));
//        assertTrue(visitor.getColumns().contains(new Column("t2", "l_suppkey")));
    }
}
