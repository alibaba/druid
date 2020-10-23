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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class MySqlInsertTest_26_special extends MysqlTest {

    public void test_insert_timestamp() throws Exception {
        String sql = "INSERT INTO SUNTEST(ID,NAME) VALUES(1,'\\_ASDFASDF')";

        {
            List<Object> outParameters = new ArrayList<Object>();
            String psql = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.mysql, outParameters);
            assertEquals("INSERT INTO SUNTEST(ID, NAME)\n" +
                    "VALUES (?, ?)", psql);

            assertEquals(2, outParameters.size());

            String rsql = ParameterizedOutputVisitorUtils.restore(psql, DbType.mysql, outParameters);
            assertEquals("INSERT INTO SUNTEST (ID, NAME)\n" +
                    "VALUES (1, '\\_ASDFASDF')", rsql);
        }

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;

        assertEquals("INSERT INTO SUNTEST (ID, NAME)\n" +
                "VALUES (1, '\\_ASDFASDF')", SQLUtils.toMySqlString(insertStmt));


    }

    public void test_insert_special_1() throws Exception {
        String sql = "INSERT INTO SUNTEST(ID,NAME) VALUES(1, \"Y\\\"'\")";

        {
            List<Object> outParameters = new ArrayList<Object>();
            String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, outParameters);
            assertEquals("INSERT INTO SUNTEST(ID, NAME)\n" +
                    "VALUES (?, ?)", psql);

            assertEquals(2, outParameters.size());

            String rsql = ParameterizedOutputVisitorUtils.restore(psql, JdbcConstants.MYSQL, outParameters);
            assertEquals("INSERT INTO SUNTEST (ID, NAME)\n" +
                    "VALUES (1, 'Y\"''')", rsql);
        }

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;

        assertEquals("INSERT INTO SUNTEST (ID, NAME)\n" +
                "VALUES (1, 'Y\"''')", SQLUtils.toMySqlString(insertStmt));


    }

}
