package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleBlockTest9 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DECLARE" + //
                     "  my_emp_id NUMBER(6);" + //
                     "  my_job_id VARCHAR2(10);" + //
                     "  my_sal    NUMBER(8,2);" + //
                     "  CURSOR c1 IS" + //
                     "    SELECT employee_id, job_id, salary" + //
                     "    FROM employees FOR UPDATE;" + //
                     "BEGIN" + //
                     "  OPEN c1;" + //
                     "  LOOP" + //
                     "    FETCH c1 INTO my_emp_id, my_job_id, my_sal;" + //
                     "    IF my_job_id = 'SA_REP' THEN" + //
                     "      UPDATE employees" + //
                     "      SET salary = salary * 1.02" + //
                     "      WHERE CURRENT OF c1;" + //
                     "    END IF;" + //
                     "    EXIT WHEN c1%NOTFOUND;" + //
                     "  END LOOP;" + //
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

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));

        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "job_id")));
    }
}
