package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleBlockTest13 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE" + //
                     "  emp_id        employees.employee_id%TYPE;" + //
                     "  emp_lastname  employees.last_name%TYPE;" + //
                     "  emp_salary    employees.salary%TYPE;" + //
                     "" + //
                     "BEGIN" + //
                     "  SELECT employee_id, last_name, salary" + //
                     "  INTO emp_id, emp_lastname, emp_salary " + //
                     "  FROM employees" + //
                     "  WHERE employee_id = 120;" + //
                     " " + //
                     "  UPDATE emp_name" + //
                     "  SET salary = salary * 1.1" + //
                     "  WHERE employee_id = emp_id;" + //
                     "" + //
                     "  DELETE FROM emp_name" + //
                     "  WHERE employee_id = 130;" + //
                     "" + //
                     "  SAVEPOINT do_insert;" + //
                     " " + //
                     "  INSERT INTO emp_name (employee_id, last_name, salary)" + //
                     "  VALUES (emp_id, emp_lastname, emp_salary);" + //
                     " " + //
                     "EXCEPTION" + //
                     "  WHEN DUP_VAL_ON_INDEX THEN" + //
                     "    ROLLBACK TO do_insert;" + //
                     "  DBMS_OUTPUT.PUT_LINE('Insert was rolled back');" + //
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

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp_name")));

        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(3, visitor.getConditions().size());
        Assert.assertEquals(1, visitor.getRelationships().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
    }
}
