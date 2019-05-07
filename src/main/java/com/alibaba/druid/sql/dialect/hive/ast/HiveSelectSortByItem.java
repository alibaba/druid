package com.alibaba.druid.sql.dialect.hive.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HiveSelectSortByItem extends SQLObjectImpl implements SQLReplaceable {

	protected SQLExpr expr;
	protected SQLOrderingSpecification type;

	public HiveSelectSortByItem() {

	}

	public HiveSelectSortByItem(SQLExpr expr) {
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

	public SQLOrderingSpecification getType() {
		return this.type;
	}

	public void setType(SQLOrderingSpecification type) {
		this.type = type;
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
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		HiveSelectSortByItem other = (HiveSelectSortByItem) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (type != other.type)
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

	public HiveSelectSortByItem clone() {
		HiveSelectSortByItem x = new HiveSelectSortByItem();
		if (expr != null) {
			x.setExpr(expr.clone());
		}
		x.type = type;
		return x;
	}
}
