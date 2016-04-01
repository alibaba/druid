package com.alibaba.druid.bvt.sql.teradata;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.TeradataTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.teradata.ast.stmt.TeradataInsertStatement;
import com.alibaba.druid.sql.dialect.teradata.ast.stmt.TeradataSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.parser.TeradataStatementParser;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;


public class TeradataInsertTest_1 extends TeradataTest {
	public void test_values() throws Exception {
        String ins_sql = "INSERT INTO t1(c1, c2) VALUES "
        		+ "('a', 'b'),"
                + "('c', 'd'),"
                + "('e', 'f')";
        
		TeradataStatementParser parser = new TeradataStatementParser(ins_sql);
		List<SQLStatement> statementList = parser.parseStatementList();
		SQLStatement stmt = statementList.get(0);
		
		Assert.assertTrue(stmt instanceof SQLInsertStatement);
		
		TeradataInsertStatement insertStmt = (TeradataInsertStatement) stmt;
		Assert.assertEquals("t1", insertStmt.getTableSource().toString());
		Assert.assertEquals("c1", insertStmt.getColumns().get(0).toString());
		Assert.assertEquals("c2", insertStmt.getColumns().get(1).toString());
		Assert.assertEquals(3, insertStmt.getValuesList().size());
		Assert.assertEquals(2, insertStmt.getValuesList().get(0).getValues().size());
		
        SQLSelect insertQuery = insertStmt.getQuery();
        Assert.assertNull(insertQuery);
        
        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        insertStmt.accept(visitor);
        System.out.println(visitor.getColumns());
        System.out.println(visitor.getTables());
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
	}
	
	public void test_select() throws Exception {
		String ins_sql = "INSERT INTO d1.t1(col1, col2) "
				+ "SELECT c1, c2 "
				+ "FROM d2.t2 "
				+ "WHERE c1=0;";
		TeradataStatementParser parser = new TeradataStatementParser(ins_sql);
		List<SQLStatement> statementList = parser.parseStatementList();
		SQLStatement stmt = statementList.get(0);
		
		Assert.assertTrue(stmt instanceof SQLInsertStatement);
			
		TeradataInsertStatement insertStmt = (TeradataInsertStatement) stmt;
		SQLSelect insertQuery = insertStmt.getQuery();
		Assert.assertNotNull(insertQuery);
		
		// retrieve SELECT sub-clause from INSERT query
		TeradataSelectQueryBlock insertBlock = (TeradataSelectQueryBlock) insertQuery.getQuery();
		Assert.assertNotNull(insertBlock);
		
		// target table
		Assert.assertEquals("d1.t1", insertStmt.getTableSource().toString());
		Assert.assertEquals(2, insertStmt.getColumns().size());
		Assert.assertEquals("col1", insertStmt.getColumns().get(0).toString());
		Assert.assertEquals("col2", insertStmt.getColumns().get(1).toString());
		// source table
		Assert.assertEquals("d2.t2", insertBlock.getFrom().toString());
		Assert.assertEquals(2, insertBlock.getSelectList().size());
		Assert.assertEquals("c1", insertBlock.getSelectList().get(0).toString());
		Assert.assertEquals("c2", insertBlock.getSelectList().get(1).toString());
        
        System.out.println("***********below is useful info **********");
        System.out.println("target table: " + insertStmt.getTableSource());
        System.out.println("target columns: " + insertStmt.getColumns());
        System.out.println("source from: " + insertBlock.getFrom());
        System.out.println("source columns : " + insertBlock.getSelectList());
        System.out.println("source condition: " + insertBlock.getWhere());
        System.out.println("********");
		
        TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        System.out.println("------ end of " + new Object(){}.getClass().getEnclosingMethod().getName() + " ------");
	}
}
