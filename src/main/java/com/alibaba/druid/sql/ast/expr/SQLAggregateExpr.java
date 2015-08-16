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
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAggregateExpr extends SQLExprImpl implements Serializable {

    private static final long     serialVersionUID = 1L;
    protected String              methodName;
    protected SQLAggregateOption  option;
    protected final List<SQLExpr> arguments        = new ArrayList<SQLExpr>();
    protected SQLOver             over;
    protected SQLOrderBy          withinGroup;
    protected boolean             ignoreNulls      = false;

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

    public SQLOver getOver() {
        return over;
    }

    public void setOver(SQLOver over) {
        this.over = over;
    }
    
    public boolean isIgnoreNulls() {
        return this.ignoreNulls;
    }

    public void setIgnoreNulls(boolean ignoreNulls) {
        this.ignoreNulls = ignoreNulls;
    }


    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.arguments);
            acceptChild(visitor, this.over);
        }

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result + ((option == null) ? 0 : option.hashCode());
        result = prime * result + ((over == null) ? 0 : over.hashCode());
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
        SQLAggregateExpr other = (SQLAggregateExpr) obj;
        if (arguments == null) {
            if (other.arguments != null) {
                return false;
            }
        } else if (!arguments.equals(other.arguments)) {
            return false;
        }
        if (methodName == null) {
            if (other.methodName != null) {
                return false;
            }
        } else if (!methodName.equals(other.methodName)) {
            return false;
        }
        if (over == null) {
            if (other.over != null) {
                return false;
            }
        } else if (!over.equals(other.over)) {
            return false;
        }
        if (option != other.option) {
            return false;
        }
        return true;
    }

}
