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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_54 extends MysqlTest {

    public void test_0() throws Exception {
        String sql =  "SELECT t.*,a.param_id FROM lhwtag AS t RIGHT JOIN lhwtag_relation AS a USING(`tag_id`) WHERE a.`type_id`=1 AND a.`param_id` IN ('0', '1', '2', '3', '4', '5', '6', '7', '8', '9') ORDER BY t.`content_count`";

        System.out.println(sql);


        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
//        assertEquals(1, visitor.getTables().size());
//        assertEquals(1, visitor.getColumns().size());
//        assertEquals(0, visitor.getConditions().size());
//        assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT t.*, a.param_id\n" +
                            "FROM lhwtag t\n" +
                            "\tRIGHT JOIN lhwtag_relation a USING (`tag_id`)\n" +
                            "WHERE a.`type_id` = 1\n" +
                            "\tAND a.`param_id` IN (\n" +
                            "\t\t'0', \n" +
                            "\t\t'1', \n" +
                            "\t\t'2', \n" +
                            "\t\t'3', \n" +
                            "\t\t'4', \n" +
                            "\t\t'5', \n" +
                            "\t\t'6', \n" +
                            "\t\t'7', \n" +
                            "\t\t'8', \n" +
                            "\t\t'9'\n" +
                            "\t)\n" +
                            "ORDER BY t.`content_count`", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select t.*, a.param_id\n" +
                            "from lhwtag t\n" +
                            "\tright join lhwtag_relation a using (`tag_id`)\n" +
                            "where a.`type_id` = 1\n" +
                            "\tand a.`param_id` in (\n" +
                            "\t\t'0', \n" +
                            "\t\t'1', \n" +
                            "\t\t'2', \n" +
                            "\t\t'3', \n" +
                            "\t\t'4', \n" +
                            "\t\t'5', \n" +
                            "\t\t'6', \n" +
                            "\t\t'7', \n" +
                            "\t\t'8', \n" +
                            "\t\t'9'\n" +
                            "\t)\n" +
                            "order by t.`content_count`", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT t.*, a.param_id\n" +
                            "FROM lhwtag t\n" +
                            "\tRIGHT JOIN lhwtag_relation a USING (`tag_id`)\n" +
                            "WHERE a.`type_id` = ?\n" +
                            "\tAND a.`param_id` IN (?)\n" +
                            "ORDER BY t.`content_count`", //
                    output);
        }
    }
}
