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

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class OracleAnyTest extends TestCase {

    public void test_any() throws Exception {
        String sql = "SELECT country, prod, year, s FROM sales_view " + "MODEL PARTITION BY (country) "
                     + "DIMENSION BY (prod, year) MEASURES (sale s) " + "IGNORE NAV "
                     + "UNIQUE DIMENSION RULES UPSERT SEQUENTIAL ORDER (s[ANY, 2000] = 0) "
                     + "ORDER BY country, prod, year;";

        String expect = "SELECT country, prod, year, s\n" + "FROM sales_view\n" + "MODEL\n"
                        + "\tPARTITION BY (country)\n" + "\tDIMENSION BY (prod, year)\n" + "\tMEASURES (sale s)\n"
                        + "\tIGNORE NAV\n" + "\tUNIQUE DIMENSION\n"
                        + "\tRULES UPSERT SEQUENTIAL ORDER (s[ANY, 2000] = 0)\n" + "ORDER BY country, prod, year;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
