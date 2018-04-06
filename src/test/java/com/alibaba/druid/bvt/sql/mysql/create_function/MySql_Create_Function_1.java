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
package com.alibaba.druid.bvt.sql.mysql.create_function;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySql_Create_Function_1 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE DEFINER=`root`@`%` FUNCTION `hello`(s CHAR(20)) RETURNS char(50) CHARSET big5\n" +
                "    DETERMINISTIC\n" +
                "RETURN CONCAT('Hello, ',s,'!')";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        
        assertEquals("CREATE FUNCTION `hello` (\n" +
                        "\ts CHAR(20)\n" +
                        ")\n" +
                        "RETURNS char(50) CHARACTER SET big5 DETERMINISTIC\n" +
                        "RETURN CONCAT('Hello, ', s, '!')", //
                            SQLUtils.toMySqlString(stmt));
        
        assertEquals("create function `hello` (\n" +
                        "\ts CHAR(20)\n" +
                        ")\n" +
                        "returns char(50) character set big5 deterministic\n" +
                        "return CONCAT('Hello, ', s, '!')", //
                            SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(0, visitor.getTables().size());
        assertEquals(1, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("City")));
//        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t2")));
        
//        Assert.assertTrue(visitor.getColumns().contains(new Column("t2", "id")));
    }
}
