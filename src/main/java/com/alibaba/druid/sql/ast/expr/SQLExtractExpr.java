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

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLExtractExpr extends SQLExprImpl implements SQLReplaceable {

    private SQLExpr         value;
    private SQLIntervalUnit unit;

    public SQLExtractExpr(){
    }

    public SQLExtractExpr clone() {
        SQLExtractExpr x = new SQLExtractExpr();
        if (value != null) {
            x.setValue(value.clone());
        }
        x.unit = unit;
        return x;
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

    public SQLIntervalUnit getUnit() {
        return unit;
    }

    public void setUnit(SQLIntervalUnit unit) {
        this.unit = unit;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.value != null) {
                this.value.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.value == expr) {
            setValue(target);
            return true;
        }
        return false;
    }

    @Override
    public List getChildren() {
        return Collections.singletonList(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        if (!(obj instanceof SQLExtractExpr)) {
            return false;
        }
        SQLExtractExpr other = (SQLExtractExpr) obj;
        if (unit != other.unit) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    public SQLDataType computeDataType() {
        return SQLIntegerExpr.DATA_TYPE;
    }
}
