package com.alibaba.druid.sql.dialect.teradata.ast;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TeradataDateTimeDataType extends SQLDataTypeImpl implements TeradataObject {
	
	private boolean withTimeZone = false;
	
	public TeradataDateTimeDataType() {
		
	}
	
	public TeradataDateTimeDataType(String type) {
		super.setName(type);
	}
	
	
	protected void accept0(SQLASTVisitor visitor) {
		this.accept0((TeradataASTVisitor) visitor);
	}
	
	public void accept0(TeradataASTVisitor visitor) {
		if (visitor.visit(this)) {
            acceptChild(visitor, getArguments());
        }
        visitor.endVisit(this);
	}
	
	public boolean isWithTimeZone() {
		return this.withTimeZone;
	}
	
	public void setWithTimeZone(boolean withTimeZone) {
		this.withTimeZone = withTimeZone;
	}
	
}
