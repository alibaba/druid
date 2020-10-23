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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLGroupingSetExpr extends SQLExprImpl implements SQLReplaceable {

    private final List<SQLExpr> parameters = new ArrayList<SQLExpr>();

    public SQLGroupingSetExpr clone() {
        SQLGroupingSetExpr x = new SQLGroupingSetExpr();
        for (SQLExpr p : parameters) {
            SQLExpr p2 = p.clone();
            p2.setParent(x);
            x.parameters.add(p2);
        }
        return x;
    }

    public List<SQLExpr> getParameters() {
        return parameters;
    }
    
    public void addParameter(SQLExpr parameter) {
        if (parameter != null) {
            parameter.setParent(this);
        }
        this.parameters.add(parameter);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, parameters);
        }
        visitor.endVisit(this);
    }

    @Override
    public List getChildren() {
        return this.parameters;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + parameters.hashCode();
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
        if (!(obj instanceof SQLGroupingSetExpr)) {
            return false;
        }
        SQLGroupingSetExpr other = (SQLGroupingSetExpr) obj;
        if (!parameters.equals(other.parameters)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i) == expr) {
                target.setParent(this);
                parameters.set(i, target);
                return true;
            }
        }
        return false;
    }
}
