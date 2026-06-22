package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Objects;

public class SQLJSONValueExpr extends SQLExprImpl {
    private SQLExpr json;
    private SQLExpr path;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SQLJSONValueExpr that = (SQLJSONValueExpr) o;
        return Objects.equals(json, that.json) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(json, path);
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
    }

    @Override
    public SQLExpr clone() {
        return null;
    }
}
