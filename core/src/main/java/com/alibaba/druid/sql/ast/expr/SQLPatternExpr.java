package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLPatternExpr extends SQLExprImpl {
    private String prefix;
    private SQLExpr expr;
    private String suffix;

    public SQLPatternExpr() {
        this.prefix = "{{";
        this.suffix = "}}";
    }

    public SQLPatternExpr(SQLExpr expr) {
        this.prefix = "{{";
        this.suffix = "}}";
        this.expr = expr;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLPatternExpr other = (SQLPatternExpr) obj;
        if (!prefix.equals(other.prefix)) {
            return false;
        }
        if (!suffix.equals(other.suffix)) {
            return false;
        }
        return expr.equals(other.expr);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode())
                + prefix.hashCode() + suffix.hashCode();
        return result;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof SQLASTOutputVisitor) {
            SQLASTOutputVisitor visitor = (SQLASTOutputVisitor) v;
            visitor.print(prefix);
            visitor.print(" ");
            expr.accept(visitor);
            visitor.print(" ");
            visitor.print(suffix);
        }
    }

    @Override
    public SQLExpr clone() {
        SQLPatternExpr x = new SQLPatternExpr();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.prefix = prefix;
        x.suffix = suffix;
        return x;
    }
}
