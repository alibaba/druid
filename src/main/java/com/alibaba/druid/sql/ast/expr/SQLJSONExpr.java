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
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLJSONExpr extends SQLExprImpl implements SQLValuableExpr {
    public static final SQLDataType DATA_TYPE = new SQLDataTypeImpl("JSON");

    protected String  literal;

    public SQLJSONExpr(){

    }

    public SQLJSONExpr(String literal){
        this.literal = literal;
    }

    public SQLJSONExpr clone() {
        SQLJSONExpr x = new SQLJSONExpr(literal);
        return x;
    }

    public String getValue() {
        return literal;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLJSONExpr that = (SQLJSONExpr) o;

        return literal != null ? literal.equals(that.literal) : that.literal == null;
    }

    @Override
    public int hashCode() {
        return literal != null ? literal.hashCode() : 0;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public String toString() {
        return SQLUtils.toSQLString(this, (DbType) null);
    }

    public SQLDataType computeDataType() {
        return DATA_TYPE;
    }

    @Override
    public List getChildren() {
        return Collections.emptyList();
    }
}
