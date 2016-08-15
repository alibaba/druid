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
package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleIntervalExpr extends SQLExprImpl implements SQLLiteralExpr, OracleExpr {

    private SQLExpr            value;
    private OracleIntervalType type;
    private Integer            precision;
    private Integer            factionalSecondsPrecision;
    private OracleIntervalType toType;
    private Integer            toFactionalSecondsPrecision;

    public OracleIntervalExpr(){

    }

    public SQLExpr getValue() {
        return this.value;
    }

    public void setValue(SQLExpr value) {
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

    public void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((factionalSecondsPrecision == null) ? 0 : factionalSecondsPrecision.hashCode());
        result = prime * result + ((precision == null) ? 0 : precision.hashCode());
        result = prime * result + ((toFactionalSecondsPrecision == null) ? 0 : toFactionalSecondsPrecision.hashCode());
        result = prime * result + ((toType == null) ? 0 : toType.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        OracleIntervalExpr other = (OracleIntervalExpr) obj;
        if (factionalSecondsPrecision == null) {
            if (other.factionalSecondsPrecision != null) {
                return false;
            }
        } else if (!factionalSecondsPrecision.equals(other.factionalSecondsPrecision)) {
            return false;
        }
        if (precision == null) {
            if (other.precision != null) {
                return false;
            }
        } else if (!precision.equals(other.precision)) {
            return false;
        }
        if (toFactionalSecondsPrecision == null) {
            if (other.toFactionalSecondsPrecision != null) {
                return false;
            }
        } else if (!toFactionalSecondsPrecision.equals(other.toFactionalSecondsPrecision)) {
            return false;
        }
        if (toType != other.toType) {
            return false;
        }
        if (type != other.type) {
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

}
