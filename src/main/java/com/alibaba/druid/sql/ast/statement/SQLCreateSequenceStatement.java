/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/9/14.
 */
public class SQLCreateSequenceStatement extends SQLStatementImpl implements SQLCreateStatement {
    private SQLName name;

    private SQLExpr startWith;
    private SQLExpr incrementBy;
    private SQLExpr minValue;
    private SQLExpr maxValue;
    private boolean noMaxValue;
    private boolean noMinValue;

    private Boolean cycle;
    private Boolean cache;
    private SQLExpr cacheValue;

    private Boolean order;

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
}
