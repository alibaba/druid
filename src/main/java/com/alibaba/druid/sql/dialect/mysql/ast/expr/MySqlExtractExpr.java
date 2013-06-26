/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlExtractExpr extends SQLExprImpl implements MySqlExpr {

    private static final long serialVersionUID = 1L;

    private SQLExpr           value;
    private MySqlIntervalUnit unit;

    public MySqlExtractExpr(){
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public MySqlIntervalUnit getUnit() {
        return unit;
    }

    public void setUnit(MySqlIntervalUnit unit) {
        this.unit = unit;
    }

    protected void accept0(SQLASTVisitor visitor) {
        MySqlASTVisitor mysqlVisitor = (MySqlASTVisitor) visitor;
        mysqlVisitor.visit(this);
        mysqlVisitor.endVisit(this);
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
        if (!(obj instanceof MySqlExtractExpr)) {
            return false;
        }
        MySqlExtractExpr other = (MySqlExtractExpr) obj;
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

}
