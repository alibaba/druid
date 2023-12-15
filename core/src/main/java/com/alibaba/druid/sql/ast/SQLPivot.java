package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLPivot extends SQLObjectImpl {
    protected boolean xml;
    protected final List<SQLSelectItem> items = new ArrayList<SQLSelectItem>();
    protected final List<SQLExpr> pivotFor = new ArrayList<>();
    protected final List<SQLSelectItem> pivotIn = new ArrayList<>();

    public List<SQLExpr> getPivotFor() {
        return pivotFor;
    }

    public List<SQLSelectItem> getPivotIn() {
        return pivotIn;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, this.items);
            acceptChild(v, this.pivotFor);
            acceptChild(v, this.pivotIn);
        }
        v.endVisit(this);
    }

    public List<SQLSelectItem> getItems() {
        return items;
    }

    public void addItem(SQLSelectItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public boolean isXml() {
        return xml;
    }

    public void setXml(boolean xml) {
        this.xml = xml;
    }

    @Override
    public SQLPivot clone() {
        SQLPivot x = new SQLPivot();

        x.setXml(this.xml);

        for (SQLSelectItem e : this.items) {
            SQLSelectItem e2 = e.clone();
            e2.setParent(x);
            x.getItems().add(e2);
        }

        for (SQLExpr e : this.pivotFor) {
            SQLExpr e2 = e.clone();
            e2.setParent(x);
            x.getPivotFor().add(e2);
        }

        for (SQLSelectItem e : this.pivotIn) {
            SQLSelectItem e2 = e.clone();
            e2.setParent(x);
            x.getPivotIn().add(e2);
        }

        return x;
    }
}
