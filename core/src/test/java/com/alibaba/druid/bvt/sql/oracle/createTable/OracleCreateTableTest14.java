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
package com.alibaba.druid.bvt.sql.oracle.createTable;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import static org.junit.Assert.*;

import java.util.List;

public class OracleCreateTableTest14 extends OracleTest {
    public void test_charTypes() throws Exception {
        String sql = //
                "create table T (" + //
                        "F1 CHAR(1)," + //
                        "F2 CHAR(1 BYTE)," + //
                        "F3 CHAR(1 CHAR)," + //
                        "F4 VARCHAR2(2)," + //
                        "F5 VARCHAR2(2 BYTE)," + //
                        "F6 VARCHAR2(2 CHAR)," + //
                        "F7 NCHAR(3 BYTE), " + //
                        "F8 NCHAR(3 CHAR), " + //
                        "F9 NCHAR(3), " + //
                        "F10 NVARCHAR2(4), " + //
                        "F11 NVARCHAR2(4 BYTE), " + //
                        "F12 NVARCHAR2(4 CHAR) " + //
                        ") ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE T (" + //
                "\n\tF1 CHAR(1)," + //
                "\n\tF2 CHAR(1 BYTE)," + //
                "\n\tF3 CHAR(1 CHAR)," + //
                "\n\tF4 VARCHAR2(2)," + //
                "\n\tF5 VARCHAR2(2 BYTE)," + //
                "\n\tF6 VARCHAR2(2 CHAR)," + //
                "\n\tF7 NCHAR(3 BYTE)," + //
                "\n\tF8 NCHAR(3 CHAR)," + //
                "\n\tF9 NCHAR(3)," + //
                "\n\tF10 NVARCHAR2(4)," + //
                "\n\tF11 NVARCHAR2(4 BYTE)," + //
                "\n\tF12 NVARCHAR2(4 CHAR)" + //
                "\n)", SQLUtils.toSQLString(statement, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertEquals(12, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F1")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F2")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F3")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F4")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F5")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F6")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F7")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F8")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F9")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F10")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F11")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F12")));
    }
}
