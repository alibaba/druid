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

public class PGSelectTest56 extends TestCase {
    private final String dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "SELECT to_char((CreateDate || ' ' || CAST (HourArgment AS VARCHAR) || ':00:00') :: TIMESTAMP,'YYYY-MM-DD HH24') \"intoTime \" FROM analyzedata.StatTime AS cs WHERE 1 = 1 AND cs.CreateDate >= to_date( '2017-08-31 00:00:00', 'YYYY-MM-DD HH24:MI:SS' ) AND cs.CreateDate <= to_date( '2017-08-31 23:00:00', 'YYYY-MM-DD HH24:MI:SS' ) GROUP BY to_char(( CreateDate || ' ' || CAST (HourArgment AS VARCHAR) || ':00:00') :: TIMESTAMP, 'YYYY-MM-DD HH24')";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT to_char((CreateDate || ' ' || CAST(HourArgment AS VARCHAR) || ':00:00')::TIMESTAMP, 'YYYY-MM-DD HH24') AS \"intoTime \"\n" +
                "FROM analyzedata.StatTime cs\n" +
                "WHERE 1 = 1\n" +
                "\tAND cs.CreateDate >= to_date('2017-08-31 00:00:00', 'YYYY-MM-DD HH24:MI:SS')\n" +
                "\tAND cs.CreateDate <= to_date('2017-08-31 23:00:00', 'YYYY-MM-DD HH24:MI:SS')\n" +
                "GROUP BY to_char((CreateDate || ' ' || CAST(HourArgment AS VARCHAR) || ':00:00')::TIMESTAMP, 'YYYY-MM-DD HH24')", SQLUtils.toPGString(stmt));
        
        assertEquals("select to_char((CreateDate || ' ' || cast(HourArgment as VARCHAR) || ':00:00')::TIMESTAMP, 'YYYY-MM-DD HH24') as \"intoTime \"\n" +
                "from analyzedata.StatTime cs\n" +
                "where 1 = 1\n" +
                "\tand cs.CreateDate >= to_date('2017-08-31 00:00:00', 'YYYY-MM-DD HH24:MI:SS')\n" +
                "\tand cs.CreateDate <= to_date('2017-08-31 23:00:00', 'YYYY-MM-DD HH24:MI:SS')\n" +
                "group by to_char((CreateDate || ' ' || cast(HourArgment as VARCHAR) || ':00:00')::TIMESTAMP, 'YYYY-MM-DD HH24')", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(2, visitor.getColumns().size());
        assertEquals(1, visitor.getTables().size());
    }
}
