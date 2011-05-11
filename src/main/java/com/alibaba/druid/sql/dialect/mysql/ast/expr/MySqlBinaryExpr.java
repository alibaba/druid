package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlBinaryExpr extends SQLLiteralExpr implements MySqlExpr {
    private static final long serialVersionUID = 1L;

    private String value;

    public MySqlBinaryExpr() {

    }

    public MySqlBinaryExpr(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((MySqlASTVisitor) visitor);
    }

    protected void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("b'");
        buf.append(value);
        buf.append('\'');
    }
}
