package com.alibaba.druid.sql.dialect.hive.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HiveClusterBy extends SQLObjectImpl {
	
	protected final List<HiveClusterByItem> items = new ArrayList<HiveClusterByItem>();

	public HiveClusterBy() {

	}

	public HiveClusterBy(SQLExpr expr) {
		HiveClusterByItem item = new HiveClusterByItem(expr);
		addItem(item);
	}

	public void addItem(HiveClusterByItem item) {
		if (item != null) {
			item.setParent(this);
		}
		this.items.add(item);
	}

	public List<HiveClusterByItem> getItems() {
		return this.items;
	}

	protected void accept0(SQLASTVisitor visitor) {
		if (visitor instanceof HiveASTVisitor) {
			accept0((HiveASTVisitor) visitor);
		}
	}

	protected void accept0(HiveASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, this.items);
		}
		visitor.endVisit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		HiveSortBy other = (HiveSortBy) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	public void addItem(SQLExpr expr) {
		HiveClusterByItem item = createItem();
		item.setExpr(expr);
		addItem(item);
	}

	protected HiveClusterByItem createItem() {
		return new HiveClusterByItem();
	}

	public HiveClusterBy clone() {
		HiveClusterBy x = new HiveClusterBy();
		for (HiveClusterByItem item : items) {
			HiveClusterByItem item1 = item.clone();
			item1.setParent(x);
			x.items.add(item1);
		}
		return x;
	}
}
