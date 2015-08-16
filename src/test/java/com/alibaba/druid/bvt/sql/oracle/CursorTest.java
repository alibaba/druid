/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

public class CursorTest extends TestCase {

    public void test_cursor() throws Exception {
        String sql = "SELECT department_name, CURSOR(SELECT salary, commission_pct FROM employees e WHERE e.department_id = d.department_id) "
                     + "FROM departments d;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals("SELECT department_name, CURSOR(\n\t\tSELECT salary, commission_pct\n" + "\t\tFROM employees e\n"
                            + "\t\tWHERE e.department_id = d.department_id\n\t)\n" + "FROM departments d;\n", text);

        System.out.println(text);
    }
}
