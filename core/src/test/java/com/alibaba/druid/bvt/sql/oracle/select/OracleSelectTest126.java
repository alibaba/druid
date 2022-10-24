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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;


public class OracleSelectTest126 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT *\n" +
                "FROM insurance_order insurance_order, allot_track at\n" +
                "WHERE at.order_id_s = insurance_order.order_id\n" +
                "  AND at.end_date = to_date('1900-01-01', 'yyyy-mm-dd')\n" +
                "  AND insurance_order.status = '3'\n" +
                "  AND at.produce_id = 'BX'";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT *\n" +
                "FROM insurance_order insurance_order, allot_track at\n" +
                "WHERE at.order_id_s = insurance_order.order_id\n" +
                "\tAND at.end_date = to_date('1900-01-01', 'yyyy-mm-dd')\n" +
                "\tAND insurance_order.status = '3'\n" +
                "\tAND at.produce_id = 'BX'", stmt.toString());

        assertEquals("select *\n" +
                "from insurance_order insurance_order, allot_track at\n" +
                "where at.order_id_s = insurance_order.order_id\n" +
                "\tand at.end_date = to_date('1900-01-01', 'yyyy-mm-dd')\n" +
                "\tand insurance_order.status = '3'\n" +
                "\tand at.produce_id = 'BX'", stmt.toLowerCaseString());
    }

}