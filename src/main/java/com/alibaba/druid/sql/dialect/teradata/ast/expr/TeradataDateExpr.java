package com.alibaba.druid.sql.dialect.teradata.ast.expr;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TeradataDateExpr extends SQLExprImpl implements SQLLiteralExpr, TeradataExpr{
	
	private String           literal;
	private TeradataDateType type;
	
	public TeradataDateExpr() {
		
	}
	
	public TeradataDateType getType() {
		return this.type;
	}
	
	public void setType(TeradataDateType type) {
		this.type = type;
	}
	
	public String getLiteral() {
		return literal;
	}
	
	public void setLiteral(String literal) {
		this.literal = literal;
	}
	
	@Override
	protected void accept0(SQLASTVisitor visitor) {
		// TODO Auto-generated method stub
		this.accept0((TeradataASTVisitor) visitor);
	}
	
	public void accept0(TeradataASTVisitor visitor) {
		visitor.visit(this);
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
        TeradataDateExpr other = (TeradataDateExpr) obj;
        if (literal == null) {
            if (other.literal != null) {
                return false;
            }
        } else if (!literal.equals(other.literal)) {
            return false;
        }
        
        if (type != other.type) {
        	return false;
        }
        return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
        result = prime * result + ((literal == null) ? 0 : literal.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
	}
}
