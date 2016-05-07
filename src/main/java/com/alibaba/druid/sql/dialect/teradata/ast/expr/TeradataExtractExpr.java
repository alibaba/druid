package com.alibaba.druid.sql.dialect.teradata.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TeradataExtractExpr extends SQLExprImpl implements TeradataExpr{

	private TeradataDateTimeUnit unit;
	private SQLExpr              from;
	
	public TeradataExtractExpr() {
		
	}
	
	public TeradataDateTimeUnit getUnit() {
		return this.unit;
	}
	
	public void setUnit(TeradataDateTimeUnit unit) {
		this.unit = unit;
	}
	
	public SQLExpr getFrom() {
		return this.from;
	}
	
	public void setFrom(SQLExpr from) {
		this.from = from;
	}
	
	@Override
	protected void accept0(SQLASTVisitor visitor) {
		this.accept0((TeradataASTVisitor) visitor);
	}
	
	public void accept0(TeradataASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, this.from);
		}
		
		visitor.endVisit(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        TeradataExtractExpr other = (TeradataExtractExpr) obj;
        if (from == null) {
            if (other.from != null) {
                return false;
            }
        } else if (!from.equals(other.from)) {
            return false;
        }
        if (unit != other.unit) {
            return false;
        }
        return true;        
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}
}
