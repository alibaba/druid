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

public class OracleBlockTest11 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DROP TABLE emp;" +
        		"CREATE TABLE emp AS SELECT * FROM employees;" +
        		" " +
        		"DECLARE" +
        		"  CURSOR c1 IS" +
        		"    SELECT * FROM emp" +
        		"    FOR UPDATE OF salary" +
        		"    ORDER BY employee_id;" +
        		"   emp_rec  emp%ROWTYPE;" +
        		"BEGIN" +
        		"  OPEN c1;" +
        		"  LOOP" +
        		"    FETCH c1 INTO emp_rec;  -- fails on second iteration\n" +
        		"    EXIT WHEN c1%NOTFOUND;" +
        		"    DBMS_OUTPUT.PUT_LINE (" +
        		"      'emp_rec.employee_id = ' ||" +
        		"      TO_CHAR(emp_rec.employee_id)" +
        		"    );" +
        		"    " +
        		"    UPDATE emp" +
        		"    SET salary = salary * 1.05" +
        		"    WHERE employee_id = 105;" +
        		" " +
        		"    COMMIT;  -- releases locks\n" +
        		"  END LOOP;" +
        		"END;"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

		for (SQLStatement stmt : statementList) {
			System.out.println(stmt);
			System.out.println();
		}

        Assert.assertEquals(3, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp")));

        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());

        Assert.assertTrue(visitor.containsColumn("employees", "*"));
        Assert.assertTrue(visitor.containsColumn("emp", "employee_id"));
		Assert.assertTrue(visitor.containsColumn("emp", "*"));
		Assert.assertTrue(visitor.containsColumn("emp", "salary"));
    }
}
