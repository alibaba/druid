package com.alibaba.druid.sql;

import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;

public class OracleTest extends TestCase {
	protected String output(List<SQLStatement> stmtList) {
		StringBuilder out = new StringBuilder();
		OracleOutputVisitor visitor = new OracleOutputVisitor(out);

		for (SQLStatement stmt : stmtList) {
			stmt.accept(visitor);
		}

		return out.toString();
	}
}
