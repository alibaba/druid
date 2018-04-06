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
package com.alibaba.druid.bvt.sql.oracle.block;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleBlockTest22_comment extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE\n" +
                "  howmany     NUMBER;\n" +
                "  num_tables  NUMBER;\n" +
                "BEGIN\n" +
                "  -- Begin processing\n" +
                "  SELECT COUNT(*) INTO howmany\n" +
                "  FROM USER_OBJECTS\n" +
                "  WHERE OBJECT_TYPE = 'TABLE'; -- Check number of tables\n" +
                "  num_tables := howmany;       -- Compute another value\n" +
                "END;"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toOracleString(stmtList.get(0));
        System.out.println(result);
        assertEquals("DECLARE\n" +
                "\thowmany NUMBER;\n" +
                "\tnum_tables NUMBER;\n" +
                "BEGIN\n" +
                "\t-- Begin processing\n" +
                "\tSELECT COUNT(*)\n" +
                "\tINTO howmany\n" +
                "\tFROM USER_OBJECTS\n" +
                "\tWHERE OBJECT_TYPE = 'TABLE';\n" +
                "\tnum_tables := howmany;\n" +
                "END;", result);

        assertEquals(1, stmtList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        for (SQLStatement statement : stmtList) {
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("USER_OBJECTS")));

        assertEquals(2, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());

         assertTrue(visitor.containsColumn("USER_OBJECTS", "OBJECT_TYPE"));
         assertTrue(visitor.containsColumn("USER_OBJECTS", "*"));
    }
}
