package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSubqueryTableSource extends SQLTableSource {
    private static final long serialVersionUID = 1L;

    protected SQLSelect select;

    public SQLSubqueryTableSource() {

    }

    public SQLSubqueryTableSource(String alias) {
        super(alias);
    }

    public SQLSubqueryTableSource(SQLSelect select, String alias) {
        super(alias);
        this.select = select;
    }

    public SQLSubqueryTableSource(SQLSelect select) {

        this.select = select;
    }

    public SQLSelect getSelect() {
        return this.select;
    }

    public void setSelect(SQLSelect select) {
        this.select = select;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        throw new UnsupportedOperationException();
    }

    public void output(StringBuffer buf) {
        buf.append("(");
        this.select.output(buf);
        buf.append(")");
    }
}
