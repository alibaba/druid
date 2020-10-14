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
package com.alibaba.druid.bvt.sql.mysql.alter;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class MySqlAlterUserTest extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "ALTER USER 'jeffrey'@'localhost' PASSWORD EXPIRE;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER USER 'jeffrey'@'localhost' PASSWORD EXPIRE;", //
                            output);

        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }

    public void test_1() throws Exception {
        String sql = "alter user IF EXISTS user1 IDENTIFIED BY 'auth_string'";

        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("ALTER USER IF EXISTS user1 IDENTIFIED BY 'auth_string'", statement.toString());
    }

    public void test_2() throws Exception {
        String sql = "alter user IF EXISTS user1 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE";

        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("ALTER USER IF EXISTS user1 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE", statement.toString());
    }

    public void test_3() throws Exception {
        String sql = "alter user IF EXISTS user1 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE DEFAULT";

        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("ALTER USER IF EXISTS user1 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE DEFAULT", statement.toString());
    }

    public void test_4() throws Exception {
        String sql = "alter user IF EXISTS user1 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE NEVER";

        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("ALTER USER IF EXISTS user1 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE NEVER", statement.toString());
    }

    public void test_5() throws Exception {
        String sql = "alter user IF EXISTS user1 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE INTERVAL 5 DAY";

        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("ALTER USER IF EXISTS user1 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE INTERVAL 5 DAY", statement.toString());
    }

    public void test_6() throws Exception {
        String sql = "alter user IF EXISTS user1 IDENTIFIED BY 'auth_string', user2 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE INTERVAL 5 DAY";

        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("ALTER USER IF EXISTS user1 IDENTIFIED BY 'auth_string', user2 IDENTIFIED BY 'auth_string' PASSWORD EXPIRE INTERVAL 5 DAY", statement.toString());
    }
}
