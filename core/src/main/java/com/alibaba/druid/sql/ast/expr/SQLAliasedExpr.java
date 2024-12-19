package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Objects;

public class SQLAliasedExpr extends SQLExprImpl implements SQLReplaceable {
    protected SQLExpr expr;
    protected String alias;

    public SQLAliasedExpr() {
        this(null, null);
    }

    public SQLAliasedExpr(SQLExpr expr) {
        this(expr, null);
    }

    public SQLAliasedExpr(SQLExpr expr, String alias) {
        this.setExpr(expr);
        this.alias = alias;
    }

    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            if (expr != null) {
                expr.accept(v);
            }
        }
        v.endVisit(this);
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.expr == expr) {
            setExpr(target);
            return true;
        }

        return false;
    }

    public String computeAlias() {
        String alias = this.getAlias();
        if (alias == null) {
            if (expr instanceof SQLIdentifierExpr) {
                alias = ((SQLIdentifierExpr) expr).getName();
            } else if (expr instanceof SQLPropertyExpr) {
                alias = ((SQLPropertyExpr) expr).getName();
            }
        }

        return SQLUtils.normalize(alias);
    }

    public SQLDataType computeDataType() {
        if (expr == null) {
            return null;
        }

        return expr.computeDataType();
    }

    public String getAlias() {
        return this.alias;
    }

    public String getAlias2() {
        if (this.alias == null || this.alias.length() == 0) {
            return alias;
        }

        char first = alias.charAt(0);
        if (first == '"' || first == '\'') {
            char[] chars = new char[alias.length() - 2];
            int len = 0;
            for (int i = 1; i < alias.length() - 1; ++i) {
                char ch = alias.charAt(i);
                if (ch == '\\') {
                    ++i;
                    ch = alias.charAt(i);
                }
                chars[len++] = ch;
            }
            return new String(chars, 0, len);
        }

        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public SQLAliasedExpr clone() {
        SQLAliasedExpr x = new SQLAliasedExpr();
        cloneTo(x);
        return x;
    }

    protected void cloneTo(SQLAliasedExpr x) {
        super.cloneTo(x);
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.setAlias(alias);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SQLAliasedExpr that = (SQLAliasedExpr) o;

        if (!Objects.equals(expr, that.expr)) {
            return false;
        }
        return Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        int result = expr != null ? expr.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }
}
