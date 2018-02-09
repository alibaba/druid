/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class SHOW_TABLES_Syntax_Test extends TestCase {

    public void test_0() throws Exception {

        String sql = "SHOW TABLES FROM     SUNTEST   ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        String text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW TABLES FROM SUNTEST", text);

        sql = "SHOW       TABLES";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();
        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW TABLES", text);

        sql = "SHOW   FULL    TABLES";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();
        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW FULL TABLES", text);

        sql = "SHOW   FULL    TABLES    FROM    SUNTEST   ";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();
        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW FULL TABLES FROM SUNTEST", text);

        sql = "SHOW TABLES IN SUNTEST   ";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();
        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW TABLES FROM SUNTEST", text);

        sql = "SHOW FULL TABLES IN SUNTEST   ";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();
        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW FULL TABLES FROM SUNTEST", text);


        sql = "SHOW FULL TABLES IN SUNTEST  LIKE '%DDD%' ";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();
        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW FULL TABLES FROM SUNTEST LIKE '%DDD%'", text);

        sql = "SHOW FULL TABLES FROM SUNTEST  LIKE '%DDD%' ";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();
        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW FULL TABLES FROM SUNTEST LIKE '%DDD%'", text);

        sql = "SHOW FULL TABLES IN SUNTEST  WHERE NAME =  'DDD' ";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();
        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW FULL TABLES FROM SUNTEST WHERE NAME = 'DDD'", text);


        sql = "SHOW FULL TABLES FROM SUNTEST  WHERE NAME =  'DDD' ";
        parser = new MySqlStatementParser(sql);
        stmtList = parser.parseStatementList();
        text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SHOW FULL TABLES FROM SUNTEST WHERE NAME = 'DDD'", text);

    }

}
