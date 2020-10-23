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
package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSQLObject;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSQLObjectImpl;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class PGSelectQueryBlock extends SQLSelectQueryBlock implements PGSQLObject{

    private List<SQLExpr> distinctOn = new ArrayList<SQLExpr>(2);

    private FetchClause   fetch;
    private ForClause     forClause;
    private IntoOption    intoOption;

    public static enum IntoOption {
        TEMPORARY, TEMP, UNLOGGED
    }

    public PGSelectQueryBlock() {
        dbType = DbType.postgresql;
    }

    public IntoOption getIntoOption() {
        return intoOption;
    }

    public void setIntoOption(IntoOption intoOption) {
        this.intoOption = intoOption;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PGASTVisitor) {
            accept0((PGASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.distinctOn);
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.into);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
            acceptChild(visitor, this.windows);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.limit);
            acceptChild(visitor, this.fetch);
            acceptChild(visitor, this.forClause);
        }
        visitor.endVisit(this);
    }

    public FetchClause getFetch() {
        return fetch;
    }

    public void setFetch(FetchClause fetch) {
        this.fetch = fetch;
    }

    public ForClause getForClause() {
        return forClause;
    }

    public void setForClause(ForClause forClause) {
        this.forClause = forClause;
    }

    public List<SQLExpr> getDistinctOn() {
        return distinctOn;
    }

    public void setDistinctOn(List<SQLExpr> distinctOn) {
        this.distinctOn = distinctOn;
    }

    public static class FetchClause extends PGSQLObjectImpl {

        public static enum Option {
            FIRST, NEXT
        }

        private Option  option;
        private SQLExpr count;

        public Option getOption() {
            return option;
        }

        public void setOption(Option option) {
            this.option = option;
        }

        public SQLExpr getCount() {
            return count;
        }

        public void setCount(SQLExpr count) {
            this.count = count;
        }

        @Override
        public void accept0(PGASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, count);
            }
            visitor.endVisit(this);
        }

    }

    public static class ForClause extends PGSQLObjectImpl {

        public static enum Option {
            UPDATE, SHARE
        }

        private List<SQLExpr> of = new ArrayList<SQLExpr>(2);
        private boolean       noWait;
        private Option        option;

        public Option getOption() {
            return option;
        }

        public void setOption(Option option) {
            this.option = option;
        }

        public List<SQLExpr> getOf() {
            return of;
        }

        public void setOf(List<SQLExpr> of) {
            this.of = of;
        }

        public boolean isNoWait() {
            return noWait;
        }

        public void setNoWait(boolean noWait) {
            this.noWait = noWait;
        }

        @Override
        public void accept0(PGASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, of);
            }
            visitor.endVisit(this);
        }
    }



}
