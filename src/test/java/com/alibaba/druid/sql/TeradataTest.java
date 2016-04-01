package com.alibaba.druid.sql;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataOutputVisitor;

import junit.framework.TestCase;

public class TeradataTest extends TestCase{
	protected String output(List<SQLStatement> stmtList) {
		StringBuilder out = new StringBuilder();
		TeradataOutputVisitor visitor = new TeradataOutputVisitor(out);

		for (SQLStatement stmt : stmtList) {
			stmt.accept(visitor);
		}

		return out.toString();
	}
	
	   protected void print(List<SQLStatement> stmtList) {
	        String text = output(stmtList);
	        String outputProperty = System.getProperty("druid.output");
	        if ("false".equals(outputProperty)) {
	            return;
	        }
	        System.out.println(text);
	    }
}
