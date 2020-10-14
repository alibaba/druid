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

public class MySqlCreateFullTextAnalyzerTest extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "create fulltext ANALYZER test1 ("
                     + "'tokenizer' = 'dictionary-name',"
                     + "\"charfilter\" = ['name1','name2'],"
                     + "'tokenfilter'=['name1', 'name2']"
                     + ")";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE FULLTEXT ANALYZER test1(\n"
                            + "\"TOKENIZER\" = dictionary-name,\n"
                            + "\"CHARFILTER\" = [\"name1\", \"name2\"],\n"
                            + "\"TOKENFILTER\" = [\"name1\", \"name2\"]\n" + ")", output);
    }

    @Test
    public void test_create() throws Exception {
        String sql = "create fulltext ANALYZER test1 ("
                     + "\"tokenizer\" = \"dictionary-name\","
                     + "\"charfilter\" = [\"name1\",\"name2\"],"
                     + "\"tokenfilter\"=[\"name1\",\"name2\"]"
                     + ")";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE FULLTEXT ANALYZER test1(\n"
                            + "\"TOKENIZER\" = dictionary-name,\n"
                            + "\"CHARFILTER\" = [\"name1\", \"name2\"],\n"
                            + "\"TOKENFILTER\" = [\"name1\", \"name2\"]\n"
                            + ")", output);
    }

    @Test
    public void test_create2() throws Exception {
        String sql = "create fulltext analyzer test_analyzer ( \"charfilter\"=[\"test_stconv\"],   \"tokenizer\"= \"test_alinlp\", \"tokenfilter\"=[\"test_pinyin\"] );";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE FULLTEXT ANALYZER test_analyzer(\n"
                            + "\"TOKENIZER\" = test_alinlp,\n"
                            + "\"CHARFILTER\" = [\"test_stconv\"],\n"
                            + "\"TOKENFILTER\" = [\"test_pinyin\"]\n"
                            + ");", output);
    }

    @Test
    public void test_1() throws Exception {
        String sql = "show fulltext analyzers";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SHOW FULLTEXT ANALYZERS", output);
    }

    @Test
    public void test_2() throws Exception {
        String sql = "show create fulltext analyzer test1";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SHOW CREATE FULLTEXT ANALYZER test1", output);
    }

    @Test
    public void test_3() throws Exception {
        String sql = "alter fulltext analyzer test1 set k = 'a';";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER FULLTEXT ANALYZER test1 SET k = 'a';", output);
    }

    @Test
    public void test_drop() throws Exception {
        String sql = "drop fulltext analyzer test1";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP FULLTEXT ANALYZER test1", output);
    }
}
