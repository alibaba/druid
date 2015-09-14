package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * 
 * @Description: MySql procedure while statement
 * @author zz email:455910092@qq.com
 * @date 2015-9-14
 * @version V1.0
 */
public class MySqlWhileStatement extends MySqlStatementImpl {
	
	//while expr
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

    public List<SQLStatement> getStatements() {
        return statements;
    }

    public void setStatements(List<SQLStatement> statements) {
        this.statements = statements;
    }
    public SQLExpr getCondition() {
		return condition;
	}

	public void setCondition(SQLExpr condition) {
		this.condition = condition;
	}
}
