package com.alibaba.druid.hdriver.impl.hbql.ast;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HBQLFamilyDefinition extends SQLObjectImpl implements SQLTableElement {

    private static final long serialVersionUID = 1L;

    private String            columnName;

    private Integer           blockSize;
    private Integer           minVersions;
    private Integer           maxVersions;
    private String            compressionType;
    private String            compactionCompressionType;
    private Boolean           inMemory;
    private Integer           timeToLive;
    private Boolean           blockCacheEnabled;
    private String            bloomFilterType;

    @Override
    protected void accept0(SQLASTVisitor visitor) {

    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }

    public Integer getMinVersions() {
        return minVersions;
    }

    public void setMinVersions(Integer minVersions) {
        this.minVersions = minVersions;
    }

    public Integer getMaxVersions() {
        return maxVersions;
    }

    public void setMaxVersions(Integer maxVersions) {
        this.maxVersions = maxVersions;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public String getCompactionCompressionType() {
        return compactionCompressionType;
    }

    public void setCompactionCompressionType(String compactionCompressionType) {
        this.compactionCompressionType = compactionCompressionType;
    }

    public Boolean getInMemory() {
        return inMemory;
    }

    public void setInMemory(Boolean inMemory) {
        this.inMemory = inMemory;
    }

    public Integer getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Integer timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Boolean getBlockCacheEnabled() {
        return blockCacheEnabled;
    }

    public void setBlockCacheEnabled(Boolean blockCacheEnabled) {
        this.blockCacheEnabled = blockCacheEnabled;
    }

    public String getBloomFilterType() {
        return bloomFilterType;
    }

    public void setBloomFilterType(String bloomFilterType) {
        this.bloomFilterType = bloomFilterType;
    }

}
