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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SQLExprTableSource extends SQLTableSourceImpl implements SQLReplaceable {

    protected SQLExpr          expr;
    protected List<SQLName>    partitions;
    protected SQLTableSampling sampling;
    protected SchemaObject     schemaObject;

    protected List<SQLName>    columns;

    public SQLExprTableSource() {

    }

    public SQLExprTableSource(String tableName) {
        this(SQLUtils.toSQLExpr(tableName), null);
    }

    public SQLExprTableSource(SQLExpr expr) {
        this(expr, null);
    }

    public SQLExprTableSource(SQLExpr expr, String alias) {
        this.setExpr(expr);
        this.setAlias(alias);
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.expr = x;
    }

    public void setExpr(String name) {
        this.setExpr(new SQLIdentifierExpr(name));
    }

    public SQLTableSampling getSampling() {
        return sampling;
    }

    public void setSampling(SQLTableSampling x) {
        if (x != null) {
            x.setParent(this);
        }
        this.sampling = x;
    }

    public SQLName getName() {
        if (expr instanceof SQLName) {
            return (SQLName) expr;
        }
        return null;
    }

    public String getTableName(boolean normalize) {
        String tableName = getTableName();
        if (normalize) {
            return SQLUtils.normalize(tableName);
        }
        return tableName;
    }

    public String getTableName() {
        if (expr == null) {
            return null;
        }

        if (expr instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) expr).getName();
        }

        if (expr instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) expr).getSimpleName();
        }

        return null;
    }

    public String getSchema() {
        if (expr == null) {
            return null;
        }

        if (expr instanceof SQLPropertyExpr) {
            SQLExpr owner = ((SQLPropertyExpr) expr).getOwner();
            if (owner instanceof SQLIdentifierExpr) {
                return ((SQLIdentifierExpr) owner).getName();
            }

            if (owner instanceof SQLPropertyExpr) {
                return ((SQLPropertyExpr) owner).getSimpleName();
            }

            if (owner instanceof SQLAllColumnExpr) {
                return "*";
            }

            return null;
        }

        return null;
    }

    public String getCatalog() {
        if (expr instanceof SQLPropertyExpr) {
            SQLExpr owner = ((SQLPropertyExpr) expr).getOwner();

            if (owner instanceof SQLPropertyExpr) {
                SQLExpr catalogExpr = ((SQLPropertyExpr) owner).getOwner();
                if (catalogExpr instanceof SQLIdentifierExpr) {
                    return ((SQLIdentifierExpr) catalogExpr).getName();
                }
            }

            return null;
        }

        return null;
    }

    public boolean setCatalog(String catalog) {
        if (expr instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
            SQLExpr owner = propertyExpr.getOwner();
            if (owner instanceof SQLIdentifierExpr) {
                if (catalog == null) {
                    return false;
                }

                propertyExpr.setOwner(
                        new SQLPropertyExpr(catalog
                                , ((SQLIdentifierExpr) owner).getName())
                );
                return true;
            } else if (owner instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyOwner = (SQLPropertyExpr) owner;
                final SQLExpr propertyOwnerOwner = propertyOwner.getOwner();
                if (propertyOwnerOwner instanceof SQLIdentifierExpr) {
                    if (catalog == null) {
                        propertyExpr.setOwner(((SQLIdentifierExpr) propertyOwnerOwner).getName());
                    } else {
                        propertyOwner.setOwner(
                                new SQLIdentifierExpr(catalog));
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public void setCatalog(String catalog, String schema) {
        if (catalog == null) {
            throw new IllegalArgumentException("catalog is null.");
        }

        if (schema == null) {
            throw new IllegalArgumentException("schema is null.");
        }

        setSchema(schema);
        setCatalog(catalog);
    }

    public void setSchema(String schema) {
        if (expr instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
            if (StringUtils.isEmpty(schema)) {
                this.setExpr(new SQLIdentifierExpr(propertyExpr.getName()));
            } else {
                propertyExpr.setOwner(schema);
            }
        } else {
            if(StringUtils.isEmpty(schema)) {
                return;
            }

            String ident = ((SQLIdentifierExpr) expr).getName();
            this.setExpr(new SQLPropertyExpr(schema, ident));
        }
    }

    public void setSimpleName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("schema is empty.");
        }
        if (expr == null) {
            expr = new SQLIdentifierExpr(name);
        } else if (expr instanceof SQLPropertyExpr) {
            ((SQLPropertyExpr) expr).setName(name);
        } else {
            expr = new SQLIdentifierExpr(name);
        }
    }

    public List<SQLName> getPartitions() {
        if (this.partitions == null) {
            this.partitions = new ArrayList<SQLName>(2);
        }

        return partitions;
    }

    public int getPartitionSize() {
        if (this.partitions == null) {
            return 0;
        }
        return this.partitions.size();
    }

    public void addPartition(SQLName partition) {
        if (partition != null) {
            partition.setParent(this);
        }

        if (this.partitions == null) {
            this.partitions = new ArrayList<SQLName>(2);
        }
        this.partitions.add(partition);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (expr != null) {
                expr.accept(visitor);
            }

            if (sampling != null) {
                sampling.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public String computeAlias() {
        String alias = this.getAlias();

        if (alias == null) {
            if (expr instanceof SQLName) {
                alias = ((SQLName) expr).getSimpleName();
            }
        }

        return SQLUtils.normalize(alias);
    }

    public SQLExprTableSource clone() {
        SQLExprTableSource x = new SQLExprTableSource();
        cloneTo(x);
        return x;
    }

    public void cloneTo(SQLExprTableSource x) {
        x.alias = alias;

        if (expr != null) {
            x.setExpr(expr.clone());
        }

        if (partitions != null) {
            for (SQLName p : partitions) {
                SQLName p1 = p.clone();
                x.addPartition(p1);
            }
        }

        if (schemaObject != null) {
            x.setSchemaObject(schemaObject);
        }

        if (columns != null) {
            x.columns = new ArrayList<SQLName>(columns.size());

            for (SQLName column : columns) {
                SQLName clonedColumn = column.clone();
                clonedColumn.setParent(x);
                x.columns.add(clonedColumn);
            }
        }
    }

    public List<SQLName> getColumns() {
        if (columns == null) {
            columns = new ArrayList<SQLName>(2);
        }
        return columns;
    }

    public List<SQLName> getColumnsDirect() {
        return columns;
    }

    public SchemaObject getSchemaObject() {
        return schemaObject;
    }

    public void setSchemaObject(SchemaObject schemaObject) {
        this.schemaObject = schemaObject;
    }

    public boolean containsAlias(String alias) {
        long hashCode64 = FnvHash.hashCode64(alias);

        return containsAlias(hashCode64);
    }

    public boolean containsAlias(long aliasHash) {
        if (this.aliasHashCode64() == aliasHash) {
            return true;
        }

        if (expr instanceof SQLPropertyExpr) {
            long exprNameHash = ((SQLPropertyExpr) expr).hashCode64();
            if (exprNameHash == aliasHash) {
                return true;
            }
        }

        if (expr instanceof SQLName) {
            long exprNameHash = ((SQLName) expr).nameHashCode64();
            return exprNameHash == aliasHash;
        }

        return false;
    }

    public SQLColumnDefinition findColumn(String columnName) {
        if (columnName == null) {
            return null;
        }

        long hash = FnvHash.hashCode64(columnName);
        return findColumn(hash);
    }

    public SQLColumnDefinition findColumn(long columnNameHash) {
        SQLObject object = resolveColum(columnNameHash);
        if (object instanceof SQLColumnDefinition) {
            return (SQLColumnDefinition) object;
        }
        return null;
    }

    public SQLObject resolveColum(long columnNameHash) {
        if (schemaObject != null) {
            SQLStatement stmt = schemaObject.getStatement();
            if (stmt instanceof SQLCreateTableStatement) {
                SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) stmt;
                return createTableStmt.findColumn(columnNameHash);
            }
        }

        SQLObject resolvedOwnerObject = null;
        if (expr instanceof SQLIdentifierExpr) {
            resolvedOwnerObject = ((SQLIdentifierExpr) expr).getResolvedOwnerObject();
        }

        if (resolvedOwnerObject == null) {
            return resolvedOwnerObject;
        }

        if (resolvedOwnerObject instanceof SQLWithSubqueryClause.Entry) {
            final SQLSelect subQuery = ((SQLWithSubqueryClause.Entry) resolvedOwnerObject)
                    .getSubQuery();
            if (subQuery == null) {
                return null;
            }

            final SQLSelectQueryBlock firstQueryBlock = subQuery.getFirstQueryBlock();
            if (firstQueryBlock == null) {
                return null;
            }

            SQLSelectItem selectItem = firstQueryBlock.findSelectItem(columnNameHash);
            if (selectItem != null) {
                return selectItem;
            }
        }
        return null;
    }

    public SQLTableSource findTableSourceWithColumn(String columnName) {
        if (columnName == null) {
            return null;
        }

        long hash = FnvHash.hashCode64(columnName);
        return findTableSourceWithColumn(hash, columnName, 0);
    }

    public SQLTableSource findTableSourceWithColumn(long columnName_hash, String name, int option) {
        if (schemaObject != null) {
            SQLStatement stmt = schemaObject.getStatement();
            if (stmt instanceof SQLCreateTableStatement) {
                SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) stmt;
                if (createTableStmt.findColumn(columnName_hash) != null) {
                    return this;
                }
            }
        }

        if (expr instanceof SQLIdentifierExpr) {
            SQLTableSource tableSource = ((SQLIdentifierExpr) expr).getResolvedTableSource();
            if (tableSource != null) {
                return this;
            }
        }

        return null;
    }

    public SQLTableSource findTableSource(long alias_hash) {
        if (alias_hash == 0) {
            return null;
        }

        if (aliasHashCode64() == alias_hash) {
            return this;
        }

        if (expr instanceof SQLName) {
            long exprNameHash = ((SQLName) expr).nameHashCode64();
            if (exprNameHash == alias_hash) {
                return this;
            }
        }

        if (expr instanceof SQLPropertyExpr) {
            long hash = ((SQLPropertyExpr) expr).hashCode64();
            if (hash == alias_hash) {
                return this;
            }
        }

        return null;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (expr == this.expr) {
            this.setExpr(target);
            return true;
        }

        if (partitions != null) {
            for (int i = 0; i < partitions.size(); i++) {
                if (partitions.get(i) == expr) {
                    target.setParent(this);
                    partitions.set(i, (SQLName) target);
                    return true;
                }
            }
        }

        return false;
    }

    public long aliasHashCode64() {

        if (alias != null) {
            if (aliasHashCode64 == 0) {
                aliasHashCode64 = FnvHash.hashCode64(alias);
            }
            return aliasHashCode64;
        }

        if (expr instanceof SQLName) {
            return ((SQLName) expr).nameHashCode64();
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SQLExprTableSource that = (SQLExprTableSource) o;

        if (expr != null ? !expr.equals(that.expr) : that.expr != null) return false;
        if (partitions != null ? !partitions.equals(that.partitions) : that.partitions != null) return false;
        if (sampling != null ? !sampling.equals(that.sampling) : that.sampling != null) return false;
        if (schemaObject != null ? !schemaObject.equals(that.schemaObject) : that.schemaObject != null) return false;
        return columns != null ? columns.equals(that.columns) : that.columns == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (expr != null ? expr.hashCode() : 0);
        result = 31 * result + (partitions != null ? partitions.hashCode() : 0);
        result = 31 * result + (sampling != null ? sampling.hashCode() : 0);
        result = 31 * result + (schemaObject != null ? schemaObject.hashCode() : 0);
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        return result;
    }
}
