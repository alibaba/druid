package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OracleJSONTableExpr extends SQLExprImpl implements OracleExpr {
    private final List<Column> columns = new ArrayList<Column>();
    private SQLExpr expr;
    private SQLExpr path;

    @Override
    public void accept0(OracleASTVisitor v) {
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

    public static class Column extends OracleSQLObjectImpl {
        private final List<Column> nestedColumns = new ArrayList<Column>();
        private SQLName name;
        private SQLDataType dataType;
        private SQLExpr path;
        private boolean ordinality;
        private boolean exists;
        private SQLExpr onError;
        private SQLExpr onEmpty;

        @Override
        public void accept0(OracleASTVisitor v) {
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

        @Override
        public Column clone() {
            Column result = new Column();
            for(Column nestedColumn : nestedColumns) {
                result.addNestedColumn(nestedColumn.clone());
            }
            if(name != null) {
                result.setName(name.clone());
            }
            if(dataType != null) {
                result.setDataType(dataType.clone());
            }
            if(path != null) {
                result.setPath(path.clone());
            }
            result.setOrdinality(ordinality);
            result.setExists(exists);
            if(onError != null) {
                result.setOnEmpty(onError.clone());
            }
            if(onEmpty != null) {
                result.setOnEmpty(onEmpty.clone());
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Column)) return false;
            Column column = (Column) o;
            return ordinality == column.ordinality && exists == column.exists && Objects.equals(nestedColumns, column.nestedColumns) && Objects.equals(name, column.name) && Objects.equals(dataType, column.dataType) && Objects.equals(path, column.path) && Objects.equals(onError, column.onError) && Objects.equals(onEmpty, column.onEmpty);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nestedColumns, name, dataType, path, ordinality, exists, onError, onEmpty);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OracleJSONTableExpr)) return false;
        OracleJSONTableExpr that = (OracleJSONTableExpr) o;
        return Objects.equals(columns, that.columns) && Objects.equals(expr, that.expr) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns, expr, path);
    }

    @Override
    public SQLExpr clone() {
        OracleJSONTableExpr result = new OracleJSONTableExpr();
        for(Column column : columns) {
            result.addColumn(column.clone());
        }
        if(expr != null) {
            result.setExpr(expr.clone());
        }
        if(path != null) {
            result.setPath(path.clone());
        }
        return result;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        this.accept0((OracleASTVisitor) v);
    }

}
