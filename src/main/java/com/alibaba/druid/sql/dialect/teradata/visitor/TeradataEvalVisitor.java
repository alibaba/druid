/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.teradata.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.sql.visitor.functions.Function;

public class TeradataEvalVisitor extends TeradataASTVisitorAdapter implements SQLEvalVisitor {
	private Map<String, Function> functions        = new HashMap<String, Function>();
    private List<Object>          parameters       = new ArrayList<Object>();

    private int                   variantIndex     = -1;

    private boolean               markVariantIndex = true;
    
    public TeradataEvalVisitor() {
    	this(new ArrayList<Object>(1));
    }
    
	public TeradataEvalVisitor(List<Object> parameters) {
		this.parameters = parameters;
	}

	@Override
	public Function getFunction(String funcName) {
		return functions.get(funcName);
	}

	@Override
	public void registerFunction(String funcName, Function function) {
		functions.put(funcName, function);
	}

	@Override
	public void unregisterFunction(String funcName) {
		functions.remove(funcName);
	}

	@Override
	public List<Object> getParameters() {
		return parameters;
	}

	@Override
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}

	@Override
	public int incrementAndGetVariantIndex() {
		return ++variantIndex;
	}

	@Override
	public boolean isMarkVariantIndex() {
		return markVariantIndex;
	}

	@Override
	public void setMarkVariantIndex(boolean markVariantIndex) {
		this.markVariantIndex = markVariantIndex;
	}
	
    public boolean visit(SQLBinaryOpExpr x) {
        return SQLEvalVisitorUtils.visit(this, x);
    }

}
