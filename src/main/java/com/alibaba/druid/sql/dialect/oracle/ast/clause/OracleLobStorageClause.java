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
package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSegmentAttributesImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleLobStorageClause extends OracleSegmentAttributesImpl implements OracleSQLObject {

    private SQLName             segementName;

    private final List<SQLName> items      = new ArrayList<SQLName>();

    private boolean             secureFile = false;
    private boolean             basicFile  = false;


    private Boolean             enable;

    private SQLExpr             chunk;

    private Boolean             cache;
    private Boolean             logging;

    private Boolean             compress;
    private Boolean             keepDuplicate;
    private boolean             retention;

    private OracleStorageClause storageClause;

    private SQLExpr             pctversion;

    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, segementName);
            acceptChild(visitor, items);
            acceptChild(visitor, tablespace);
        }
        visitor.endVisit(this);
    }

    public void cloneTo(OracleLobStorageClause x) {
        super.cloneTo(x);

        if (segementName != null) {
            x.setSegementName(segementName.clone());
        }

        for (SQLName item : items) {
            SQLName item2 = item.clone();
            item2.setParent(x);
            x.items.add(item2);
        }

        x.secureFile = secureFile;
        x.basicFile = basicFile;
        x.enable = enable;

        if (chunk != null) {
            x.setChunk(chunk.clone());
        }

        x.cache = cache;
        x.logging = logging;
        x.compress = compress;
        x.keepDuplicate = keepDuplicate;
        x.retention = retention;

        if (storageClause != null) {
            x.setStorageClause(storageClause.clone());
        }

        if (pctversion != null) {
            x.setPctversion(pctversion.clone());
        }
    }

    public OracleLobStorageClause clone() {
        OracleLobStorageClause x = new OracleLobStorageClause();
        cloneTo(x);
        return x;
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

    public boolean isRetention() {
        return retention;
    }

    public void setRetention(boolean retention) {
        this.retention = retention;
    }

    public OracleStorageClause getStorageClause() {
        return storageClause;
    }

    public void setStorageClause(OracleStorageClause storageClause) {
        if (storageClause != null) {
            storageClause.setParent(this);
        }
        this.storageClause = storageClause;
    }

    public SQLExpr getPctversion() {
        return pctversion;
    }

    public void setPctversion(SQLExpr pctversion) {
        if (pctversion != null) {
            pctversion.setParent(this);
        }
        this.pctversion = pctversion;
    }

    public SQLName getSegementName() {
        return segementName;
    }

    public void setSegementName(SQLName segementName) {
        this.segementName = segementName;
    }
}
