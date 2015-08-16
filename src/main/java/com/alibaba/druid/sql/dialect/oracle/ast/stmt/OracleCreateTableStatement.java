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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartitioningClause;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTableStatement extends SQLCreateTableStatement implements OracleDDLStatement {

    private SQLName                 tablespace;

    private SQLSelect               select;

    private boolean                 inMemoryMetadata;

    private boolean                 cursorSpecificSegment;

    // NOPARALLEL
    private Boolean                 parallel;

    private OracleStorageClause     storage;
    private OracleLobStorageClause  lobStorage;

    private boolean                 organizationIndex = false;

    private SQLExpr                 ptcfree;
    private SQLExpr                 pctused;
    private SQLExpr                 initrans;
    private SQLExpr                 maxtrans;

    private Boolean                 logging;
    private Boolean                 compress;
    private boolean                 onCommit;
    private boolean                 preserveRows;

    private Boolean                 cache;

    private SQLPartitioningClause   partitioning;

    private DeferredSegmentCreation deferredSegmentCreation;
    
    public OracleCreateTableStatement() {
        super (JdbcConstants.ORACLE);
    }

    public OracleLobStorageClause getLobStorage() {
        return lobStorage;
    }

    public void setLobStorage(OracleLobStorageClause lobStorage) {
        this.lobStorage = lobStorage;
    }

    public DeferredSegmentCreation getDeferredSegmentCreation() {
        return deferredSegmentCreation;
    }

    public void setDeferredSegmentCreation(DeferredSegmentCreation deferredSegmentCreation) {
        this.deferredSegmentCreation = deferredSegmentCreation;
    }

    public SQLPartitioningClause getPartitioning() {
        return partitioning;
    }

    public void setPartitioning(SQLPartitioningClause partitioning) {
        this.partitioning = partitioning;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public boolean isOnCommit() {
        return onCommit;
    }

    public void setOnCommit(boolean onCommit) {
        this.onCommit = onCommit;
    }

    public boolean isPreserveRows() {
        return preserveRows;
    }

    public void setPreserveRows(boolean preserveRows) {
        this.preserveRows = preserveRows;
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

    public SQLExpr getPtcfree() {
        return ptcfree;
    }

    public void setPtcfree(SQLExpr ptcfree) {
        this.ptcfree = ptcfree;
    }

    public SQLExpr getPctused() {
        return pctused;
    }

    public void setPctused(SQLExpr pctused) {
        this.pctused = pctused;
    }

    public SQLExpr getInitrans() {
        return initrans;
    }

    public void setInitrans(SQLExpr initrans) {
        this.initrans = initrans;
    }

    public SQLExpr getMaxtrans() {
        return maxtrans;
    }

    public void setMaxtrans(SQLExpr maxtrans) {
        this.maxtrans = maxtrans;
    }

    public boolean isOrganizationIndex() {
        return organizationIndex;
    }

    public void setOrganizationIndex(boolean organizationIndex) {
        this.organizationIndex = organizationIndex;
    }

    public Boolean getParallel() {
        return parallel;
    }

    public void setParallel(Boolean parallel) {
        this.parallel = parallel;
    }

    public boolean isCursorSpecificSegment() {
        return cursorSpecificSegment;
    }

    public void setCursorSpecificSegment(boolean cursorSpecificSegment) {
        this.cursorSpecificSegment = cursorSpecificSegment;
    }

    public boolean isInMemoryMetadata() {
        return inMemoryMetadata;
    }

    public void setInMemoryMetadata(boolean inMemoryMetadata) {
        this.inMemoryMetadata = inMemoryMetadata;
    }

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName tablespace) {
        this.tablespace = tablespace;
    }

    public SQLSelect getSelect() {
        return select;
    }

    public void setSelect(SQLSelect select) {
        this.select = select;
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    public OracleStorageClause getStorage() {
        return storage;
    }

    public void setStorage(OracleStorageClause storage) {
        this.storage = storage;
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, tableElementList);
            this.acceptChild(visitor, tablespace);
            this.acceptChild(visitor, select);
            this.acceptChild(visitor, storage);
            this.acceptChild(visitor, partitioning);
        }
        visitor.endVisit(this);
    }

    public static enum DeferredSegmentCreation {
        IMMEDIATE, DEFERRED
    }
}
