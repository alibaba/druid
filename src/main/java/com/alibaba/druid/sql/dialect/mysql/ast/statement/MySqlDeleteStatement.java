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
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlDeleteStatement extends SQLDeleteStatement {

    private boolean              lowPriority        = false;
    private boolean              quick              = false;
    private boolean              ignore             = false;
    private SQLOrderBy           orderBy;
    private SQLLimit             limit;
    // for petadata
    private boolean              forceAllPartitions = false;
    private SQLName              forcePartition;

    private List<SQLCommentHint> hints;

    private boolean fulltextDictionary = false;

    public MySqlDeleteStatement(){
        super(DbType.mysql);
    }

    public MySqlDeleteStatement clone() {
        MySqlDeleteStatement x = new MySqlDeleteStatement();
        cloneTo(x);

        x.lowPriority = lowPriority;
        x.quick = quick;
        x.ignore = ignore;
        x.fulltextDictionary = fulltextDictionary;

        if (using != null) {
            x.setUsing(using.clone());
        }
        if (orderBy != null) {
            x.setOrderBy(orderBy.clone());
        }
        if (limit != null) {
            x.setLimit(limit.clone());
        }

        return x;
    }

    public List<SQLCommentHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLCommentHint>();
        }
        return hints;
    }
    
    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }
        
        return hints.size();
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isQuick() {
        return quick;
    }

    public void setQuick(boolean quick) {
        this.quick = quick;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        this.orderBy = orderBy;
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

    public boolean isFulltextDictionary() {
        return fulltextDictionary;
    }

    public void setFulltextDictionary(boolean fulltextDictionary) {
        this.fulltextDictionary = fulltextDictionary;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (with != null) {
                with.accept(visitor);
            }

            if (tableSource != null) {
                tableSource.accept(visitor);
            }

            if (where != null) {
                where.accept(visitor);
            }

            if (from != null) {
                from.accept(visitor);
            }

            if (using != null) {
                using.accept(visitor);
            }

            if (orderBy != null) {
                orderBy.accept(visitor);
            }

            if (limit != null) {
                limit.accept(visitor);
            }
        }

        visitor.endVisit(this);
    }

    public boolean isForceAllPartitions() {
        return forceAllPartitions;
    }

    public void setForceAllPartitions(boolean forceAllPartitions) {
        this.forceAllPartitions = forceAllPartitions;
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
}
