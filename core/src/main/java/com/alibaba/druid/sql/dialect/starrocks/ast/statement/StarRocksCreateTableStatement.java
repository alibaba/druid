package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StarRocksCreateTableStatement extends SQLCreateTableStatement {
    protected SQLName aggDuplicate;
    protected boolean primary;
    protected boolean unique;
    protected final List<SQLExpr> primaryUniqueParameters = new ArrayList<>();
    protected final List<SQLExpr> AggDuplicateParameters = new ArrayList<>();

    protected List<SQLExpr> partitionBy = new ArrayList<>();
    protected SQLName partitionByName;
    protected SQLExpr start;
    protected SQLExpr end;
    protected SQLExpr every;
    protected SQLName distributedBy;
    protected final List<SQLExpr> distributedByParameters = new ArrayList<>();

    protected boolean lessThan;
    protected boolean fixedRange;
    protected boolean startEnd;
    protected SQLExpr engine;

    protected final List<SQLExpr> orderBy = new ArrayList<>();

    protected Map<SQLExpr, SQLExpr> lessThanMap = new LinkedHashMap<>();
    protected Map<SQLExpr, List<SQLExpr>> fixedRangeMap = new LinkedHashMap<>();
    protected Map<SQLCharExpr, SQLCharExpr> propertiesMap = new LinkedHashMap<>();
    protected Map<SQLCharExpr, SQLCharExpr> lBracketPropertiesMap = new LinkedHashMap<>();
    protected Map<SQLCharExpr, SQLCharExpr> brokerPropertiesMap = new LinkedHashMap<>();

    public StarRocksCreateTableStatement() {
        super(DbType.starrocks);
    }

    public Map<SQLCharExpr, SQLCharExpr> getPropertiesMap() {
        return propertiesMap;
    }

    public Map<SQLCharExpr, SQLCharExpr> getlBracketPropertiesMap() {
        return lBracketPropertiesMap;
    }

    public Map<SQLCharExpr, SQLCharExpr> getBrokerPropertiesMap() {
        return brokerPropertiesMap;
    }

    public void setPropertiesMap(Map<SQLCharExpr, SQLCharExpr> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    public void setlBracketPropertiesMap(Map<SQLCharExpr, SQLCharExpr> lBracketPropertiesMap) {
        this.lBracketPropertiesMap = lBracketPropertiesMap;
    }

    public void setBrokerPropertiesMap(Map<SQLCharExpr, SQLCharExpr> brokerPropertiesMap) {
        this.brokerPropertiesMap = brokerPropertiesMap;
    }

    public void setStartEnd(boolean startEnd) {
        this.startEnd = startEnd;
    }

    public boolean isStartEnd() {
        return startEnd;
    }

    public void setDistributedBy(SQLName distributedBy) {
        this.distributedBy = distributedBy;
    }

    public SQLExpr getDistributedBy() {
        return distributedBy;
    }

    public SQLExpr getStart() {
        return start;
    }

    public SQLExpr getEnd() {
        return end;
    }

    public SQLExpr getEvery() {
        return every;
    }

    public void setStart(SQLExpr start) {
        this.start = start;
    }

    public void setEnd(SQLExpr end) {
        this.end = end;
    }

    public void setEvery(SQLExpr every) {
        this.every = every;
    }

    public boolean isFixedRange() {
        return fixedRange;
    }

    public void setFixedRange(boolean fixedRange) {
        this.fixedRange = fixedRange;
    }

    public Map<SQLExpr, List<SQLExpr>> getFixedRangeMap() {
        return fixedRangeMap;
    }

    public void setFixedRangeMap(Map<SQLExpr, List<SQLExpr>> fixedRangeMap) {
        this.fixedRangeMap = fixedRangeMap;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isLessThan() {
        return lessThan;
    }

    public void setLessThan(boolean lessThan) {
        this.lessThan = lessThan;
    }

    public Map<SQLExpr, SQLExpr> getLessThanMap() {
        return lessThanMap;
    }

    public void setLessThanMap(Map<SQLExpr, SQLExpr> lessThanMap) {
        this.lessThanMap = lessThanMap;
    }

    public SQLName getAggDuplicate() {
        return aggDuplicate;
    }

    public SQLName getPartitionByName() {
        return this.partitionByName;
    }

    public void setPartitionByName(SQLName partitionByName) {
        this.partitionByName = partitionByName;
    }

    public void setAggDuplicate(SQLName aggDuplicate) {
        this.aggDuplicate = aggDuplicate;
    }

    public List<SQLExpr> getAggDuplicateParameters() {
        return AggDuplicateParameters;
    }

    public List<SQLExpr> getDistributedByParameters() {
        return distributedByParameters;
    }

    public List<SQLExpr> getPrimaryUniqueParameters() {
        return primaryUniqueParameters;
    }

    public List<SQLExpr> getOrderBy() {
        return orderBy;
    }

    public void setPartitionBy(List<SQLExpr> x) {
        this.partitionBy = x;
    }

    public List<SQLExpr> getPartitionBy() {
        return partitionBy;
    }

    public SQLExpr getEngine() {
        return engine;
    }

    public void setEngine(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.engine = x;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof StarRocksASTVisitor) {
            StarRocksASTVisitor vv = (StarRocksASTVisitor) v;
            if (vv.visit(this)) {
                acceptChild(vv);
            }
            vv.endVisit(this);
            return;
        }

        if (v.visit(this)) {
            acceptChild(v);
        }
        v.endVisit(this);
    }

}
