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
package com.alibaba.druid.bvt.sql.db2;

import com.alibaba.druid.sql.DB2Test;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class DB2SelectTest_31 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "select rtrim(current_server), rtrim(current schema), rtrim(current_user) from sysibm.sysdummy1";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT rtrim(current_server), rtrim(CURRENT SCHEMA)\n" +
                "\t, rtrim(current_user)\n" +
                "FROM sysibm.sysdummy1", stmt.getSelect().toString());

        assertEquals("sysibm.sysdummy1", stmt.getSelect().getQueryBlock().getFrom().toString());

        assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(2, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("sysibm.sysdummy1")));

//         assertTrue(visitor.getColumns().contains(new Column("DSN8B10.EMP", "WORKDEPT")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        assertEquals("SELECT rtrim(current_server), rtrim(CURRENT SCHEMA)\n" +
                        "\t, rtrim(current_user)\n" +
                        "FROM sysibm.sysdummy1", //
                SQLUtils.toSQLString(stmt, JdbcConstants.DB2));

        assertEquals("select rtrim(current_server), rtrim(CURRENT SCHEMA)\n" +
                        "\t, rtrim(current_user)\n" +
                        "from sysibm.sysdummy1", //
                SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
