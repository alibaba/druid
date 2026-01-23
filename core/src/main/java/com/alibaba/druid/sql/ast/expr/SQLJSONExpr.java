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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLJSONExpr extends SQLTypeExpr {
    public SQLJSONExpr() {
        super(new SQLDataTypeImpl(SQLDataType.Constants.JSON));
    }

    public SQLJSONExpr(String value) {
        this();
        this.value = value;
    }

    public SQLJSONExpr clone() {
        SQLJSONExpr x = new SQLJSONExpr(getValue());
        return x;
    }

    public String getValue() {
        return (String) value;
    }

    public void setValue(String literal) {
        this.value = literal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SQLJSONExpr that = (SQLJSONExpr) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public String toString() {
        return SQLUtils.toSQLString(this, (DbType) null);
    }
}
