package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

/**
 * version 1.0
 * Author zzy
 * Date 2019-06-04 11:27
 */
public class SQLIndexDefinition extends SQLObjectImpl implements SQLIndex {

    /**
     * [CONSTRAINT [symbol]] [GLOBAL|LOCAL] [FULLTEXT|SPATIAL|UNIQUE|PRIMARY] [INDEX|KEY]
     * [index_name] [index_type] (key_part,...) [COVERING (col_name,...)] [index_option] ...
     */

    private boolean hasConstraint;
    private SQLName symbol;
    private boolean global;
    private boolean local;
    private String type;
    private boolean hashMapType; //for ads
    private boolean hashType; //for ads
    private boolean index;
    private boolean key;
    private SQLName name;
    private SQLTableSource table;
    private List<SQLSelectOrderByItem> columns = new ArrayList<SQLSelectOrderByItem>();
    private SQLIndexOptions options;

    // DRDS
    private SQLExpr dbPartitionBy;
    private SQLExpr tbPartitionBy;
    private SQLExpr tbPartitions;
    private List<SQLName> covering = new ArrayList<SQLName>();

    // For fulltext index when create table.
    private SQLName analyzerName;
    private SQLName indexAnalyzerName;
    private SQLName queryAnalyzerName;
    private SQLName withDicName;

    // Compatible layer.
    private List<SQLAssignItem> compatibleOptions = new ArrayList<SQLAssignItem>();

    public boolean hasConstraint() {
        return hasConstraint;
    }

    public void setHasConstraint(boolean hasConstraint) {
        this.hasConstraint = hasConstraint;
    }

    public SQLName getSymbol() {
        return symbol;
    }

    public void setSymbol(SQLName symbol) {
        if (symbol != null) {
            if (getParent() != null) {
                symbol.setParent(getParent());
            } else {
                symbol.setParent(this);
            }
        }
        this.symbol = symbol;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHashMapType() {
        return hashMapType;
    }

    public void setHashMapType(boolean hashMapType) {
        this.hashMapType = hashMapType;
    }

    public boolean isHashType() {
        return hashType;
    }

    public void setHashType(boolean hashType) {
        this.hashType = hashType;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            if (getParent() != null) {
                name.setParent(getParent());
            } else {
                name.setParent(this);
            }
        }
        this.name = name;
    }

    public SQLTableSource getTable() {
        return table;
    }

    public void setTable(SQLTableSource table) {
        if (table != null) {
            if (getParent() != null) {
                table.setParent(getParent());
            } else {
                table.setParent(this);
            }
        }
        this.table = table;
    }

