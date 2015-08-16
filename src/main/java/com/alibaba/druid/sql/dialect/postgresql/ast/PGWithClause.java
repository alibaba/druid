/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.postgresql.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public class PGWithClause extends PGSQLObjectImpl {

    private boolean           recursive = false;
    private List<PGWithQuery> withQuery = new ArrayList<PGWithQuery>(2);

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public List<PGWithQuery> getWithQuery() {
        return withQuery;
    }

    public void setWithQuery(List<PGWithQuery> withQuery) {
        this.withQuery = withQuery;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, withQuery);
        }
        visitor.endVisit(this);
    }
}
