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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSegmentAttributes;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 30/06/2017.
 */
public class SQLCreateMaterializedViewStatement extends SQLStatementImpl implements OracleSegmentAttributes, SQLCreateStatement, SQLReplaceable {
    private SQLName name;
    private List<SQLName> columns = new ArrayList<SQLName>();

    private boolean refreshFast;
    private boolean refreshComplete;
    private boolean refreshForce;
    private boolean refreshOnCommit;
    private boolean refreshOnDemand;
    private boolean refreshStartWith;
    private boolean refreshNext;

    private boolean buildImmediate;
    private boolean buildDeferred;

    private SQLSelect query;

    // oracle
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
    private Boolean cache;

    protected SQLName tablespace;
    protected SQLObject storage;

    private Boolean parallel;
    private Integer parallelValue;

    private Boolean enableQueryRewrite;

    private SQLPartitionBy partitionBy;
    private SQLExpr startWith;
    private SQLExpr next;

    private boolean withRowId;

    // for ADB
    protected boolean refreshOnOverWrite;
    protected List<SQLTableElement> tableElementList = new ArrayList<SQLTableElement>();
    protected SQLName distributedByType;
    protected List<SQLName> distributedBy = new ArrayList<SQLName>();
    protected final List<SQLAssignItem> tableOptions = new ArrayList<SQLAssignItem>();
    protected SQLExpr comment;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public SQLSelect getQuery() {
        return query;
    }

    public void setQuery(SQLSelect query) {
        if (query != null) {
            query.setParent(this);
        }
        this.query = query;
    }

    public boolean isBuildImmediate() {
        return buildImmediate;
    }

    public void setBuildImmediate(boolean buildImmediate) {
        this.buildImmediate = buildImmediate;
    }

    public boolean isBuildDeferred() {
        return buildDeferred;
    }

    public void setBuildDeferred(boolean buildDeferred) {
        this.buildDeferred = buildDeferred;
    }

    public boolean isRefresh() {
        return this.refreshFast || refreshComplete || refreshForce || refreshOnDemand || refreshOnCommit || refreshStartWith || refreshNext;
    }

    public boolean isRefreshFast() {
        return refreshFast;
    }

    public void setRefreshFast(boolean refreshFast) {
        this.refreshFast = refreshFast;
    }

    public boolean isRefreshComplete() {
        return refreshComplete;
    }

    public void setRefreshComplete(boolean refreshComplete) {
        this.refreshComplete = refreshComplete;
    }

    public boolean isRefreshForce() {
        return refreshForce;
    }

    public void setRefreshForce(boolean refreshForce) {
        this.refreshForce = refreshForce;
    }

    public boolean isRefreshOnCommit() {
        return refreshOnCommit;
    }

    public void setRefreshOnCommit(boolean refreshOnCommit) {
        this.refreshOnCommit = refreshOnCommit;
    }

    public boolean isRefreshOnDemand() {
        return refreshOnDemand;
    }

    public void setRefreshOnDemand(boolean refreshOnDemand) {
        this.refreshOnDemand = refreshOnDemand;
    }

    public boolean isRefreshOnOverWrite() {
        return refreshOnOverWrite;
    }

    public void setRefreshOnOverWrite(boolean refreshOnOverWrite) {
        this.refreshOnOverWrite = refreshOnOverWrite;
    }

    public boolean isRefreshStartWith() {
        return refreshStartWith;
    }

    public void setRefreshStartWith(boolean refreshStartWith) {
        this.refreshStartWith = refreshStartWith;
    }

    public boolean isRefreshNext() {
        return refreshNext;
    }

    public void setRefreshNext(boolean refreshNext) {
        this.refreshNext = refreshNext;
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

    public Integer getFreeLists() {
        return freeLists;
    }

    public void setFreeLists(Integer freeLists) {
        this.freeLists = freeLists;
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

    public boolean isCompressForOltp() {
        return compressForOltp;
    }

    public void setCompressForOltp(boolean compressForOltp) {
        this.compressForOltp = compressForOltp;
    }

    public Integer getPctthreshold() {
        return pctthreshold;
    }

    public void setPctthreshold(Integer pctthreshold) {
        this.pctthreshold = pctthreshold;
    }

    public Boolean getLogging() {
        return logging;
    }

    public void setLogging(Boolean logging) {
        this.logging = logging;
    }

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName tablespace) {
        if (tablespace != null) {
            tablespace.setParent(this);
        }
        this.tablespace = tablespace;
    }

    public SQLObject getStorage() {
        return storage;
    }

    public void setStorage(SQLObject storage) {
        if (storage != null) {
            storage.setParent(this);
        }
        this.storage = storage;
    }

    public Boolean getParallel() {
        return parallel;
    }

    public void setParallel(Boolean parallel) {
        this.parallel = parallel;
    }

    public Integer getParallelValue() {
        return parallelValue;
    }

    public void setParallelValue(Integer parallelValue) {
        this.parallelValue = parallelValue;
    }

    public Boolean getEnableQueryRewrite() {
        return enableQueryRewrite;
    }

    public void setEnableQueryRewrite(Boolean enableQueryRewrite) {
        this.enableQueryRewrite = enableQueryRewrite;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public SQLPartitionBy getPartitionBy() {
        return partitionBy;
    }

    public List<SQLTableElement> getTableElementList() {
        return tableElementList;
    }

    public List<SQLName> getDistributedBy() {
        return distributedBy;
    }

    public SQLName getDistributedByType() {
        return distributedByType;
    }

    public void setDistributedByType(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.distributedByType = x;
    }

    public SQLExpr getStartWith() {
        return startWith;
    }

    public void setStartWith(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.startWith = x;
    }

    public SQLExpr getNext() {
        return next;
    }

    public void setNext(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.next = x;
    }

    public void setPartitionBy(SQLPartitionBy x) {
        if (x != null) {
            x.setParent(this);
        }
        this.partitionBy = x;
    }

    public boolean isWithRowId() {
        return withRowId;
    }

    public void setWithRowId(boolean withRowId) {
        this.withRowId = withRowId;
    }

    public void addOption(String name, SQLExpr value) {
        SQLAssignItem assignItem = new SQLAssignItem(new SQLIdentifierExpr(name), value);
        assignItem.setParent(this);
        tableOptions.add(assignItem);
    }

    public List<SQLAssignItem> getTableOptions() {
        return tableOptions;
    }

    public SQLExpr getOption(String name) {
        if (name == null) {
            return null;
        }

        long hash64 = FnvHash.hashCode64(name);

        for (SQLAssignItem item : tableOptions) {
            final SQLExpr target = item.getTarget();
            if (target instanceof SQLIdentifierExpr) {
                if (((SQLIdentifierExpr) target).hashCode64() == hash64) {
                    return item.getValue();
                }
            }
        }

        return null;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, columns);
            acceptChild(visitor, partitionBy);
            acceptChild(visitor, query);
            acceptChild(visitor, tableElementList);
            acceptChild(visitor, distributedBy);
            acceptChild(visitor, distributedByType);
            acceptChild(visitor, startWith);
            acceptChild(visitor, next);
            acceptChild(visitor, tableOptions);
            acceptChild(visitor, comment);
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (expr == name) {
            setName((SQLName) target);
            return true;
        }
        if (expr == next) {
            setNext(target);
            return true;
        }
        if (expr == startWith) {
            setStartWith(target);
            return true;
        }
        return false;
    }
}
