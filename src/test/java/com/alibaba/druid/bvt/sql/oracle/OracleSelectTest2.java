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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "WITH " + //
                     "   dept_costs AS (" + //
                     "      SELECT d.department_name, SUM(d.salary) dept_total" + //
                     "         FROM employees e, departments d" + //
                     "         WHERE e.department_id = d.department_id" + //
                     "      GROUP BY d.department_name)," + //
                     "   avg_cost AS (" + //
                     "      SELECT SUM(dept_total)/COUNT(*) avg" + //
                     "      FROM dept_costs)" + //
                     "SELECT * FROM dept_costs" + //
                     "   WHERE dept_total >" + //
                     "      (SELECT avg FROM avg_cost)" + //
                     "      ORDER BY department_name;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(2, visitor.getTables().size());
        
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("departments")));

        Assert.assertEquals(4, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("films", "producer_id")));
    }

}
