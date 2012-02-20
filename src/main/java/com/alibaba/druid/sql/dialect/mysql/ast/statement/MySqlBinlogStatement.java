package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlBinlogStatement extends MySqlStatementImpl {
	private static final long serialVersionUID = 1L;

	private SQLExpr expr;

	public SQLExpr getExpr() {
		return expr;
	}

	public void setExpr(SQLExpr expr) {
		this.expr = expr;
	}

	protected void accept0(MySqlASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, expr);
		}
	}
}
