package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlChecksumTableStatement extends MySqlStatementImpl {
    private final List<SQLExprTableSource> tables = new ArrayList<SQLExprTableSource>();

    private boolean quick;
    private boolean extended;

    public MySqlChecksumTableStatement() {

    }

    public void addTable(SQLExprTableSource table) {
        if (table == null) {
            return;
        }

        table.setParent(this);
        tables.add(table);
    }

    public List<SQLExprTableSource> getTables() {
        return tables;
    }

    public boolean isQuick() {
        return quick;
    }

    public void setQuick(boolean quick) {
        this.quick = quick;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tables);
        }
        visitor.endVisit(this);
    }
}
