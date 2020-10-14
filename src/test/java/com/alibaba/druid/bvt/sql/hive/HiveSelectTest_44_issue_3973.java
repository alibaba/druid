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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_44_issue_3973 extends TestCase {

    public void test_0() throws Exception {
        String sql = "WITH t AS (SELECT * FROM t1 ) INSERT OVERWRITE TABLE t2 SELECT * FROM t;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive);
        SQLStatement stmt =  statementList.get(0);

        assertEquals("WITH t AS (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM t1\n" +
                "\t)\n" +
                "INSERT OVERWRITE TABLE t2\n" +
                "SELECT *\n" +
                "FROM t;", stmt.toString());

    }
}
