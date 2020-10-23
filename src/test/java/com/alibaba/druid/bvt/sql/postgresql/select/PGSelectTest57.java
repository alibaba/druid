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
package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest57 extends TestCase {
    private final DbType dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "SELECT ts#, file#, block#, cols, IFNULL(size$, -1), pctfree$, pctused$\n" +
                "  , initrans, maxtrans, hashkeys, func, extind\n" +
                "  , avgchn, IFNULL(degree, 1)\n" +
                "  , IFNULL(instances, 1)\n" +
                "  , IFNULL(flags, 0)\n" +
                "  , IFNULL(spare1, 0)\n" +
                "FROM clu$\n" +
                "WHERE obj# ='633'";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT ts#, file#, block#, cols\n" +
                "\t, IFNULL(size$, -1), pctfree$\n" +
                "\t, pctused$, initrans, maxtrans, hashkeys, func\n" +
                "\t, extind, avgchn, IFNULL(degree, 1)\n" +
                "\t, IFNULL(instances, 1)\n" +
                "\t, IFNULL(flags, 0)\n" +
                "\t, IFNULL(spare1, 0)\n" +
                "FROM clu$\n" +
                "WHERE obj# = '633'", SQLUtils.toPGString(stmt));
        
        assertEquals("select ts#, file#, block#, cols\n" +
                "\t, IFNULL(size$, -1), pctfree$\n" +
                "\t, pctused$, initrans, maxtrans, hashkeys, func\n" +
                "\t, extind, avgchn, IFNULL(degree, 1)\n" +
                "\t, IFNULL(instances, 1)\n" +
                "\t, IFNULL(flags, 0)\n" +
                "\t, IFNULL(spare1, 0)\n" +
                "from clu$\n" +
                "where obj# = '633'", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(18, visitor.getColumns().size());
        assertEquals(1, visitor.getTables().size());
    }
}
