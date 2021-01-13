package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLAlterSequenceStatement extends SQLStatementImpl implements SQLAlterStatement {
    private SQLName name;

    private Boolean withCache;
    private SQLExpr startWith;
    private SQLExpr incrementBy;
    private SQLExpr minValue;
    private SQLExpr maxValue;
    private boolean noMaxValue;
    private boolean noMinValue;

    private Boolean cycle;
    private Boolean cache;
    private SQLExpr cacheValue;

    private boolean restart;
    private SQLExpr restartWith;

    private Boolean order;

    private boolean changeToSimple;
    private boolean changeToGroup;
    private boolean changeToTime;

    public SQLAlterSequenceStatement() {

    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, startWith);
            acceptChild(visitor, incrementBy);
            acceptChild(visitor, minValue);
            acceptChild(visitor, maxValue);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (name != null) {
            children.add(name);
        }
        if (startWith != null) {
            children.add(startWith);
        }
        if (incrementBy != null) {
            children.add(incrementBy);
        }
        if (minValue != null) {
            children.add(minValue);
        }
        if (maxValue != null) {
            children.add(maxValue);
        }
        return children;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLExpr getStartWith() {
        return startWith;
    }

    public void setStartWith(SQLExpr startWith) {
        this.startWith = startWith;
    }

    public SQLExpr getIncrementBy() {
        return incrementBy;
    }

    public void setIncrementBy(SQLExpr incrementBy) {
        this.incrementBy = incrementBy;
    }

    public SQLExpr getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(SQLExpr maxValue) {
        this.maxValue = maxValue;
    }

    public Boolean getCycle() {
        return cycle;
    }

    public void setCycle(Boolean cycle) {
        this.cycle = cycle;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Boolean getWithCache() {
        return withCache;
    }

    public void setWithCache(Boolean withCache) {
        this.withCache = withCache;
    }

    public Boolean getOrder() {
        return order;
    }

    public void setOrder(Boolean order) {
        this.order = order;
    }

    public SQLExpr getMinValue() {
        return minValue;
    }

    public void setMinValue(SQLExpr minValue) {
        this.minValue = minValue;
    }

    public boolean isNoMaxValue() {
        return noMaxValue;
    }

    public void setNoMaxValue(boolean noMaxValue) {
        this.noMaxValue = noMaxValue;
    }

    public boolean isNoMinValue() {
        return noMinValue;
    }

    public void setNoMinValue(boolean noMinValue) {
        this.noMinValue = noMinValue;
    }

    public String getSchema() {
        SQLName name = getName();
        if (name == null) {
            return null;
        }

        if (name instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) name).getOwnernName();
        }

        return null;
    }

    public SQLExpr getCacheValue() {
        return cacheValue;
    }

    public void setCacheValue(SQLExpr cacheValue) {
        if (cacheValue != null) {
            cacheValue.setParent(this);
        }
        this.cacheValue = cacheValue;
    }

    public boolean isChangeToSimple() {
        return changeToSimple;
    }

    public void setChangeToSimple(boolean changeToSimple) {
        this.changeToSimple = changeToSimple;
    }

    public boolean isChangeToGroup() {
        return changeToGroup;
    }

    public void setChangeToGroup(boolean changeToGroup) {
        this.changeToGroup = changeToGroup;
    }

    public boolean isChangeToTime() {
        return changeToTime;
    }

    public void setChangeToTime(boolean changeToTime) {
        this.changeToTime = changeToTime;
    }

    public boolean isRestart() {
        return restart;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public SQLExpr getRestartWith() {
        return restartWith;
    }

    public void setRestartWith(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.restartWith = x;
    }
}
