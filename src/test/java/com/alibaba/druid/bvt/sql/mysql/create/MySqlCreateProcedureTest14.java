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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class MySqlCreateProcedureTest14 extends MysqlTest {

    public void test_0() throws Exception {
    	String sql = "CREATE DEFINER = 'admin'@'localhost' PROCEDURE account_count()\n" +
                "SQL SECURITY INVOKER\n" +
                "BEGIN\n" +
                "  SELECT 'Number of accounts:', COUNT(*) FROM mysql.user;\n" +
                "END;";

    	List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
    	SQLStatement stmt = statementList.get(0);
//    	print(statementList);
        assertEquals(1, statementList.size());

        System.out.println(SQLUtils.toMySqlString(stmt));

        assertEquals("CREATE PROCEDURE account_count ()\n" +
                "SQL SECURITY INVOKER\n" +
                "BEGIN\n" +
                "\tSELECT 'Number of accounts:', COUNT(*)\n" +
                "\tFROM mysql.user;\n" +
                "END;", SQLUtils.toMySqlString(stmt));

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(1, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsColumn("mysql.user", "*"));
    }

    
}
