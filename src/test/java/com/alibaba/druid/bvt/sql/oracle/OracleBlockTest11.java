package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleBlockTest11 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DROP TABLE emp;" +
        		"CREATE TABLE emp AS SELECT * FROM employees;" +
        		" " +
        		"DECLARE" +
        		"  CURSOR c1 IS" +
        		"    SELECT * FROM emp" +
        		"    FOR UPDATE OF salary" +
        		"    ORDER BY employee_id;" +
        		"   emp_rec  emp%ROWTYPE;" +
        		"BEGIN" +
        		"  OPEN c1;" +
        		"  LOOP" +
        		"    FETCH c1 INTO emp_rec;  -- fails on second iteration\n" +
        		"    EXIT WHEN c1%NOTFOUND;" +
        		"    DBMS_OUTPUT.PUT_LINE (" +
        		"      'emp_rec.employee_id = ' ||" +
        		"      TO_CHAR(emp_rec.employee_id)" +
        		"    );" +
        		"    " +
        		"    UPDATE emp" +
        		"    SET salary = salary * 1.05" +
        		"    WHERE employee_id = 105;" +
        		" " +
        		"    COMMIT;  -- releases locks\n" +
        		"  END LOOP;" +
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
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("emp")));

        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());

//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
    }
}
