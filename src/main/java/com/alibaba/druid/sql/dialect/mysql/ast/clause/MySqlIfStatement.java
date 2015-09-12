package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * MySql procedure if statement
 * @author sei.zz
 *
 */
public class MySqlIfStatement extends MySqlStatementImpl{
	 private SQLExpr            condition;
	 private List<SQLStatement> statements = new ArrayList<SQLStatement>();
	 private List<MySqlElseIfStatement>       elseIfList = new ArrayList<MySqlElseIfStatement>();
	 private MySqlElseStatement               elseItem;
	
	@Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
        	acceptChild(visitor, condition);
        	acceptChild(visitor, statements);
            acceptChild(visitor, elseIfList);
            acceptChild(visitor, elseItem);
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
	public List<MySqlElseIfStatement> getElseIfList() {
		return elseIfList;
	}
	public void setElseIfList(List<MySqlElseIfStatement> elseIfList) {
		this.elseIfList = elseIfList;
	}
	public MySqlElseStatement getElseItem() {
		return elseItem;
	}
	public void setElseItem(MySqlElseStatement elseItem) {
		this.elseItem = elseItem;
	}
	/**
	 * comment: else if语句
	 * @author sei.zz
	 *
	 */
	public static class MySqlElseIfStatement extends MySqlObjectImpl {

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
