package com.alibaba.druid.sql.dialect.postgresql.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public class PGWithClause extends PGSQLObjectImpl {
	private static final long serialVersionUID = 1L;
	private boolean recursive = false;
	private List<PGWithQuery> withQuery = new ArrayList<PGWithQuery>(2);

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public List<PGWithQuery> getWithQuery() {
		return withQuery;
	}

	public void setWithQuery(List<PGWithQuery> withQuery) {
		this.withQuery = withQuery;
	}

	@Override
	public void accept0(PGASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, withQuery);
		}
		visitor.endVisit(this);
	}
}