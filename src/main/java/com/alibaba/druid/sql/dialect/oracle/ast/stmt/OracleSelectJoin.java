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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelectJoin extends SQLJoinTableSource implements OracleSelectTableSource {

    protected OracleSelectPivotBase pivot;

    public OracleSelectJoin(String alias){
        super(alias);
    }

    public OracleSelectJoin(){

    }

    public OracleSelectJoin(SQLTableSource left, JoinType joinType, SQLTableSource right, SQLExpr condition){
        super (left, joinType, right, condition);
    }

    public OracleSelectPivotBase getPivot() {
        return pivot;
    }

    public void setPivot(OracleSelectPivotBase pivot) {
        this.pivot = pivot;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OracleASTVisitor) {
            this.accept0((OracleASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.left);
            acceptChild(visitor, this.right);
            acceptChild(visitor, this.condition);
            acceptChild(visitor, this.using);
            acceptChild(visitor, this.flashback);
        }

        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OracleSelectJoin that = (OracleSelectJoin) o;

        if (pivot != null ? !pivot.equals(that.pivot) : that.pivot != null) return false;
        return flashback != null ? flashback.equals(that.flashback) : that.flashback == null;
    }

    @Override
    public int hashCode() {
        int result = pivot != null ? pivot.hashCode() : 0;
        result = 31 * result + (flashback != null ? flashback.hashCode() : 0);
        return result;
    }

    public String toString () {
        return SQLUtils.toOracleString(this);
    }

    public SQLJoinTableSource clone() {
        OracleSelectJoin x = new OracleSelectJoin();
        cloneTo(x);

        if (pivot != null) {
            x.setPivot(pivot.clone());
        }

        if (flashback != null) {
            x.setFlashback(flashback.clone());
        }

        return x;
    }

    public void setLeft(String tableName) {
        SQLExprTableSource tableSource;
        if (tableName == null || tableName.length() == 0) {
            tableSource = null;
        } else {
            tableSource = new OracleSelectTableReference(new SQLIdentifierExpr(tableName));
        }
        this.setLeft(tableSource);
    }

    public void setRight(String tableName) {
        SQLExprTableSource tableSource;
        if (tableName == null || tableName.length() == 0) {
            tableSource = null;
        } else {
            tableSource = new OracleSelectTableReference(new SQLIdentifierExpr(tableName));
        }
        this.setRight(tableSource);
    }

    public SQLJoinTableSource join(SQLTableSource right, JoinType joinType, SQLExpr condition) {
        SQLJoinTableSource joined = new OracleSelectJoin(this, joinType, right, condition);
        return joined;
    }
}
