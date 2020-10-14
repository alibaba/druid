/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Assert;

import java.util.List;

public class OracleMergeTest10 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "merge into bonuses d \n" +
                "   using (select employee_id.* from employees) s \n" +
                "   on (employee_id = a) \n" +
                "   when not matched then insert (d.employee_id, d.bonus) \n" +
                "     values (s.employee_id, s.salary)\n" +
                "     where (s.salary <= 8000)\n" +
                "   when matched then update set d.bonus = bonus \n" +
                "     delete where (salary > 8000)";

        SQLStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLMergeStatement mergeStatement = (SQLMergeStatement) stmtList.get(0);
        String result = SQLUtils.toOracleString(mergeStatement);
        Assert.assertEquals("MERGE INTO bonuses d\n" +
                        "USING (\n" +
                        "\tSELECT employee_id.*\n" +
                        "\tFROM employees\n" +
                        ") s ON (employee_id = a) \n" +
                        "WHEN MATCHED THEN UPDATE SET d.bonus = bonus\n" +
                        "\tDELETE WHERE salary > 8000\n" +
                        "WHEN NOT MATCHED THEN INSERT (d.employee_id, d.bonus) VALUES (s.employee_id, s.salary)\n" +
                        "\tWHERE s.salary <= 8000",
                result);
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "department_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "bonus")));
    }

}
