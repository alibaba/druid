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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

public class SQLAssignItem extends SQLExprImpl implements SQLReplaceable {

    private SQLExpr target;
    private SQLExpr value;

    public SQLAssignItem(){
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLAssignItem that = (SQLAssignItem) o;

        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public SQLAssignItem(SQLExpr target, SQLExpr value){
        setTarget(target);
        setValue(value);
    }

    public SQLAssignItem clone() {
        SQLAssignItem x = new SQLAssignItem();
        if (target != null) {
            x.setTarget(target.clone());
        }
        if (value != null) {
            x.setValue(value.clone());
        }
        return x;
    }

    @Override
    public List<SQLObject> getChildren() {
        return null;
    }

    public SQLExpr getTarget() {
        return target;
    }

    public void setTarget(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.target = x;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.value = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.target);
            acceptChild(visitor, this.value);
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.target == expr) {
            setTarget(target);
            return true;
        }

        if (this.value == expr) {
            setValue(target);
            return true;
        }
        return false;
    }
}
