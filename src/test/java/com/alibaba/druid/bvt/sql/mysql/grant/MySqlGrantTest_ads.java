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
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlGrantTest_ads extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "GRANT SELECT ON TABLE * TO 'ALIYUN$ads_user1@aliyun.com'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT SELECT ON TABLE * TO 'ALIYUN$ads_user1@aliyun.com'", //
                            output);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_1() throws Exception {
        String sql = "GRANT SELECT(C1, C2), DUMP DATA(C3, C4), SELECT(C3, DB1.TB1.C4) ON TABLE DB1.TB1 TO 'ALIYUN$ads_user1@aliyun.com', 'ALIYUN$ads_user2@aliyun.com' WITH MAX_QUERIES_PER_HOUR 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT SELECT(C1, C2), DUMP DATA(C3, C4), SELECT(C3, DB1.TB1.C4) ON TABLE DB1.TB1 TO 'ALIYUN$ads_user1@aliyun.com','ALIYUN$ads_user2@aliyun.com' WITH MAX_QUERIES_PER_HOUR 1", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_2() throws Exception {
        String sql = "GRANT SELECT(C1, C2), DUMP DATA(C3, C4), SELECT(C3, DB1.TB1.C4) ON TABLE *.* TO 'ALIYUN$ads_user1@aliyun.com', 'ALIYUN$ads_user2@aliyun.com' WITH MAX_QUERIES_PER_HOUR 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT SELECT(C1, C2), DUMP DATA(C3, C4), SELECT(C3, DB1.TB1.C4) ON TABLE *.* TO 'ALIYUN$ads_user1@aliyun.com','ALIYUN$ads_user2@aliyun.com' WITH MAX_QUERIES_PER_HOUR 1", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_3() throws Exception {
        String sql = "GRANT ALL ON SYSTEM *.* TO 'ALIYUN$ads_user1@aliyun.com', 'ALIYUN$ads_user2@aliyun.com' ";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT ALL ON SYSTEM *.* TO 'ALIYUN$ads_user1@aliyun.com','ALIYUN$ads_user2@aliyun.com'", //
                output);

        assertEquals(0, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_4() throws Exception {
        String sql = "GRANT ALL ON *.* TO 'ALIYUN$ads_user1@aliyun.com', 'ALIYUN$ads_user2@aliyun.com'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT ALL ON *.* TO 'ALIYUN$ads_user1@aliyun.com','ALIYUN$ads_user2@aliyun.com'", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_5() throws Exception {
        String sql = "GRANT INSERT ON *.* TO 'ALIYUN$ads_user1@aliyun.com', 'ALIYUN$ads_user2@aliyun.com'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT INSERT ON *.* TO 'ALIYUN$ads_user1@aliyun.com','ALIYUN$ads_user2@aliyun.com'", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

    }

    public void test_6() throws Exception {
        String sql = "GRANT DELETE, UPDATE ON *.* TO 'ALIYUN$ads_user1@aliyun.com', 'ALIYUN$ads_user2@aliyun.com'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT DELETE, UPDATE ON *.* TO 'ALIYUN$ads_user1@aliyun.com','ALIYUN$ads_user2@aliyun.com'", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }



    public void test_7() throws Exception {
        String sql = "GRANT ALL ON sysdb.* TO '%'@'192.168.1.2'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT ALL ON sysdb.* TO '%'@'192.168.1.2'", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }


    public void test_8() throws Exception {
        String sql = "GRANT ALL ON sysdb.* TO '%'@'192.168.1.2'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT ALL ON sysdb.* TO '%'@'192.168.1.2'", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_9() throws Exception {
        String sql = "GRANT ALL  ON   sysdb.*   TO   '%'@'192.168.1/20'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT ALL ON sysdb.* TO '%'@'192.168.1/20'", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_10() throws Exception {
        String sql = "GRANT ALL  ON   sysdb.*   TO   '%'@'192.168.1/20', 'ALIYUN$ads_user1@aliyun.com'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLGrantStatement stmt = (SQLGrantStatement) statementList.get(0);

        assertEquals(1, statementList.size());


        SQLExpr user0 = stmt.getUsers().get(0);
        assertEquals(user0.toString().split("'@'")[0].replaceAll("'",""), "%");
        assertEquals(user0.toString().split("'@'")[1].replaceAll("'", ""), "192.168.1/20");

        assertEquals("ALIYUN$ads_user1@aliyun.com", stmt.getUsers().get(1).toString().split("'@'")[0].replaceAll("'", ""));
        assertEquals(1, stmt.getUsers().get(1).toString().split("'@'").length);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT ALL ON sysdb.* TO '%'@'192.168.1/20','ALIYUN$ads_user1@aliyun.com'", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_11() throws Exception {
        String sql = "GRANT Select, Show  ON   ads.*   TO    'ALIYUN$ads_user1@aliyun.com'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLGrantStatement stmt = (SQLGrantStatement) statementList.get(0);

        assertEquals(1, statementList.size());


        assertEquals("ALIYUN$ads_user1@aliyun.com", stmt.getUsers().get(0).toString().split("'@'")[0].replaceAll("'", ""));
        assertEquals(1, stmt.getUsers().get(0).toString().split("'@'").length);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT SELECT, SHOW ON ads.* TO 'ALIYUN$ads_user1@aliyun.com'", //
                output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

    }


    public void test_doc_0() throws Exception {
        String sql = "GRANT describe, select ON db_name.table_group_name TO 'ALIYUN$account_name'@'%';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLGrantStatement stmt = (SQLGrantStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("GRANT DESCRIBE, SELECT ON db_name.table_group_name TO 'ALIYUN$account_name'@'%';", //
                stmt.toString());
    }

    public void test_doc_1() throws Exception {
        String sql = "GRANT all ON db_name.table_group_name TO 'ALIYUN$account_name'@'%';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLGrantStatement stmt = (SQLGrantStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("GRANT ALL ON db_name.table_group_name TO 'ALIYUN$account_name'@'%';", //
                stmt.toString());
    }

    public void test_doc_2() throws Exception {
        String sql = "GRANT describe, select (col1, col2) ON db_name.* TO 'ALIYUN$account_name';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLGrantStatement stmt = (SQLGrantStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("GRANT DESCRIBE, SELECT(col1, col2) ON db_name.* TO 'ALIYUN$account_name';", //
                stmt.toString());
    }

    public void test_doc_3() throws Exception {
        String sql = "GRANT describe, select (col1, col2) ON db_name.table_name TO 'ALIYUN$account_name';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLGrantStatement stmt = (SQLGrantStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("GRANT DESCRIBE, SELECT(col1, col2) ON db_name.table_name TO 'ALIYUN$account_name';", //
                stmt.toString());
    }


}
