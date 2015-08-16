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
package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleLobStorageClause extends OracleSQLObjectImpl {

    private final List<SQLName> items      = new ArrayList<SQLName>();

    private boolean             secureFile = false;
    private boolean             basicFile  = false;

    private SQLName             tableSpace;

    private Boolean             enable;

    private SQLExpr             chunk;

    private Boolean             cache;
    private Boolean             logging;

    private Boolean             compress;
    private Boolean             keepDuplicate;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, items);
            acceptChild(visitor, tableSpace);
        }
        visitor.endVisit(this);
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public SQLExpr getChunk() {
        return chunk;
    }

    public void setChunk(SQLExpr chunk) {
        this.chunk = chunk;
    }

    public List<SQLName> getItems() {
        return items;
    }

    public boolean isSecureFile() {
        return secureFile;
    }

    public void setSecureFile(boolean secureFile) {
        this.secureFile = secureFile;
    }

    public boolean isBasicFile() {
        return basicFile;
    }

    public void setBasicFile(boolean basicFile) {
        this.basicFile = basicFile;
    }

    public SQLName getTableSpace() {
        return tableSpace;
    }

    public void setTableSpace(SQLName tableSpace) {
        this.tableSpace = tableSpace;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Boolean getLogging() {
        return logging;
    }

    public void setLogging(Boolean logging) {
        this.logging = logging;
    }

    public Boolean getCompress() {
        return compress;
    }

    public void setCompress(Boolean compress) {
        this.compress = compress;
    }

    public Boolean getKeepDuplicate() {
        return keepDuplicate;
    }

    public void setKeepDuplicate(Boolean keepDuplicate) {
        this.keepDuplicate = keepDuplicate;
    }

}
