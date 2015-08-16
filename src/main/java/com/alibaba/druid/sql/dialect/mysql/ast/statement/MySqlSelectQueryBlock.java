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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObject;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlSelectQueryBlock extends SQLSelectQueryBlock implements MySqlObject {

    private boolean              hignPriority;
    private boolean              straightJoin;

    private boolean              smallResult;
    private boolean              bigResult;
    private boolean              bufferResult;
    private Boolean              cache;
    private boolean              calcFoundRows;

    private SQLOrderBy           orderBy;

    private Limit                limit;

    private SQLName              procedureName;
    private List<SQLExpr>        procedureArgumentList;

    private boolean              forUpdate       = false;
    private boolean              lockInShareMode = false;

    private List<SQLCommentHint> hints;

    public MySqlSelectQueryBlock(){

    }

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }

        return hints.size();
    }

    public List<SQLCommentHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLCommentHint>(2);
        }
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
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
        if (procedureArgumentList == null) {
            procedureArgumentList = new ArrayList<SQLExpr>(2);
        }
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
        if (limit != null) {
            limit.setParent(this);
        }
        this.limit = limit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (bigResult ? 1231 : 1237);
        result = prime * result + (bufferResult ? 1231 : 1237);
        result = prime * result + ((cache == null) ? 0 : cache.hashCode());
        result = prime * result + (calcFoundRows ? 1231 : 1237);
        result = prime * result + (forUpdate ? 1231 : 1237);
        result = prime * result + (hignPriority ? 1231 : 1237);
        result = prime * result + ((hints == null) ? 0 : hints.hashCode());
        result = prime * result + ((limit == null) ? 0 : limit.hashCode());
        result = prime * result + (lockInShareMode ? 1231 : 1237);
        result = prime * result + ((orderBy == null) ? 0 : orderBy.hashCode());
        result = prime * result + ((procedureArgumentList == null) ? 0 : procedureArgumentList.hashCode());
        result = prime * result + ((procedureName == null) ? 0 : procedureName.hashCode());
        result = prime * result + (smallResult ? 1231 : 1237);
        result = prime * result + (straightJoin ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MySqlSelectQueryBlock other = (MySqlSelectQueryBlock) obj;
        if (bigResult != other.bigResult) return false;
        if (bufferResult != other.bufferResult) return false;
        if (cache == null) {
            if (other.cache != null) return false;
        } else if (!cache.equals(other.cache)) return false;
        if (calcFoundRows != other.calcFoundRows) return false;
        if (forUpdate != other.forUpdate) return false;
        if (hignPriority != other.hignPriority) return false;
        if (hints == null) {
            if (other.hints != null) return false;
        } else if (!hints.equals(other.hints)) return false;
        if (limit == null) {
            if (other.limit != null) return false;
        } else if (!limit.equals(other.limit)) return false;
        if (lockInShareMode != other.lockInShareMode) return false;
        if (orderBy == null) {
            if (other.orderBy != null) return false;
        } else if (!orderBy.equals(other.orderBy)) return false;
        if (procedureArgumentList == null) {
            if (other.procedureArgumentList != null) return false;
        } else if (!procedureArgumentList.equals(other.procedureArgumentList)) return false;
        if (procedureName == null) {
            if (other.procedureName != null) return false;
        } else if (!procedureName.equals(other.procedureName)) return false;
        if (smallResult != other.smallResult) return false;
        if (straightJoin != other.straightJoin) return false;
        return true;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
            return;
        }

        super.accept0(visitor);
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
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
        
        public Limit(SQLExpr rowCount){
            this.setRowCount(rowCount);
        }

        private SQLExpr rowCount;
        private SQLExpr offset;

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
            if (visitor instanceof MySqlASTVisitor) {
                MySqlASTVisitor mysqlVisitor = (MySqlASTVisitor) visitor;

                if (mysqlVisitor.visit(this)) {
                    acceptChild(visitor, offset);
                    acceptChild(visitor, rowCount);
                }
                mysqlVisitor.endVisit(this);
            }
        }

    }

}
