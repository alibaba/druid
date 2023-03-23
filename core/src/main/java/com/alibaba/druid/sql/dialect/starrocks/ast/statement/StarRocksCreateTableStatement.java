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
    protected SQLName modelKey;

    protected SQLExpr partitionBy;
    protected SQLExpr start;
    protected SQLExpr end;
    protected SQLExpr every;
    protected SQLExpr distributedBy;

    protected boolean lessThan;
    protected boolean fixedRange;
    protected boolean startEnd;

    protected final List<SQLExpr> modelKeyParameters = new ArrayList<SQLExpr>();

    protected Map<SQLExpr, SQLExpr> lessThanMap = new LinkedHashMap<>();
    protected Map<SQLExpr, List<SQLExpr>> fixedRangeMap = new LinkedHashMap<>();
    protected Map<SQLCharExpr, SQLCharExpr> propertiesMap = new LinkedHashMap<>();
    protected Map<SQLCharExpr, SQLCharExpr> lBracketPropertiesMap = new LinkedHashMap<>();

    public StarRocksCreateTableStatement() {
        super(DbType.starrocks);
    }

    public Map<SQLCharExpr, SQLCharExpr> getPropertiesMap() {
        return propertiesMap;
    }

    public Map<SQLCharExpr, SQLCharExpr> getlBracketPropertiesMap() {
        return lBracketPropertiesMap;
    }

    public void setPropertiesMap(Map<SQLCharExpr, SQLCharExpr> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    public void setlBracketPropertiesMap(Map<SQLCharExpr, SQLCharExpr> lBracketPropertiesMap) {
        this.lBracketPropertiesMap = lBracketPropertiesMap;
    }

    public void setStartEnd(boolean startEnd) {
        this.startEnd = startEnd;
    }

    public boolean isStartEnd() {
        return startEnd;
    }

    public void setDistributedBy(SQLExpr distributedBy) {
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

    public SQLName getModelKey() {
        return modelKey;
    }

    public void setModelKey(SQLName modelKey) {
        this.modelKey = modelKey;
    }

    public List<SQLExpr> getModelKeyParameters() {
        return modelKeyParameters;
    }

    public void setPartitionBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.partitionBy = x;
    }

    public SQLExpr getPartitionBy() {
        return partitionBy;
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
