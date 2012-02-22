package com.alibaba.druid.sql.dialect.postgresql.visitor;

import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.IntoClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WindowClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WithQuery;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class PGSchemaStatVisitor extends SchemaStatVisitor implements
		PGASTVisitor {

	@Override
	public void endVisit(WindowClause x) {

	}

	@Override
	public boolean visit(WindowClause x) {
		return true;
	}

	@Override
	public void endVisit(FetchClause x) {

	}

	@Override
	public boolean visit(FetchClause x) {
		return true;
	}

	@Override
	public void endVisit(ForClause x) {

	}

	@Override
	public boolean visit(ForClause x) {

		return true;
	}

	@Override
	public void endVisit(WithQuery x) {

	}

	@Override
	public boolean visit(WithQuery x) {

		return true;
	}

	@Override
	public void endVisit(WithClause x) {

	}

	@Override
	public boolean visit(WithClause x) {

		return true;
	}

	@Override
	public void endVisit(IntoClause x) {
		
	}

	@Override
	public boolean visit(IntoClause x) {
		String ident = x.getTable().toString();
		
		TableStat stat = tableStats.get(ident);
		if (stat == null) {
			stat = new TableStat();
			tableStats.put(new TableStat.Name(ident), stat);
		}
		stat.incrementInsertCount();
		return false;
	}

}
