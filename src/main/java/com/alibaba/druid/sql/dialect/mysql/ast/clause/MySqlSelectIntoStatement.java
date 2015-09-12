package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.ast.SQLExpr;
/**
 * MySql procedure select into statement
 * @author sei.zz
 *
 */
public class MySqlSelectIntoStatement extends MySqlStatementImpl{

	//select statement
	private SQLSelect select;
	//var list
	private List<SQLExpr> varList=new ArrayList<SQLExpr>();
	
	public SQLSelect getSelect() {
		return select;
	}

	public void setSelect(SQLSelect select) {
		this.select = select;
	}

	public List<SQLExpr> getVarList() {
		return varList;
	}

	public void setVarList(List<SQLExpr> varList) {
		this.varList = varList;
	}

	
	
	@Override
	public void accept0(MySqlASTVisitor visitor) {
		// TODO Auto-generated method stub
		if (visitor.visit(this)) {
            acceptChild(visitor, select);
            acceptChild(visitor, varList);
        }
        visitor.endVisit(this);
	}

}
