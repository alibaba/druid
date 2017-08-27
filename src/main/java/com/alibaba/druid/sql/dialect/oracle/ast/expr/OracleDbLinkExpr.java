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
import com.alibaba.druid.util.FNVUtils;

public class OracleDbLinkExpr extends SQLExprImpl implements SQLName, OracleExpr {

    private SQLExpr expr;
    private String  dbLink;

    protected transient long dbLink_hash;

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

    public long name_hash_lower() {
        if (dbLink_hash == 0
                && dbLink != null) {
            final int len = dbLink.length();

            boolean quote = false;

            String name = this.dbLink;
            if (len > 2) {
                char c0 = name.charAt(0);
                char c1 = name.charAt(len - 1);
                if (c0 == c1
                        && (c0 == '`' || c1 == '"')) {
                    quote = true;
                }
            }
            if (quote) {
                dbLink_hash = FNVUtils.fnv_64_lower(name, 1, len -1);
            } else {
                dbLink_hash = FNVUtils.fnv_64_lower(name);
            }
        }
        return dbLink_hash;
    }
}
