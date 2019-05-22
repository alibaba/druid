package com.alibaba.druid.sql.dialect.hive.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class HiveShowTablesStatement extends SQLStatementImpl {

	protected SQLExpr database;
	protected SQLExpr like;

	public HiveShowTablesStatement() {
		dbType = JdbcConstants.HIVE;
	}

	public SQLExpr getDatabase() {
		return database;
	}

	public void setDatabase(SQLExpr database) {
		this.database = database;
	}

	public SQLExpr getLike() {
		return like;
	}

	public void setLike(SQLExpr like) {
		this.like = like;
	}

	@Override
	protected void accept0(SQLASTVisitor visitor) {
		if (visitor instanceof HiveASTVisitor) {
			accept0((HiveASTVisitor) visitor);
		} else {
			acceptChild(visitor, database);
			acceptChild(visitor, like);
		}
	}

	public void accept0(HiveASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, database);
			acceptChild(visitor, like);
		}
		visitor.endVisit(this);
	}
}
