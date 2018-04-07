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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

public class SQLSelectItem extends SQLObjectImpl implements SQLReplaceable {

    protected SQLExpr expr;
    protected String  alias;

    protected boolean connectByRoot = false;

    protected transient long aliasHashCode64;

    public SQLSelectItem(){

    }

    public SQLSelectItem(SQLExpr expr){
        this(expr, null);
    }

    public SQLSelectItem(SQLExpr expr, String alias){
        this.expr = expr;
        this.alias = alias;

        if (expr != null) {
            expr.setParent(this);
        }
    }
    
    public SQLSelectItem(SQLExpr expr, String alias, boolean connectByRoot){
        this.connectByRoot = connectByRoot;
        this.expr = expr;
        this.alias = alias;
        
        if (expr != null) {
            expr.setParent(this);
        }
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    public String computeAlias() {
        String alias = this.getAlias();
        if (alias == null) {
            if (expr instanceof SQLIdentifierExpr) {
                alias = ((SQLIdentifierExpr) expr).getName();
            } else if (expr instanceof SQLPropertyExpr) {
                alias = ((SQLPropertyExpr) expr).getName();
            }
        }

        return SQLUtils.normalize(alias);
    }

    public SQLDataType computeDataType() {
        if (expr == null) {
            return null;
        }

        return expr.computeDataType();
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void output(StringBuffer buf) {
        if(this.connectByRoot) {
            buf.append(" CONNECT_BY_ROOT ");
        }
        this.expr.output(buf);
        if ((this.alias != null) && (this.alias.length() != 0)) {
            buf.append(" AS ");
            buf.append(this.alias);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SQLSelectItem other = (SQLSelectItem) obj;
        if (alias == null) {
            if (other.alias != null) return false;
        } else if (!alias.equals(other.alias)) return false;
        if (expr == null) {
            if (other.expr != null) return false;
        } else if (!expr.equals(other.expr)) return false;
        return true;
    }

    public boolean isConnectByRoot() {
        return connectByRoot;
    }

    public void setConnectByRoot(boolean connectByRoot) {
        this.connectByRoot = connectByRoot;
    }

    public SQLSelectItem clone() {
        SQLSelectItem x = new SQLSelectItem();
        x.alias = alias;
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.connectByRoot = connectByRoot;
        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.expr == expr) {
            setExpr(target);
            return true;
        }

        return false;
    }

    public boolean match(String alias) {
        if (alias == null) {
            return false;
        }

        long hash = FnvHash.hashCode64(alias);
        return match(hash);
    }

    public long alias_hash() {
        if (this.aliasHashCode64 == 0) {
            this.aliasHashCode64 = FnvHash.hashCode64(alias);
        }
        return aliasHashCode64;
    }

    public boolean match(long alias_hash) {
        long hash = alias_hash();

        if (hash == alias_hash) {
            return true;
        }

        if (expr instanceof SQLAllColumnExpr) {
            SQLTableSource resolvedTableSource = ((SQLAllColumnExpr) expr).getResolvedTableSource();
            if (resolvedTableSource != null
                    && resolvedTableSource.findColumn(alias_hash) != null) {
                return true;
            }
            return false;
        }

        if (expr instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) expr).nameHashCode64() == alias_hash;
        }

        if (expr instanceof SQLPropertyExpr) {
            String ident = ((SQLPropertyExpr) expr).getName();
            if ("*".equals(ident)) {
                SQLTableSource resolvedTableSource = ((SQLPropertyExpr) expr).getResolvedTableSource();
                if (resolvedTableSource != null
                        && resolvedTableSource.findColumn(alias_hash) != null) {
                    return true;
                }
                return false;
            }

            return ((SQLPropertyExpr) expr).nameHashCode64() == alias_hash;
        }

        return false;
    }

    public String toString() {
        String dbType = null;
        if (parent instanceof OracleSQLObject) {
            dbType = JdbcConstants.ORACLE;
        }
        return SQLUtils.toSQLString(this, dbType);
    }
}
