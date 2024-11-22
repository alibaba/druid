package com.alibaba.druid.sql.dialect.oracle.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLPartitionSingle;
import com.alibaba.druid.sql.ast.SQLSubPartition;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OraclePartitionSingle extends SQLPartitionSingle implements OracleSegmentAttributes {
    protected boolean segmentCreationImmediate;
    protected boolean segmentCreationDeferred;
    protected SQLObject lobStorage;
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
    protected SQLObject storage;
    public SQLExpr getLocality() {
        return locality;
    }

    public void setLocality(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.locality = x;
    }
    public SQLObject getLobStorage() {
        return lobStorage;
    }

    public void setLobStorage(SQLObject lobStorage) {
        if (lobStorage != null) {
            lobStorage.setParent(this);
        }
        this.lobStorage = lobStorage;
    }

    public boolean isSegmentCreationImmediate() {
        return segmentCreationImmediate;
    }

    public void setSegmentCreationImmediate(boolean segmentCreationImmediate) {
        this.segmentCreationImmediate = segmentCreationImmediate;
    }

    public boolean isSegmentCreationDeferred() {
        return segmentCreationDeferred;
    }

    public void setSegmentCreationDeferred(boolean segmentCreationDeferred) {
        this.segmentCreationDeferred = segmentCreationDeferred;
    }

    @Override
    public Integer getPctfree() {
        return pctfree;
    }

    @Override
    public void setPctfree(Integer pctfree) {
        this.pctfree = pctfree;
    }

    @Override
    public Integer getPctused() {
        return pctused;
    }

    @Override
    public void setPctused(Integer pctused) {
        this.pctused = pctused;
    }

    @Override
    public Integer getInitrans() {
        return initrans;
    }

    @Override
    public void setInitrans(Integer initrans) {
        this.initrans = initrans;
    }

    @Override
    public Integer getMaxtrans() {
        return maxtrans;
    }

    @Override
    public void setMaxtrans(Integer maxtrans) {
        this.maxtrans = maxtrans;
    }

    @Override
    public Integer getPctincrease() {
        return pctincrease;
    }

    @Override
    public void setPctincrease(Integer pctincrease) {
        this.pctincrease = pctincrease;
    }

    public Integer getFreeLists() {
        return freeLists;
    }

    public void setFreeLists(Integer freeLists) {
        this.freeLists = freeLists;
    }

    @Override
    public Boolean getCompress() {
        return compress;
    }

    @Override
    public void setCompress(Boolean compress) {
        this.compress = compress;
    }

    @Override
    public Integer getCompressLevel() {
        return compressLevel;
    }

    @Override
    public void setCompressLevel(Integer compressLevel) {
        this.compressLevel = compressLevel;
    }

    @Override
    public boolean isCompressForOltp() {
        return compressForOltp;
    }

    @Override
    public void setCompressForOltp(boolean compressForOltp) {
        this.compressForOltp = compressForOltp;
    }

    public Integer getPctthreshold() {
        return pctthreshold;
    }

    public void setPctthreshold(Integer pctthreshold) {
        this.pctthreshold = pctthreshold;
    }

    @Override
    public Boolean getLogging() {
        return logging;
    }

    @Override
    public void setLogging(Boolean logging) {
        this.logging = logging;
    }

    @Override
    public SQLObject getStorage() {
        return storage;
    }

    @Override
    public void setStorage(SQLObject storage) {
        this.storage = storage;
    }

    @Override
    public OraclePartitionSingle clone() {
        OraclePartitionSingle x = new OraclePartitionSingle();

        if (name != null) {
            x.setName(name.clone());
        }

        if (subPartitionsCount != null) {
            x.setSubPartitionsCount(subPartitionsCount.clone());
        }

        for (SQLSubPartition p : subPartitions) {
            SQLSubPartition p2 = p.clone();
            p2.setParent(x);
            x.subPartitions.add(p2);
        }

        if (values != null) {
            x.setValues(values.clone());
        }

        x.segmentCreationImmediate = segmentCreationImmediate;
        x.segmentCreationDeferred = segmentCreationDeferred;

        if (lobStorage != null) {
            x.setLobStorage(lobStorage.clone());
        }

        return x;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, values);
            acceptChild(visitor, tablespace);
            acceptChild(visitor, subPartitionsCount);
            acceptChild(visitor, storage);
            acceptChild(visitor, subPartitions);
            acceptChild(visitor, locality);
            acceptChild(visitor, lobStorage);
        }
        visitor.endVisit(this);
    }
}
