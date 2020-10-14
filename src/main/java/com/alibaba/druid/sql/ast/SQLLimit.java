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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObject;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.HashMap;

/**
 * Created by wenshao on 16/9/25.
 */
public final class SQLLimit extends SQLObjectImpl implements SQLReplaceable {
    private SQLExpr rowCount;
    private SQLExpr offset;

    public SQLLimit() {

    }

    public SQLLimit(int rowCount) {
        this.setRowCount(new SQLIntegerExpr(rowCount));
    }

    public SQLLimit(SQLExpr rowCount) {
        this.setRowCount(rowCount);
    }

    public SQLLimit(SQLExpr offset, SQLExpr rowCount) {
        this.setOffset(offset);
        this.setRowCount(rowCount);
    }


    public SQLExpr getRowCount() {
        return rowCount;
    }

    public void setRowCount(SQLExpr rowCount) {
        if (rowCount != null) {
            rowCount.setParent(this);
        }
        this.rowCount = rowCount;
    }

    public void setRowCount(int rowCount) {
        this.setRowCount(new SQLIntegerExpr(rowCount));
    }

    public SQLExpr getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.setOffset(new SQLIntegerExpr(offset));
    }

    public void setOffset(SQLExpr offset) {
        if (offset != null) {
            offset.setParent(this);
        }
        this.offset = offset;
    }

    public void merge(SQLLimit limit) {
        if (limit == null) {
            return;
        }

        if (limit.offset != null) {
            if (this.offset == null) {
                this.offset = limit.offset.clone();
            }
        }

        if (limit.rowCount != null) {
            if (this.rowCount == null) {
                this.rowCount = limit.rowCount.clone();
            }
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (offset != null) {
                offset.accept(visitor);
            }

            if (rowCount != null) {
                rowCount.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public SQLLimit clone() {
        SQLLimit x = new SQLLimit();

        if (offset != null) {
            x.setOffset(offset.clone());
        }

        if (rowCount != null) {
            x.setRowCount(rowCount.clone());
        }

        if (attributes != null) {
            x.attributes = (HashMap) ((HashMap) attributes).clone();
        }

        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (rowCount == expr) {
            setRowCount(target);
            return true;
        }

        if (offset == expr) {
            setOffset(target);
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLLimit limit = (SQLLimit) o;

        if (rowCount != null ? !rowCount.equals(limit.rowCount) : limit.rowCount != null) return false;
        return offset != null ? offset.equals(limit.offset) : limit.offset == null;
    }

    @Override
    public int hashCode() {
        int result = rowCount != null ? rowCount.hashCode() : 0;
        result = 31 * result + (offset != null ? offset.hashCode() : 0);
        return result;
    }
}
