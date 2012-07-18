package com.alibaba.druid.sql.dialect.postgresql.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGAnalytic;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PGAggregateExpr extends SQLAggregateExpr implements PGSQLObject {

	private PGAnalytic    over;
	
	public PGAnalytic getOver() {
		return over;
	}

	public void setOver(PGAnalytic over) {
		this.over = over;
	}

	private static final long serialVersionUID = 1L;

	public PGAggregateExpr(String methodName, Option option) {
		super(methodName, option);
	}

	public PGAggregateExpr(String methodName) {
		super(methodName);
	}
	
    public void output(StringBuffer buf) {
        buf.append(this.methodName);
        buf.append("(");
        int i = 0;
        for (int size = this.arguments.size(); i < size; ++i) {
            ((SQLExpr) this.arguments.get(i)).output(buf);
        }
        buf.append(")");

        if (this.over != null) {
            buf.append(" ");
            this.over.output(buf);
        }
    }

	@Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PGASTVisitor) {
            this.accept0((PGASTVisitor) visitor);
        } else {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.arguments);
                acceptChild(visitor, this.over);
            }
            visitor.endVisit(this);
        }
    }

    public void accept0(PGASTVisitor visitor) {
    	if (visitor.visit(this)) {
    		acceptChild(visitor, this.arguments);
    		acceptChild(visitor, this.over);
    	}
    	visitor.endVisit(this);
    }
}
