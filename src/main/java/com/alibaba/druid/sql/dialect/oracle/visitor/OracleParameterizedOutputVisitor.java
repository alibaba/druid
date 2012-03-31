package com.alibaba.druid.sql.dialect.oracle.visitor;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;

public class OracleParameterizedOutputVisitor extends OracleOutputVisitor {

    public OracleParameterizedOutputVisitor() {
        this (new StringBuilder());
    }

    public OracleParameterizedOutputVisitor(Appendable appender){
        super(appender);
    }
    
    public OracleParameterizedOutputVisitor(Appendable appender, boolean printPostSemi){
        super (appender, printPostSemi);
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
