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
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_47_group_concat extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT student_name,\n" +
                "    GROUP_CONCAT(DISTINCT test_score\n" +
                "            ORDER BY test_score DESC SEPARATOR ' ')\n" +
                "    FROM student\n" +
                "    GROUP BY student_name;";


        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
//        Assert.assertEquals(1, visitor.getTables().size());
//        Assert.assertEquals(1, visitor.getColumns().size());
//        Assert.assertEquals(0, visitor.getConditions().size());
//        Assert.assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT student_name, GROUP_CONCAT(DISTINCT test_score ORDER BY test_score DESC SEPARATOR ' ')\n" +
                            "FROM student\n" +
                            "GROUP BY student_name;", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("select student_name, group_concat(DISTINCT test_score order by test_score desc separator ' ')\n" +
                            "from student\n" +
                            "group by student_name;", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            Assert.assertEquals("SELECT student_name, GROUP_CONCAT(DISTINCT test_score ORDER BY test_score DESC SEPARATOR ' ')\n" +
                            "FROM student\n" +
                            "GROUP BY student_name;", //
                    output);
        }
    }
}
