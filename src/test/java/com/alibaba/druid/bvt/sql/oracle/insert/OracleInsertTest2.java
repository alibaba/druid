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
package com.alibaba.druid.bvt.sql.oracle.insert;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleInsertTest2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO employees " + //
                     "      (employee_id, last_name, email, hire_date, job_id, salary)" + //
                     "   VALUES " + //
                     "   (employees_seq.nextval, 'Doe', 'john.doe@example.com', " + //
                     "       SYSDATE, 'SH_CLERK', 2400) " + //
                     "   RETURNING salary*12, job_id INTO :bnd1, :bnd2; ";

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

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(6, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "last_name")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "email")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "hire_date")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "job_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
    }

}
