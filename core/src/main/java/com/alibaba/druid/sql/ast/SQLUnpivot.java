package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLUnpivot extends SQLObjectImpl {
    protected NullsIncludeType nullsIncludeType;
    protected List<SQLExpr> items = new ArrayList<>();
    protected final List<SQLExpr> pivotFor = new ArrayList<>();
    protected final List<SQLSelectItem> pivotIn = new ArrayList<>();

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, this.items);
            acceptChild(v, this.pivotFor);
            acceptChild(v, this.pivotIn);
        }
        v.endVisit(this);
    }

    public NullsIncludeType getNullsIncludeType() {
        return nullsIncludeType;
    }

    public void setNullsIncludeType(NullsIncludeType nullsIncludeType) {
        this.nullsIncludeType = nullsIncludeType;
    }

    public List<SQLExpr> getPivotFor() {
        return pivotFor;
    }

    public List<SQLSelectItem> getPivotIn() {
        return pivotIn;
    }

    public List<SQLExpr> getItems() {
        return this.items;
    }

    public void addItem(SQLExpr item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public SQLUnpivot clone() {
        SQLUnpivot x = new SQLUnpivot();

        x.setNullsIncludeType(nullsIncludeType);

        for (SQLExpr e : this.items) {
            SQLExpr e2 = e.clone();
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

    public static enum NullsIncludeType {
        INCLUDE_NULLS, EXCLUDE_NULLS;

        public static String toString(SQLUnpivot.NullsIncludeType type, boolean ucase) {
            if (INCLUDE_NULLS.equals(type)) {
                return ucase ? "INCLUDE NULLS" : "include nulls";
            }
            if (EXCLUDE_NULLS.equals(type)) {
                return ucase ? "EXCLUDE NULLS" : "exclude nulls";
            }

            throw new IllegalArgumentException();
        }
    }
}
