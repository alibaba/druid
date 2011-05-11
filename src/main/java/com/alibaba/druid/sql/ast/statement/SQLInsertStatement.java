package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLInsertStatement extends SQLStatementImpl {
    private static final long serialVersionUID = 1L;
    private SQLName tableName;

    private final List<SQLExpr> columns = new ArrayList<SQLExpr>();
    private ValuesClause values;
    private SQLQueryExpr query;

    public SQLInsertStatement() {

    }

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.tableName = tableName;
    }

    public SQLQueryExpr getQuery() {
        return query;
    }

    public void setQuery(SQLQueryExpr query) {
        this.query = query;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public ValuesClause getValues() {
        return values;
    }

    public void setValues(ValuesClause values) {
        this.values = values;
    }

    public void output(StringBuffer buf) {
        buf.append("INSERT INTO ");
        this.tableName.output(buf);
        if (columns.size() > 0) {
            buf.append(" (");
            for (int i = 0, size = columns.size(); i < size; ++i) {
                if (i != 0) {
                    buf.append(", ");
                }
                columns.get(i).output(buf);
            }
            buf.append(")");
        }

        if (values != null) {
            values.output(buf);
        } else {
            buf.append(" ");
            this.query.output(buf);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableName);
            this.acceptChild(visitor, columns);
            this.acceptChild(visitor, values);
            this.acceptChild(visitor, query);
        }

        visitor.endVisit(this);
    }

    public static class ValuesClause extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;
        private final List<SQLExpr> values = new ArrayList<SQLExpr>();

        public List<SQLExpr> getValues() {
            return values;
        }

        public void output(StringBuffer buf) {
            buf.append(" VALUES (");
            for (int i = 0, size = values.size(); i < size; ++i) {
                if (i != 0) {
                    buf.append(", ");
                }
                values.get(i).output(buf);
            }
            buf.append(")");
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                this.acceptChild(visitor, values);
            }

            visitor.endVisit(this);
        }
    }
}
