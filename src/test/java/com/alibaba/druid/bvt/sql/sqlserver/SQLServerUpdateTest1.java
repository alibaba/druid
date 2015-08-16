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
package com.alibaba.druid.bvt.sql.sqlserver;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerUpdateTest1 extends TestCase {

    public void test_update() throws Exception {
        String sql = "UPDATE dbo.Table2 " + //
                     "SET dbo.Table2.ColB = dbo.Table2.ColB + dbo.Table1.ColB " + //
                     "FROM dbo.Table2 " + //
                     "    INNER JOIN dbo.Table1 " + //
                     "    ON (dbo.Table2.ColA = dbo.Table1.ColA);";

        String expect = "UPDATE dbo.Table2" + //
                        "\nSET dbo.Table2.ColB = dbo.Table2.ColB + dbo.Table1.ColB" + //
                        "\nFROM dbo.Table2" + //
                        "\n\tINNER JOIN dbo.Table1 ON dbo.Table2.ColA = dbo.Table1.ColA";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
