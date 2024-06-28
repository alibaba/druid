package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Objects;

public class SQLCurrentTimeExpr extends SQLExprImpl {
    private final Type type;
    private String timeZone;

    public SQLCurrentTimeExpr(Type type) {
        if (type == null) {
            throw new NullPointerException();
        }

        this.type = type;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        v.visit(this);
        v.endVisit(this);
    }

    public Type getType() {
        return type;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SQLCurrentTimeExpr that = (SQLCurrentTimeExpr) o;

        if (type != that.type) {
            return false;
        }
        return Objects.equals(timeZone, that.timeZone);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (timeZone != null ? timeZone.hashCode() : 0);
        return result;
    }

    public SQLCurrentTimeExpr clone() {
        SQLCurrentTimeExpr x = new SQLCurrentTimeExpr(type);
        x.setTimeZone(timeZone);
        return x;
    }

    public static enum Type {
        CURRENT_TIME("CURRENT_TIME"),
        CURRENT_DATE("CURRENT_DATE"),
        CURDATE("CURDATE"),
        CURTIME("CURTIME"),
        CURRENT_TIMESTAMP("CURRENT_TIMESTAMP"),
        LOCALTIME("LOCALTIME"),
        LOCALTIMESTAMP("LOCALTIMESTAMP"),
        SYSDATE("SYSDATE");

        public final String name;
        public final String nameLCase;

        Type(String name) {
            this.name = name;
            this.nameLCase = name.toLowerCase();
        }
    }
}
