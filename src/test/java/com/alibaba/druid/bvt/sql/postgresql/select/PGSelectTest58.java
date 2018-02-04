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
package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest58 extends TestCase {
    private final String dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "select id,sum(uv[1]) uv1,sum(uv[2]) uv2\n" +
                "from xxxxx where a in\n" +
                "                     (  \n" +
                "                        ?\n" +
                "                     ) \n" +
                " and ta->'taAge' ??|\n" +
                "                 \n" +
                "                         '{  \n" +
                "                            1\n" +
                "                         , \n" +
                "                            2\n" +
                "                         , \n" +
                "                            3\n" +
                "                         }' \n" +
                "group by id";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT id, SUM(uv[1]) AS uv1, SUM(uv[2]) AS uv2\n" +
                "FROM xxxxx\n" +
                "WHERE a IN (?)\n" +
                "\tAND ta -> 'taAge' ?| '{  \n" +
                "                            1\n" +
                "                         , \n" +
                "                            2\n" +
                "                         , \n" +
                "                            3\n" +
                "                         }'\n" +
                "GROUP BY id", SQLUtils.toPGString(stmt));
        
        assertEquals("select id, sum(uv[1]) as uv1, sum(uv[2]) as uv2\n" +
                "from xxxxx\n" +
                "where a in (?)\n" +
                "\tand ta -> 'taAge' ?| '{  \n" +
                "                            1\n" +
                "                         , \n" +
                "                            2\n" +
                "                         , \n" +
                "                            3\n" +
                "                         }'\n" +
                "group by id", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(4, visitor.getColumns().size());
        assertEquals(1, visitor.getTables().size());
    }
}
