package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlExtractExpr extends SQLExprImpl implements MySqlExpr {
    private static final long serialVersionUID = 1L;

    private SQLExpr value;
    private MySqlIntervalUnit unit;

    public MySqlExtractExpr() {
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public MySqlIntervalUnit getUnit() {
        return unit;
    }

    public void setUnit(MySqlIntervalUnit unit) {
        this.unit = unit;
    }

    @Override
    public void output(StringBuffer buf) {
        value.output(buf);
        buf.append(' ');
        buf.append(unit.name());
    }

    protected void accept0(SQLASTVisitor visitor) {
        MySqlASTVisitor mysqlVisitor = (MySqlASTVisitor) visitor;
        mysqlVisitor.visit(this);
        mysqlVisitor.endVisit(this);
    }

}
