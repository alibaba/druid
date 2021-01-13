package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLTableSampling extends SQLObjectImpl implements SQLReplaceable {
    private SQLExpr bucket;
    private SQLExpr outOf;
    private SQLExpr on;
    private SQLExpr percent;
    private SQLExpr rows;
    private SQLExpr byteLength;

    private boolean bernoulli;
    private boolean system;

    public SQLTableSampling clone() {
        SQLTableSampling x = new SQLTableSampling();

        if (bucket != null) {
            x.setBucket(bucket.clone());
        }

        if (outOf != null) {
            x.setOutOf(outOf.clone());
        }

        if (on != null) {
            x.setOn(on.clone());
        }

        if (percent != null) {
            x.setPercent(percent.clone());
        }

        if (rows != null) {
            x.setRows(rows.clone());
        }

        if (byteLength != null) {
            x.setByteLength(byteLength.clone());
        }

        x.bernoulli = bernoulli;
        x.system = system;
        return x;
    }

    public SQLExpr getBucket() {
        return bucket;
    }

    public void setBucket(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.bucket = x;
    }

    public SQLExpr getOutOf() {
        return outOf;
    }

    public void setOutOf(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.outOf = x;
    }

    public SQLExpr getOn() {
        return on;
    }

    public void setOn(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.on = x;
    }

    public SQLExpr getPercent() {
        return percent;
    }

    public void setPercent(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.percent = x;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, bucket);
            acceptChild(v, outOf);
            acceptChild(v, on);
            acceptChild(v, percent);
            acceptChild(v, byteLength);
        }
        v.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (bucket == expr) {
            setBucket(target);
            return true;
        }

        if (outOf == expr) {
            setOutOf(target);
            return true;
        }

        if (on == expr) {
            setOn(target);
            return true;
        }

        if (percent == expr) {
            setPercent(target);
            return true;
        }

        if (byteLength == expr) {
            setByteLength(target);
            return true;
        }
        return false;
    }

    public SQLExpr getByteLength() {
        return byteLength;
    }

    public void setByteLength(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.byteLength = x;
    }

    public SQLExpr getRows() {
        return rows;
    }

    public void setRows(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.rows = x;
    }

    public boolean isBernoulli() {
        return bernoulli;
    }

    public void setBernoulli(boolean bernoulli) {
        this.bernoulli = bernoulli;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
}
