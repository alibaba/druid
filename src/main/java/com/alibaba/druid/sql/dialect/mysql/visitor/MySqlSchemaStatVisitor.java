package com.alibaba.druid.sql.dialect.mysql.visitor;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;

public class MySqlSchemaStatVisitor extends MySqlASTVisitorAdapter {

    public boolean visit(SQLPropertyExpr x) {
        return true;
    }

    public boolean visit(SQLIdentifierExpr astNode) {
        return true;
    }
}
