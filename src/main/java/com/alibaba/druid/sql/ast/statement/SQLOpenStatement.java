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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zz [455910092@qq.com]
 */
public class SQLOpenStatement extends SQLStatementImpl{
	
	//cursor name
	private SQLName cursorName;

	private final List<SQLName> columns = new ArrayList<SQLName>();

	private SQLExpr forExpr;

	public SQLOpenStatement() {

	}
	
	public SQLName getCursorName() {
		return cursorName;
	}
	
	public void setCursorName(String cursorName) {
		setCursorName(new SQLIdentifierExpr(cursorName));
	}

	public void setCursorName(SQLName cursorName) {
		if (cursorName != null) {
			cursorName.setParent(this);
		}
		this.cursorName = cursorName;
	}

	@Override
	protected void accept0(SQLASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, cursorName);
			acceptChild(visitor, forExpr);
			acceptChild(visitor, columns);
		}
	    visitor.endVisit(this);
	}

	public SQLExpr getFor() {
		return forExpr;
	}

	public void setFor(SQLExpr forExpr) {
		if (forExpr != null) {
			forExpr.setParent(this);
		}
		this.forExpr = forExpr;
	}

	public List<SQLName> getColumns() {
		return columns;
	}
}
