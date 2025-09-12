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
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class DB2CreateTableTest_08 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE test.EAST_JRG_0911 \n" +
                "AS ( SELECT * FROM test.EAST_JRGJXXB a WHERE 1=2 ) \n" +
                "WITH NO DATA";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        System.out.println(SQLUtils.toDB2String(stmt));

        assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(2, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("test.EAST_JRG_0911"));

        assertEquals("CREATE TABLE test.EAST_JRG_0911\n" +
                        "AS\n" +
                        "(SELECT *\n" +
                        "FROM test.EAST_JRGJXXB a\n" +
                        "WHERE 1 = 2)\n" +
                        "WITH NO DATA",
                SQLUtils.toSQLString(stmt, JdbcConstants.DB2));

        assertEquals("create table test.EAST_JRG_0911\n" +
                        "as\n" +
                        "(select *\n" +
                        "from test.EAST_JRGJXXB a\n" +
                        "where 1 = 2)\n" +
                        "with no data",
                SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
