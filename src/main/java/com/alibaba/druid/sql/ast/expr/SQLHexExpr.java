package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLHexExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    private final String hex;

    public SQLHexExpr(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public void output(StringBuffer buf) {
        buf.append("0x");
        buf.append(this.hex);

        String charset = (String) getAttribute("USING");
        if (charset != null) {
            buf.append(" USING ");
            buf.append(charset);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
