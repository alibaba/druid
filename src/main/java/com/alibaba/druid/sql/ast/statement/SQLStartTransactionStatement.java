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
package com.alibaba.druid.sql.ast.statement;

import java.util.Collections;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLStartTransactionStatement extends SQLStatementImpl {

    private boolean              consistentSnapshot = false;

    private boolean              begin              = false;
    private boolean              work               = false;
    private SQLExpr              name;

    private List<SQLCommentHint> hints;

    public boolean isConsistentSnapshot() {
        return consistentSnapshot;
    }

    public void setConsistentSnapshot(boolean consistentSnapshot) {
        this.consistentSnapshot = consistentSnapshot;
    }

    public boolean isBegin() {
        return begin;
    }

    public void setBegin(boolean begin) {
        this.begin = begin;
    }

    public boolean isWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    public void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    @Override
    public List<SQLObject> getChildren() {
        if (name != null) {
            return Collections.<SQLObject>singletonList(name);
        }
        return Collections.emptyList();
    }
}
