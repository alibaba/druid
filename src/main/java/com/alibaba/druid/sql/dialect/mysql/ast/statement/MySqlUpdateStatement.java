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
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlUpdateStatement extends SQLUpdateStatement implements MySqlStatement {
    private SQLLimit                limit;

    private boolean                 lowPriority        = false;
    private boolean                 ignore             = false;
    private boolean                 commitOnSuccess    = false;
    private boolean                 rollBackOnFail     = false;
    private boolean                 queryOnPk          = false;
    private SQLExpr                 targetAffectRow;

    // for petadata
    private boolean                 forceAllPartitions = false;
    private SQLName                 forcePartition;

    protected List<SQLCommentHint>  hints;

    public MySqlUpdateStatement(){
        super(DbType.mysql);
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit limit) {
        if (limit != null) {
            limit.setParent(this);
        }
        this.limit = limit;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (tableSource != null) {
                tableSource.accept(visitor);
            }

            if (from != null) {
                from.accept(visitor);
            }

            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    SQLUpdateSetItem item = items.get(i);
                    if (item != null) {
                        item.accept(visitor);
                    }
                }
            }

            if (where != null) {
                where.accept(visitor);
            }

            if (orderBy != null) {
                orderBy.accept(visitor);
            }

            if (limit != null) {
                limit.accept(visitor);
            }


            if (hints != null) {
                for (int i = 0; i < hints.size(); i++) {
                    SQLCommentHint hint = hints.get(i);
                    if (hint != null) {
                        hint.accept(visitor);
                    }
                }
            }
        }
        visitor.endVisit(this);
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isCommitOnSuccess() {
        return commitOnSuccess;
    }

    public void setCommitOnSuccess(boolean commitOnSuccess) {
        this.commitOnSuccess = commitOnSuccess;
    }

    public boolean isRollBackOnFail() {
        return rollBackOnFail;
    }

    public void setRollBackOnFail(boolean rollBackOnFail) {
        this.rollBackOnFail = rollBackOnFail;
    }

    public boolean isQueryOnPk() {
        return queryOnPk;
    }

    public void setQueryOnPk(boolean queryOnPk) {
        this.queryOnPk = queryOnPk;
    }

    public SQLExpr getTargetAffectRow() {
        return targetAffectRow;
    }

    public void setTargetAffectRow(SQLExpr targetAffectRow) {
        if (targetAffectRow != null) {
            targetAffectRow.setParent(this);
        }
        this.targetAffectRow = targetAffectRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MySqlUpdateStatement that = (MySqlUpdateStatement) o;

        if (lowPriority != that.lowPriority) return false;
        if (ignore != that.ignore) return false;
        if (commitOnSuccess != that.commitOnSuccess) return false;
        if (rollBackOnFail != that.rollBackOnFail) return false;
        if (queryOnPk != that.queryOnPk) return false;
        if (this.hints != null ? hints.equals(that.hints) : that.hints != null) return false;
        if (limit != null ? !limit.equals(that.limit) : that.limit != null) return false;
        return targetAffectRow != null ? targetAffectRow.equals(that.targetAffectRow) : that.targetAffectRow == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (lowPriority ? 1 : 0);
        result = 31 * result + (ignore ? 1 : 0);
        result = 31 * result + (commitOnSuccess ? 1 : 0);
        result = 31 * result + (rollBackOnFail ? 1 : 0);
        result = 31 * result + (queryOnPk ? 1 : 0);
        result = 31 * result + (targetAffectRow != null ? targetAffectRow.hashCode() : 0);
        result = 31 * result + (hints != null ? hints.hashCode() : 0);
        return result;
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

    public boolean isForceAllPartitions() {
        return forceAllPartitions;
    }

    public void setForceAllPartitions(boolean forceAllPartitions) {
        this.forceAllPartitions = forceAllPartitions;
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

}
