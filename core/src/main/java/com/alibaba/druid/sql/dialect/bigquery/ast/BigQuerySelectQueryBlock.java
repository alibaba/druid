package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.bigquery.visitor.BigQueryVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BigQuerySelectQueryBlock extends SQLSelectQueryBlock
        implements BigQueryObject{
    protected boolean asStruct;
    private DifferentialPrivacy differentialPrivacy;

    public DifferentialPrivacy getDifferentialPrivacy() {
        return differentialPrivacy;
    }

    public void setDifferentialPrivacy(DifferentialPrivacy x) {
        if (x != null) {
            x.setParent(this);
        }
        this.differentialPrivacy = x;
    }

    public boolean isAsStruct() {
        return asStruct;
    }

    public void setAsStruct(boolean asStruct) {
        this.asStruct = asStruct;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof BigQueryVisitor) {
            accept0((BigQueryVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    @Override
    public void accept0(BigQueryVisitor v) {
        super.accept0(v);
    }

    protected void acceptChild(SQLASTVisitor v) {
        if (differentialPrivacy != null) {
            differentialPrivacy.accept(v);
        }
        super.acceptChild(v);
    }

    public static class DifferentialPrivacy
            extends SQLObjectImpl implements BigQueryObject {
        private final List<SQLAssignItem> options = new ArrayList<>();

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            DifferentialPrivacy that = (DifferentialPrivacy) object;
            return options.equals(that.options);
        }

        public List<SQLAssignItem> getOptions() {
            return options;
        }

        @Override
        public int hashCode() {
            return Objects.hash(options);
        }

        @Override
        public void accept0(SQLASTVisitor v) {
            if (v instanceof BigQueryVisitor) {
                accept0((BigQueryVisitor) v);
            }
        }

        @Override
        public void accept0(BigQueryVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, options);
            }
            visitor.endVisit(this);
        }

        @Override
        public DifferentialPrivacy clone() {
            DifferentialPrivacy x = new DifferentialPrivacy();
            for (SQLAssignItem option : options) {
                SQLAssignItem cloned = option.clone();
                x.options.add(cloned);
            }
            return x;
        }
    }

    public BigQuerySelectQueryBlock clone() {
        BigQuerySelectQueryBlock x = new BigQuerySelectQueryBlock();
        cloneTo(x);
        x.asStruct = this.asStruct;
        if (differentialPrivacy != null) {
            x.differentialPrivacy = this.differentialPrivacy.clone();
        }
        return x;
    }
}
