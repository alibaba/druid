package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
/**
 * 
 * @Description: MySql procedure Case statement
 * @author zz email:455910092@qq.com
 * @version V1.0
 */
public class MySqlCaseStatement extends MySqlStatementImpl{

	//case expr
	private SQLExpr            		  condition;
	//when statement list
	private List<MySqlWhenfStatement> whenList=new ArrayList<MySqlCaseStatement.MySqlWhenfStatement>();
	//else statement
	private MySqlElseStatement        elseItem;
	
	public SQLExpr getCondition() {
		return condition;
	}

	public void setCondition(SQLExpr condition) {
		this.condition = condition;
	}

	public List<MySqlWhenfStatement> getWhenList() {
		return whenList;
	}

	public void setWhenList(List<MySqlWhenfStatement> whenList) {
		this.whenList = whenList;
	}
	
	public void addWhenStatement(MySqlWhenfStatement stmt)
	{
		this.whenList.add(stmt);
	}

	public MySqlElseStatement getElseItem() {
		return elseItem;
	}

	public void setElseItem(MySqlElseStatement elseItem) {
		this.elseItem = elseItem;
	}

	@Override
	public void accept0(MySqlASTVisitor visitor) {
		// TODO Auto-generated method stub
		if (visitor.visit(this)) {
            acceptChild(visitor, condition);
            acceptChild(visitor, whenList);
            acceptChild(visitor, elseItem);
        }
        visitor.endVisit(this);
	}
	
	/**
	 * case when statement
	 * @author zz
	 *
	 */
	public static class MySqlWhenfStatement extends MySqlObjectImpl {

        private SQLExpr            condition;
        private List<SQLStatement> statements = new ArrayList<SQLStatement>();

        @Override
        public void accept0(MySqlASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, condition);
                acceptChild(visitor, statements);
            }
            visitor.endVisit(this);
        }

        public SQLExpr getCondition() {
            return condition;
        }

        public void setCondition(SQLExpr condition) {
            this.condition = condition;
        }

        public List<SQLStatement> getStatements() {
            return statements;
        }

        public void setStatements(List<SQLStatement> statements) {
            this.statements = statements;
        }

    }

}
