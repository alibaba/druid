package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
/**
 * MySql declare statement
 * @author sei.zz
 *
 */
public class MySqlDeclareStatement extends MySqlStatementImpl{
	
	//var type
	private SQLDataType type; 
	//var list
	private List<SQLExpr> varList=new ArrayList<SQLExpr>();
	

	public List<SQLExpr> getVarList() {
		return varList;
	}
	public void addVar(SQLExpr expr)
	{
		varList.add(expr);
	}

	public void setVarList(List<SQLExpr> varList) {
		this.varList = varList;
	}

	public SQLDataType getType() {
		return type;
	}

	public void setType(SQLDataType type) {
		this.type = type;
	}

	@Override
	public void accept0(MySqlASTVisitor visitor) {
		// TODO Auto-generated method stub
		 if (visitor.visit(this)) {
			 acceptChild(visitor, type);
	         acceptChild(visitor, varList);
	        }
	        visitor.endVisit(this);
		
	}

}