    @Override
    public List<SQLSelectOrderByItem> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLSelectOrderByItem> columns) {
        this.columns = columns;
    }

    public boolean hasOptions() {
        return options != null;
    }

    public SQLIndexOptions getOptions() {
        if (null == options) {
            options = new SQLIndexOptions();
            options.setParent(this);
        }
        return options;
    }

    public SQLExpr getDbPartitionBy() {
        return dbPartitionBy;
    }

    public void setDbPartitionBy(SQLExpr dbPartitionBy) {
        if (dbPartitionBy != null) {
            if (getParent() != null) {
                dbPartitionBy.setParent(getParent());
            } else {
                dbPartitionBy.setParent(this);
            }
        }
        this.dbPartitionBy = dbPartitionBy;
    }

    public SQLExpr getTbPartitionBy() {
        return tbPartitionBy;
    }

    public void setTbPartitionBy(SQLExpr tbPartitionBy) {
        if (tbPartitionBy != null) {
            if (getParent() != null) {
                tbPartitionBy.setParent(getParent());
            } else {
                tbPartitionBy.setParent(this);
            }
        }
        this.tbPartitionBy = tbPartitionBy;
    }

    public SQLExpr getTbPartitions() {
        return tbPartitions;
    }

    public void setTbPartitions(SQLExpr tbPartitions) {
        if (tbPartitions != null) {
            if (getParent() != null) {
                tbPartitions.setParent(getParent());
            } else {
                tbPartitions.setParent(this);
            }
        }
        this.tbPartitions = tbPartitions;
    }

    @Override
    public List<SQLName> getCovering() {
        return covering;
    }

    public void setCovering(List<SQLName> covering) {
        this.covering = covering;
    }

    public SQLName getAnalyzerName() {
        return analyzerName;
    }

    public void setAnalyzerName(SQLName analyzerName) {
        if (analyzerName != null) {
            if (getParent() != null) {
                analyzerName.setParent(getParent());
            } else {
                analyzerName.setParent(this);
            }
        }
        this.analyzerName = analyzerName;
    }

    public SQLName getIndexAnalyzerName() {
        return indexAnalyzerName;
    }

    public void setIndexAnalyzerName(SQLName indexAnalyzerName) {
        if (indexAnalyzerName != null) {
            if (getParent() != null) {
                indexAnalyzerName.setParent(getParent());
            } else {
                indexAnalyzerName.setParent(this);
            }
        }
        this.indexAnalyzerName = indexAnalyzerName;
    }

    public SQLName getQueryAnalyzerName() {
        return queryAnalyzerName;
    }

    public void setQueryAnalyzerName(SQLName queryAnalyzerName) {
        if (queryAnalyzerName != null) {
            if (getParent() != null) {
                queryAnalyzerName.setParent(getParent());
            } else {
                queryAnalyzerName.setParent(this);
            }
        }
        this.queryAnalyzerName = queryAnalyzerName;
    }

    public SQLName getWithDicName() {
        return withDicName;
    }

    public void setWithDicName(SQLName withDicName) {
        if (withDicName != null) {
            if (getParent() != null) {
                withDicName.setParent(getParent());
            } else {
                withDicName.setParent(this);
            }
        }
        this.withDicName = withDicName;
    }

    public List<SQLAssignItem> getCompatibleOptions() {
        return compatibleOptions;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (name != null) {
                name.accept(visitor);
            }

            for (final SQLSelectOrderByItem item : columns) {
                if (item != null) {
                    item.accept(visitor);
                }
            }
            for (final SQLName item : covering) {
                if (item != null) {
                    item.accept(visitor);
                }
            }
        }
        visitor.endVisit(this);
    }

    public void cloneTo(SQLIndexDefinition definition) {
        SQLObject parent;
        if (definition.getParent() != null) {
            parent = definition.getParent();
        } else {
            parent = definition;
        }
        definition.hasConstraint = hasConstraint;
        if (symbol != null) {
            definition.symbol = symbol.clone();
            definition.symbol.setParent(parent);
        }
        definition.global = global;
        definition.local = local;
        definition.type = type;
        definition.hashMapType = hashMapType;
        definition.index = index;
        definition.key = key;
        if (name != null) {
            definition.name = name.clone();
            definition.name.setParent(parent);
        }
        if (table != null) {
            definition.table = table.clone();
            definition.table.setParent(parent);
        }
        for (SQLSelectOrderByItem item : columns) {
            SQLSelectOrderByItem item1 = item.clone();
            item1.setParent(parent);
            definition.columns.add(item1);
        }
        if (options != null) {
            options.cloneTo(definition.getOptions());
        }
        if (dbPartitionBy != null) {
            definition.dbPartitionBy = dbPartitionBy.clone();
            definition.dbPartitionBy.setParent(parent);
        }
        if (tbPartitionBy != null) {
            definition.tbPartitionBy = tbPartitionBy.clone();
            definition.tbPartitionBy.setParent(parent);
        }
        if (tbPartitions != null) {
            definition.tbPartitions = tbPartitions.clone();
            definition.tbPartitions.setParent(parent);
        }
        for (SQLName name : covering) {
            SQLName name1 = name.clone();
            name1.setParent(parent);
            definition.covering.add(name1);
        }
        if (analyzerName != null) {
            definition.analyzerName = analyzerName.clone();
            definition.analyzerName.setParent(parent);
        }
        if (indexAnalyzerName != null) {
            definition.indexAnalyzerName = indexAnalyzerName.clone();
            definition.indexAnalyzerName.setParent(parent);
        }
        if (withDicName != null) {
            definition.withDicName = withDicName.clone();
            definition.withDicName.setParent(parent);
        }
        if (queryAnalyzerName != null) {
            definition.queryAnalyzerName = queryAnalyzerName.clone();
            definition.queryAnalyzerName.setParent(parent);
        }
        for (SQLAssignItem item : compatibleOptions) {
            SQLAssignItem item2 = item.clone();
            item2.setParent(parent);
            definition.compatibleOptions.add(item2);
        }
    }

    //
    // Function for compatibility.
    //

    public void addOption(String name, SQLExpr value) {
        SQLAssignItem assignItem = new SQLAssignItem(new SQLIdentifierExpr(name), value);
        if (getParent() != null) {
            assignItem.setParent(getParent());
        } else {
            assignItem.setParent(this);
        }
        getCompatibleOptions().add(assignItem);
    }

    public SQLExpr getOption(String name) {
        if (name == null) {
            return null;
        }

        return getOption(
                FnvHash.hashCode64(name));
    }

    public SQLExpr getOption(long hash64) {
        // Search in compatible list first.
        for (SQLAssignItem item : compatibleOptions) {
            final SQLExpr target = item.getTarget();
            if (target instanceof SQLIdentifierExpr) {
                if (((SQLIdentifierExpr) target).hashCode64() == hash64) {
                    return item.getValue();
                }
            }
        }

        // Now search in new options.
        if (null == options) {
            return null;
        }

        if (hash64 == FnvHash.Constants.KEY_BLOCK_SIZE) {
            return options.getKeyBlockSize();
        } else if (hash64 == FnvHash.Constants.ALGORITHM) {
            if (options.getAlgorithm() != null) {
                return new SQLIdentifierExpr(options.getAlgorithm());
            }
            return null;
        } else if (hash64 == FnvHash.hashCode64("LOCK")) {
            if (options.getLock() != null) {
                return new SQLIdentifierExpr(options.getLock());
            }
            return null;
        }

        for (SQLAssignItem item : options.getOtherOptions()) {
            final SQLExpr target = item.getTarget();
            if (target instanceof SQLIdentifierExpr) {
                if (((SQLIdentifierExpr) target).hashCode64() == hash64) {
                    return item.getValue();
                }
            }
        }

        return null;
    }

    public String getDistanceMeasure() {
        SQLExpr expr = getOption(FnvHash.Constants.DISTANCEMEASURE);
        if (expr == null) {
            return null;
        }

        return expr.toString();
    }

    public String getAlgorithm() {
        if (options != null && options.getAlgorithm() != null) {
            return options.getAlgorithm();
        }

        SQLExpr expr = getOption(FnvHash.Constants.ALGORITHM);
        if (expr == null) {
            return null;
        }

        return expr.toString();
    }

}
