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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelect extends SQLSelect {

    private OracleSelectForUpdate   forUpdate;
    private OracleSelectRestriction restriction;

    public OracleSelect(){

    }

    public OracleSelectRestriction getRestriction() {
        return this.restriction;
    }

    public void setRestriction(OracleSelectRestriction restriction) {
        this.restriction = restriction;
    }

    public OracleSelectForUpdate getForUpdate() {
        return this.forUpdate;
    }

    public void setForUpdate(OracleSelectForUpdate forUpdate) {
        this.forUpdate = forUpdate;
    }

    public void output(StringBuffer buf) {
        this.query.output(buf);
        buf.append(" ");

        if (this.orderBy != null) {
            this.orderBy.output(buf);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.withSubQuery);
            acceptChild(visitor, this.query);
            acceptChild(visitor, this.restriction);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.forUpdate);
        }
        visitor.endVisit(this);
    }
    
    public String toString() {
        return SQLUtils.toOracleString(this);
    }
}
