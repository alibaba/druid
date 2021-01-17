package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.IOException;

public class OdpsNewExpr extends SQLMethodInvokeExpr implements OdpsObject {
    @Override
    public OdpsNewExpr clone() {
        OdpsNewExpr x = new OdpsNewExpr();
        cloneTo(x);
        return x;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        accept0((OdpsASTVisitor) v);
    }

    @Override
    public void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.owner != null) {
                this.owner.accept(visitor);
            }

            for (SQLExpr arg : this.arguments) {
                if (arg != null) {
                    arg.accept(visitor);
                }
            }

            if (this.from != null) {
                this.from.accept(visitor);
            }

            if (this.using != null) {
                this.using.accept(visitor);
            }

            if (this._for != null) {
                this._for.accept(visitor);
            }

            visitor.endVisit(this);
        }
        visitor.endVisit(this);
    }

    public void output(Appendable buf) {
        try {
            buf.append("new ");
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
        super.output(buf);
    }
}
