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
package com.alibaba.druid.bvt.sql.oracle.block;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleBlockTest12 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE" +
        		"  emp_id        NUMBER(6);" +
        		"  emp_lastname  VARCHAR2(25);" +
        		"  emp_salary    NUMBER(8,2);" +
        		"  emp_jobid     VARCHAR2(10);" +
        		"BEGIN" +
        		"  SELECT employee_id, last_name, salary, job_id" +
        		"  INTO emp_id, emp_lastname, emp_salary, emp_jobid" +
        		"  FROM employees" +
        		"  WHERE employee_id = 120;" +
        		"" +
        		"  INSERT INTO emp_name (employee_id, last_name)" +
        		"  VALUES (emp_id, emp_lastname); " +
        		"  INSERT INTO emp_sal (employee_id, salary) " +
        		"  VALUES (emp_id, emp_salary);" +
        		"" +
        		"  INSERT INTO emp_job (employee_id, job_id)" +
        		"  VALUES (emp_id, emp_jobid);" +
        		" " +
        		"EXCEPTION" +
        		"  WHEN DUP_VAL_ON_INDEX THEN" +
        		"    ROLLBACK;" +
        		"    DBMS_OUTPUT.PUT_LINE('Inserts were rolled back');" +
        		"END;"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(4, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp_name")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp_sal")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp_job")));

        Assert.assertEquals(11, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());

//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
    }
}
