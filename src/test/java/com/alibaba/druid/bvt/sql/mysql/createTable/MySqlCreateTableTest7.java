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
package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

import java.util.List;

public class MySqlCreateTableTest7 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE Orders\n" + //
                     "(\n" + //
                     "O_Id int NOT NULL,\n" + //
                     "OrderNo int NOT NULL,\n" + //
                     "Id_P int,\n" + //
                     "PRIMARY KEY (O_Id),\n" + //
                     "FOREIGN KEY (Id_P) REFERENCES Persons(Id_P)\n" + //
                     ")";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) statementList.get(0);
//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("Orders")));

        assertTrue(visitor.containsColumn("Orders", "O_Id"));
        assertTrue(visitor.containsColumn("Orders", "OrderNo"));
        assertTrue(visitor.containsColumn("Orders", "Id_P"));

        SQLColumnDefinition column = stmt.findColumn("O_id");
        assertNotNull(column);
        assertEquals(1, column.getConstraints().size());
        assertTrue(column.isPrimaryKey());
    }
}
