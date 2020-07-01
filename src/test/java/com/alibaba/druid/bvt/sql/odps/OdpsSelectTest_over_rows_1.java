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
package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class OdpsSelectTest_over_rows_1 extends TestCase {

    public void test_select() throws Exception {
        String sql = "select last_name, first_name, department_id, hire_date, salary,\n" +
                "     SUM (salary)\n" +
                "    OVER (PARTITION BY department_id ORDER BY hire_date\n" +
                "          RANGE BETWEEN 365 PRECEDING AND 365 FOLLOWING) department_total\n" +
                "  from employee\n" +
                "  order by department_id, hire_date;";//
        Assert.assertEquals("SELECT last_name, first_name, department_id, hire_date, salary\n" +
                "\t, SUM(salary) OVER (PARTITION BY department_id ORDER BY hire_date RANGE BETWEEN 365 PRECEDING AND 365 FOLLOWING) AS department_total\n" +
                "FROM employee\n" +
                "ORDER BY department_id, \n" +
                "\thire_date;", SQLUtils.formatOdps(sql));
        Assert.assertEquals("select last_name, first_name, department_id, hire_date, salary\n" +
                "\t, sum(salary) over (partition by department_id order by hire_date range between 365 preceding and 365 following) as department_total\n" +
                "from employee\n" +
                "order by department_id, \n" +
                "\thire_date;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());
        
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);
        
//        System.out.println("Tables : " + visitor.getTables());
//      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        
//        Assert.assertTrue(visitor.getColumns().contains(new Column("abc", "name")));
    }
    
}
