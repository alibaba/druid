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
package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

public class OracleDbLinkExpr extends SQLExprImpl implements SQLName, OracleExpr {

    private SQLExpr expr;
    private String  dbLink;

    private long    dbLinkHashCode64;
    private long    hashCode64;


    public OracleDbLinkExpr(){

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

    public void output(StringBuffer buf) {
        this.expr.output(buf);
        buf.append("@");
        buf.append(this.dbLink);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbLink == null) ? 0 : dbLink.hashCode());
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        return result;
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
        OracleDbLinkExpr other = (OracleDbLinkExpr) obj;
        if (dbLink == null) {
            if (other.dbLink != null) {
                return false;
            }
        } else if (!dbLink.equals(other.dbLink)) {
            return false;
        }
        if (expr == null) {
            if (other.expr != null) {
                return false;
            }
        } else if (!expr.equals(other.expr)) {
            return false;
        }
        return true;
    }

    public OracleDbLinkExpr clone() {
        OracleDbLinkExpr x = new OracleDbLinkExpr();

        if (expr != null) {
            expr = expr.clone();
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

                hash ^= '.';
                hash *= FnvHash.PRIME;
            } else if (expr == null){
                hash = FnvHash.BASIC;
            } else {
                hash = FnvHash.fnv_64_lower(expr.toString());

                hash ^= '.';
                hash *= FnvHash.PRIME;
            }
            hash = FnvHash.hashCode64(hash, dbLink);
            hashCode64 = hash;
        }

        return hashCode64;
    }
}
