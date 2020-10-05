/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.ads.parser.AdsStatementParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class MySqlSelectTest_adsHintAndTypeConvert extends MysqlTest {

    public void test_1() throws Exception {
        String sql = "/*+engine=mpp*/ select timestamp '2017-01-01 11:11:11'";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();

        assertEquals("/*+engine=mpp*/\n" + "SELECT TIMESTAMP '2017-01-01 11:11:11'", statement.toString());
    }

    public void test_2() throws Exception {
        String sql = "/*+engine=mpp*/select year_of_week(DATE '2017-01-01')";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();

        assertEquals("/*+engine=mpp*/\n" + "SELECT year_of_week(DATE '2017-01-01')", statement.toString());
    }

    public void test_3() throws Exception {
        String sql = "/*+engine=mpp*/select year_of_week(DATE '2017-01-01')";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();

        assertEquals("/*+engine=mpp*/\n" + "SELECT year_of_week(DATE '2017-01-01')", statement.toString());
    }

    public void test_4() throws Exception {
        String sql = "select * from test1 where TIMESTAMP '2019-1-1 00:00:00' = timestamp_test";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();

        assertEquals("SELECT *\n" + "FROM test1\n" + "WHERE TIMESTAMP '2019-1-1 00:00:00' = timestamp_test", statement.toString());
    }

    public void test_5() throws Exception {
        String sql = "select * from test1 where timestamp_test = TIMESTAMP '2019-1-1 00:00:00'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();

        assertEquals("SELECT *\n" + "FROM test1\n" + "WHERE timestamp_test = TIMESTAMP '2019-1-1 00:00:00'", statement.toString());
    }

    public void test_6() throws Exception {
        String sql = "select * from test1 where  DATE '2019-1-1' = date_test";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();

        assertEquals("SELECT *\n" + "FROM test1\n" + "WHERE DATE '2019-1-1' = date_test", statement.toString());
    }

    public void test_7() throws Exception {
        String sql = "select DATE '2019-1-1'";

        AdsStatementParser parser = new AdsStatementParser(sql);
        SQLStatement statement = parser.parseStatement();

        assertEquals("SELECT DATE '2019-1-1'", statement.toString());
    }
}
