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
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_52_comment extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT comment.id,user.id AS user_id,user.username,user.avatar,comment.created_at,\n" +
                "                getDistance('31.860968', '117.252579', user.latitude, user.longitude) AS distance,\n" +
                "                1 AS type\n" +
                "                FROM user,comment\n" +
                "                WHERE user.id=comment.user_id\n" +
                "                AND getDistance('31.860968', '117.252579', user.latitude, user.longitude)>=0\n" +
                "                AND getDistance('31.860968', '117.252579', user.latitude, user.longitude)<=10000\n" +
                "                UNION\n" +
                "                SELECT user_book_list.id,user.id AS user_id,user.username,user.avatar,user_book_list.created_at,\n" +
                "                getDistance('31.860968', '117.252579', user.latitude, user.longitude) AS distance,\n" +
                "                2 AS type\n" +
                "                FROM user_book_list,user\n" +
                "                WHERE user.id=user_book_list.user_id\n" +
                "                AND getDistance('31.860968', '117.252579', user.latitude, user.longitude)>=0\n" +
                "                AND getDistance('31.860968', '117.252579', user.latitude, user.longitude)<=10000\n" +
                "                ORDER BY created_at DESC\n" +
                "                LIMIT 10, 10";


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
            assertEquals("SELECT comment.id, user.id AS user_id, user.username, user.avatar, comment.created_at\n" +
                            "\t, getDistance('31.860968', '117.252579', user.latitude, user.longitude) AS distance\n" +
                            "\t, 1 AS type\n" +
                            "FROM user, comment\n" +
                            "WHERE user.id = comment.user_id\n" +
                            "\tAND getDistance('31.860968', '117.252579', user.latitude, user.longitude) >= 0\n" +
                            "\tAND getDistance('31.860968', '117.252579', user.latitude, user.longitude) <= 10000\n" +
                            "UNION\n" +
                            "SELECT user_book_list.id, user.id AS user_id, user.username, user.avatar, user_book_list.created_at\n" +
                            "\t, getDistance('31.860968', '117.252579', user.latitude, user.longitude) AS distance\n" +
                            "\t, 2 AS type\n" +
                            "FROM user_book_list, user\n" +
                            "WHERE user.id = user_book_list.user_id\n" +
                            "\tAND getDistance('31.860968', '117.252579', user.latitude, user.longitude) >= 0\n" +
                            "\tAND getDistance('31.860968', '117.252579', user.latitude, user.longitude) <= 10000\n" +
                            "ORDER BY created_at DESC\n" +
                            "LIMIT 10, 10", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select comment.id, user.id as user_id, user.username, user.avatar, comment.created_at\n" +
                            "\t, getDistance('31.860968', '117.252579', user.latitude, user.longitude) as distance\n" +
                            "\t, 1 as type\n" +
                            "from user, comment\n" +
                            "where user.id = comment.user_id\n" +
                            "\tand getDistance('31.860968', '117.252579', user.latitude, user.longitude) >= 0\n" +
                            "\tand getDistance('31.860968', '117.252579', user.latitude, user.longitude) <= 10000\n" +
                            "union\n" +
                            "select user_book_list.id, user.id as user_id, user.username, user.avatar, user_book_list.created_at\n" +
                            "\t, getDistance('31.860968', '117.252579', user.latitude, user.longitude) as distance\n" +
                            "\t, 2 as type\n" +
                            "from user_book_list, user\n" +
                            "where user.id = user_book_list.user_id\n" +
                            "\tand getDistance('31.860968', '117.252579', user.latitude, user.longitude) >= 0\n" +
                            "\tand getDistance('31.860968', '117.252579', user.latitude, user.longitude) <= 10000\n" +
                            "order by created_at desc\n" +
                            "limit 10, 10", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT comment.id, user.id AS user_id, user.username, user.avatar, comment.created_at\n" +
                            "\t, getDistance(?, ?, user.latitude, user.longitude) AS distance\n" +
                            "\t, ? AS type\n" +
                            "FROM user, comment\n" +
                            "WHERE user.id = comment.user_id\n" +
                            "\tAND getDistance(?, ?, user.latitude, user.longitude) >= ?\n" +
                            "\tAND getDistance(?, ?, user.latitude, user.longitude) <= ?\n" +
                            "UNION\n" +
                            "SELECT user_book_list.id, user.id AS user_id, user.username, user.avatar, user_book_list.created_at\n" +
                            "\t, getDistance(?, ?, user.latitude, user.longitude) AS distance\n" +
                            "\t, ? AS type\n" +
                            "FROM user_book_list, user\n" +
                            "WHERE user.id = user_book_list.user_id\n" +
                            "\tAND getDistance(?, ?, user.latitude, user.longitude) >= ?\n" +
                            "\tAND getDistance(?, ?, user.latitude, user.longitude) <= ?\n" +
                            "ORDER BY created_at DESC\n" +
                            "LIMIT ?, ?", //
                    output);
        }
    }
}
