package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;


public class MySqlHexadecimalExpr extends MySqlExprImpl implements SQLLiteralExpr {

    private static final long serialVersionUID = 1L;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        
    }

}
