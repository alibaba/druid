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

public class SQLServerSelectTest30 extends TestCase {

    public void test_simple() throws Exception {
        String sql = //
                "SELECT First_Name + ' ' + Last_Name FROM Employees ORDER BY First_Name OFFSET 10 ROWS FETCH NEXT 5 ROWS ONLY;"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.SQL_SERVER);

        {
            String text = SQLUtils.toSQLString(stmtList, JdbcConstants.SQL_SERVER);

            assertEquals("SELECT First_Name + ' ' + Last_Name\n" +
                    "FROM Employees\n" +
                    "ORDER BY First_Name\n" +
                    "OFFSET 10 ROWS FETCH NEXT 5 ROWS ONLY;", text);
        }
        {
            String text = SQLUtils.toSQLString(stmtList, JdbcConstants.SQL_SERVER, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            assertEquals("select First_Name + ' ' + Last_Name\n" +
                    "from Employees\n" +
                    "order by First_Name\n" +
                    "offset 10 rows fetch next 5 rows only;", text);
        }
    }
}
