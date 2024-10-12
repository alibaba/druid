package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class BigQueryCharExpr extends SQLCharExpr implements SQLExpr {
    private String prefix;
    private boolean space;
    private boolean isAlias;

    public BigQueryCharExpr() {
    }

    public boolean hasPrefix() {
        return prefix != null;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isSpace() {
        return space;
    }

    public void setSpace(boolean space) {
        this.space = space;
    }

    public boolean isAlias() {
        return isAlias;
    }

    public void setAlias(boolean alias) {
        isAlias = alias;
    }

    public BigQueryCharExpr(String text, String prefix) {
        this(text, prefix, false, false);
    }

    public BigQueryCharExpr(String text, String prefix, boolean space, boolean isAlias) {
        this.prefix = prefix;
        this.text = text;
        this.space = space;
        this.isAlias = isAlias;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof SQLASTOutputVisitor) {
            SQLASTOutputVisitor visitor = (SQLASTOutputVisitor) v;
            if (hasPrefix()) {
                visitor.print(prefix);
            }
            if (isSpace()) {
                visitor.print(" ");
            }
            if (!isAlias) {
                visitor.print("'");
            }
            visitor.print(text);
            if (!isAlias) {
                visitor.print("'");
            }
        }
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public BigQueryCharExpr clone() {
        BigQueryCharExpr clone = new BigQueryCharExpr();
        clone.setPrefix(this.prefix);
        clone.setText(this.text);
        clone.setSpace(this.space);
        clone.setAlias(this.isAlias);
        return clone;
    }

}
