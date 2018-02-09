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
package com.alibaba.druid.bvt.sql.oracle.create;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTableTest29 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE dept_20" //
                + "   (employee_id     NUMBER(4) PRIMARY KEY, "//
                + "    last_name       VARCHAR2(10), "//
                + "    job_id          VARCHAR2(9), "//
                + "    manager_id      NUMBER(4), "//
                + "    salary          NUMBER(7,2), "//
                + "    commission_pct  NUMBER(7,2), "//
                + "    department_id   NUMBER(2),"//
                + "    CONSTRAINT check_sal CHECK (salary * commission_pct <= 5000));";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE dept_20 (" //
                            + "\n\temployee_id NUMBER(4) PRIMARY KEY," //
                            + "\n\tlast_name VARCHAR2(10)," //
                            + "\n\tjob_id VARCHAR2(9)," //
                            + "\n\tmanager_id NUMBER(4)," //
                            + "\n\tsalary NUMBER(7, 2)," //
                            + "\n\tcommission_pct NUMBER(7, 2)," //
                            + "\n\tdepartment_id NUMBER(2)," //
                            + "\n\tCONSTRAINT check_sal CHECK (salary * commission_pct <= 5000)" //
                            + "\n);",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(7, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("dept_20", "employee_id")));
    }
}
