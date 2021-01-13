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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLTableSourceImpl extends SQLObjectImpl implements SQLTableSource {
    protected String        alias;
    protected List<SQLHint> hints;
    protected SQLExpr       flashback;
    protected long aliasHashCode64;

    public SQLTableSourceImpl(){

    }

    public SQLTableSourceImpl(String alias){
        this.alias = alias;
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
        this.aliasHashCode64 = 0L;
    }

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }

        return hints.size();
    }

    public List<SQLHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLHint>(2);
        }
        return hints;
    }

    public void setHints(List<SQLHint> hints) {
        this.hints = hints;
    }

    public SQLTableSource clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public String computeAlias() {
        return alias;
    }

    public SQLExpr getFlashback() {
        return flashback;
    }

    public void setFlashback(SQLExpr flashback) {
        if (flashback != null) {
            flashback.setParent(this);
        }
        this.flashback = flashback;
    }

    public boolean containsAlias(String alias) {
        if (SQLUtils.nameEquals(this.alias, alias)) {
            return true;
        }

        return false;
    }

    public long aliasHashCode64() {
        if (aliasHashCode64 == 0
                && alias != null) {
            aliasHashCode64 = FnvHash.hashCode64(alias);
        }
        return aliasHashCode64;
    }

    public SQLColumnDefinition findColumn(String columnName) {
        if (columnName == null) {
            return null;
        }

        long hash = FnvHash.hashCode64(alias);
        return findColumn(hash);
    }

    public SQLColumnDefinition findColumn(long columnNameHash) {
        return null;
    }

    public SQLObject resolveColum(long columnNameHash) {
        return findColumn(columnNameHash);
    }

    public SQLTableSource findTableSourceWithColumn(String columnName) {
        if (columnName == null) {
            return null;
        }

        long hash = FnvHash.hashCode64(alias);
        return findTableSourceWithColumn(hash, columnName, 0);
    }

    public SQLTableSource findTableSourceWithColumn(SQLName columnName) {
        if (columnName instanceof SQLIdentifierExpr) {
            return findTableSourceWithColumn(
                    columnName.nameHashCode64(), columnName.getSimpleName(), 0);
        }

        if (columnName instanceof SQLPropertyExpr) {
            SQLExpr owner = ((SQLPropertyExpr) columnName).getOwner();
            if (owner instanceof SQLIdentifierExpr) {
                return findTableSource(((SQLIdentifierExpr) owner).nameHashCode64());
            }
        }

        return null;
    }

    public SQLTableSource findTableSourceWithColumn(long columnNameHash) {
        return findTableSourceWithColumn(columnNameHash, null, 0);
    }

    public SQLTableSource findTableSourceWithColumn(long columnNameHash, String columnName, int option) {
        return null;
    }

    public SQLTableSource findTableSource(String alias) {
        long hash = FnvHash.hashCode64(alias);
        return findTableSource(hash);
    }

    public SQLTableSource findTableSource(long alias_hash) {
        long hash = this.aliasHashCode64();
        if (hash != 0 && hash == alias_hash) {
            return this;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLTableSourceImpl that = (SQLTableSourceImpl) o;

        if (aliasHashCode64() != that.aliasHashCode64()) return false;
        if (hints != null ? !hints.equals(that.hints) : that.hints != null) return false;
        return flashback != null ? flashback.equals(that.flashback) : that.flashback == null;
    }

    @Override
    public int hashCode() {
        int result = (hints != null ? hints.hashCode() : 0);
        result = 31 * result + (flashback != null ? flashback.hashCode() : 0);
        result = 31 * result + (int) (aliasHashCode64() ^ (aliasHashCode64() >>> 32));
        return result;
    }
}
