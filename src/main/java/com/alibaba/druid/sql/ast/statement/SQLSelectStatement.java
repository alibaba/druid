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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLSelectStatement extends SQLStatementImpl {

    protected SQLSelect select;

    public SQLSelectStatement(){

    }

    public SQLSelectStatement(DbType dbType){
        super (dbType);
    }

    public SQLSelectStatement(SQLSelect select){
        this.setSelect(select);
    }

    public SQLSelectStatement(SQLSelect select, DbType dbType){
        this(dbType);
        this.setSelect(select);
    }

    public SQLSelect getSelect() {
        return this.select;
    }

    public void setSelect(SQLSelect select) {
        if (select != null) {
            select.setParent(this);
        }
        this.select = select;
    }

    public void output(Appendable buf) {
        this.select.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.select != null) {
                this.select.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public SQLSelectStatement clone() {
        SQLSelectStatement x = new SQLSelectStatement();
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

    @Override
    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(select);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLSelectStatement that = (SQLSelectStatement) o;

        return select != null ? select.equals(that.select) : that.select == null;
    }

    @Override
    public int hashCode() {
        return select != null ? select.hashCode() : 0;
    }

    public List<String> computeSelecteListAlias() {
        return select.computeSelecteListAlias();
    }

    public boolean addWhere(SQLExpr where) {
        return select.addWhere(where);
    }
}
