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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

@SuppressWarnings("serial")
public class MySqlSelectQueryBlock extends SQLSelectQueryBlock {

    private boolean       hignPriority;
    private boolean       straightJoin;

    private boolean       smallResult;
    private boolean       bigResult;
    private boolean       bufferResult;
    private Boolean       cache;
    private boolean       calcFoundRows;

    private SQLOrderBy    orderBy;

    private Limit         limit;

    private SQLName       procedureName;
    private List<SQLExpr> procedureArgumentList = new ArrayList<SQLExpr>();

    private boolean       forUpdate             = false;
    private boolean       lockInShareMode       = false;

    public MySqlSelectQueryBlock(){

    }

    public boolean isForUpdate() {
        return forUpdate;
    }

    public void setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public boolean isLockInShareMode() {
        return lockInShareMode;
    }

    public void setLockInShareMode(boolean lockInShareMode) {
        this.lockInShareMode = lockInShareMode;
    }

    public SQLName getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(SQLName procedureName) {
        this.procedureName = procedureName;
    }

    public List<SQLExpr> getProcedureArgumentList() {
        return procedureArgumentList;
    }

    public void setProcedureArgumentList(List<SQLExpr> procedureArgumentList) {
        this.procedureArgumentList = procedureArgumentList;
    }

    public boolean isHignPriority() {
        return hignPriority;
    }

    public void setHignPriority(boolean hignPriority) {
        this.hignPriority = hignPriority;
    }

    public boolean isStraightJoin() {
        return straightJoin;
    }

    public void setStraightJoin(boolean straightJoin) {
        this.straightJoin = straightJoin;
    }

    public boolean isSmallResult() {
        return smallResult;
    }

    public void setSmallResult(boolean smallResult) {
        this.smallResult = smallResult;
    }

    public boolean isBigResult() {
        return bigResult;
    }

    public void setBigResult(boolean bigResult) {
        this.bigResult = bigResult;
    }

    public boolean isBufferResult() {
        return bufferResult;
    }

    public void setBufferResult(boolean bufferResult) {
        this.bufferResult = bufferResult;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public boolean isCalcFoundRows() {
        return calcFoundRows;
    }

    public void setCalcFoundRows(boolean calcFoundRows) {
        this.calcFoundRows = calcFoundRows;
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.limit);
            acceptChild(visitor, this.procedureName);
            acceptChild(visitor, this.procedureArgumentList);
            acceptChild(visitor, this.into);
        }

        visitor.endVisit(this);
    }

    public static class Limit extends SQLObjectImpl {

        public Limit(){

        }

        private SQLExpr rowCount;
        private SQLExpr offset;

        public SQLExpr getRowCount() {
            return rowCount;
        }

        public void setRowCount(SQLExpr rowCount) {
            this.rowCount = rowCount;
        }

        public SQLExpr getOffset() {
            return offset;
        }

        public void setOffset(SQLExpr offset) {
            this.offset = offset;
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            if (visitor instanceof MySqlASTVisitor) {
                MySqlASTVisitor mysqlVisitor = (MySqlASTVisitor) visitor;

                if (mysqlVisitor.visit(this)) {
                    acceptChild(visitor, offset);
                    acceptChild(visitor, rowCount);
                }
            }
        }

    }

}
