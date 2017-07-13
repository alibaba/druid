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
package com.alibaba.druid.sql.dialect.odps.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OdpsSelectQueryBlock extends SQLSelectQueryBlock {

    private SQLOrderBy orderBy;

    protected List<SQLHint> hints;

    protected List<SQLExpr>              distributeBy = new ArrayList<SQLExpr>();
    protected List<SQLSelectOrderByItem> sortBy = new ArrayList<SQLSelectOrderByItem>(2);

    public OdpsSelectQueryBlock(){

    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public List<SQLExpr> getDistributeBy() {
        return distributeBy;
    }

    public List<SQLSelectOrderByItem> getSortBy() {
        return sortBy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((limit == null) ? 0 : limit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        OdpsSelectQueryBlock other = (OdpsSelectQueryBlock) obj;
        if (limit == null) {
            if (other.limit != null) return false;
        } else if (!limit.equals(other.limit)) return false;
        return true;
    }

    public List<SQLHint> getHintsDirect() {
        return hints;
    }

    public List<SQLHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLHint>(2);
        }
        return hints;
    }

    public void setHints(List<SQLHint> hints) {
        this.hints = hints;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OdpsASTVisitor) {
            accept0((OdpsASTVisitor) visitor);
            return;
        }

        super.accept0(visitor);
    }

    public void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.distributeBy);
            acceptChild(visitor, this.sortBy);
            acceptChild(visitor, this.limit);
            acceptChild(visitor, this.into);
        }

        visitor.endVisit(this);
    }

    public String toString() {
        return SQLUtils.toOdpsString(this);
    }

    public void limit(int rowCount, int offset) {
        if (offset > 0) {
            throw new UnsupportedOperationException("not support offset");
        }

        setLimit(new SQLLimit(new SQLIntegerExpr(rowCount)));
    }
}
