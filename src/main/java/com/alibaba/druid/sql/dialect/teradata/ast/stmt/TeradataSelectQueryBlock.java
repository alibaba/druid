package com.alibaba.druid.sql.dialect.teradata.ast.stmt;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TeradataSelectQueryBlock extends SQLSelectQueryBlock {
	private SQLOrderBy    orderBy;
	private SQLExpr qualifyClause;
	
	public TeradataSelectQueryBlock() {
		
	}
	
	@Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof TeradataASTVisitor) {
            accept0((TeradataASTVisitor) visitor);
            return;
        }

        super.accept0(visitor);
    }

    protected void accept0(TeradataASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.into);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
            acceptChild(visitor, this.qualifyClause);
            acceptChild(visitor, this.orderBy);
        }
        visitor.endVisit(this);
    }
    
    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        this.orderBy = orderBy;
    }
    
    public SQLExpr getQualifyClause() {
    	return qualifyClause;
    }
    
    public void setQualifyClause(SQLExpr qualify) {
    	if (qualify != null) {
    		qualify.setParent(this);
    	}
    	this.qualifyClause = qualify;
    }
    
    public String toString() {
    	return SQLUtils.toTeradataString(this);
    }
}
