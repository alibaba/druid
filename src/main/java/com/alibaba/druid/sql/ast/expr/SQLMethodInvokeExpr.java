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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLMethodInvokeExpr extends SQLExprImpl implements SQLReplaceable, Serializable {

    private static final long   serialVersionUID = 1L;
    private String              methodName;
    private SQLExpr             owner;
    private final List<SQLExpr> parameters       = new ArrayList<SQLExpr>();

    private SQLExpr             from;

    public SQLMethodInvokeExpr(){

    }

    public SQLMethodInvokeExpr(String methodName){
        this.methodName = methodName;
    }

    public SQLMethodInvokeExpr(String methodName, SQLExpr owner){

        this.methodName = methodName;
        setOwner(owner);
    }

    public SQLMethodInvokeExpr(String methodName, SQLExpr owner, SQLExpr... params){
        this.methodName = methodName;
        setOwner(owner);
        for (SQLExpr param : params) {
            this.addParameter(param);
        }
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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

    public void setFrom(SQLExpr from) {
        this.from = from;
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

    public void output(StringBuffer buf) {
        if (this.owner != null) {
            this.owner.output(buf);
            buf.append(".");
        }

        buf.append(this.methodName);
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
        }

        visitor.endVisit(this);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.owner);
            acceptChild(visitor, this.parameters);
        }

        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLMethodInvokeExpr that = (SQLMethodInvokeExpr) o;

        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        return from != null ? from.equals(that.from) : that.from == null;

    }

    @Override
    public int hashCode() {
        int result = methodName != null ? methodName.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        return result;
    }

    public SQLMethodInvokeExpr clone() {
        SQLMethodInvokeExpr x = new SQLMethodInvokeExpr();

        x.methodName = methodName;

        if (owner != null) {
            x.setOwner(owner.clone());
        }

        for (SQLExpr param : parameters) {
            x.addParameter(param.clone());
        }

        if (from != null) {
            x.setFrom(from.clone());
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
        return false;
    }

    public boolean match(String owner, String function) {
        if (function == null) {
            return false;
        }

        if (!SQLUtils.nameEquals(function, methodName)) {
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
        if (parameters.size() == 1) {
            if (SQLUtils.nameEquals("trunc", methodName)) {
                return parameters.get(0).computeDataType();
            }
        } else if (parameters.size() == 2) {
            SQLExpr param0 = parameters.get(0);
            SQLExpr param1 = parameters.get(1);
            if (SQLUtils.nameEquals("nvl", methodName) || SQLUtils.nameEquals("ifnull", methodName)) {
                SQLDataType dataType = param0.computeDataType();
                if (dataType != null) {
                    return dataType;
                }

                return param1.computeDataType();
            }
        }
        return null;
    }
}
