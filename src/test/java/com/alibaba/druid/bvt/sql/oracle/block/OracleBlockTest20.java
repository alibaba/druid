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

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleBlockTest20 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE\n" +
                "   sql_stmt    VARCHAR2(200);\n" +
                "   plsql_block VARCHAR2(500);\n" +
                "   emp_id      NUMBER(4) := 7566;\n" +
                "   salary      NUMBER(7,2);\n" +
                "   dept_id     NUMBER(2) := 50;\n" +
                "   dept_name   VARCHAR2(14) := 'PERSONNEL';\n" +
                "   location    VARCHAR2(13) := 'DALLAS';\n" +
                "   emp_rec     emp%ROWTYPE;\n" +
                "BEGIN\n" +
                "   EXECUTE IMMEDIATE 'CREATE TABLE bonus (id NUMBER, amt NUMBER)';\n" +
                "   sql_stmt := 'INSERT INTO dept VALUES (:1, :2, :3)';\n" +
                "   EXECUTE IMMEDIATE sql_stmt USING dept_id, dept_name, location;\n" +
                "   sql_stmt := 'SELECT * FROM emp WHERE empno = :id';\n" +
                "   EXECUTE IMMEDIATE sql_stmt INTO emp_rec USING emp_id;\n" +
                "   plsql_block := 'BEGIN emp_pkg.raise_salary(:id, :amt); END;';\n" +
                "   EXECUTE IMMEDIATE plsql_block USING 7788, 500;\n" +
                "   sql_stmt := 'UPDATE emp SET sal = 2000 WHERE empno = :1\n" +
                "      RETURNING sal INTO :2';\n" +
                "   EXECUTE IMMEDIATE sql_stmt USING emp_id RETURNING INTO salary;\n" +
                "   EXECUTE IMMEDIATE 'DELETE FROM dept WHERE deptno = :num'\n" +
                "      USING dept_id;\n" +
                "   EXECUTE IMMEDIATE 'ALTER SESSION SET SQL_TRACE TRUE';\n" +
                "END;"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, stmtList.size());

        String result = SQLUtils.toOracleString(stmtList.get(0));
        System.out.println(result);
        assertEquals("DECLARE\n" +
                "\tsql_stmt VARCHAR2(200);\n" +
                "\tplsql_block VARCHAR2(500);\n" +
                "\temp_id NUMBER(4) := 7566;\n" +
                "\tsalary NUMBER(7, 2);\n" +
                "\tdept_id NUMBER(2) := 50;\n" +
                "\tdept_name VARCHAR2(14) := 'PERSONNEL';\n" +
                "\tlocation VARCHAR2(13) := 'DALLAS';\n" +
                "\temp_rec emp%ROWTYPE;\n" +
                "BEGIN\n" +
                "\tEXECUTE IMMEDIATE 'CREATE TABLE bonus (id NUMBER, amt NUMBER)';\n" +
                "\tsql_stmt := 'INSERT INTO dept VALUES (:1, :2, :3)';\n" +
                "\tEXECUTE IMMEDIATE sql_stmt USING dept_id, dept_name, location;\n" +
                "\tsql_stmt := 'SELECT * FROM emp WHERE empno = :id';\n" +
                "\tEXECUTE IMMEDIATE sql_stmt INTO emp_rec USING emp_id;\n" +
                "\tplsql_block := 'BEGIN emp_pkg.raise_salary(:id, :amt); END;';\n" +
                "\tEXECUTE IMMEDIATE plsql_block USING 7788, 500;\n" +
                "\tsql_stmt := 'UPDATE emp SET sal = 2000 WHERE empno = :1\n" +
                "      RETURNING sal INTO :2';\n" +
                "\tEXECUTE IMMEDIATE sql_stmt USING emp_id RETURNNING INTO salary;\n" +
                "\tEXECUTE IMMEDIATE 'DELETE FROM dept WHERE deptno = :num' USING dept_id;\n" +
                "\tEXECUTE IMMEDIATE 'ALTER SESSION SET SQL_TRACE TRUE';\n" +
                "END;", result);

        assertEquals(1, stmtList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        for (SQLStatement statement : stmtList) {
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(3, visitor.getTables().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("bonus")));

        assertEquals(6, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());

         assertTrue(visitor.getColumns().contains(new TableStat.Column("bonus", "id")));
    }
}
