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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

public class MySqlAlterDbTest2 extends TestCase {

    public void test_0() throws Exception {
        String sql = "ALTER DATABASE logical_db SET read_only=1";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);
        
        assertEquals("ALTER DATABASE logical_db SET read_only = 1", stmt.toString());
        assertEquals("alter database logical_db set read_only = 1", stmt.toLowerCaseString());
    }

    public void test_1() throws Exception {
        String sql = "ALTER DATABASE logical_db SET read_only=1 ON physical_db";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("ALTER DATABASE logical_db SET read_only = 1 ON physical_db", stmt.toString());
        assertEquals("alter database logical_db set read_only = 1 on physical_db", stmt.toLowerCaseString());
    }

    public void test_2() throws Exception {
        String sql = "ALTER DATABASE logical_db SET READ_ONLY=1 ,TIME=10 ON physical_db;";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("ALTER DATABASE logical_db SET READ_ONLY = 1, TIME = 10 ON physical_db;", stmt.toString());
        assertEquals("alter database logical_db set READ_ONLY = 1, TIME = 10 on physical_db;", stmt.toLowerCaseString());
    }

}
