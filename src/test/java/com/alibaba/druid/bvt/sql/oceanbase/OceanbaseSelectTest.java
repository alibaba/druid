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
package com.alibaba.druid.bvt.sql.oceanbase;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OceanbaseSelectTest extends MysqlTest {
    protected final DbType dbType = JdbcConstants.MYSQL;

    public void test_0() throws Exception {
        String sql = "SELECT EmpID, EmpName, MgrId, Level FROM emp START WITH MgrId IS NULL CONNECT BY PRIOR EmpId = MgrId"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        {
            String result = SQLUtils.toSQLString(stmt, dbType);
            assertEquals("SELECT EmpID, EmpName, MgrId, Level\n" +
                    "FROM emp\n" +
                    "START WITH MgrId IS NULL\n" +
                    "CONNECT BY PRIOR EmpId = MgrId", result);
        }
        {
            String result = SQLUtils.toSQLString(stmt, dbType, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select EmpID, EmpName, MgrId, Level\n" +
                    "from emp\n" +
                    "start with MgrId is null\n" +
                    "connect by prior EmpId = MgrId", result);
        }

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(3, visitor.getConditions().size());

        // assertTrue(visitor.getTables().containsKey(new TableStat.Name("t_basic_store")));

    }
}
