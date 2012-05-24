package com.alibaba.druid.sql.dialect.oracle.visitor;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;


public class OracleEvalVisitor extends OracleASTVisitorAdapter implements SQLEvalVisitor {
    private List<Object> parameters       = new ArrayList<Object>();

    private int          variantIndex     = -1;

    private boolean      markVariantIndex = true;

    public OracleEvalVisitor(){
        this(new ArrayList<Object>(1));
    }

    public OracleEvalVisitor(List<Object> parameters){
        this.parameters = parameters;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public boolean visit(SQLCharExpr x) {
        return SQLEvalVisitorUtils.visit(this, x);
    }

    public int incrementAndGetVariantIndex() {
        return ++variantIndex;
    }

    public int getVariantIndex() {
        return variantIndex;
    }

    public boolean visit(SQLVariantRefExpr x) {
        return SQLEvalVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLBinaryOpExpr x) {
        return SQLEvalVisitorUtils.visit(this, x);
    }

    public boolean isMarkVariantIndex() {
        return markVariantIndex;
    }

    public void setMarkVariantIndex(boolean markVariantIndex) {
        this.markVariantIndex = markVariantIndex;
    }

}
