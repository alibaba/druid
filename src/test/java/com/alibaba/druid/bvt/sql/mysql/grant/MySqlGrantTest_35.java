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
package com.alibaba.druid.bvt.sql.mysql.grant;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlGrantTest_35 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "GRANT CREATE ON . TO hello@'%'; ";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT CREATE ON * TO 'hello'@'%';", //
                            output);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_1() throws Exception {
        String sql = "GRANT ALTER ON . TO 'hello'@'%'; ";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT ALTER ON * TO 'hello'@'%';", //
                output);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_2() throws Exception {
        String sql = "GRANT CREATE TEMPORARY TABLES ON . TO hello@'%';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT CREATE TEMPORARY TABLES ON * TO 'hello'@'%';", //
                output);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_3() throws Exception {
        String sql = "GRANT ALTER ROUTINE ON . TO hello@'%';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT ALTER ROUTINE ON * TO 'hello'@'%';", //
                output);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_4() throws Exception {
        String sql = "grant all on . to hello@'%' identified by 'helloPassword' with grant option MAX_QUERIES_PER_HOUR 90;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT ALL ON * TO 'hello'@'%' IDENTIFIED BY 'helloPassword' WITH GRANT OPTION WITH MAX_QUERIES_PER_HOUR 90;", //
                output);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_5() throws Exception {
        String sql = "GRANT Describe,Select,alter,update,drop  ON   ads.*   TO    'ALIYUN$ads_user1@aliyun.com'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT DESCRIBE, SELECT, ALTER, UPDATE, DROP ON ads.* TO 'ALIYUN$ads_user1@aliyun.com'", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_6() throws Exception {
        String sql = "grant all on *.* to 'xx'@'localhost' with grant option";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLGrantStatement stmt = (SQLGrantStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        assertTrue(stmt.getWithGrantOption());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals("GRANT ALL ON *.* TO 'xx'@'localhost' WITH GRANT OPTION", //
                stmt.toString());

        assertEquals("grant ALL on *.* to 'xx'@'localhost' with grant option", //
                stmt.toLowerCaseString());

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

}
