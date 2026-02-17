package com.alibaba.druid.bvt.sql.ast;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;

import static org.junit.Assert.assertEquals;

public class SQLInsertStatementTest {
	@Test
	public void test_0() throws Exception {
		SQLInsertStatement stmt = new SQLInsertStatement();
		stmt.setTableSource(new SQLExprTableSource("my_table"));

		// Add columns
		stmt.addColumn(new SQLIdentifierExpr("id"));
		stmt.addColumn(new SQLIdentifierExpr("name"));

		// Add values
		List<SQLExpr> values = new ArrayList<>();
		SQLExpr value1 = new SQLIdentifierExpr("1");
		SQLExpr value2 = new SQLIdentifierExpr("'abc'");
		values.add(value1);
		values.add(value2);
		SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause(values);
		stmt.addValueCause(valuesClause);
		assertEquals(value1.getParent(), valuesClause);
		assertEquals(value2.getParent(), valuesClause);

		// clone
		SQLInsertStatement clone = stmt.clone();
		assertEquals(value1.getParent(), valuesClause);
		assertEquals(value2.getParent(), valuesClause);

		assertEquals(stmt.toString(), clone.toString());
	}
}
