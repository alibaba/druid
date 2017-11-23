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
package com.alibaba.druid.bvt.sql.sqlserver;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.util.JdbcUtils;

public class SQLServerCommitTest extends TestCase {

    public void test_0() {
        String sql = "COMMIT WORK";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        Assert.assertEquals(1, stmtList.size());

        String text = SQLUtils.toSQLString(stmtList, JdbcUtils.SQL_SERVER);

        Assert.assertEquals("COMMIT WORK", text);
    }

    public void test_1() {
        String sql = "COMMIT TRAN";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        Assert.assertEquals(1, stmtList.size());

        String text = SQLUtils.toSQLString(stmtList, JdbcUtils.SQL_SERVER);

        Assert.assertEquals("COMMIT TRANSACTION", text);
    }

    public void test_2() {
        String sql = "COMMIT TRANSACTION @tran_name_variable  WITH ( DELAYED_DURABILITY = OFF )";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        Assert.assertEquals(1, stmtList.size());

        String text = SQLUtils.toSQLString(stmtList, JdbcUtils.SQL_SERVER);

        Assert.assertEquals("COMMIT TRANSACTION @tran_name_variable WITH ( DELAYED_DURABILITY = OFF )", text);
    }

}
