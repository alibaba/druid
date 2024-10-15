/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLAggregateExpr extends SQLMethodInvokeExpr implements Serializable, SQLReplaceable {
    private static final long serialVersionUID = 1L;

    protected SQLAggregateOption option;

    protected SQLKeep keep;
    protected SQLExpr filter;
    protected SQLOver over;
    protected SQLName overRef;
    protected SQLOrderBy orderBy;
    protected SQLExpr limit;
    protected boolean withinGroup;

    // For BigQuery scenario.
    protected boolean ignoreNulls;

    // For BigQuery scenario.
    protected boolean respectNulls;

    public SQLAggregateExpr(String methodName) {
        this.methodName = methodName;
        this.ignoreNulls = false;
        this.respectNulls = false;
    }

    public SQLAggregateExpr(String methodName, SQLAggregateOption option) {
        this.methodName = methodName;
        this.option = option;
        this.ignoreNulls = false;
        this.respectNulls = false;
    }

    public SQLAggregateExpr(String methodName, SQLAggregateOption option, SQLExpr... arguments) {
        this.methodName = methodName;
        this.option = option;
        this.ignoreNulls = false;
        this.respectNulls = false;
        if (arguments != null) {
            for (SQLExpr argument : arguments) {
                if (argument != null) {
                    addArgument(argument);
                }
            }
        }
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy x) {
        if (x != null) {
            x.setParent(this);
        }
        this.orderBy = x;
    }

    public SQLExpr getLimit() {
        return limit;
    }

    public void setLimit(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.limit = x;
    }

    public SQLAggregateOption getOption() {
        return this.option;
    }

    public void setOption(SQLAggregateOption option) {
        this.option = option;
    }

    public boolean isDistinct() {
        return option == SQLAggregateOption.DISTINCT;
    }

    public SQLOver getOver() {
        return over;
    }

    public void setOver(SQLOver x) {
        if (x != null) {
            x.setParent(this);
        }
        this.over = x;
    }

    public SQLName getOverRef() {
        return overRef;
    }

    public void setOverRef(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.overRef = x;
    }

    public SQLKeep getKeep() {
        return keep;
    }

    public void setKeep(SQLKeep keep) {
        if (keep != null) {
            keep.setParent(this);
        }
        this.keep = keep;
    }

    public boolean isWithinGroup() {
        return withinGroup;
    }

    public void setWithinGroup(boolean withinGroup) {
        this.withinGroup = withinGroup;
    }

    //为了兼容之前的逻辑
    @Deprecated
    public SQLOrderBy getWithinGroup() {
        return orderBy;
    }

    public boolean isIgnoreNulls() {
        return this.ignoreNulls;
    }

    public boolean getIgnoreNulls() {
        return this.ignoreNulls;
    }

    public boolean isRespectNulls() {
        return this.respectNulls;
    }

    public void setIgnoreNulls(boolean ignoreNulls) {
        this.ignoreNulls = ignoreNulls;
    }

    public void setRespectNulls(boolean respectNulls) {
        this.respectNulls = respectNulls;
    }

    public String toString() {
        return SQLUtils.toSQLString(this);
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            if (this.owner != null) {
                this.owner.accept(v);
            }

            for (SQLExpr arg : this.arguments) {
                if (arg != null) {
                    arg.accept(v);
                }
            }

            if (this.keep != null) {
                this.keep.accept(v);
            }

            if (this.filter != null) {
                this.filter.accept(v);
            }

            if (this.over != null) {
                this.over.accept(v);
            }

            if (this.overRef != null) {
                this.overRef.accept(v);
            }
            acceptChild(v, orderBy);
            acceptChild(v, limit);
        }

        v.endVisit(this);
    }

    @Override
    public List getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        children.addAll(this.arguments);
        if (keep != null) {
            children.add(this.keep);
        }
        if (over != null) {
            children.add(over);
        }
        if (orderBy != null) {
            children.add(orderBy);
        }
        return children;
    }

    public SQLExpr getFilter() {
        return filter;
    }

    public void setFilter(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }

        this.filter = x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SQLAggregateExpr that = (SQLAggregateExpr) o;

        if (option != that.option) {
            return false;
        }
        if (keep != null ? !keep.equals(that.keep) : that.keep != null) {
            return false;
        }
        if (filter != null ? !filter.equals(that.filter) : that.filter != null) {
            return false;
        }
        if (over != null ? !over.equals(that.over) : that.over != null) {
            return false;
        }
        if (overRef != null ? !overRef.equals(that.overRef) : that.overRef != null) {
            return false;
        }
        if (orderBy != null ? !orderBy.equals(that.orderBy) : that.orderBy != null) {
            return false;
        }
        return ignoreNulls == that.ignoreNulls && respectNulls == that.respectNulls;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (option != null ? option.hashCode() : 0);
        result = 31 * result + (keep != null ? keep.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        result = 31 * result + (over != null ? over.hashCode() : 0);
        result = 31 * result + (overRef != null ? overRef.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        return result;
    }

    public SQLAggregateExpr clone() {
        SQLAggregateExpr x = new SQLAggregateExpr(methodName);

        x.option = option;

        for (SQLExpr arg : arguments) {
            x.addArgument(arg.clone());
        }

        if (keep != null) {
            x.setKeep(keep.clone());
        }

        if (over != null) {
            x.setOver(over.clone());
        }

        if (overRef != null) {
            x.setOverRef(overRef.clone());
        }

        if (orderBy != null) {
            x.setOrderBy(orderBy.clone());
        }

        // 修复listagg wthin group语句转换错误的问题
        // https://github.com/alibaba/druid/issues/5930
        x.setWithinGroup(withinGroup);

        x.ignoreNulls = ignoreNulls;

        if (attributes != null) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof SQLObject) {
                    value = ((SQLObject) value).clone();
                }
                x.putAttribute(key, value);
            }
        }

        return x;
    }

    public SQLDataType computeDataType() {
        if (resolvedReturnDataType != null) {
            return resolvedReturnDataType;
        }

        long hash = methodNameHashCode64();

        if (hash == FnvHash.Constants.COUNT
                || hash == FnvHash.Constants.ROW_NUMBER) {
            return SQLIntegerExpr.DATA_TYPE;
        }

        if (arguments.size() > 0) {
            SQLDataType dataType = arguments.get(0)
                    .computeDataType();
            if (dataType != null
                    && (dataType.nameHashCode64() != FnvHash.Constants.BOOLEAN)) {
                return dataType;
            }
        }

        if (hash == FnvHash.Constants.SUM) {
            return SQLNumberExpr.DATA_TYPE_DOUBLE;
        }

        if (hash == FnvHash.Constants.WM_CONCAT
                || hash == FnvHash.Constants.GROUP_CONCAT) {
            return SQLCharExpr.DATA_TYPE;
        }

        return null;
    }

    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (target == null) {
            return false;
        }

        for (int i = 0; i < arguments.size(); ++i) {
            if (arguments.get(i) == expr) {
                arguments.set(i, target);
                target.setParent(this);
                return true;
            }
        }

        if (overRef == expr) {
            setOverRef((SQLName) target);
            return true;
        }

        if (filter != null) {
            filter = target;
            target.setParent(this);
        }

        return false;
    }
}
