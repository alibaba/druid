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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest71_window extends TestCase {
    private final String dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "select a.*, (a.swanav-lead(a.swanav,1,null::numeric) over w)/lead(a.swanav,1,null::numeric) over w as roe_lag\n" +
                "from ffund.ffund_eval_prod_nv a\n" +
                "where a.prod_id='D20171206191156525S0034R234'\n" +
                "and a.stat_date>='0000-00-00'\n" +
                "and a.stat_date<='9999-99-99'\n" +
                "and a.nav>0 and a.swanav>0\n" +
                "window w as (order by a.stat_date desc)";
        System.out.println(sql);

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        assertEquals("SELECT a.*\n" +
                "\t, (a.swanav - lead(a.swanav, 1, NULL::numeric)) / lead(a.swanav, 1, NULL::numeric) AS roe_lag\n" +
                "FROM ffund.ffund_eval_prod_nv a\n" +
                "WHERE a.prod_id = 'D20171206191156525S0034R234'\n" +
                "\tAND a.stat_date >= '0000-00-00'\n" +
                "\tAND a.stat_date <= '9999-99-99'\n" +
                "\tAND a.nav > 0\n" +
                "\tAND a.swanav > 0", SQLUtils.toPGString(stmt));
        
        assertEquals("select a.*\n" +
                "\t, (a.swanav - lead(a.swanav, 1, null::numeric)) / lead(a.swanav, 1, null::numeric) as roe_lag\n" +
                "from ffund.ffund_eval_prod_nv a\n" +
                "where a.prod_id = 'D20171206191156525S0034R234'\n" +
                "\tand a.stat_date >= '0000-00-00'\n" +
                "\tand a.stat_date <= '9999-99-99'\n" +
                "\tand a.nav > 0\n" +
                "\tand a.swanav > 0", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(5, visitor.getColumns().size());
        assertEquals(1, visitor.getTables().size());
    }
}
