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
package com.alibaba.druid.bvt.sql.mysql.insert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class MySqlInsertTest_27_str_concat extends MysqlTest {

    public void test_insert_concat() throws Exception {
        String sql = "insert ignore into ktv_sms_test (cp) values ('a' 'b')";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;

        assertEquals("INSERT IGNORE INTO ktv_sms_test (cp)\n" +
                "VALUES ('ab')", SQLUtils.toMySqlString(insertStmt));

        System.out.println(sql);
        System.out.println(stmt.toString());

        {
            List<Object> outParameters = new ArrayList<Object>();
            String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters);
            assertEquals("INSERT IGNORE INTO ktv_sms_test(cp)\n" +
                    "VALUES (?)", psql);

            assertEquals(1, outParameters.size());

            String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, outParameters);
            assertEquals("INSERT IGNORE INTO ktv_sms_test (cp)\n" +
                    "VALUES ('ab')", rsql);
        }
    }

    public void test_insert_concat_2() throws Exception {
        String sql = "insert ignore into ktv_sms_test (cp) values (\"a\" \"b\")";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;

        assertEquals("INSERT IGNORE INTO ktv_sms_test (cp)\n" +
                "VALUES ('ab')", SQLUtils.toMySqlString(insertStmt));

        System.out.println(sql);
        System.out.println(stmt.toString());

        {
            List<Object> outParameters = new ArrayList<Object>();
            String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters);
            assertEquals("INSERT IGNORE INTO ktv_sms_test(cp)\n" +
                    "VALUES (?)", psql);

            assertEquals(1, outParameters.size());

            String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, outParameters);
            assertEquals("INSERT IGNORE INTO ktv_sms_test (cp)\n" +
                    "VALUES ('ab')", rsql);
        }
    }

    public void test_insert_concat_3() throws Exception {
        String sql = "insert ignore into ktv_sms_test (cp) values (\"a\" 'b')";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;

        assertEquals("INSERT IGNORE INTO ktv_sms_test (cp)\n" +
                "VALUES ('ab')", SQLUtils.toMySqlString(insertStmt));

        System.out.println(sql);
        System.out.println(stmt.toString());

        {
            List<Object> outParameters = new ArrayList<Object>();
            String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters);
            assertEquals("INSERT IGNORE INTO ktv_sms_test(cp)\n" +
                    "VALUES (?)", psql);

            assertEquals(1, outParameters.size());

            String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, outParameters);
            assertEquals("INSERT IGNORE INTO ktv_sms_test (cp)\n" +
                    "VALUES ('ab')", rsql);
        }
    }
}
