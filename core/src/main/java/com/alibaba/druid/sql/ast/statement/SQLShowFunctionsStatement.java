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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLShowFunctionsStatement extends SQLStatementImpl implements SQLShowStatement, SQLReplaceable {
    protected SQLExpr like;

    public SQLExpr getLike() {
        return like;
    }

    public void setLike(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.like = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (like != null) {
                like.accept(visitor);
            }
        }
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (like == expr) {
            setLike(target);
            return true;
        }

        return false;
    }
}
