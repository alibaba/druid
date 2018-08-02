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
package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class OracleEXTRACTTest2 extends TestCase {

    public void test_Extract() throws Exception {
        String sql = "SELECT warehouse_name, EXTRACT(warehouse_spec, '/Warehouse/Docks')\n" +
                "   \"Number of Docks\"\n" +
                "   FROM warehouses\n" +
                "   WHERE warehouse_spec IS NOT NULL;";

        String expect = "SELECT warehouse_name, EXTRACT(warehouse_spec, '/Warehouse/Docks') AS \"Number of Docks\"\n" +
                "FROM warehouses\n" +
                "WHERE warehouse_spec IS NOT NULL;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        assertEquals(expect, text);
    }
}
