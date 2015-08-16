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
package com.alibaba.druid.sql.dialect.sqlserver.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLServerSelect extends SQLSelect implements SQLServerObject {

    private boolean      forBrowse;
    private List<String> forXmlOptions = new ArrayList<String>(4);

    private SQLExpr      rowCount;
    private SQLExpr      offset;

    public boolean isForBrowse() {
        return forBrowse;
    }

    public void setForBrowse(boolean forBrowse) {
        this.forBrowse = forBrowse;
    }

    public List<String> getForXmlOptions() {
        return forXmlOptions;
    }

    public void setForXmlOptions(List<String> forXmlOptions) {
        this.forXmlOptions = forXmlOptions;
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

    public SQLExpr getOffset() {
        return offset;
    }

    public void setOffset(SQLExpr offset) {
        if (offset != null) {
            offset.setParent(this);
        }
        this.offset = offset;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((SQLServerASTVisitor) visitor);
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.query);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.offset);
            acceptChild(visitor, this.rowCount);
        }

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (forBrowse ? 1231 : 1237);
        result = prime * result + ((forXmlOptions == null) ? 0 : forXmlOptions.hashCode());
        result = prime * result + ((offset == null) ? 0 : offset.hashCode());
        result = prime * result + ((rowCount == null) ? 0 : rowCount.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        SQLServerSelect other = (SQLServerSelect) obj;
        if (forBrowse != other.forBrowse) return false;
        if (forXmlOptions == null) {
            if (other.forXmlOptions != null) return false;
        } else if (!forXmlOptions.equals(other.forXmlOptions)) return false;
        if (offset == null) {
            if (other.offset != null) return false;
        } else if (!offset.equals(other.offset)) return false;
        if (rowCount == null) {
            if (other.rowCount != null) return false;
        } else if (!rowCount.equals(other.rowCount)) return false;
        return true;
    }

}
