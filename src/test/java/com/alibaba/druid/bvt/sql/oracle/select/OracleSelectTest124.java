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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;


public class OracleSelectTest124 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "J01.COL_A,\n" +
                "J01.COL_B,\n" +
                "\"SUM\"(J01.COL_C) OVER (\n" +
                "PARTITION BY J01.COL_A ORDER BY J01.COL_B NULLS FIRST ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW\n" +
                ") AS COL_C\n" +
                "FROM\n" +
                "TAB_A J01";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT J01.COL_A, J01.COL_B, \"SUM\"(J01.COL_C) OVER (PARTITION BY J01.COL_A ORDER BY J01.COL_B NULLS FIRST ROWS  BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS COL_C\n" +
                "FROM TAB_A J01", stmt.toString());
    }

}