/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObject;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlSelectQueryBlock extends SQLSelectQueryBlock implements MySqlObject {
    private boolean              hignPriority;
    private boolean              straightJoin;
    private boolean              smallResult;
    private boolean              bigResult;
    private boolean              bufferResult;
    private Boolean              cache;
    private boolean              calcFoundRows;
    private SQLName              procedureName;
    private List<SQLExpr>        procedureArgumentList;
    private boolean              lockInShareMode;
    private SQLName              forcePartition; // for petadata

    public MySqlSelectQueryBlock(){
        dbType = JdbcConstants.MYSQL;
    }

    public MySqlSelectQueryBlock clone() {
        MySqlSelectQueryBlock x = new MySqlSelectQueryBlock();
        cloneTo(x);

        x.hignPriority = hignPriority;
        x.straightJoin = straightJoin;

        x.smallResult = smallResult;
        x.bigResult = bigResult;
        x.bufferResult = bufferResult;
        x.cache = cache;
        x.calcFoundRows = calcFoundRows;

        if (procedureName != null) {
            x.setProcedureName(procedureName.clone());
        }
        if (procedureArgumentList != null) {
            for (SQLExpr arg : procedureArgumentList) {
                SQLExpr arg_cloned = arg.clone();
                arg_cloned.setParent(this);
                x.procedureArgumentList.add(arg_cloned);
            }
        }
        x.lockInShareMode = lockInShareMode;

        return x;
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
            acceptChild(visitor, this.forcePartition);
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

    public SQLName getForcePartition() {
        return forcePartition;
    }

    public void setForcePartition(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.forcePartition = x;
    }

    public String toString() {
        return SQLUtils.toMySqlString(this);
    }
}
