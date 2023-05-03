/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

/**
 * @author lizongbo
 * @see <a href="https://docs.pingcap.com/tidb/stable/sql-statement-begin">BEGIN | TiDB SQL Statement Reference</a>
 */
public class SQLBeginStatement extends SQLStatementImpl implements SQLReplaceable {
    //tidb
    private SQLName tidbTxnMode;

    public SQLBeginStatement() {
    }

    public SQLBeginStatement(DbType dbType) {
        super(dbType);
    }

    public SQLName getTidbTxnMode() {
        return tidbTxnMode;
    }

    public void setTidbTxnMode(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.tidbTxnMode = x;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tidbTxnMode);
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.tidbTxnMode == expr) {
            setTidbTxnMode((SQLName) target);
            return true;
        }

        return false;
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.singletonList(tidbTxnMode);
    }
}
