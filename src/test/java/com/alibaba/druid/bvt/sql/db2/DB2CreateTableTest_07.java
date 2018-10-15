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

public class DB2CreateTableTest_07 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE EMPLOYEE\n" +
                "  (EMPNO      INTEGER GENERATED ALWAYS AS IDENTITY\n" +
                "              PRIMARY KEY NOT NULL,\n" +
                "   NAME       CHAR(30) NOT NULL,\n" +
                "   SALARY     DECIMAL(7,2) NOT NULL,\n" +
                "   WORKDEPT   SMALLINT);";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        System.out.println(SQLUtils.toDB2String(stmt));

        assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("EMPLOYEE"));

//         assertTrue(visitor.getColumns().contains(new Column("DSN8B10.EMP", "WORKDEPT")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        assertEquals("CREATE TABLE EMPLOYEE (\n" +
                        "\tEMPNO INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY,\n" +
                        "\tNAME CHAR(30) NOT NULL,\n" +
                        "\tSALARY DECIMAL(7, 2) NOT NULL,\n" +
                        "\tWORKDEPT SMALLINT\n" +
                        ");", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        
        assertEquals("create table EMPLOYEE (\n" +
                        "\tEMPNO INTEGER primary key not null generated always as identity,\n" +
                        "\tNAME CHAR(30) not null,\n" +
                        "\tSALARY DECIMAL(7, 2) not null,\n" +
                        "\tWORKDEPT SMALLINT\n" +
                        ");", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
