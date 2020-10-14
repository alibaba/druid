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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLRevokeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlRevokeTest_ads_1 extends MysqlTest {

    public void test_doc_0() throws Exception {
        String sql = "REVOKE describe, select ON db_name.table_group_name FROM 'ALIYUN$account_name'@'%';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLRevokeStatement stmt = (SQLRevokeStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("REVOKE DESCRIBE, SELECT ON db_name.table_group_name FROM 'ALIYUN$account_name'@'%';", //
                stmt.toString());
    }

    public void test_doc_1() throws Exception {
        String sql = "REVOKE all ON db_name.table_group_name FROM 'ALIYUN$account_name'@'%';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLRevokeStatement stmt = (SQLRevokeStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("REVOKE ALL ON db_name.table_group_name FROM 'ALIYUN$account_name'@'%';", //
                stmt.toString());
    }

    public void test_doc_2() throws Exception {
        String sql = "REVOKE describe, select (col1, col2) ON db_name.* FROM 'ALIYUN$account_name';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLRevokeStatement stmt = (SQLRevokeStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("REVOKE DESCRIBE, SELECT(col1, col2) ON db_name.* FROM 'ALIYUN$account_name';", //
                stmt.toString());
    }

    public void test_doc_3() throws Exception {
        String sql = "REVOKE describe, select (col1, col2) ON db_name.table_name FROM 'ALIYUN$account_name';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLRevokeStatement stmt = (SQLRevokeStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("REVOKE DESCRIBE, SELECT(col1, col2) ON db_name.table_name FROM 'ALIYUN$account_name';", //
                stmt.toString());
    }

    public void test_show_0() throws Exception {
        String sql = "show grants on db_name.*;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlShowGrantsStatement stmt = (MySqlShowGrantsStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SHOW GRANTS ON db_name.*;", //
                stmt.toString());
        assertEquals("show grants on db_name.*;", //
                stmt.toLowerCaseString());
    }

    public void test_show_1() throws Exception {
        String sql = "show grants for 'ALIYUN$account_name' on db_name.*;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlShowGrantsStatement stmt = (MySqlShowGrantsStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SHOW GRANTS FOR 'ALIYUN$account_name' ON db_name.*;", //
                stmt.toString());
        assertEquals("show grants for 'ALIYUN$account_name' on db_name.*;", //
                stmt.toLowerCaseString());
    }

    public void test_show_2() throws Exception {
        String sql = "show grants for user@''";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlShowGrantsStatement stmt = (MySqlShowGrantsStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SHOW GRANTS FOR 'user'@''", //
                stmt.toString());
        assertEquals("show grants for 'user'@''", //
                stmt.toLowerCaseString());


        MySqlUserName userName = new MySqlUserName();
        userName.setUserName("");
        userName.setHost("");

        userName.getSimpleName();

        userName.toString();
    }


    public void test_show_3() throws Exception {
        String sql = "show grants for 'user'@'localhost' ";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlShowGrantsStatement stmt = (MySqlShowGrantsStatement) statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SHOW GRANTS FOR 'user'@'localhost'", //
                stmt.toString());
        assertEquals("show grants for 'user'@'localhost'", //
                stmt.toLowerCaseString());

    }


}
