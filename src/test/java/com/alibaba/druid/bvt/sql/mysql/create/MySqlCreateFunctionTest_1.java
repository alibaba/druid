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

import java.util.List;

public class MySqlCreateFunctionTest_1 extends MysqlTest {

    public void test_0() throws Exception {
    	String sql = "sql : \n" +
                "     create function `test1`.`proc1`(`a` enum('1','2') charset utf8)\n" +
                "               returns int(10)\n" +
                "               DETERMINISTIC \n" +
                "     BEGIN\n" +
                "              return 0;\n" +
                "     END ";

    	List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
    	SQLStatement stmt = statementList.get(0);
//    	print(statementList);
        assertEquals(1, statementList.size());

        System.out.println(SQLUtils.toMySqlString(stmt));

        assertEquals("CREATE FUNCTION `test1`.`proc1` (\n" +
                "\t`a` enum('1', '2') CHARACTER SET utf8\n" +
                ")\n" +
                "RETURNS int(10) DETERMINISTIC\n" +
                "BEGIN\n" +
                "\tRETURN 0;\n" +
                "END", SQLUtils.toMySqlString(stmt));

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

    }

    
}
