/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.util.JdbcUtils;

public class OracleSQLParserResourceTest extends TestCase {

    public void test_0() throws Exception {
        // for (int i = 0; i <= 53; ++i) {
        // String resource = "bvt/parser/oracle-" + i + ".txt";
        // exec_test(resource);
        // }
        exec_test("bvt/parser/oracle-55.txt");
    }

    public void exec_test(String resource) throws Exception {
        System.out.println(resource);
        InputStream is = null;

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        Reader reader = new InputStreamReader(is, "UTF-8");
        String input = Utils.read(reader);
        JdbcUtils.close(reader);
        String[] items = input.split("---------------------------");
        String sql = items[0].trim();
        String expect = items[1].trim();

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        Assert.assertEquals(1, statementList.size());

        String text = TestUtils.outputOracle(statementList);
        expect = expect.replaceAll("\\r\\n", "\n");
        System.out.println(text);
        Assert.assertEquals(expect, text.trim());

    }
}
