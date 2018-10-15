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

public class MySqlCreateProcedureTest15 extends MysqlTest {

    public void test_0() throws Exception {
    	String sql = "CREATE DEFINER=`root`@`%` PROCEDURE `load_part_tab`()\n" +
                "BEGIN \n" +
                "DECLARE v INT DEFAULT 0; \n" +
                "WHILE v < 1 DO \n" +
                "INSERT INTO part_tab \n" +
                "VALUES (v,'testing partitions',ADDDATE('1995-01-01',(RAND(v)*36520) MOD 3652)); \n" +
                "SET v = v + 1; \n" +
                "END WHILE; \n" +
                "END";

    	List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
    	SQLStatement stmt = statementList.get(0);
//    	print(statementList);
        assertEquals(1, statementList.size());

        System.out.println(SQLUtils.toMySqlString(stmt));

        assertEquals("CREATE PROCEDURE `load_part_tab` ()\n" +
                "BEGIN\n" +
                "\tDECLARE v INT DEFAULT 0;\n" +
                "\tWHILE v < 1 DO\n" +
                "\tINSERT INTO part_tab\n" +
                "\tVALUES (v, 'testing partitions', ADDDATE('1995-01-01', RAND(v) * 36520 % 3652));\n" +
                "\tSET v = v + 1;\n" +
                "\tEND WHILE\n" +
                "END", SQLUtils.toMySqlString(stmt));

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("part_tab"));
    }

    
}
