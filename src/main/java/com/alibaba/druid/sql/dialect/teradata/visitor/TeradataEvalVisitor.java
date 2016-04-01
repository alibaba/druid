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
