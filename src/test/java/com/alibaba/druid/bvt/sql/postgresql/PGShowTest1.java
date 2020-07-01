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
package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGShowStatement;

public class PGShowTest1 extends PGTest {
    public void testShowAll() throws Exception {
        String sql = "show all;";
        String expected = "SHOW ALL;";
        testParseSql(sql, expected, expected, PGShowStatement.class);

        sql = "show transaction_read_only;";
        expected = "SHOW transaction_read_only;";
        testParseSql(sql, expected, expected, PGShowStatement.class);
    }
}
