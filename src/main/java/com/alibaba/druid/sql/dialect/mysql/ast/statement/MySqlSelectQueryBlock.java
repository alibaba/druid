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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLWindow;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObject;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

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
        dbType = DbType.mysql;
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

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }

        return hints.size();
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MySqlSelectQueryBlock that = (MySqlSelectQueryBlock) o;

        if (hignPriority != that.hignPriority) return false;
        if (straightJoin != that.straightJoin) return false;
        if (smallResult != that.smallResult) return false;
        if (bigResult != that.bigResult) return false;
        if (bufferResult != that.bufferResult) return false;
        if (calcFoundRows != that.calcFoundRows) return false;
        if (lockInShareMode != that.lockInShareMode) return false;
        if (cache != null ? !cache.equals(that.cache) : that.cache != null) return false;
        if (procedureName != null ? !procedureName.equals(that.procedureName) : that.procedureName != null)
            return false;
        if (procedureArgumentList != null ? !procedureArgumentList.equals(that.procedureArgumentList) :
                that.procedureArgumentList != null) return false;
        if (hints != null ? !hints.equals(that.hints) : that.hints != null) return false;
        if (forcePartition != null ? !forcePartition.equals(that.forcePartition) : that.forcePartition != null)
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (hignPriority ? 1 : 0);
        result = 31 * result + (straightJoin ? 1 : 0);
        result = 31 * result + (smallResult ? 1 : 0);
        result = 31 * result + (bigResult ? 1 : 0);
        result = 31 * result + (bufferResult ? 1 : 0);
        result = 31 * result + (cache != null ? cache.hashCode() : 0);
        result = 31 * result + (calcFoundRows ? 1 : 0);
        result = 31 * result + (procedureName != null ? procedureName.hashCode() : 0);
        result = 31 * result + (procedureArgumentList != null ? procedureArgumentList.hashCode() : 0);
        result = 31 * result + (lockInShareMode ? 1 : 0);
        result = 31 * result + (hints != null ? hints.hashCode() : 0);
        result = 31 * result + (forcePartition != null ? forcePartition.hashCode() : 0);
        return result;
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
            for (int i = 0; i < this.selectList.size(); i++) {
                SQLSelectItem item = this.selectList.get(i);
                if (item != null) {
                    item.accept(visitor);
                }
            }

            if (this.from != null) {
                this.from.accept(visitor);
            }

            if (this.into != null) {
                this.into.accept(visitor);
            }

            if (this.where != null) {
                this.where.accept(visitor);
            }

            if (this.startWith != null) {
                this.startWith.accept(visitor);
            }

            if (this.connectBy != null) {
                this.connectBy.accept(visitor);
            }

            if (this.groupBy != null) {
                this.groupBy.accept(visitor);
            }

            if (this.windows != null) {
                for (SQLWindow item : windows) {
                    item.accept(visitor);
                }
            }

            if (this.orderBy != null) {
                this.orderBy.accept(visitor);
            }

            if (this.distributeBy != null) {
                for (int i = 0; i < distributeBy.size(); i++) {
                    SQLSelectOrderByItem item = distributeBy.get(i);
                    item.accept(visitor);
                }
            }

            if (this.sortBy != null) {
                for (int i = 0; i < sortBy.size(); i++) {
                    SQLSelectOrderByItem item = sortBy.get(i);
                    item.accept(visitor);
                }
            }

            if (this.waitTime != null) {
                this.waitTime.accept(visitor);
            }

            if (this.limit != null) {
                this.limit.accept(visitor);
            }

            if (this.procedureName != null) {
                this.procedureName.accept(visitor);
            }

            if (this.procedureArgumentList != null) {
                for (SQLExpr item : procedureArgumentList) {
                    item.accept(visitor);
                }
            }
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
