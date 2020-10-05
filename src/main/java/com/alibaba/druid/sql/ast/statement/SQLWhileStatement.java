/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zz [455910092@qq.com]
 */
public class SQLWhileStatement extends SQLStatementImpl implements SQLReplaceable {
	
	//while expr
	private SQLExpr            condition;
	private List<SQLStatement> statements = new ArrayList<SQLStatement>();
	//while label name
	private String labelName;
	
    
	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
        	acceptChild(visitor, condition);
            acceptChild(visitor, statements);
        }
        visitor.endVisit(this);
    }

	@Override
	public List<SQLObject> getChildren() {
		List<SQLObject> children = new ArrayList<SQLObject>();
		children.add(condition);
		children.addAll(this.statements);
		return children;
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

	public void setCondition(SQLExpr x) {
		if (x != null) {
			x.setParent(this);
		}

		this.condition = x;
	}

	public SQLWhileStatement clone() {
		SQLWhileStatement x = new SQLWhileStatement();

		if (condition != null) {
			x.setCondition(condition.clone());
		}
		for (SQLStatement stmt : statements) {
			SQLStatement stmt2 = stmt.clone();
			stmt2.setParent(x);
			x.statements.add(stmt2);
		}
		x.labelName = labelName;
		return x;
	}

	@Override
	public boolean replace(SQLExpr expr, SQLExpr target) {
		if (condition == expr) {
			setCondition(target);
			return true;
		}

		return false;
	}
}
