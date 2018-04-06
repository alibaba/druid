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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class MySqlSelectTest_114 extends TestCase {

    public void test_0() throws Exception {
        String sql = "select count(0) from (select id from auth WHERE 1=1 AND/**/b=2 ORDER BY create_time DESC) as total";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        assertEquals(1, statementList.size());
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
//        print(statementList);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
        assertEquals(1, visitor.getOrderByColumns().size());

        assertEquals("SELECT COUNT(0)\n" +
                "FROM (\n" +
                "\tSELECT id\n" +
                "\tFROM auth\n" +
                "\tWHERE 1 = 1\n" +
                "\t\tAND /**/\n" +
                "\t\tb = 2\n" +
                "\tORDER BY create_time DESC\n" +
                ") total", stmt.toString());

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL);
        assertEquals("SELECT COUNT(0)\n" +
                "FROM (\n" +
                "\tSELECT id\n" +
                "\tFROM auth\n" +
                "\tWHERE 1 = 1\n" +
                "\t\tAND \n" +
                "\t\tb = ?\n" +
                "\tORDER BY create_time DESC\n" +
                ") total", psql);
    }
    
    
    
}
