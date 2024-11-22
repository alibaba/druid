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
package com.alibaba.druid.bvt.sql.oracle.pl;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class Oracle_pl_bulk_collect_0 extends OracleTest {
    public void test_0() throws Exception {
        String sql = "select a.employee_id,a.first_name,a.hire_date,a.salary bulk collect\n" +
                "    into v_table_emp\n" +
                "    from hr.employees a\n";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, statementList.size());
        SQLStatement stmt= statementList.get(0);
        {
            String output = SQLUtils.toOracleString(stmt);
            assertEquals("SELECT a.employee_id, a.first_name, a.hire_date, a.salary\n" +
                            "BULK COLLECT \n" +
                            "INTO v_table_emp\n" +
                            "FROM hr.employees a", //
                    output);
        }
    }

    public void test_1() throws Exception {
        String sql = "DECLARE\n" +
                "\tTYPE EmpCurTyp IS REF CURSOR;\n" +
                "\tTYPE NumList IS TABLE OF NUMBER;\n" +
                "\tTYPE NameList IS TABLE OF VARCHAR2(15);\n" +
                "\temp_cv EmpCurTyp;\n" +
                "\tempnos NumList;\n" +
                "\tenames NameList;\n" +
                "\tsals NumList;\n" +
                "BEGIN\n" +
                "\tOPEN emp_cv FOR \n" +
                "\t\tSELECT empno, ename\n" +
                "\t\tFROM emp;\n" +
                "\tFETCH emp_cv BULK COLLECT INTO empnos, enames;\n" +
                "\tCLOSE emp_cv;\n" +
                "\tEXECUTE IMMEDIATE 'SELECT sal FROM emp' BULK COLLECT INTO sals;\n" +
                "END;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, statementList.size());
        SQLStatement stmt= statementList.get(0);
        {
            String output = SQLUtils.toOracleString(stmt);
            assertEquals("DECLARE\n" +
                            "\tTYPE EmpCurTyp IS REF CURSOR;\n" +
                            "\tTYPE NumList IS TABLE OF NUMBER;\n" +
                            "\tTYPE NameList IS TABLE OF VARCHAR2(15);\n" +
                            "\temp_cv EmpCurTyp;\n" +
                            "\tempnos NumList;\n" +
                            "\tenames NameList;\n" +
                            "\tsals NumList;\n" +
                            "BEGIN\n" +
                            "\tOPEN emp_cv FOR \n" +
                            "\t\tSELECT empno, ename\n" +
                            "\t\tFROM emp;\n" +
                            "\tFETCH emp_cv BULK COLLECT INTO empnos, enames;\n" +
                            "\tCLOSE emp_cv;\n" +
                            "\tEXECUTE IMMEDIATE 'SELECT sal FROM emp' BULK COLLECT INTO sals;\n" +
                            "END;", //
                    output);
        }

    }
}
