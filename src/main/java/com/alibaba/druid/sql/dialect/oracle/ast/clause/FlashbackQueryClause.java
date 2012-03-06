package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public abstract class FlashbackQueryClause extends OracleSQLObjectImpl {

    private static final long serialVersionUID = 1L;

    private Type              type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        SCN, TIMESTAMP
    }

    public static class VersionsFlashbackQueryClause extends FlashbackQueryClause {

        private static final long serialVersionUID = 1L;

        private SQLExpr           begin;
        private SQLExpr           end;

        public SQLExpr getBegin() {
            return begin;
        }

        public void setBegin(SQLExpr begin) {
            this.begin = begin;
        }

        public SQLExpr getEnd() {
            return end;
        }

        public void setEnd(SQLExpr end) {
            this.end = end;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, begin);
                acceptChild(visitor, end);
            }
            visitor.endVisit(this);
        }
    }

    public static class AsOfFlashbackQueryClause extends FlashbackQueryClause {

        private static final long serialVersionUID = 1L;

        private SQLExpr           expr;

        public SQLExpr getExpr() {
            return expr;
        }

        public void setExpr(SQLExpr expr) {
            this.expr = expr;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, expr);
            }
            visitor.endVisit(this);
        }
    }
    
    public static class AsOfSnapshotClause extends FlashbackQueryClause {

        private static final long serialVersionUID = 1L;

        private SQLExpr           expr;

        public SQLExpr getExpr() {
            return expr;
        }

        public void setExpr(SQLExpr expr) {
            this.expr = expr;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, expr);
            }
            visitor.endVisit(this);
        }
    }
}
