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
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTableTest26 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE dept_20 " //
                + "   (employee_id     NUMBER(4), "//
                + "    last_name       VARCHAR2(10), "//
                + "    job_id          VARCHAR2(9), "//
                + "    manager_id      NUMBER(4), "//
                + "    hire_date       DATE, "//
                + "    salary          NUMBER(7,2), "//
                + "    commission_pct  NUMBER(7,2), "//
                + "    department_id   CONSTRAINT fk_deptno "//
                + "                    REFERENCES departments(department_id) ); ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE dept_20 (" //
                            + "\n\temployee_id NUMBER(4),"//
                            + "\n\tlast_name VARCHAR2(10),"//
                            + "\n\tjob_id VARCHAR2(9),"//
                            + "\n\tmanager_id NUMBER(4),"//
                            + "\n\thire_date DATE,"//
                            + "\n\tsalary NUMBER(7, 2),"//
                            + "\n\tcommission_pct NUMBER(7, 2),"//
                            + "\n\tdepartment_id"//
                            + "\n\t\tCONSTRAINT fk_deptno REFERENCES departments (department_id)"//
                            + "\n)",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(8, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("dept_20", "employee_id")));
    }
}
