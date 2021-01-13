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

public class MySqlCreateFullTextTokenFilterTest extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "create fulltext tokenfilter test1 ("
                     + "'type' = 'typename',"
                     + "'key'='name'"
                     + ")";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE FULLTEXT TOKENFILTER test1(\n"
                            + "\"type\" = 'typename',\n"
                            + "'key' = 'name'\n"
                            + ")", output);
    }

    @Test
    public void test_1() throws Exception {
        String sql = "show fulltext tokenfilters";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SHOW FULLTEXT TOKENFILTERS", output);
    }

    @Test
    public void test_2() throws Exception {
        String sql = "show create fulltext tokenfilter test1";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SHOW CREATE FULLTEXT TOKENFILTER test1", output);
    }

    @Test
    public void test_3() throws Exception {
        String sql = "alter fulltext tokenfilter test1 set k = 'a';";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER FULLTEXT TOKENFILTER test1 SET k = 'a';", output);
    }

    @Test
    public void test_drop() throws Exception {
        String sql = "drop fulltext tokenfilter test1";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP FULLTEXT TOKENFILTER test1", output);
    }
}
