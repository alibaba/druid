package com.alibaba.druid.sql.dialect.hive.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HiveDistributeByItem extends SQLObjectImpl implements SQLReplaceable {
	protected SQLExpr expr;

	public HiveDistributeByItem() {

	}

	public HiveDistributeByItem(SQLExpr expr) {
		this.setExpr(expr);
	}

	public SQLExpr getExpr() {
		return this.expr;
	}

	public void setExpr(SQLExpr expr) {
		if (expr != null) {
			expr.setParent(this);
		}
		this.expr = expr;
	}

	protected void accept0(SQLASTVisitor visitor) {
		if (visitor instanceof HiveASTVisitor) {
			accept0((HiveASTVisitor) visitor);
		}
	}

	protected void accept0(HiveASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, this.expr);
		}
		visitor.endVisit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HiveDistributeByItem other = (HiveDistributeByItem) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		return true;
	}

	@Override
	public boolean replace(SQLExpr expr, SQLExpr target) {
		if (this.expr == expr) {
			this.setExpr(target);
			return true;
		}
		return false;
	}

	public HiveDistributeByItem clone() {
		HiveDistributeByItem x = new HiveDistributeByItem();
		if (expr != null) {
			x.setExpr(expr.clone());
		}
		return x;
	}
}
