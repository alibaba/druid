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
package com.alibaba.druid.sql.dialect.presto.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;

/**
 * presto 的 select语句
 * <p>
 * author zhangcanlong
 * date 2022/01/11
 */
public class PrestoSelectStatement extends SQLSelectStatement implements PrestoSQLStatement {
    public PrestoSelectStatement() {
        super(DbType.presto);
    }

    public PrestoSelectStatement(SQLSelect select) {
        super(select, DbType.presto);
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PrestoASTVisitor) {
            this.accept0((PrestoASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void accept0(PrestoASTVisitor visitor) {
        super.accept0(visitor);
    }

    @Override
    public PrestoSelectStatement clone() {
        PrestoSelectStatement x = new PrestoSelectStatement();
        x.dbType = dbType;
        x.afterSemi = afterSemi;
        if (select != null) {
            x.setSelect(select.clone());
        }
        if (headHints != null) {
            for (SQLCommentHint h : headHints) {
                SQLCommentHint h2 = h.clone();
                h2.setParent(x);
                if (x.headHints == null) {
                    x.headHints = new ArrayList<SQLCommentHint>(headHints.size());
                }
                x.headHints.add(h2);
            }
        }
        return x;
    }
}
