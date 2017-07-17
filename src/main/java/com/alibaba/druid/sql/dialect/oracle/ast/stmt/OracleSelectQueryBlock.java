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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelectQueryBlock extends SQLSelectQueryBlock {

    private List<SQLCommentHint>       hints;

    private ModelClause                modelClause;

    private List<SQLExpr>              forUpdateOf;
    private boolean                    skipLocked  = false;

    public OracleSelectQueryBlock clone() {
        OracleSelectQueryBlock x = new OracleSelectQueryBlock();

        super.cloneTo(x);

        if (hints != null) {
            for (SQLCommentHint hint : hints) {
                SQLCommentHint hint1 = hint.clone();
                hint1.setParent(x);
                x.getHints().add(hint1);
            }
        }

        if (modelClause != null) {
            x.setModelClause(modelClause.clone());
        }

        if (forUpdateOf != null) {
            for (SQLExpr item : forUpdateOf) {
                SQLExpr item1 = item.clone();
                item1.setParent(x);
                forUpdateOf.add(item1);
            }
        }

        x.skipLocked = skipLocked;

        return x;
    }

    public OracleSelectQueryBlock(){

    }

    public ModelClause getModelClause() {
        return modelClause;
    }

    public void setModelClause(ModelClause modelClause) {
        this.modelClause = modelClause;
    }

    public List<SQLCommentHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLCommentHint>(1);
        }
        return this.hints;
    }

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }

        return hints.size();
    }

    public List<SQLExpr> getForUpdateOf() {
        if (forUpdateOf == null) {
            forUpdateOf = new ArrayList<SQLExpr>(1);
        }
        return forUpdateOf;
    }

    public int getForUpdateOfSize() {
        if (forUpdateOf == null) {
            return 0;
        }

        return forUpdateOf.size();
    }

    public boolean isSkipLocked() {
        return skipLocked;
    }

    public void setSkipLocked(boolean skipLocked) {
        this.skipLocked = skipLocked;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) visitor);
            return;
        }

        super.accept0(visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.into);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.startWith);
            acceptChild(visitor, this.connectBy);
            acceptChild(visitor, this.groupBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.waitTime);
            acceptChild(visitor, this.limit);
            acceptChild(visitor, this.modelClause);
            acceptChild(visitor, this.forUpdateOf);
        }
        visitor.endVisit(this);
    }
    
    public String toString() {
        return SQLUtils.toOracleString(this);
    }

    public void limit(int rowCount, int offset) {
        if (offset <= 0) {
            SQLExpr rowCountExpr = new SQLIntegerExpr(rowCount);
            SQLExpr newCondition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, rowCountExpr, false,
                    where);
            setWhere(newCondition);
        } else {
            throw new UnsupportedOperationException("not support offset");
        }
    }

    public void setFrom(String tableName) {
        SQLExprTableSource from;
        if (tableName == null || tableName.length() == 0) {
            from = null;
        } else {
            from = new OracleSelectTableReference(new SQLIdentifierExpr(tableName));
        }
        this.setFrom(from);
    }
}
