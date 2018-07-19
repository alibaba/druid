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
package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

public class SQLAggregateExpr extends SQLExprImpl implements Serializable, SQLReplaceable {

    private static final long     serialVersionUID = 1L;

    protected String              methodName;
    protected long                methodNameHashCod64;

    protected SQLAggregateOption  option;
    protected final List<SQLExpr> arguments        = new ArrayList<SQLExpr>();
    protected SQLKeep             keep;
    protected SQLExpr             filter;
    protected SQLOver             over;
    protected SQLName             overRef;
    protected SQLOrderBy          withinGroup;
    protected Boolean             ignoreNulls      = false;

    public SQLAggregateExpr(String methodName){
        this.methodName = methodName;
    }

    public SQLAggregateExpr(String methodName, SQLAggregateOption option){
        this.methodName = methodName;
        this.option = option;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long methodNameHashCod64() {
        if (methodNameHashCod64 == 0) {
            methodNameHashCod64 = FnvHash.hashCode64(methodName);
        }
        return methodNameHashCod64;
    }

    public SQLOrderBy getWithinGroup() {
        return withinGroup;
    }

    public void setWithinGroup(SQLOrderBy withinGroup) {
        if (withinGroup != null) {
            withinGroup.setParent(this);
        }

        this.withinGroup = withinGroup;
    }

    public SQLAggregateOption getOption() {
        return this.option;
    }

    public void setOption(SQLAggregateOption option) {
        this.option = option;
    }

    public List<SQLExpr> getArguments() {
        return this.arguments;
    }
    
    public void addArgument(SQLExpr argument) {
        if (argument != null) {
            argument.setParent(this);
        }
        this.arguments.add(argument);
    }

    public SQLOver getOver() {
        return over;
    }

    public void setOver(SQLOver over) {
        if (over != null) {
            over.setParent(this);
        }
        this.over = over;
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
    
    public boolean isIgnoreNulls() {
        return this.ignoreNulls != null && this.ignoreNulls;
    }

    public Boolean getIgnoreNulls() {
        return this.ignoreNulls;
    }

    public void setIgnoreNulls(boolean ignoreNulls) {
        this.ignoreNulls = ignoreNulls;
    }

    public String toString() {
        return SQLUtils.toSQLString(this);
    }


    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.arguments);
            acceptChild(visitor, this.keep);
            acceptChild(visitor, this.over);
            acceptChild(visitor, this.overRef);
            acceptChild(visitor, this.withinGroup);
        }

        visitor.endVisit(this);
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
        if (withinGroup != null) {
            children.add(withinGroup);
        }
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLAggregateExpr that = (SQLAggregateExpr) o;

        if (methodNameHashCod64 != that.methodNameHashCod64) return false;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        if (option != that.option) return false;
        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
        if (keep != null ? !keep.equals(that.keep) : that.keep != null) return false;
        if (filter != null ? !filter.equals(that.filter) : that.filter != null) return false;
        if (over != null ? !over.equals(that.over) : that.over != null) return false;
        if (overRef != null ? !overRef.equals(that.overRef) : that.overRef != null) return false;
        if (withinGroup != null ? !withinGroup.equals(that.withinGroup) : that.withinGroup != null) return false;
        return ignoreNulls != null ? ignoreNulls.equals(that.ignoreNulls) : that.ignoreNulls == null;
    }

    @Override
    public int hashCode() {
        int result = methodName != null ? methodName.hashCode() : 0;
        result = 31 * result + (int) (methodNameHashCod64 ^ (methodNameHashCod64 >>> 32));
        result = 31 * result + (option != null ? option.hashCode() : 0);
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + (keep != null ? keep.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        result = 31 * result + (over != null ? over.hashCode() : 0);
        result = 31 * result + (overRef != null ? overRef.hashCode() : 0);
        result = 31 * result + (withinGroup != null ? withinGroup.hashCode() : 0);
        result = 31 * result + (ignoreNulls != null ? ignoreNulls.hashCode() : 0);
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

        if (filter != null) {
            x.setFilter(filter.clone());
        }

        if (over != null) {
            x.setOver(over.clone());
        }

        if (overRef != null) {
            x.setOverRef(overRef.clone());
        }

        if (withinGroup != null) {
            x.setWithinGroup(withinGroup.clone());
        }

        x.ignoreNulls = ignoreNulls;

        return x;
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

    public SQLDataType computeDataType() {
        long hash = methodNameHashCod64();

        if (hash == FnvHash.Constants.COUNT
                || hash == FnvHash.Constants.ROW_NUMBER) {
            return SQLIntegerExpr.DEFAULT_DATA_TYPE;
        }

        if (arguments.size() > 0) {
            SQLDataType dataType = arguments.get(0).computeDataType();
            if (dataType != null) {
                return dataType;
            }
        }

        if (hash == FnvHash.Constants.WM_CONCAT
                || hash == FnvHash.Constants.GROUP_CONCAT) {
            return SQLCharExpr.DEFAULT_DATA_TYPE;
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

        if (filter == expr) {
            this.filter = target;
            target.setParent(this);
        }

        return false;
    }
}
