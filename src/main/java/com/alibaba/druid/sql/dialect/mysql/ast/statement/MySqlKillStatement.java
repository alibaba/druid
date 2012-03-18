package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlKillStatement extends MySqlStatementImpl {
	private static final long serialVersionUID = 1L;
	private Type type;
	private SQLExpr threadId;

	public static enum Type {
		CONNECTION, QUERY
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public SQLExpr getThreadId() {
		return threadId;
	}

	public void setThreadId(SQLExpr threadId) {
		this.threadId = threadId;
	}

	public void accept0(MySqlASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, threadId);
		}
	}
}
