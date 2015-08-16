/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLMethodInvokeExpr extends SQLExprImpl implements Serializable {

    private static final long   serialVersionUID = 1L;
    private String              methodName;
    private SQLExpr             owner;
    private final List<SQLExpr> parameters       = new ArrayList<SQLExpr>();

    public SQLMethodInvokeExpr(){

    }

    public SQLMethodInvokeExpr(String methodName){
        this.methodName = methodName;
    }

    public SQLMethodInvokeExpr(String methodName, SQLExpr owner){

        this.methodName = methodName;
        setOwner(owner);
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLMethodInvokeExpr other = (SQLMethodInvokeExpr) obj;
        if (methodName == null) {
            if (other.methodName != null) {
                return false;
            }
        } else if (!methodName.equals(other.methodName)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        if (parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!parameters.equals(other.parameters)) {
            return false;
        }
        return true;
    }
}
