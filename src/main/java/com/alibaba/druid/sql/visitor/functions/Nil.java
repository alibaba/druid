package com.alibaba.druid.sql.visitor.functions;

import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;

public class Nil implements Function {

    public final static Nil instance = new Nil();

    @Override
    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        return null;
    }

}
