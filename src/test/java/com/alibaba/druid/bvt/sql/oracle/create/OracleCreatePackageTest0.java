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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreatePackageTest0 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE OR REPLACE PACKAGE BODY emp_mgmt AS \n" +
                "   tot_emps NUMBER; \n" +
                "   tot_depts NUMBER; \n" +
                "FUNCTION hire \n" +
                "   (last_name VARCHAR2, job_id VARCHAR2, \n" +
                "    manager_id NUMBER, salary NUMBER, \n" +
                "    commission_pct NUMBER, department_id NUMBER) \n" +
                "   RETURN NUMBER IS new_empno NUMBER; \n" +
                "BEGIN \n" +
                "   SELECT employees_seq.NEXTVAL \n" +
                "      INTO new_empno \n" +
                "      FROM DUAL; \n" +
                "   INSERT INTO employees \n" +
                "      VALUES (new_empno, 'First', 'Last','first.last@oracle.com', \n" +
                "              '(123)123-1234','18-JUN-02','IT_PROG',90000000,00, \n" +
                "              100,110); \n" +
                "      tot_emps := tot_emps + 1; \n" +
                "   RETURN(new_empno); \n" +
                "END; \n" +
                "FUNCTION create_dept(department_id NUMBER, location_id NUMBER) \n" +
                "   RETURN NUMBER IS \n" +
                "      new_deptno NUMBER; \n" +
                "   BEGIN \n" +
                "      SELECT departments_seq.NEXTVAL \n" +
                "         INTO new_deptno \n" +
                "         FROM dual; \n" +
                "      INSERT INTO departments \n" +
                "         VALUES (new_deptno, 'department name', 100, 1700); \n" +
                "      tot_depts := tot_depts + 1; \n" +
                "      RETURN(new_deptno); \n" +
                "   END; \n" +
                "PROCEDURE remove_emp (employee_id NUMBER) IS \n" +
                "   BEGIN \n" +
                "      DELETE FROM employees \n" +
                "      WHERE employees.employee_id = remove_emp.employee_id; \n" +
                "      tot_emps := tot_emps - 1; \n" +
                "   END; \n" +
                "PROCEDURE remove_dept(department_id NUMBER) IS \n" +
                "   BEGIN \n" +
                "      DELETE FROM departments \n" +
                "      WHERE departments.department_id = remove_dept.department_id; \n" +
                "      tot_depts := tot_depts - 1; \n" +
                "      SELECT COUNT(*) INTO tot_emps FROM employees; \n" +
                "   END; \n" +
                "PROCEDURE increase_sal(employee_id NUMBER, salary_incr NUMBER) IS \n" +
                "   curr_sal NUMBER; \n" +
                "   BEGIN \n" +
                "      SELECT salary INTO curr_sal FROM employees \n" +
                "      WHERE employees.employee_id = increase_sal.employee_id; \n" +
                "      IF curr_sal IS NULL \n" +
                "         THEN RAISE no_sal; \n" +
                "      ELSE \n" +
                "         UPDATE employees \n" +
                "         SET salary = salary + salary_incr \n" +
                "         WHERE employee_id = employee_id; \n" +
                "      END IF; \n" +
                "   END; \n" +
                "PROCEDURE increase_comm(employee_id NUMBER, comm_incr NUMBER) IS \n" +
                "   curr_comm NUMBER; \n" +
                "   BEGIN \n" +
                "      SELECT commission_pct \n" +
                "      INTO curr_comm \n" +
                "      FROM employees \n" +
                "      WHERE employees.employee_id = increase_comm.employee_id; \n" +
                "      IF curr_comm IS NULL \n" +
                "         THEN RAISE no_comm; \n" +
                "      ELSE \n" +
                "         UPDATE employees \n" +
                "         SET commission_pct = commission_pct + comm_incr; \n" +
                "      END IF; \n" +
                "   END; \n" +
                "END emp_mgmt;  ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE PACKAGE BODY emp_mgmt\n" +
                        "BEGIN\n" +
                        "\ttot_emps NUMBER;\n" +
                        "\ttot_depts NUMBER;\n" +
                        "\tFUNCTION hire (\n" +
                        "\t\tlast_name VARCHAR2, \n" +
                        "\t\tjob_id VARCHAR2, \n" +
                        "\t\tmanager_id NUMBER, \n" +
                        "\t\tsalary NUMBER, \n" +
                        "\t\tcommission_pct NUMBER, \n" +
                        "\t\tdepartment_id NUMBER\n" +
                        "\t)\n" +
                        "\tRETURN NUMBER\n" +
                        "\tIS\n" +
                        "\tnew_empno NUMBER;\n" +
                        "\tBEGIN\n" +
                        "\t\tSELECT employees_seq.NEXTVAL\n" +
                        "\t\tINTO new_empno\n" +
                        "\t\tFROM DUAL;\n" +
                        "\t\tINSERT INTO employees\n" +
                        "\t\tVALUES (new_empno, 'First', 'Last', 'first.last@oracle.com', '(123)123-1234'\n" +
                        "\t\t\t, '18-JUN-02', 'IT_PROG', 90000000, 0, 100\n" +
                        "\t\t\t, 110);\n" +
                        "\t\ttot_emps := tot_emps + 1;\n" +
                        "\t\tRETURN new_empno;\n" +
                        "\tEND;\n" +
                        "\tFUNCTION create_dept (\n" +
                        "\t\tdepartment_id NUMBER, \n" +
                        "\t\tlocation_id NUMBER\n" +
                        "\t)\n" +
                        "\tRETURN NUMBER\n" +
                        "\tIS\n" +
                        "\tnew_deptno NUMBER;\n" +
                        "\tBEGIN\n" +
                        "\t\tSELECT departments_seq.NEXTVAL\n" +
                        "\t\tINTO new_deptno\n" +
                        "\t\tFROM dual;\n" +
                        "\t\tINSERT INTO departments\n" +
                        "\t\tVALUES (new_deptno, 'department name', 100, 1700);\n" +
                        "\t\ttot_depts := tot_depts + 1;\n" +
                        "\t\tRETURN new_deptno;\n" +
                        "\tEND;\n" +
                        "\tPROCEDURE remove_emp (\n" +
                        "\t\temployee_id NUMBER\n" +
                        "\t)\n" +
                        "\tIS\n" +
                        "\tBEGIN\n" +
                        "\t\tDELETE FROM employees\n" +
                        "\t\tWHERE employees.employee_id = remove_emp.employee_id;\n" +
                        "\t\ttot_emps := tot_emps - 1;\n" +
                        "\tEND;\n" +
                        "\tPROCEDURE remove_dept (\n" +
                        "\t\tdepartment_id NUMBER\n" +
                        "\t)\n" +
                        "\tIS\n" +
                        "\tBEGIN\n" +
                        "\t\tDELETE FROM departments\n" +
                        "\t\tWHERE departments.department_id = remove_dept.department_id;\n" +
                        "\t\ttot_depts := tot_depts - 1;\n" +
                        "\t\tSELECT COUNT(*)\n" +
                        "\t\tINTO tot_emps\n" +
                        "\t\tFROM employees;\n" +
                        "\tEND;\n" +
                        "\tPROCEDURE increase_sal (\n" +
                        "\t\temployee_id NUMBER, \n" +
                        "\t\tsalary_incr NUMBER\n" +
                        "\t)\n" +
                        "\tIS\n" +
                        "\tcurr_sal NUMBER;\n" +
                        "\tBEGIN\n" +
                        "\t\tSELECT salary\n" +
                        "\t\tINTO curr_sal\n" +
                        "\t\tFROM employees\n" +
                        "\t\tWHERE employees.employee_id = increase_sal.employee_id;\n" +
                        "\t\tIF curr_sal IS NULL THEN\n" +
                        "\t\t\tRAISE no_sal;\n" +
                        "\t\tELSE\n" +
                        "\t\t\tUPDATE employees\n" +
                        "\t\t\tSET salary = salary + salary_incr\n" +
                        "\t\t\tWHERE employee_id = employee_id;\n" +
                        "\t\tEND IF;\n" +
                        "\tEND;\n" +
                        "\tPROCEDURE increase_comm (\n" +
                        "\t\temployee_id NUMBER, \n" +
                        "\t\tcomm_incr NUMBER\n" +
                        "\t)\n" +
                        "\tIS\n" +
                        "\tcurr_comm NUMBER;\n" +
                        "\tBEGIN\n" +
                        "\t\tSELECT commission_pct\n" +
                        "\t\tINTO curr_comm\n" +
                        "\t\tFROM employees\n" +
                        "\t\tWHERE employees.employee_id = increase_comm.employee_id;\n" +
                        "\t\tIF curr_comm IS NULL THEN\n" +
                        "\t\t\tRAISE no_comm;\n" +
                        "\t\tELSE\n" +
                        "\t\t\tUPDATE employees\n" +
                        "\t\t\tSET commission_pct = commission_pct + comm_incr;\n" +
                        "\t\tEND IF;\n" +
                        "\tEND;\n" +
                        "END emp_mgmt;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertEquals(5, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("employees", "employee_id"));
        Assert.assertTrue(visitor.containsColumn("employees", "*"));
        Assert.assertTrue(visitor.containsColumn("departments", "department_id"));
        Assert.assertTrue(visitor.containsColumn("employees", "salary"));
        Assert.assertTrue(visitor.containsColumn("employees", "commission_pct"));
    }
}
