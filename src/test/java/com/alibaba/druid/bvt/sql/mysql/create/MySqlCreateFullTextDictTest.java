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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MySqlCreateFullTextDictTest extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "create fulltext dictionary test1 ("
                     + " word varchar comment 'comment1' "
                     + ") comment 'comment1'";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE FULLTEXT DICTIONARY test1(\n"
                            + "word varchar COMMENT 'comment1'\n"
                            + ") COMMENT 'comment1'", output);
    }

    @Test public void test_1() throws Exception {
        String sql = "show fulltext dictionaries";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SHOW FULLTEXT DICTIONARIES", output);
    }

    @Test public void test_2() throws Exception {
        String sql = "show fulltext dictionaries";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SHOW FULLTEXT DICTIONARIES", output);
    }

    @Test public void test_3() throws Exception {
        String sql = "drop fulltext dictionary dic_name";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP FULLTEXT DICTIONARY dic_name", output);
    }
}
