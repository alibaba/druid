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

    public BigQueryCharExpr(String text, String prefix) {
        this(text, prefix, false);
    }

    public BigQueryCharExpr(String text, String prefix, boolean space) {
        this.prefix = prefix;
        this.text = text;
        this.space = space;
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
            visitor.print("'");
            visitor.print(text);
            visitor.print("'");
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
        return clone;
    }

}
