package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelectSubqueryTableSource extends SQLSubqueryTableSource implements OracleSelectTableSource {
    private static final long serialVersionUID = 1L;

    protected OracleSelectPivotBase pivot;

    public OracleSelectSubqueryTableSource() {

    }

    public OracleSelectSubqueryTableSource(String alias) {
        super(alias);
    }

    public OracleSelectSubqueryTableSource(SQLSelect select, String alias) {
        super(select, alias);
    }

    public OracleSelectSubqueryTableSource(SQLSelect select) {
        super(select);
    }

    public OracleSelectPivotBase getPivot() {
        return pivot;
    }

    public void setPivot(OracleSelectPivotBase pivot) {
        this.pivot = pivot;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.select);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("(");
        this.select.output(buf);
        buf.append(")");
    }
}
