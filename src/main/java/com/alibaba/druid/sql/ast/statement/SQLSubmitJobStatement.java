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

public class SQLSubmitJobStatement extends SQLStatementImpl {
	
	private boolean    await;
	private SQLStatement statment;

    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, statment);
        }
        visitor.endVisit(this);
    }

	@Override
	public List<SQLObject> getChildren() {
		ArrayList<SQLObject> children = new ArrayList<SQLObject>();
		children.add(statment);
		return children;
	}

	public boolean isAwait() {
		return await;
	}

	public void setAwait(boolean await) {
		this.await = await;
	}

	public SQLStatement getStatment() {
		return statment;
	}

	public void setStatment(SQLStatement statment) {
		this.statment = statment;
	}

	public SQLSubmitJobStatement clone() {
		SQLSubmitJobStatement x = new SQLSubmitJobStatement();

		if (statment != null) {
			x.setStatment(statment.clone());
		}
		return x;
	}
}
