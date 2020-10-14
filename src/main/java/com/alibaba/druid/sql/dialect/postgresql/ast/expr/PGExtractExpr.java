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
package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

import java.util.Collections;
import java.util.List;

public class PGExtractExpr extends PGExprImpl implements SQLReplaceable {

    private PGDateField field;
    private SQLExpr     source;

    public PGExtractExpr clone() {
        PGExtractExpr x = new PGExtractExpr();
        x.field = field;
        if (source != null) {
            x.setSource(source.clone());
        }
        return x;
    }

    public PGDateField getField() {
        return field;
    }

    public void setField(PGDateField field) {
        this.field = field;
    }

    public SQLExpr getSource() {
        return source;
    }

    public void setSource(SQLExpr source) {
        if (source != null) {
            source.setParent(this);
        }
        this.source = source;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.source == expr) {
            setSource(target);
            return true;
        }

        return false;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, source);
        }
        visitor.endVisit(this);
    }

    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(source);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
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
        PGExtractExpr other = (PGExtractExpr) obj;
        if (field != other.field) {
            return false;
        }
        if (source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!source.equals(other.source)) {
            return false;
        }
        return true;
    }

}
