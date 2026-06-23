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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSegmentAttributes;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSegmentAttributesImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleCreateTableStatement extends SQLCreateTableStatement implements OracleDDLStatement, OracleSegmentAttributes {
    private boolean inMemoryMetadata;

    private boolean cursorSpecificSegment;

    // NOPARALLEL
    private Boolean parallel;
    private SQLExpr parallelValue;

    private OracleStorageClause storage;
    private OracleLobStorageClause lobStorage;

    private Integer pctfree;
    private Integer pctused;
    private Integer initrans;
    private Integer maxtrans;
    private Integer pctincrease;

    private Integer compressLevel;
    private boolean compressForOltp;

    private Boolean cache;

    private DeferredSegmentCreation deferredSegmentCreation;

    private Boolean enableRowMovement;

    private List<SQLName> clusterColumns = new ArrayList<SQLName>();
    private SQLName cluster;

    private Organization organization;

    private SQLName of;
    private OIDIndex oidIndex;
    private boolean monitoring;
    private List<SQLName> including = new ArrayList<SQLName>();
    private OracleXmlColumnProperties xmlTypeColumnProperties;

    public void simplify() {
        tablespace = null;
        storage = null;
        lobStorage = null;

        pctfree = null;
        pctused = null;
        initrans = null;
        maxtrans = null;
        pctincrease = null;

        logging = null;
        compress = null;
        compressLevel = null;
        compressForOltp = false;

        onCommitPreserveRows = false;
        onCommitDeleteRows = false;

        super.simplify();
    }

    public OracleCreateTableStatement() {
        super(DbType.oracle);
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

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public boolean isOnCommitDeleteRows() {
        return onCommitDeleteRows;
    }

    public void setOnCommitDeleteRows(boolean onCommitDeleteRows) {
        this.onCommitDeleteRows = onCommitDeleteRows;
    }

    public Integer getCompressLevel() {
        return compressLevel;
    }

    public void setCompressLevel(Integer compressLevel) {
        this.compressLevel = compressLevel;
    }

    public Integer getPctfree() {
        return pctfree;
    }

    public void setPctfree(Integer pctfree) {
        this.pctfree = pctfree;
    }

    public Integer getPctused() {
        return pctused;
    }

    public void setPctused(Integer pctused) {
        this.pctused = pctused;
    }

    public Integer getInitrans() {
        return initrans;
    }

    public void setInitrans(Integer initrans) {
        this.initrans = initrans;
    }

    public Integer getMaxtrans() {
        return maxtrans;
    }

    public void setMaxtrans(Integer maxtrans) {
        this.maxtrans = maxtrans;
    }

    public Integer getPctincrease() {
        return pctincrease;
    }

    public void setPctincrease(Integer pctincrease) {
        this.pctincrease = pctincrease;
    }

    public Boolean getParallel() {
        return parallel;
    }

    public void setParallel(Boolean parallel) {
        this.parallel = parallel;
    }

    public SQLExpr getParallelValue() {
        return parallelValue;
    }

    public void setParallelValue(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.parallelValue = x;
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

    protected void accept0(SQLASTVisitor v) {
        if (v instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    public OracleStorageClause getStorage() {
        return storage;
    }

    public void setStorage(SQLObject storage) {
        if (storage != null) {
            storage.setParent(this);
        }
        this.storage = (OracleStorageClause) storage;
    }

    public SQLName getOf() {
        return of;
    }

    public void setOf(SQLName of) {
        if (of != null) {
            of.setParent(this);
        }
        this.of = of;
    }

    public OIDIndex getOidIndex() {
        return oidIndex;
    }

    public void setOidIndex(OIDIndex oidIndex) {
        if (oidIndex != null) {
            oidIndex.setParent(this);
        }
        this.oidIndex = oidIndex;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean monitoring) {
        this.monitoring = monitoring;
    }

    public boolean isCompressForOltp() {
        return compressForOltp;
    }

    public void setCompressForOltp(boolean compressForOltp) {
        this.compressForOltp = compressForOltp;
    }

    public Boolean getEnableRowMovement() {
        return enableRowMovement;
    }

    public void setEnableRowMovement(Boolean enableRowMovement) {
        this.enableRowMovement = enableRowMovement;
    }

    public List<SQLName> getClusterColumns() {
        return clusterColumns;
    }

    public SQLName getCluster() {
        return cluster;
    }

    public void setCluster(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.cluster = x;
    }

    public List<SQLName> getIncluding() {
        return including;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        if (organization != null) {
            organization.setParent(this);
        }
        this.organization = organization;
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, of);
            this.acceptChild(visitor, tableElementList);
            this.acceptChild(visitor, tablespace);
            this.acceptChild(visitor, select);
            this.acceptChild(visitor, storage);
            this.acceptChild(visitor, partitionBy);
        }
        visitor.endVisit(this);
    }

    public void cloneTo(OracleCreateTableStatement x) {
        super.cloneTo(x);

        x.inMemoryMetadata = inMemoryMetadata;
        x.cursorSpecificSegment = cursorSpecificSegment;

        x.parallel = parallel;
        if (parallelValue != null) {
            x.setParallelValue(parallelValue.clone());
        }

        if (storage != null) {
            x.setStorage(storage.clone());
        }
        if (lobStorage != null) {
            x.setLobStorage(lobStorage.clone());
        }

        x.pctfree = pctfree;
        x.pctused = pctused;
        x.initrans = initrans;
        x.maxtrans = maxtrans;
        x.pctincrease = pctincrease;

        x.compressLevel = compressLevel;
        x.compressForOltp = compressForOltp;

        x.cache = cache;

        x.deferredSegmentCreation = deferredSegmentCreation;

        x.enableRowMovement = enableRowMovement;

        for (SQLName e : clusterColumns) {
            SQLName e2 = e.clone();
            e2.setParent(x);
            x.clusterColumns.add(e2);
        }
        if (cluster != null) {
            x.setCluster(cluster.clone());
        }

        if (organization != null) {
            x.setOrganization(organization.clone());
        }

        if (of != null) {
            x.setOf(of.clone());
        }
        if (oidIndex != null) {
            x.setOidIndex(oidIndex.clone());
        }
        x.monitoring = monitoring;
        for (SQLName e : including) {
            SQLName e2 = e.clone();
            e2.setParent(x);
            x.including.add(e2);
        }
        if (xmlTypeColumnProperties != null) {
            x.setXmlTypeColumnProperties(xmlTypeColumnProperties.clone());
        }
    }

    @Override
    public OracleCreateTableStatement clone() {
        OracleCreateTableStatement x = new OracleCreateTableStatement();
        cloneTo(x);
        return x;
    }

    public static enum DeferredSegmentCreation {
        IMMEDIATE, DEFERRED
    }

    public static class Organization extends OracleSegmentAttributesImpl implements OracleSegmentAttributes, OracleSQLObject {
        public String type;

        private SQLName externalType;
        private SQLExpr externalDirectory;
        private SQLExternalRecordFormat externalDirectoryRecordFormat;
        private List<SQLExpr> externalDirectoryLocation = new ArrayList<SQLExpr>();
        private SQLExpr externalRejectLimit;

        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, tablespace);
                acceptChild(visitor, storage);
            }
            visitor.endVisit(this);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public SQLName getExternalType() {
            return externalType;
        }

        public void setExternalType(SQLName externalType) {
            this.externalType = externalType;
        }

        public SQLExpr getExternalDirectory() {
            return externalDirectory;
        }

        public void setExternalDirectory(SQLExpr externalDirectory) {
            this.externalDirectory = externalDirectory;
        }

        public SQLExternalRecordFormat getExternalDirectoryRecordFormat() {
            return externalDirectoryRecordFormat;
        }

        public void setExternalDirectoryRecordFormat(SQLExternalRecordFormat recordFormat) {
            if (recordFormat != null) {
                recordFormat.setParent(this);
            }
            this.externalDirectoryRecordFormat = recordFormat;
        }

        public SQLExpr getExternalRejectLimit() {
            return externalRejectLimit;
        }

        public void setExternalRejectLimit(SQLExpr externalRejectLimit) {
            if (externalRejectLimit != null) {
                externalRejectLimit.setParent(this);
            }
            this.externalRejectLimit = externalRejectLimit;
        }

        public List<SQLExpr> getExternalDirectoryLocation() {
            return externalDirectoryLocation;
        }

        public void cloneTo(Organization x) {
            super.cloneTo(x);
            x.type = type;
            if (externalType != null) {
                x.setExternalType(externalType.clone());
            }
            if (externalDirectory != null) {
                x.setExternalDirectory(externalDirectory.clone());
            }
            if (externalDirectoryRecordFormat != null) {
                x.setExternalDirectoryRecordFormat(externalDirectoryRecordFormat.clone());
            }
            for (SQLExpr e : externalDirectoryLocation) {
                SQLExpr e2 = e.clone();
                e2.setParent(x);
                x.externalDirectoryLocation.add(e2);
            }
            if (externalRejectLimit != null) {
                x.setExternalRejectLimit(externalRejectLimit.clone());
            }
        }

        @Override
        public Organization clone() {
            Organization x = new Organization();
            cloneTo(x);
            return x;
        }
    }

    public OracleXmlColumnProperties getXmlTypeColumnProperties() {
        return xmlTypeColumnProperties;
    }

    public void setXmlTypeColumnProperties(OracleXmlColumnProperties x) {
        if (x != null) {
            x.setParent(this);
        }
        this.xmlTypeColumnProperties = x;
    }

    public static class OIDIndex extends OracleSegmentAttributesImpl implements OracleSQLObject {
        private SQLName name;

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, name);
                acceptChild(visitor, tablespace);
                acceptChild(visitor, storage);
            }
            visitor.endVisit(this);
        }

        protected void accept0(SQLASTVisitor visitor) {
            accept0((OracleASTVisitor) visitor);
        }

        public SQLName getName() {
            return name;
        }

        public void setName(SQLName name) {
            if (name != null) {
                name.setParent(this);
            }
            this.name = name;
        }

        public void cloneTo(OIDIndex x) {
            super.cloneTo(x);
            if (name != null) {
                x.setName(name.clone());
            }
        }

        @Override
        public OIDIndex clone() {
            OIDIndex x = new OIDIndex();
            cloneTo(x);
            return x;
        }
    }

}
