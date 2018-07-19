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
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

public class SQLMethodInvokeExpr extends SQLExprImpl implements SQLReplaceable, Serializable {

    private static final long   serialVersionUID = 1L;
    private String              name;
    private SQLExpr             owner;
    private final List<SQLExpr> parameters       = new ArrayList<SQLExpr>();

    private SQLExpr             from;
    private SQLExpr             using;
    private SQLExpr             _for;

    private String              trimOption;

    private long                nameHashCode64;

    public SQLMethodInvokeExpr(){

    }

    public SQLMethodInvokeExpr(String methodName){
        this.name = methodName;
    }

    public SQLMethodInvokeExpr(String methodName, long nameHashCode64){
        this.name = methodName;
        this.nameHashCode64 = nameHashCode64;
    }

    public SQLMethodInvokeExpr(String methodName, SQLExpr owner){

        this.name = methodName;
        setOwner(owner);
    }

    public SQLMethodInvokeExpr(String methodName, SQLExpr owner, SQLExpr... params){
        this.name = methodName;
        setOwner(owner);
        for (SQLExpr param : params) {
            this.addParameter(param);
        }
    }

    public long methodNameHashCode64() {
        if (nameHashCode64 == 0
                && name != null) {
            nameHashCode64 = FnvHash.hashCode64(name);
        }
        return nameHashCode64;
    }

    public String getMethodName() {
        return this.name;
    }

    public void setMethodName(String methodName) {
        this.name = methodName;
        this.nameHashCode64 = 0L;
    }

    public SQLExpr getOwner() {
        return this.owner;
    }

    public void setOwner(SQLExpr owner) {
        if (owner != null) {
            owner.setParent(this);
        }
        this.owner = owner;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.from = x;
    }

    public List<SQLExpr> getParameters() {
        return this.parameters;
    }
    
    public void addParameter(SQLExpr param) {
        if (param != null) {
            param.setParent(this);
        }
        this.parameters.add(param);
    }

    public void addArgument(SQLExpr arg) {
        if (arg != null) {
            arg.setParent(this);
        }
        this.parameters.add(arg);
    }

    public void output(StringBuffer buf) {
        if (this.owner != null) {
            this.owner.output(buf);
            buf.append(".");
        }

        buf.append(this.name);
        buf.append("(");
        for (int i = 0, size = this.parameters.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }

            this.parameters.get(i).output(buf);
        }
        buf.append(")");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.owner);
            acceptChild(visitor, this.parameters);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.using);
            acceptChild(visitor, this._for);
        }

        visitor.endVisit(this);
    }

    public List getChildren() {
        if (this.owner == null) {
            return this.parameters;
        }

        List<SQLObject> children = new ArrayList<SQLObject>();
        children.add(owner);
        children.addAll(this.parameters);
        return children;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.owner);
            acceptChild(visitor, this.parameters);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.using);
            acceptChild(visitor, this._for);
        }

        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLMethodInvokeExpr that = (SQLMethodInvokeExpr) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        return from != null ? from.equals(that.from) : that.from == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        return result;
    }

    public SQLMethodInvokeExpr clone() {
        SQLMethodInvokeExpr x = new SQLMethodInvokeExpr();

        x.name = name;

        if (owner != null) {
            x.setOwner(owner.clone());
        }

        for (SQLExpr param : parameters) {
            x.addParameter(param.clone());
        }

        if (from != null) {
            x.setFrom(from.clone());
        }

        if (using != null) {
            x.setUsing(using.clone());
        }

        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (target == null) {
            return false;
        }

        for (int i = 0; i < parameters.size(); ++i) {
            if (parameters.get(i) == expr) {
                parameters.set(i, target);
                target.setParent(this);
                return true;
            }
        }

        if (from == expr) {
            setFrom(target);
            return true;
        }

        if (using == expr) {
            setUsing(target);
            return true;
        }

        if (_for == expr) {
            setFor(target);
            return true;
        }

        return false;
    }

    public boolean match(String owner, String function) {
        if (function == null) {
            return false;
        }

        if (!SQLUtils.nameEquals(function, name)) {
            return false;
        }

        if (owner == null && this.owner == null) {
            return true;
        }

        if (owner == null || this.owner == null) {
            return false;
        }

        if (this.owner instanceof SQLIdentifierExpr) {
            return SQLUtils.nameEquals(((SQLIdentifierExpr) this.owner).name, owner);
        }

        return false;
    }

    public SQLDataType computeDataType() {
        if (SQLUtils.nameEquals("to_date", name)
                || SQLUtils.nameEquals("add_months", name)) {
            return SQLDateExpr.DEFAULT_DATA_TYPE;
        }

        if (parameters.size() == 1) {
            if (SQLUtils.nameEquals("trunc", name)) {
                return parameters.get(0).computeDataType();
            }
        } else if (parameters.size() == 2) {
            SQLExpr param0 = parameters.get(0);
            SQLExpr param1 = parameters.get(1);
            if (SQLUtils.nameEquals("nvl", name) || SQLUtils.nameEquals("ifnull", name)) {
                SQLDataType dataType = param0.computeDataType();
                if (dataType != null) {
                    return dataType;
                }

                return param1.computeDataType();
            }
        }
        return null;
    }

    public SQLExpr getUsing() {
        return using;
    }

    public void setUsing(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.using = x;
    }

    public SQLExpr getFor() {
        return _for;
    }

    public void setFor(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this._for = x;
    }

    public String getTrimOption() {
        return trimOption;
    }

    public void setTrimOption(String trimOption) {
        this.trimOption = trimOption;
    }
}
