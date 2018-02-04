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
package com.alibaba.druid.bvt.sql.sqlserver.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class SQLServerSelectTest29 extends TestCase {

    public void test_simple() throws Exception {
        String sql = //
                "WITH A AS (\n" +
                        "SELECT GETDATE() AS TTT\n" +
                        ")\n" +
                        "SELECT TTT FROM A\n" +
                        "UNION ALL\n" +
                        "SELECT TTT FROM A"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.SQL_SERVER);

        {
            String text = SQLUtils.toSQLString(stmtList, JdbcConstants.SQL_SERVER);

            assertEquals("WITH A AS (\n" +
                    "\t\tSELECT GETDATE() AS TTT\n" +
                    "\t)\n" +
                    "SELECT TTT\n" +
                    "FROM A\n" +
                    "UNION ALL\n" +
                    "SELECT TTT\n" +
                    "FROM A", text);
        }
        {
            String text = SQLUtils.toSQLString(stmtList, JdbcConstants.SQL_SERVER, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            assertEquals("with A as (\n" +
                    "\t\tselect GETDATE() as TTT\n" +
                    "\t)\n" +
                    "select TTT\n" +
                    "from A\n" +
                    "union all\n" +
                    "select TTT\n" +
                    "from A", text);
        }
    }
}
