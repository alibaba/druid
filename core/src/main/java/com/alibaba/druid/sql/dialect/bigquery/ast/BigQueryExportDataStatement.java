package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.bigquery.visitor.BigQueryVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class BigQueryExportDataStatement extends SQLStatementImpl implements BigQueryObject {
    private SQLExprTableSource connection;
    private final List<SQLAssignItem> options = new ArrayList<>();
    private SQLStatement asSelect;

    public BigQueryExportDataStatement() {
        dbType = DbType.bigquery;
    }

    public SQLExprTableSource getConnection() {
        return connection;
    }

    public void setConnection(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.connection = x;
    }

    public List<SQLAssignItem> getOptions() {
        return options;
    }

    public SQLStatement getAsSelect() {
        return asSelect;
    }

    public void setAsSelect(SQLStatement x) {
        if (x != null) {
            x.setParent(this);
        }
        this.asSelect = x;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof BigQueryVisitor) {
            accept0((BigQueryVisitor) v);
        }
    }

    @Override
    public void accept0(BigQueryVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, connection);
            acceptChild(v, options);
            acceptChild(v, asSelect);
        }
        v.endVisit(this);
    }
}
