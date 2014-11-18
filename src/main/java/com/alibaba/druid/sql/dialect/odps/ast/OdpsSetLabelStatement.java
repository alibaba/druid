package com.alibaba.druid.sql.dialect.odps.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

public class OdpsSetLabelStatement extends OdpsStatementImpl {

    private String         label;

    private SQLExpr        user;

    private SQLTableSource table;

    private List<SQLName>  columns = new ArrayList<SQLName>();

    @Override
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, user);
        }
        visitor.endVisit(this);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public SQLExpr getUser() {
        return user;
    }

    public void setUser(SQLExpr user) {
        this.user = user;
        user.setParent(this);
    }

    public SQLTableSource getTable() {
        return table;
    }

    public void setTable(SQLTableSource table) {
        this.table = table;
        table.setParent(this);
    }

    public List<SQLName> getColumns() {
        return columns;
    }

}
