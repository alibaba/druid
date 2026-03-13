package com.alibaba.druid.sql.dialect.dm.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLTop;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.dm.ast.DmObjectImpl;
import com.alibaba.druid.sql.dialect.dm.visitor.DmASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class DmSelectQueryBlock extends SQLSelectQueryBlock {
    private SQLTop top;
    private FetchClause fetch;
    private ForClause forClause;

    public DmSelectQueryBlock() {
        dbType = DbType.dm;
    }

    public SQLTop getTop() {
        return top;
    }

    public void setTop(SQLTop top) {
        if (top != null) {
            top.setParent(this);
        }
        this.top = top;
    }

    public void setTop(int rowCount) {
        setTop(new SQLTop(new SQLIntegerExpr(rowCount)));
    }

    public FetchClause getFetch() {
        return fetch;
    }

    public void setFetch(FetchClause fetch) {
        if (fetch != null) {
            fetch.setParent(this);
        }
        this.fetch = fetch;
    }

    public ForClause getForClause() {
        return forClause;
    }

    public void setForClause(ForClause forClause) {
        if (forClause != null) {
            forClause.setParent(this);
        }
        this.forClause = forClause;
    }

    public static class FetchClause extends DmObjectImpl {
        public enum Option {
            FIRST, NEXT
        }

        private Option option;
        private SQLExpr count;

        public Option getOption() {
            return option;
        }

        public void setOption(Option option) {
            this.option = option;
        }

        public SQLExpr getCount() {
            return count;
        }

        public void setCount(SQLExpr count) {
            if (count != null) {
                count.setParent(this);
            }
            this.count = count;
        }

        @Override
        public void accept0(DmASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, count);
            }
            visitor.endVisit(this);
        }
    }

    public static class ForClause extends DmObjectImpl {
        public enum Option {
            UPDATE, SHARE
        }

        private Option option;
        private List<SQLExpr> of = new ArrayList<>();
        private boolean noWait;
        private boolean skipLocked;
        private SQLExpr waitTimeout;

        public Option getOption() {
            return option;
        }

        public void setOption(Option option) {
            this.option = option;
        }

        public List<SQLExpr> getOf() {
            return of;
        }

        public boolean isNoWait() {
            return noWait;
        }

        public void setNoWait(boolean noWait) {
            this.noWait = noWait;
        }

        public boolean isSkipLocked() {
            return skipLocked;
        }

        public void setSkipLocked(boolean skipLocked) {
            this.skipLocked = skipLocked;
        }

        public SQLExpr getWaitTimeout() {
            return waitTimeout;
        }

        public void setWaitTimeout(SQLExpr waitTimeout) {
            if (waitTimeout != null) {
                waitTimeout.setParent(this);
            }
            this.waitTimeout = waitTimeout;
        }

        @Override
        public void accept0(DmASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, of);
                acceptChild(visitor, waitTimeout);
            }
            visitor.endVisit(this);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof DmASTVisitor) {
            accept0((DmASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void accept0(DmASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor);
        }
        visitor.endVisit(this);
    }

    protected void acceptChild(SQLASTVisitor visitor) {
        acceptChild(visitor, top);
        acceptChild(visitor, this.getSelectList());
        acceptChild(visitor, this.getFrom());
        acceptChild(visitor, this.getWhere());
        acceptChild(visitor, this.getGroupBy());
        acceptChild(visitor, this.getWindows());
        acceptChild(visitor, this.getOrderBy());
        acceptChild(visitor, this.getLimit());
        acceptChild(visitor, fetch);
        acceptChild(visitor, forClause);
    }
}
