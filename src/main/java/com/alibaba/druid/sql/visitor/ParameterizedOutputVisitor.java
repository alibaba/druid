package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;

public class ParameterizedOutputVisitor extends SQLASTOutputVisitor {
    public ParameterizedOutputVisitor() {
        this (new StringBuilder());
    }

    public ParameterizedOutputVisitor(Appendable appender){
        super(appender);
    }

    public boolean visit(SQLInListExpr x) {
        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLBinaryOpExpr x) {
        x = ParameterizedOutputVisitorUtils.merge(x);

        return super.visit(x);
    }

    public boolean visit(SQLNullExpr x) {
        print('?');
        return false;
    }

    public boolean visit(SQLIntegerExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    public boolean visit(SQLNumberExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    public boolean visit(SQLCharExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }

    public boolean visit(SQLNCharExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        return false;
    }
}
