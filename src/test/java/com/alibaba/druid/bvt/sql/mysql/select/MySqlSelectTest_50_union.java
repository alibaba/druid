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

public class MySqlSelectTest_50_union extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from (\n" +
                "select seller_name from a where id < 100\n" +
                "UNION\n" +
                "select seller_name from a where id < 100\n" +
                ") as temp limit 10";


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
            assertEquals("SELECT *\n" +
                            "FROM (\n" +
                            "\tSELECT seller_name\n" +
                            "\tFROM a\n" +
                            "\tWHERE id < 100\n" +
                            "\tUNION\n" +
                            "\tSELECT seller_name\n" +
                            "\tFROM a\n" +
                            "\tWHERE id < 100\n" +
                            ") temp\n" +
                            "LIMIT 10", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select *\n" +
                            "from (\n" +
                            "\tselect seller_name\n" +
                            "\tfrom a\n" +
                            "\twhere id < 100\n" +
                            "\tunion\n" +
                            "\tselect seller_name\n" +
                            "\tfrom a\n" +
                            "\twhere id < 100\n" +
                            ") temp\n" +
                            "limit 10", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT *\n" +
                            "FROM (\n" +
                            "\tSELECT seller_name\n" +
                            "\tFROM a\n" +
                            "\tWHERE id < ?\n" +
                            "\tUNION\n" +
                            "\tSELECT seller_name\n" +
                            "\tFROM a\n" +
                            "\tWHERE id < ?\n" +
                            ") temp\n" +
                            "LIMIT ?", //
                    output);
        }
    }
}
