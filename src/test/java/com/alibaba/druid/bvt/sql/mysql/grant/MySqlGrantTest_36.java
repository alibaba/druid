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
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;

import java.util.List;

public class MySqlGrantTest_36 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "GRANT DELETE, CREATE, DROP ON *.* TO 'oa_2'@'localhost' with grant option";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("GRANT DELETE, CREATE, DROP ON *.* TO 'oa_2'@'localhost' WITH GRANT OPTION", //
                            output);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
    }

    public void test_error() throws Exception {
        String sql = "GRANT DELETE, CREATE, DROP ON *.* TO 'oa_2'@% with grant option";

        Exception error = null;
        try {
            SQLUtils.parseSingleMysqlStatement(sql);
        } catch (ParserException ex) {
            error = ex;
        }
        assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        String sql = "GRANT DELETE, CREATE, DROP ON *.* TO 'oa_2'@@ with grant option";

        Exception error = null;
        try {
            SQLUtils.parseSingleMysqlStatement(sql);
        } catch (ParserException ex) {
            error = ex;
        }
        assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        String sql = "GRANT DELETE, CREATE, DROP ON *.* TO 'oa_2'@127.0.0.1 with grant option";

        Exception error = null;
        try {
            SQLUtils.parseSingleMysqlStatement(sql);
        } catch (ParserException ex) {
            error = ex;
        }
        assertNotNull(error);
    }

}
