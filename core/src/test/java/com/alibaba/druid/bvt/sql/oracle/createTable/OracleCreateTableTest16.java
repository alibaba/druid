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

public class OracleCreateTableTest16 extends OracleTest {
    public void test_types() throws Exception {
        String sql = //
                "create table T (" + //
                        "F1 INTERVAL YEAR TO MONTH," + //
                        "F2 INTERVAL YEAR (3) TO MONTH," + //
                        "F3 INTERVAL DAY TO SECOND," + //
                        "F4 INTERVAL DAY (3) TO SECOND," + //
                        "F5 INTERVAL DAY TO SECOND (9)," + //
                        "F6 INTERVAL DAY (3) TO SECOND (9)" + //
                        ") ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE T (" + //
                "\n\tF1 INTERVAL YEAR TO MONTH," + //
                "\n\tF2 INTERVAL YEAR(3) TO MONTH," + //
                "\n\tF3 INTERVAL DAY TO SECOND," + //
                "\n\tF4 INTERVAL DAY(3) TO SECOND," + //
                "\n\tF5 INTERVAL DAY TO SECOND(9)," + //
                "\n\tF6 INTERVAL DAY(3) TO SECOND(9)" + //
                "\n)", SQLUtils.toSQLString(statement, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertEquals(6, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F1")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F2")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F3")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F4")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F5")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("T", "F6")));
    }
}
