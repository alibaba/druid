package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleBlockTest7 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DROP TABLE employees_temp; " + //
                     "CREATE TABLE employees_temp AS " + //
                     "  SELECT employee_id, first_name, last_name " + //
                     "  FROM employees;" + //
                     " " + //
                     "DECLARE" + //
                     "  emp_id          employees_temp.employee_id%TYPE := 299;" + //
                     "  emp_first_name  employees_temp.first_name%TYPE  := 'Bob';" + //
                     "  emp_last_name   employees_temp.last_name%TYPE   := 'Henry';" + //
                     "BEGIN" + //
                     "  INSERT INTO employees_temp (employee_id, first_name, last_name) " + //
                     "  VALUES (emp_id, emp_first_name, emp_last_name);" + //
                     " " + //
                     "  UPDATE employees_temp" + //
                     "  SET first_name = 'Robert'" + //
                     "  WHERE employee_id = emp_id;" + //
                     " " + //
                     "  DELETE FROM employees_temp" + //
                     "  WHERE employee_id = emp_id" + //
                     "  RETURNING first_name, last_name" + //
                     "  INTO emp_first_name, emp_last_name;" + //
                     "" + //
                     "  COMMIT;" + //
                     "  DBMS_OUTPUT.PUT_LINE (emp_first_name || ' ' || emp_last_name);" + //
                     "END;"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);

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
         Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees_temp")));

        Assert.assertEquals(6, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());

         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
    }
}
