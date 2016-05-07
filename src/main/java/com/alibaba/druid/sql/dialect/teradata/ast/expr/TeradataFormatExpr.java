package com.alibaba.druid.sql.dialect.teradata.ast.expr;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TeradataFormatExpr extends SQLExprImpl implements SQLLiteralExpr, SQLName, TeradataExpr{

	private String literal;
	private String name;
	
	public TeradataFormatExpr() {
		
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
        TeradataFormatExpr other = (TeradataFormatExpr) obj;
        if (literal == null) {
            if (other.literal != null) {
                return false;
            }
        } else if (!literal.equals(other.literal)) {
            return false;
        }
        return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
        result = prime * result + ((literal == null) ? 0 : literal.hashCode());
        return result;
	}

	@Override
	public String getSimpleName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public String toString() {
		return "FORMAT '" + literal +"'"; 
	}
}
