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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleSelectTest_param extends OracleTest {
    private DbType dbType = JdbcConstants.ORACLE;

    public void test_0() throws Exception {
        String sql = //
                "select c1 from t1 t where t.c2=1 and (t.c3=1 or t.c3=5)"; //

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);

        assertEquals("SELECT c1\n" +
                "FROM t1 t\n" +
                "WHERE t.c2 = 1\n" +
                "\tAND (t.c3 = 1\n" +
                "\t\tOR t.c3 = 5)", SQLUtils.toSQLString(stmt, dbType));

        assertEquals("SELECT c1\n" +
                "FROM t1 t\n" +
                "WHERE t.c2 = ?\n" +
                "\tAND (t.c3 = ?)", SQLUtils.toSQLString(stmt, dbType, new SQLUtils.FormatOption(true, true, true)));
    }
}
