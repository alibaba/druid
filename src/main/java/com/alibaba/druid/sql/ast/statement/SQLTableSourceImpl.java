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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.util.FNVUtils;

public abstract class SQLTableSourceImpl extends SQLObjectImpl implements SQLTableSource {

    protected String        alias;

    protected List<SQLHint> hints;

    protected SQLExpr       flashback;

    protected long          alias_hash;

    public SQLTableSourceImpl(){

    }

    public SQLTableSourceImpl(String alias){
        this.alias = alias;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
        this.alias_hash = 0L;
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

    public long alias_hash() {
        if (alias_hash == 0
                && alias != null) {
            alias_hash = FNVUtils.fnv_64_lower_normalized(alias);
        }
        return alias_hash;
    }

    public SQLColumnDefinition findColumn(String columnName) {
        return null;
    }

    public SQLColumnDefinition findColumn(long columnNameHash) {
        return null;
    }

    public SQLTableSource findTableSourceWithColumn(String columnName) {
        return null;
    }

    public SQLTableSource findTableSourceWithColumn(long columnNameHash) {
        return null;
    }

    public SQLTableSource findTableSource(String alias) {
        long hash = FNVUtils.fnv_64_lower_normalized(alias);
        return findTableSource(hash);
    }

    public SQLTableSource findTableSource(long alias_hash) {
        long hash = this.alias_hash();
        if (hash != 0 && hash == alias_hash) {
            return this;
        }
        return null;
    }
}
