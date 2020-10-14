/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;


public class MySqlSelectTest_267 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select TABLE_SCHEMA dbName,TABLE_NAME tableName,ENGINE engine,ROW_FORMAT rowFormat,TABLE_COLLATION `collate`,CREATE_OPTIONS createOption,TABLE_COMMENT `comment`\n" +
                "from information_schema.tables\n" +
                "where TABLE_SCHEMA='test_schema' and TABLE_NAME='test_table'\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql
                );

        assertEquals("SELECT TABLE_SCHEMA AS dbName, TABLE_NAME AS tableName, ENGINE AS engine, ROW_FORMAT AS rowFormat, TABLE_COLLATION AS `collate`\n" +
                "\t, CREATE_OPTIONS AS createOption, TABLE_COMMENT AS `comment`\n" +
                "FROM information_schema.tables\n" +
                "WHERE TABLE_SCHEMA = 'test_schema'\n" +
                "\tAND TABLE_NAME = 'test_table'", stmt.toString());
    }


}