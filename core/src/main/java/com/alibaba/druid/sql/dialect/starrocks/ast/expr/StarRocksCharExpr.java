package com.alibaba.druid.sql.dialect.starrocks.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class StarRocksCharExpr extends SQLCharExpr implements StarRocksExpr {
    private String charset;

    public StarRocksCharExpr() {
    }

    public StarRocksCharExpr(String text) {
        super(text);
    }

    public StarRocksCharExpr(String text, String charset) {
        super(text);
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof StarRocksASTVisitor) {
            accept0((StarRocksASTVisitor) visitor);
        } else {
            visitor.visit(this);
            visitor.endVisit(this);
        }
    }

    public void accept0(StarRocksASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        output(buf);
        return buf.toString();
    }
}
