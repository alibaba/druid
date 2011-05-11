/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.sql.dialect.oracle.ast;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalType;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleDataTypeInterval extends SQLDataTypeImpl {
    private static final long serialVersionUID = 1L;

    private OracleIntervalType type;
    private OracleIntervalType toType;
    private SQLIntegerExpr precision;
    private SQLIntegerExpr fractionalSecondsPrecision;

    public OracleDataTypeInterval() {

    }

    public OracleIntervalType getType() {
        return this.type;
    }

    public void setType(OracleIntervalType type) {
        this.type = type;
    }

    public OracleIntervalType getToType() {
        return this.toType;
    }

    public void setToType(OracleIntervalType toType) {
        this.toType = toType;
    }

    public SQLIntegerExpr getPrecision() {
        return this.precision;
    }

    public void setPrecision(SQLIntegerExpr precision) {
        this.precision = precision;
    }

    public SQLIntegerExpr getFractionalSecondsPrecision() {
        return this.fractionalSecondsPrecision;
    }

    public void setFractionalSecondsPrecision(SQLIntegerExpr fractionalSecondsPrecision) {
        this.fractionalSecondsPrecision = fractionalSecondsPrecision;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.precision);
            acceptChild(visitor, this.fractionalSecondsPrecision);
        }

        visitor.endVisit(this);
    }
}
