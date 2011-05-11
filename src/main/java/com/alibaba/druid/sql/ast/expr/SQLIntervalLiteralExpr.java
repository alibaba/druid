package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * TODO
 * 
 * @author WENSHAO
 */
public class SQLIntervalLiteralExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    private Character sign = null;

    public Character getSign() {
        return sign;
    }

    public void setSign(Character sign) {
        this.sign = sign;
    }

    public SQLIntervalLiteralExpr() {

    }

    @Override
    public void output(StringBuffer buf) {
        buf.append("INTERVAL");
        if (sign != null) {
            buf.append(sign.charValue());
        }
        throw new RuntimeException("TODO");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

}
