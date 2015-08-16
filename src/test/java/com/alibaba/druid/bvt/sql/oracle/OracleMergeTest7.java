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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class OracleMergeTest7 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "MERGE INTO copy_emp c " + //
                     "USING employees e " + //
                     "ON (c.employee_id=e.employee_id) " + //
                     "WHEN MATCHED THEN " + //
                     "UPDATE SET " + //
                     "c.first_name=e.first_name, " + //
                     "c.last_name=e.last_name, " + //
                     "c.department_id=e.department_id " + //
                     "WHEN NOT MATCHED THEN " + //
                     "INSERT VALUES(e.employee_id,e.first_name,e.last_name," + //
                     "e.email,e.phone_number,e.hire_date,e.job_id, " + //
                     "e.salary,e.commission_pct,e.manager_id,e.department_id)";

        SQLStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        OracleMergeStatement mergeStatement = (OracleMergeStatement) stmtList.get(0);
        String result = SQLUtils.toOracleString(mergeStatement);
        Assert.assertEquals("MERGE INTO copy_emp c" //
                                    + "\nUSING employees e ON (c.employee_id = e.employee_id) " //
                                    + "\nWHEN MATCHED THEN UPDATE SET c.first_name = e.first_name, c.last_name = e.last_name, c.department_id = e.department_id" //
                                    + "\nWHEN NOT MATCHED THEN INSERT VALUES (e.employee_id, e.first_name, e.last_name, e.email, e.phone_number, e.hire_date, e.job_id, e.salary, e.commission_pct, e.manager_id, e.department_id)",
                            result);
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "department_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "bonus")));
    }

}
