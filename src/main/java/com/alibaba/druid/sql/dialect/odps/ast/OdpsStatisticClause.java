package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

public abstract class OdpsStatisticClause extends OdpsObjectImpl {

    public static abstract class ColumnStatisticClause extends OdpsStatisticClause {
    
        protected SQLName column;
    
        public SQLName getColumn() {
            return column;
        }
    
        public void setColumn(SQLName column) {
            if (column != null) {
                column.setParent(this);
            }
            this.column = column;
        }
    
    }

    public static class NullValue extends ColumnStatisticClause {
        @Override
        protected void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, column);
            }
            visitor.endVisit(this);
        }
    }

    public static class ColumnSum extends ColumnStatisticClause {
        @Override
        protected void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, column);
            }
            visitor.endVisit(this);
        }
    }

    public static class ColumnMin extends ColumnStatisticClause {
        @Override
        protected void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, column);
            }
            visitor.endVisit(this);
        }
    }

    public static class ColumnMax extends ColumnStatisticClause {
        @Override
        protected void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, column);
            }
            visitor.endVisit(this);
        }
    }

    public static class ExpressionCondition extends OdpsStatisticClause {
    
        private SQLExpr expr;
    
        public SQLExpr getExpr() {
            return expr;
        }
    
        public void setExpr(SQLExpr expr) {
            if (expr != null) {
                expr.setParent(this);
            }
            this.expr = expr;
        }
    
        @Override
        protected void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, expr);
            }
            visitor.endVisit(this);
        }
    }

    public static class TableCount extends OdpsStatisticClause {
    
        @Override
        protected void accept0(OdpsASTVisitor visitor) {
            if (visitor.visit(this)) {
    
            }
            visitor.endVisit(this);
        }
    
    }

}