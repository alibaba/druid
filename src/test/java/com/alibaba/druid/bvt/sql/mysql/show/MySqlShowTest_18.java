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
package com.alibaba.druid.bvt.sql.mysql.show;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowHMSMetaStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.List;

public class MySqlShowTest_18 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SHOW HMSMETA s1.table1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        MySqlShowHMSMetaStatement stmt = (MySqlShowHMSMetaStatement)stmtList.get(0);
        
        String result = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SHOW HMSMETA s1.table1", result);
        assertEquals("s1", stmt.getSchema());
        assertEquals("table1", stmt.getTableName());
    }
    public void test_1() throws Exception {
        String sql = "SHOW HMSMETA table1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        MySqlShowHMSMetaStatement stmt = (MySqlShowHMSMetaStatement)stmtList.get(0);

        String result = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SHOW HMSMETA table1", result);
        assertEquals(null, stmt.getSchema());
        assertEquals("table1", stmt.getTableName());
    }
}
