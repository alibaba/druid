package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class CobarShowStatus extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;

    protected void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        
        visitor.endVisit(this);
    }
}
