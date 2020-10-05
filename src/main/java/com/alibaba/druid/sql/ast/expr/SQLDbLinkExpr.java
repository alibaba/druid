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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.util.Collections;
import java.util.List;

public class SQLDbLinkExpr extends SQLExprImpl implements SQLName, SQLExpr, SQLReplaceable {

    private SQLExpr expr;
    private String  dbLink;

    private long    dbLinkHashCode64;
    private long    hashCode64;


    public SQLDbLinkExpr(){

    }

    public String getSimpleName() {
        return dbLink;
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    public String getDbLink() {
        return this.dbLink;
    }

    public void setDbLink(String dbLink) {
        this.dbLink = dbLink;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        visitor.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.expr == expr) {
            setExpr(target);
            return true;
        }
        return false;
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(this.expr);
    }

    @Override
    public int hashCode() {
        long value = hashCode64();
        return (int)(value ^ (value >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLDbLinkExpr other = (SQLDbLinkExpr) obj;
        return this.hashCode64() == other.hashCode64();
    }

    public SQLDbLinkExpr clone() {
        SQLDbLinkExpr x = new SQLDbLinkExpr();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.dbLink = dbLink;
        return x;
    }

    public long nameHashCode64() {
        if (dbLinkHashCode64 == 0
                && dbLink != null) {
            dbLinkHashCode64 = FnvHash.hashCode64(dbLink);
        }
        return dbLinkHashCode64;
    }

    @Override
    public long hashCode64() {
        if (hashCode64 == 0) {
            long hash;
            if (expr instanceof SQLName) {
                hash = ((SQLName) expr).hashCode64();

                hash ^= '@';
                hash *= FnvHash.PRIME;
            } else if (expr == null){
                hash = FnvHash.BASIC;
            } else {
                hash = FnvHash.fnv1a_64_lower(expr.toString());

                hash ^= '@';
                hash *= FnvHash.PRIME;
            }
            hash = FnvHash.hashCode64(hash, dbLink);
            hashCode64 = hash;
        }

        return hashCode64;
    }

    @Override
    public SQLColumnDefinition getResolvedColumn() {
        return null;
    }
}
