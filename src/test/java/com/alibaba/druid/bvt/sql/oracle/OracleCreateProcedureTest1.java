package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleCreateProcedureTest1 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE OR REPLACE PROCEDURE p (" +
        		"  dept_no NUMBER" +
        		") AS " +
        		"BEGIN" +
        		"  DELETE FROM dept_temp" +
        		"  WHERE department_id = dept_no;" +
        		" " +
        		"  IF SQL%FOUND THEN" +
        		"    DBMS_OUTPUT.PUT_LINE (" +
        		"      'Delete succeeded for department number ' || dept_no" +
        		"    );" +
        		"  ELSE" +
        		"    DBMS_OUTPUT.PUT_LINE ('No department number ' || dept_no);" +
        		"  END IF;" +
        		"END;" +
        		"/" +
        		"BEGIN" +
        		"  p(270);" +
        		"  p(400);" +
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

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("dept_temp")));

//        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());
        Assert.assertEquals(1, visitor.getRelationships().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
    }
}
