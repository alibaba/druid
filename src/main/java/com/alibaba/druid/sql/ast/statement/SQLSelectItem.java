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
import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SQLSelectItem extends SQLObjectImpl implements SQLReplaceable {

    protected SQLExpr expr;
    protected String  alias;

    protected boolean connectByRoot = false;
    protected transient long aliasHashCode64;
    protected List<String> aliasList;

    public SQLSelectItem(){

    }

    public SQLSelectItem(SQLExpr expr){
        this(expr, null);
    }

    public SQLSelectItem(int value){
        this(new SQLIntegerExpr(value), null);
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

    public SQLSelectItem(SQLExpr expr, List<String> aliasList, boolean connectByRoot){
        this.connectByRoot = connectByRoot;
        this.expr = expr;
        this.aliasList = aliasList;

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

    public String getAlias2() {
        if (this.alias == null || this.alias.length() == 0) {
            return alias;
        }

        char first = alias.charAt(0);
        if (first == '"' || first == '\'') {
            char[] chars = new char[alias.length() - 2];
            int len = 0;
            for (int i = 1; i < alias.length() - 1; ++i) {
                char ch = alias.charAt(i);
                if (ch == '\\') {
                    ++i;
                    ch = alias.charAt(i);
                }
                chars[len++] = ch;
            }
            return new String(chars, 0, len);
        }

        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void output(Appendable buf) {
        try {
            if (this.connectByRoot) {
                buf.append(" CONNECT_BY_ROOT ");
            }
            this.expr.output(buf);
            if ((this.alias != null) && (this.alias.length() != 0)) {
                buf.append(" AS ");
                buf.append(this.alias);
            }
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            if (expr != null) {
                expr.accept(v);
            }
        }
        v.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SQLSelectItem that = (SQLSelectItem) o;

        if (connectByRoot != that.connectByRoot) {
            return false;
        }
        if (alias_hash() != that.alias_hash()) {
            return false;
        }
        if (expr != null ? !expr.equals(that.expr) : that.expr != null) {
            return false;
        }

        return aliasList != null
                ? aliasList.equals(that.aliasList)
                : that.aliasList == null;
    }

    @Override
    public int hashCode() {
        int result = expr != null ? expr.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (connectByRoot ? 1 : 0);
        result = 31 * result + (int) (alias_hash() ^ (alias_hash() >>> 32));
        result = 31 * result + (aliasList != null ? aliasList.hashCode() : 0);
        return result;
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
        if (aliasList != null) {
            x.aliasList = new ArrayList<String>(aliasList);
        }
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

            return alias == null && ((SQLPropertyExpr) expr).nameHashCode64() == alias_hash;
        }

        return false;
    }

    public List<String> getAliasList() {
        return aliasList;
    }

    public String toString() {
        DbType dbType = null;
        if (parent instanceof OracleSQLObject) {
            dbType = DbType.oracle;
        }
        return SQLUtils.toSQLString(this, dbType);
    }

    public boolean isUDTFSelectItem() {
        return aliasList != null && aliasList.size() > 0;
    }
}
