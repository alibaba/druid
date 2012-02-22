package com.alibaba.druid.sql;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;

import junit.framework.TestCase;

public class PGTest extends TestCase {
	protected String output(List<SQLStatement> stmtList) {
		StringBuilder out = new StringBuilder();
		PGOutputVisitor visitor = new PGOutputVisitor(out);

		for (SQLStatement stmt : stmtList) {
			stmt.accept(visitor);
		}

		return out.toString();
	}
}
