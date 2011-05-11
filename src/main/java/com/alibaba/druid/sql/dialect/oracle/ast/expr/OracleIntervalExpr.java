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
package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleIntervalExpr extends SQLLiteralExpr {
    private String value;
    private OracleIntervalType type;
    private Integer precision;
    private Integer factionalSecondsPrecision;
    private OracleIntervalType toType;
    private Integer toFactionalSecondsPrecision;

    public OracleIntervalExpr() {

    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public OracleIntervalType getType() {
        return this.type;
    }

    public void setType(OracleIntervalType type) {
        this.type = type;
    }

    public Integer getPrecision() {
        return this.precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getFactionalSecondsPrecision() {
        return this.factionalSecondsPrecision;
    }

    public void setFactionalSecondsPrecision(Integer factionalSecondsPrecision) {
        this.factionalSecondsPrecision = factionalSecondsPrecision;
    }

    public OracleIntervalType getToType() {
        return this.toType;
    }

    public void setToType(OracleIntervalType toType) {
        this.toType = toType;
    }

    public Integer getToFactionalSecondsPrecision() {
        return this.toFactionalSecondsPrecision;
    }

    public void setToFactionalSecondsPrecision(Integer toFactionalSecondsPrecision) {
        this.toFactionalSecondsPrecision = toFactionalSecondsPrecision;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
