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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSegmentAttributes;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class OracleCreateIndexStatement extends SQLCreateIndexStatement implements OracleDDLStatement, OracleSegmentAttributes, SQLCreateStatement {

    private boolean online            = false;

    private boolean indexOnlyTopLevel = false;
    private boolean cluster           = false;

    private boolean noParallel;

    private SQLExpr parallel;

    private Integer pctfree;
    private Integer pctused;
    private Integer initrans;

    private Integer maxtrans;
    private Integer pctincrease;
    private Integer freeLists;
    private Boolean compress;
    private Integer compressLevel;
    private boolean compressForOltp;
    private Integer pctthreshold;

    private Boolean logging;
    private Boolean sort;

    protected SQLName tablespace;
    protected SQLObject storage;

    private Boolean enable            = null;

    private boolean computeStatistics = false;
    
    public OracleCreateIndexStatement() {
        super (JdbcConstants.ORACLE);
    }

    public SQLExpr getParallel() {
        return parallel;
    }

    public void setParallel(SQLExpr parallel) {
        this.parallel = parallel;
    }

    public boolean isNoParallel() {
        return noParallel;
    }

    public void setNoParallel(boolean noParallel) {
        this.noParallel = noParallel;
    }

    public boolean isIndexOnlyTopLevel() {
        return indexOnlyTopLevel;
    }

    public void setIndexOnlyTopLevel(boolean indexOnlyTopLevel) {
        this.indexOnlyTopLevel = indexOnlyTopLevel;
    }

    public Boolean getSort() {
        return sort;
    }

    public void setSort(Boolean sort) {
        this.sort = sort;
    }

    private boolean local;
    private List<SQLName> localStoreIn = new ArrayList<SQLName>();
    private List<SQLPartition> localPartitions = new ArrayList<SQLPartition>();

    private boolean global;
    private List<SQLPartitionBy> globalPartitions = new ArrayList<SQLPartitionBy>();

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getName());
            acceptChild(visitor, getTable());
            acceptChild(visitor, getItems());
            acceptChild(visitor, getTablespace());
            acceptChild(visitor, parallel);
        }
        visitor.endVisit(this);
    }


    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public boolean isComputeStatistics() {
        return computeStatistics;
    }

    public void setComputeStatistics(boolean computeStatistics) {
        this.computeStatistics = computeStatistics;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    //////////////

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName tablespace) {
        if (tablespace != null) {
            tablespace.setParent(this);
        }
        this.tablespace = tablespace;
    }

    public Boolean getCompress() {
        return compress;
    }

    public void setCompress(Boolean compress) {
        this.compress = compress;
    }

    public Integer getCompressLevel() {
        return compressLevel;
    }

    public void setCompressLevel(Integer compressLevel) {
        this.compressLevel = compressLevel;
    }

    public Integer getPctthreshold() {
        return pctthreshold;
    }

    public void setPctthreshold(Integer pctthreshold) {
        this.pctthreshold = pctthreshold;
    }

    public Integer getPctfree() {
        return pctfree;
    }

    public void setPctfree(Integer ptcfree) {
        this.pctfree = ptcfree;
    }

    public Integer getPctused() {
        return pctused;
    }

    public void setPctused(Integer ptcused) {
        this.pctused = ptcused;
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

    public Integer getFreeLists() {
        return freeLists;
    }

    public void setFreeLists(Integer freeLists) {
        this.freeLists = freeLists;
    }

    public Boolean getLogging() {
        return logging;
    }

    public void setLogging(Boolean logging) {
        this.logging = logging;
    }

    public SQLObject getStorage() {
        return storage;
    }

    public void setStorage(SQLObject storage) {
        this.storage = storage;
    }

    public boolean isCompressForOltp() {
        return compressForOltp;
    }

    public void setCompressForOltp(boolean compressForOltp) {
        this.compressForOltp = compressForOltp;
    }

    public List<SQLPartition> getLocalPartitions() {
        return localPartitions;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public List<SQLName> getLocalStoreIn() {
        return localStoreIn;
    }

    public List<SQLPartitionBy> getGlobalPartitions() {
        return globalPartitions;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }
}
