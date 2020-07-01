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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAssignItem extends SQLObjectImpl implements SQLReplaceable {

    private SQLExpr target;
    private SQLExpr value;

    public SQLAssignItem(){
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

    public SQLExpr getTarget() {
        return target;
    }

    public void setTarget(SQLExpr target) {
        if (target != null) {
            target.setParent(this);
        }
        this.target = target;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        if (value != null) {
            value.setParent(this);
        }
        this.value = value;
    }

    public void output(StringBuffer buf) {
        target.output(buf);
        buf.append(" = ");
        value.output(buf);
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
