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

public class MySqlSelectTest_53_match extends MysqlTest {

    public void test_0() throws Exception {
        String sql =  "SELECT *, MATCH(question_content_fulltext) " + //
                "AGAINST('2015431867 2636826089 3807520117 2796321160 2615920174' IN BOOLEAN MODE) AS score " + //
                "FROM aws_question " + //
                "WHERE MATCH(question_content_fulltext) " + //
                "   AGAINST('2015431867 2636826089 3807520117 2796321160 2615920174' IN BOOLEAN MODE)  " + //
                "ORDER BY score DESC, agree_count DESC LIMIT 10";

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
            assertEquals("SELECT *, MATCH (question_content_fulltext) AGAINST ('2015431867 2636826089 3807520117 2796321160 2615920174' IN BOOLEAN MODE) AS score\n" +
                            "FROM aws_question\n" +
                            "WHERE MATCH (question_content_fulltext) AGAINST ('2015431867 2636826089 3807520117 2796321160 2615920174' IN BOOLEAN MODE)\n" +
                            "ORDER BY score DESC, agree_count DESC\n" +
                            "LIMIT 10", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select *, match (question_content_fulltext) against ('2015431867 2636826089 3807520117 2796321160 2615920174' in boolean mode) as score\n" +
                            "from aws_question\n" +
                            "where match (question_content_fulltext) against ('2015431867 2636826089 3807520117 2796321160 2615920174' in boolean mode)\n" +
                            "order by score desc, agree_count desc\n" +
                            "limit 10", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT *, MATCH (question_content_fulltext) AGAINST (? IN BOOLEAN MODE) AS score\n" +
                            "FROM aws_question\n" +
                            "WHERE MATCH (question_content_fulltext) AGAINST (? IN BOOLEAN MODE)\n" +
                            "ORDER BY score DESC, agree_count DESC\n" +
                            "LIMIT ?", //
                    output);
        }
    }
}
