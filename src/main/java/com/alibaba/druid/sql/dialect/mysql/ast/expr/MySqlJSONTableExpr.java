package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlJSONTableExpr extends MySqlExprImpl {
    private final List<Column> columns = new ArrayList<Column>();
    private SQLExpr expr;
    private SQLExpr path;

    @Override
    public void accept0(MySqlASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, expr);
            acceptChild(v, path);
            acceptChild(v, columns);
        }
        v.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        return null;
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }

        this.expr = x;
    }

    public SQLExpr getPath() {
        return path;
    }

    public void setPath(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.path = x;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void addColumn(Column column) {
        column.setParent(this);
        this.columns.add(column);
    }

    public static class Column extends MySqlObjectImpl {
        private final List<Column> nestedColumns = new ArrayList<Column>();
        private SQLName name;
        private SQLDataType dataType;
        private SQLExpr path;
        private boolean ordinality;
        private boolean exists;
        private SQLExpr onError;
        private SQLExpr onEmpty;

        @Override
        public void accept0(MySqlASTVisitor v) {
            if (v.visit(this)) {
                acceptChild(v, name);
                acceptChild(v, dataType);
                acceptChild(v, path);
                acceptChild(v, onEmpty);
                acceptChild(v, onError);
            }
            v.endVisit(this);
        }

        public SQLName getName() {
            return name;
        }

        public void setName(SQLName x) {
            if (x != null) {
                x.setParent(this);
            }
            this.name = x;
        }

        public SQLDataType getDataType() {
            return dataType;
        }

        public void setDataType(SQLDataType x) {
            if (x != null) {
                x.setParent(this);
            }
            this.dataType = x;
        }

        public SQLExpr getPath() {
            return path;
        }

        public void setPath(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.path = x;
        }

        public boolean isOrdinality() {
            return ordinality;
        }

        public void setOrdinality(boolean ordinality) {
            this.ordinality = ordinality;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }

        public SQLExpr getOnError() {
            return onError;
        }

        public void setOnError(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.onError = x;
        }

        public SQLExpr getOnEmpty() {
            return onEmpty;
        }

        public void setOnEmpty(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.onEmpty = x;
        }

        public List<Column> getNestedColumns() {
            return nestedColumns;
        }

        public void addNestedColumn(Column column) {
            column.setParent(this);
            this.nestedColumns.add(column);
        }
    }
}
