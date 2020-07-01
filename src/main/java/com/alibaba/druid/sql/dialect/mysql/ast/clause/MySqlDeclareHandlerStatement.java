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

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
/**
 * 
 * @author zhujun [455910092@qq.com]
 */
public class MySqlDeclareHandlerStatement extends MySqlStatementImpl{
	
	//DECLARE handler_type HANDLER FOR condition_value[,...] sp_statement
	
	//handler type
	private MySqlHandlerType handleType; 
	//sp statement
	private SQLStatement spStatement;
	
	private List<ConditionValue> conditionValues;
	
	
	public MySqlDeclareHandlerStatement() {
		conditionValues = new ArrayList<ConditionValue>();
	}

	public List<ConditionValue> getConditionValues() {
		return conditionValues;
	}

	public void setConditionValues(List<ConditionValue> conditionValues) {
		this.conditionValues = conditionValues;
	}

	public MySqlHandlerType getHandleType() {
		return handleType;
	}

	public void setHandleType(MySqlHandlerType handleType) {
		this.handleType = handleType;
	}

	public SQLStatement getSpStatement() {
		return spStatement;
	}

	public void setSpStatement(SQLStatement spStatement) {
		this.spStatement = spStatement;
	}

	@Override
	public void accept0(MySqlASTVisitor visitor) {
		if (visitor.visit(this)) {
			acceptChild(visitor, spStatement);
		}
		visitor.endVisit(this);
	}

}

