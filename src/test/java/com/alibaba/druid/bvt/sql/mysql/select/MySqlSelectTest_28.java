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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlSelectTest_28 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/* 0a7d0d8614637128401131809d4d9d/9// */"
                + "SELECT id, name "
                + "FROM `t_0248` AS `i_trash` "
                + "WHERE `gmt_create` < DATE_ADD(NOW(), INTERVAL (- 7) DAY) "
                + "LIMIT 0, 1000";

        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("/* 0a7d0d8614637128401131809d4d9d/9// */\n" +
                            "SELECT id, name\n" +
                            "FROM `t_0248` `i_trash`\n" +
                            "WHERE `gmt_create` < DATE_ADD(NOW(), INTERVAL -7 DAY)\n" +
                            "LIMIT 0, 1000", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("/* 0a7d0d8614637128401131809d4d9d/9// */\n" +
                            "select id, name\n" +
                            "from `t_0248` `i_trash`\n" +
                            "where `gmt_create` < DATE_ADD(NOW(), interval -7 day)\n" +
                            "limit 0, 1000", //
                                output);
        }
    }
    
    
    
}
