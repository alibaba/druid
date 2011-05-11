package com.alibaba.druid.sql.dialect.mysql.visitor;

import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;

public class MySql2OracleOutputVisitor extends MySqlOutputVisitor {
    public MySql2OracleOutputVisitor(Appendable appender) {
        super(appender);
    }

    public boolean visit(MySqlBooleanExpr x) {
        return true;
    }

    public void endVisit(MySqlBooleanExpr x) {
    }

}
