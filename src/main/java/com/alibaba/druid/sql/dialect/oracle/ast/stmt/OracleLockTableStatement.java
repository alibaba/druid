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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLLockTableStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleLockTableStatement extends OracleStatementImpl implements SQLLockTableStatement {

    private SQLExprTableSource table;
    private LockMode lockMode;
    private boolean  noWait = false;
    private SQLExpr  wait;
    private SQLExpr  partition;

    public boolean isNoWait() {
        return noWait;
    }

    public void setNoWait(boolean noWait) {
        this.noWait = noWait;
    }

    public SQLExpr getWait() {
        return wait;
    }

    public void setWait(SQLExpr wait) {
        this.wait = wait;
    }

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLExprTableSource table) {
        if (table != null) {
            table.setParent(this);
        }
        this.table = table;
    }

    public void setTable(SQLName table) {
        this.setTable(new SQLExprTableSource(table));
        this.table.setParent(this);
    }

    public LockMode getLockMode() {
        return lockMode;
    }

    public void setLockMode(LockMode lockMode) {
        this.lockMode = lockMode;
    }

    public SQLExpr getPartition() {
        return partition;
    }

    public void setPartition(SQLExpr partition) {
        this.partition = partition;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, table);
            acceptChild(visitor, partition);
            acceptChild(visitor, wait);
        }
        visitor.endVisit(this);
    }

    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (table != null) {
            children.add(table);
        }
        if (wait != null) {
            children.add(wait);
        }
        if (partition != null) {
            children.add(partition);
        }
        return children;
    }

    public static enum LockMode {
        ROW_SHARE,
        ROW_EXCLUSIVE,
        SHARE_UPDATE,
        SHARE,
        SHARE_ROW_EXCLUSIVE,
        EXCLUSIVE,
        ;

        public String toString() {
            switch (this) {
                case ROW_SHARE:
                    return "ROW SHARE";
                case ROW_EXCLUSIVE:
                    return "ROW EXCLUSIVE";
                case SHARE_UPDATE:
                    return "SHARE UPDATE";
                case SHARE_ROW_EXCLUSIVE:
                    return "SHARE ROW EXCLUSIVE";
                default:
                    return this.name();
            }
        }
    }
}
