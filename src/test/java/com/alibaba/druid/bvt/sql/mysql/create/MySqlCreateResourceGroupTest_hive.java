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
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MySqlCreateResourceGroupTest_hive
        extends MysqlTest {

    @Test
    public void test_create() throws Exception {
        String sql = "CREATE RESOURCE GROUP sql_thread TYPE = USER VCPU = 1,3 THREAD_PRIORITY = -20";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE RESOURCE GROUP sql_thread THREAD_PRIORITY = -20 VCPU = 1,3 TYPE = USER", output);
    }

    @Test
    public void test_alter() throws Exception {
        String sql = "ALTER RESOURCE GROUP sql_thread TYPE = USER VCPU = 1,3 THREAD_PRIORITY = -20";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER RESOURCE GROUP sql_thread THREAD_PRIORITY = -20 VCPU = 1,3 TYPE = USER", output);
    }

    @Test
    public void test_drop() throws Exception {
        String sql = "DROP RESOURCE GROUP sql_thread;";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP RESOURCE GROUP sql_thread;", output);
    }

}
