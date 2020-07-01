/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLIfStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
/**
 * 
 * @author zz [455910092@qq.com]
 */
public class MySqlCaseStatement extends MySqlStatementImpl{

	//case expr
	private SQLExpr            		  condition;
	//when statement list
	private List<MySqlWhenStatement> whenList = new ArrayList<MySqlCaseStatement.MySqlWhenStatement>();
	//else statement
	private SQLIfStatement.Else        elseItem;
	
	public SQLExpr getCondition() {
		return condition;
	}

	public void setCondition(SQLExpr condition) {
		this.condition = condition;
	}

	public List<MySqlWhenStatement> getWhenList() {
		return whenList;
	}

	public void setWhenList(List<MySqlWhenStatement> whenList) {
		this.whenList = whenList;
	}
	
	public void addWhenStatement(MySqlWhenStatement stmt)
	{
		this.whenList.add(stmt);
	}

	public SQLIfStatement.Else getElseItem() {
		return elseItem;
	}

	public void setElseItem(SQLIfStatement.Else elseItem) {
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

	@Override
	public List<SQLObject> getChildren() {
		List<SQLObject> children = new ArrayList<SQLObject>();
		children.addAll(children);
		children.addAll(whenList);
		children.addAll(whenList);
		if (elseItem != null) {
			children.add(elseItem);
		}
		return children;
	}

	/**
	 * case when statement
	 * @author zz
	 *
	 */
	public static class MySqlWhenStatement extends MySqlObjectImpl {

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
