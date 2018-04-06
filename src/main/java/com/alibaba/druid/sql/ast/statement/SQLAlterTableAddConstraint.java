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

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableAddConstraint extends SQLObjectImpl implements SQLAlterTableItem {

    private SQLConstraint constraint;
    private boolean      withNoCheck = false;

    public SQLAlterTableAddConstraint(){

    }

    public SQLAlterTableAddConstraint(SQLConstraint constraint){
        this.setConstraint(constraint);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, constraint);
        }
        visitor.endVisit(this);
    }

    public SQLConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(SQLConstraint constraint) {
        if (constraint != null) {
            constraint.setParent(this);
        }
        this.constraint = constraint;
    }

    public boolean isWithNoCheck() {
        return withNoCheck;
    }

    public void setWithNoCheck(boolean withNoCheck) {
        this.withNoCheck = withNoCheck;
    }

}
