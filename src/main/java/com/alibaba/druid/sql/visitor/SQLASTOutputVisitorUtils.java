package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;

public class SQLASTOutputVisitorUtils {

    public static boolean visit(SQLPrintableVisitor visitor, SQLIntegerExpr x) {
        visitor.print(x.getNumber().toString());
        return false;
    }
    
    public static boolean visit(SQLPrintableVisitor visitor, SQLNumberExpr x) {
        visitor.print(x.getNumber().toString());
        return false;
    }
}
