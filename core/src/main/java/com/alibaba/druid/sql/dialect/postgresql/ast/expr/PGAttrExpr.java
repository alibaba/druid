/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PGAttrExpr extends PGExprImpl implements SQLReplaceable {
    private SQLExpr name;
    private SQLExpr value;
    private PGExprMode mode;

    public static enum PGExprMode {
        EMPTY,
        EQ
    }

    public PGAttrExpr clone() {
        PGAttrExpr x = new PGAttrExpr();
        if (value != null) {
            x.setName(name.clone());
            x.setValue(value.clone());
            x.setMode(this.mode);
        }
        return x;
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr name) {
        this.name = name;
    }

    public SQLExpr getValue() { return value; }

    public void setValue(SQLExpr value) { this.value = value; }

    public PGExprMode getMode() { return this.mode; }

    public void setMode(PGExprMode mode) { this.mode = mode; }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, value);
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

    public List<SQLObject> getChildren() {
        return Collections.singletonList(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
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
        PGAttrExpr other = (PGAttrExpr) obj;

        return Objects.equals(this.name, other.name) &&
               Objects.equals(this.value, other.value) &&
               Objects.equals(this.mode, other.mode);
    }

}
