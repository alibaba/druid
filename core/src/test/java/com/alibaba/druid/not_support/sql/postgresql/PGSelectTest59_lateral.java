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
package com.alibaba.druid.not_support.sql.postgresql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest59_lateral extends TestCase {
    private final DbType dbType = JdbcConstants.POSTGRESQL;

    public void test_0() throws Exception {
        String sql = "SELECT m.name AS mname, pname\n" +
                "FROM manufacturers m LEFT JOIN LATERAL get_product_names(m.id) pname ON true;";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);
        String result = SQLUtils.toPGString(stmt);
        assertNotNull(result);
        assertTrue(result.contains("LATERAL"));
        assertTrue(result.contains("get_product_names"));
    }
}
