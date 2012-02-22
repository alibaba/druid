package com.alibaba.druid.sql.dialect.postgresql.visitor;

import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WindowClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSelectQueryBlock.WithQuery;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class PGOutputVisitor extends SQLASTOutputVisitor implements
		PGASTVisitor {

	public PGOutputVisitor(Appendable appender) {
		super(appender);
	}

	@Override
	public void endVisit(WindowClause x) {

	}

	@Override
	public boolean visit(WindowClause x) {
		print("WINDOW ");
		x.getName().accept(this);
		print(" AS ");
		for (int i = 0; i < x.getDefinition().size(); ++i) {
			if (i != 0) {
				println(", ");
			}
			print("(");
			x.getDefinition().get(i).accept(this);
			print(")");
		}
		return false;
	}

	@Override
	public void endVisit(FetchClause x) {

	}

	@Override
	public boolean visit(FetchClause x) {
		print("FETCH ");
		if (FetchClause.Option.FIRST.equals(x.getOption())) {
			print("FIRST ");
		} else if (FetchClause.Option.NEXT.equals(x.getOption())) {
			print("NEXT ");
		}
		x.getCount().accept(this);
		print(" ROWS ONLY");
		return false;
	}

	@Override
	public void endVisit(ForClause x) {

	}

	@Override
	public boolean visit(ForClause x) {
		print("FOR ");
		if (ForClause.Option.UPDATE.equals(x.getOption())) {
			print("UPDATE ");
		} else if (ForClause.Option.SHARE.equals(x.getOption())) {
			print("SHARE ");
		}

		if (x.getOf().size() > 0) {
			for (int i = 0; i < x.getOf().size(); ++i) {
				if (i != 0) {
					println(", ");
				}
				x.getOf().get(i).accept(this);
			}
		}
		
		if (x.isNoWait()) {
			print(" NOWAIT");
		}

		return false;
	}

	@Override
	public void endVisit(WithQuery x) {

	}

	@Override
	public boolean visit(WithQuery x) {
		x.getName().accept(this);

		if (x.getColumns().size() > 0) {
			print(" (");
			printAndAccept(x.getColumns(), ", ");
			print(")");
		}
		println();
		print("AS");
		println();
		print("(");
		incrementIndent();
		println();
		x.getSubQuery().accept(this);
		decrementIndent();
		println();
		print(")");

		return false;
	}

	@Override
	public void endVisit(WithClause x) {

	}

	@Override
	public boolean visit(WithClause x) {
		print("WITH");
		incrementIndent();
		println();
		printlnAndAccept(x.getWithQuery(), ", ");
		decrementIndent();
		return false;
	}

	public boolean visit(SQLSelectQueryBlock x) {
		if (x instanceof PGSelectQueryBlock) {
			return visit((PGSelectQueryBlock) x);
		}

		return super.visit(x);
	}

	public boolean visit(PGSelectQueryBlock x) {
		if (x.getWith() != null) {
			x.getWith().accept(this);
			println();
		}

		print("SELECT ");

		if (SQLSetQuantifier.ALL == x.getDistionOption()) {
			print("ALL ");
		} else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
			print("DISTINCT ");

			if (x.getDistinctOn() != null) {
				print("ON ");
				printAndAccept(x.getDistinctOn(), ", ");
			}
		}

		printSelectList(x.getSelectList());

		if (x.getFrom() != null) {
			println();
			print("FROM ");
			x.getFrom().accept(this);
		}

		if (x.getWhere() != null) {
			println();
			print("WHERE ");
			x.getWhere().accept(this);
		}

		if (x.getGroupBy() != null) {
			print(" ");
			x.getGroupBy().accept(this);
		}

		if (x.getWindow() != null) {
			println();
			x.getWindow().accept(this);
		}

		if (x.getOrderBy() != null) {
			println();
			x.getOrderBy().accept(this);
		}

		if (x.getLimit() != null) {
			println();
			print("LIMIT ");
			x.getLimit().accept(this);
		}

		if (x.getOffset() != null) {
			println();
			print("OFFSET ");
			x.getOffset().accept(this);
			print(" ROWS");
		}

		if (x.getFetch() != null) {
			println();
			x.getFetch().accept(this);
		}

		if (x.getForClause() != null) {
			println();
			x.getForClause().accept(this);
		}

		return false;
	}
}
