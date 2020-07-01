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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLIntegerExpr extends SQLNumericLiteralExpr implements SQLValuableExpr{
    public static final SQLDataType DEFAULT_DATA_TYPE = new SQLDataTypeImpl("bigint");

    private Number number;

    public SQLIntegerExpr(Number number){

        this.number = number;
    }

    public SQLIntegerExpr(){

    }

    public Number getNumber() {
        return this.number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public void output(StringBuffer buf) {
        buf.append(this.number);
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((number == null) ? 0 : number.hashCode());
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
        SQLIntegerExpr other = (SQLIntegerExpr) obj;
        if (number == null) {
            if (other.number != null) {
                return false;
            }
        } else if (!number.equals(other.number)) {
            return false;
        }
        return true;
    }

    @Override
    public Object getValue() {
        return this.number;
    }

    public SQLIntegerExpr clone() {
        return new SQLIntegerExpr(this.number);
    }

    public SQLDataType computeDataType() {
        return DEFAULT_DATA_TYPE;
    }

}
