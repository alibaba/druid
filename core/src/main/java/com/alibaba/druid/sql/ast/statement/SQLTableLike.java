package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLTableLike extends SQLObjectImpl implements SQLTableElement {
    private SQLExprTableSource table;
    private boolean includeProperties;
    private boolean includeDistribution;
    private boolean excludeProperties;
    private boolean excludeDistribution;
    private boolean includeDefaults;
    private boolean includeConstraints;
    private boolean includeIndexes;
    private boolean includeStorage;
    private boolean includeComments;
    private boolean includeAll;
    private boolean excludeDefaults;
    private boolean excludeConstraints;
    private boolean excludeIndexes;
    private boolean excludeStorage;
    private boolean excludeComments;
    private boolean excludeAll;

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, table);
        }
        v.endVisit(this);
    }

    public SQLTableLike clone() {
        SQLTableLike x = new SQLTableLike();
        if (table != null) {
            x.setTable(table.clone());
        }
        x.includeProperties = includeProperties;
        x.includeDistribution = includeDistribution;
        x.excludeProperties = excludeProperties;
        x.excludeDistribution = excludeDistribution;
        x.includeDefaults = includeDefaults;
        x.includeConstraints = includeConstraints;
        x.includeIndexes = includeIndexes;
        x.includeStorage = includeStorage;
        x.includeComments = includeComments;
        x.includeAll = includeAll;
        x.excludeDefaults = excludeDefaults;
        x.excludeConstraints = excludeConstraints;
        x.excludeIndexes = excludeIndexes;
        x.excludeStorage = excludeStorage;
        x.excludeComments = excludeComments;
        x.excludeAll = excludeAll;
        return x;
    }

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.table = x;
    }

    public boolean isIncludeProperties() {
        return includeProperties;
    }

    public void setIncludeProperties(boolean includeProperties) {
        this.includeProperties = includeProperties;
    }

    public boolean isExcludeProperties() {
        return excludeProperties;
    }

    public void setExcludeProperties(boolean excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    public boolean isIncludeDistribution() {
        return includeDistribution;
    }

    public void setIncludeDistribution(boolean includeDistribution) {
        this.includeDistribution = includeDistribution;
    }

    public boolean isExcludeDistribution() {
        return excludeDistribution;
    }

    public void setExcludeDistribution(boolean excludeDistribution) {
        this.excludeDistribution = excludeDistribution;
    }

    public boolean isIncludeDefaults() {
        return includeDefaults;
    }

    public void setIncludeDefaults(boolean includeDefaults) {
        this.includeDefaults = includeDefaults;
    }

    public boolean isExcludeDefaults() {
        return excludeDefaults;
    }

    public void setExcludeDefaults(boolean excludeDefaults) {
        this.excludeDefaults = excludeDefaults;
    }

    public boolean isIncludeConstraints() {
        return includeConstraints;
    }

    public void setIncludeConstraints(boolean includeConstraints) {
        this.includeConstraints = includeConstraints;
    }

    public boolean isExcludeConstraints() {
        return excludeConstraints;
    }

    public void setExcludeConstraints(boolean excludeConstraints) {
        this.excludeConstraints = excludeConstraints;
    }

    public boolean isIncludeIndexes() {
        return includeIndexes;
    }

    public void setIncludeIndexes(boolean includeIndexes) {
        this.includeIndexes = includeIndexes;
    }

    public boolean isExcludeIndexes() {
        return excludeIndexes;
    }

    public void setExcludeIndexes(boolean excludeIndexes) {
        this.excludeIndexes = excludeIndexes;
    }

    public boolean isIncludeStorage() {
        return includeStorage;
    }

    public void setIncludeStorage(boolean includeStorage) {
        this.includeStorage = includeStorage;
    }

    public boolean isExcludeStorage() {
        return excludeStorage;
    }

    public void setExcludeStorage(boolean excludeStorage) {
        this.excludeStorage = excludeStorage;
    }

    public boolean isIncludeComments() {
        return includeComments;
    }

    public void setIncludeComments(boolean includeComments) {
        this.includeComments = includeComments;
    }

    public boolean isExcludeComments() {
        return excludeComments;
    }

    public void setExcludeComments(boolean excludeComments) {
        this.excludeComments = excludeComments;
    }

    public boolean isIncludeAll() {
        return includeAll;
    }

    public void setIncludeAll(boolean includeAll) {
        this.includeAll = includeAll;
    }

    public boolean isExcludeAll() {
        return excludeAll;
    }

    public void setExcludeAll(boolean excludeAll) {
        this.excludeAll = excludeAll;
    }
}
