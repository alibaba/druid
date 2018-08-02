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
package com.alibaba.druid.sql.ast.statement;

import java.util.*;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateSynonymStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.ListDG;
import com.alibaba.druid.util.lang.Consumer;

public class SQLCreateTableStatement extends SQLStatementImpl implements SQLDDLStatement, SQLCreateStatement {

    protected boolean                          ifNotExiists = false;
    protected Type                             type;
    protected SQLExprTableSource               tableSource;

    protected List<SQLTableElement>            tableElementList = new ArrayList<SQLTableElement>();

    // for postgresql
    protected SQLExprTableSource               inherits;

    protected SQLSelect                        select;

    protected SQLExpr                          comment;

    protected SQLExprTableSource               like;

    protected Boolean                          compress;
    protected Boolean                          logging;

    protected SQLName                          tablespace;
    protected SQLPartitionBy                   partitioning;
    protected SQLName                          storedAs;

    protected boolean                          onCommitPreserveRows;
    protected boolean                          onCommitDeleteRows;

    // for hive & odps
    protected SQLExternalRecordFormat          rowFormat;
    protected final List<SQLColumnDefinition>  partitionColumns = new ArrayList<SQLColumnDefinition>(2);
    protected final List<SQLName>              clusteredBy = new ArrayList<SQLName>();
    protected final List<SQLSelectOrderByItem> sortedBy = new ArrayList<SQLSelectOrderByItem>();
    protected int                              buckets;

    protected Map<String, SQLObject> tableOptions = new LinkedHashMap<String, SQLObject>();

    public SQLCreateTableStatement(){

    }

    public SQLCreateTableStatement(String dbType){
        super(dbType);
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

    public SQLName getName() {
        if (tableSource == null) {
            return null;
        }

        return (SQLName) tableSource.getExpr();
    }

    public String getSchema() {
        SQLName name = getName();
        if (name == null) {
            return null;
        }

        if (name instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) name).getOwnernName();
        }

        return null;
    }

    public void setSchema(String name) {
        if (this.tableSource == null) {
            return;
        }
        tableSource.setSchema(name);
    }

    public void setName(SQLName name) {
        this.setTableSource(new SQLExprTableSource(name));
    }

    public void setName(String name) {
        this.setName(new SQLIdentifierExpr(name));
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
                             GLOBAL_TEMPORARY, LOCAL_TEMPORARY
    }

    public List<SQLTableElement> getTableElementList() {
        return tableElementList;
    }

    public boolean isIfNotExiists() {
        return ifNotExiists;
    }

    public void setIfNotExiists(boolean ifNotExiists) {
        this.ifNotExiists = ifNotExiists;
    }

    public SQLExprTableSource getInherits() {
        return inherits;
    }

    public void setInherits(SQLExprTableSource inherits) {
        if (inherits != null) {
            inherits.setParent(this);
        }
        this.inherits = inherits;
    }

    public SQLSelect getSelect() {
        return select;
    }

    public void setSelect(SQLSelect select) {
        if (select != null) {
            select.setParent(this);
        }
        this.select = select;
    }

    public SQLExprTableSource getLike() {
        return like;
    }

    public void setLike(SQLName like) {
        this.setLike(new SQLExprTableSource(like));
    }

    public void setLike(SQLExprTableSource like) {
        if (like != null) {
            like.setParent(this);
        }
        this.like = like;
    }

    public Boolean getCompress() {
        return compress;
    }

    public void setCompress(Boolean compress) {
        this.compress = compress;
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

    public SQLPartitionBy getPartitioning() {
        return partitioning;
    }

    public void setPartitioning(SQLPartitionBy partitioning) {
        if (partitioning != null) {
            partitioning.setParent(this);
        }

        this.partitioning = partitioning;
    }

    public Map<String, SQLObject> getTableOptions() {
        return tableOptions;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, tableElementList);
            this.acceptChild(visitor, inherits);
            this.acceptChild(visitor, select);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        children.add(tableSource);
        children.addAll(tableElementList);
        if (inherits != null) {
            children.add(inherits);
        }
        if (select != null) {
            children.add(select);
        }
        return children;
    }

    @SuppressWarnings("unchecked")
    public void addBodyBeforeComment(List<String> comments) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>(1);
        }
        
        List<String> attrComments = (List<String>) attributes.get("format.body_before_comment");
        if (attrComments == null) {
            attributes.put("format.body_before_comment", comments);
        } else {
            attrComments.addAll(comments);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getBodyBeforeCommentsDirect() {
        if (attributes == null) {
            return null;
        }
        
        return (List<String>) attributes.get("format.body_before_comment");
    }
    
    public boolean hasBodyBeforeComment() {
        List<String> comments = getBodyBeforeCommentsDirect();
        if (comments == null) {
            return false;
        }
        
        return !comments.isEmpty();
    }

    public String computeName() {
        if (tableSource == null) {
            return null;
        }

        SQLExpr expr = tableSource.getExpr();
        if (expr instanceof SQLName) {
            String name = ((SQLName) expr).getSimpleName();
            return SQLUtils.normalize(name);
        }

        return null;
    }

    public SQLColumnDefinition findColumn(String columName) {
        if (columName == null) {
            return null;
        }

        long hash = FnvHash.hashCode64(columName);
        return findColumn(hash);
    }

    public SQLColumnDefinition findColumn(long columName_hash) {
        for (SQLTableElement element : tableElementList) {
            if (element instanceof SQLColumnDefinition) {
                SQLColumnDefinition column = (SQLColumnDefinition) element;
                SQLName columnName = column.getName();
                if (columnName != null && columnName.nameHashCode64() == columName_hash) {
                    return column;
                }
            }
        }

        return null;
    }

    public boolean isPrimaryColumn(String columnName) {
        SQLPrimaryKey pk = this.findPrimaryKey();
        if (pk == null) {
            return false;
        }

        return pk.containsColumn(columnName);
    }

    /**
     * only for show columns
     */
    public boolean isMUL(String columnName) {
        for (SQLTableElement element : this.tableElementList) {
            if (element instanceof MySqlUnique) {
                MySqlUnique unique = (MySqlUnique) element;

                SQLExpr column = unique.getColumns().get(0).getExpr();
                if (column instanceof SQLIdentifierExpr
                        && SQLUtils.nameEquals(columnName, ((SQLIdentifierExpr) column).getName())) {
                    return unique.columns.size() > 1;
                } else if (column instanceof SQLMethodInvokeExpr
                        && SQLUtils.nameEquals(((SQLMethodInvokeExpr) column).getMethodName(), columnName)) {
                    return true;
                }
            } else if (element instanceof MySqlKey) {
                MySqlKey unique = (MySqlKey) element;

                SQLExpr column = unique.getColumns().get(0).getExpr();
                if (column instanceof SQLIdentifierExpr
                        && SQLUtils.nameEquals(columnName, ((SQLIdentifierExpr) column).getName())) {
                    return true;
                } else if (column instanceof SQLMethodInvokeExpr
                        && SQLUtils.nameEquals(((SQLMethodInvokeExpr) column).getMethodName(), columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * only for show columns
     */
    public boolean isUNI(String columnName) {
        for (SQLTableElement element : this.tableElementList) {
            if (element instanceof MySqlUnique) {
                MySqlUnique unique = (MySqlUnique) element;

                if (unique.getColumns().size() == 0) {
                    continue;
                }

                SQLExpr column = unique.getColumns().get(0).getExpr();
                if (column instanceof SQLIdentifierExpr
                        && SQLUtils.nameEquals(columnName, ((SQLIdentifierExpr) column).getName())) {
                    return unique.columns.size() == 1;
                } else if (column instanceof SQLMethodInvokeExpr
                        && SQLUtils.nameEquals(((SQLMethodInvokeExpr) column).getMethodName(), columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public MySqlUnique findUnique(String columnName) {
        for (SQLTableElement element : this.tableElementList) {
            if (element instanceof MySqlUnique) {
                MySqlUnique unique = (MySqlUnique) element;

                if (unique.containsColumn(columnName)) {
                    return unique;
                }
            }
        }

        return null;
    }

    public SQLTableElement findIndex(String columnName) {
        for (SQLTableElement element : tableElementList) {
            if (element instanceof SQLUniqueConstraint) {
                SQLUniqueConstraint unique = (SQLUniqueConstraint) element;
                for (SQLSelectOrderByItem item : unique.getColumns()) {
                    SQLExpr columnExpr = item.getExpr();
                    if (columnExpr instanceof SQLIdentifierExpr) {
                        String keyColumName = ((SQLIdentifierExpr) columnExpr).getName();
                        keyColumName = SQLUtils.normalize(keyColumName);
                        if (keyColumName.equalsIgnoreCase(columnName)) {
                            return element;
                        }
                    }
                }

            } else if (element instanceof MySqlTableIndex) {
                List<SQLSelectOrderByItem> indexColumns = ((MySqlTableIndex) element).getColumns();
                for (SQLSelectOrderByItem orderByItem : indexColumns) {
                    SQLExpr columnExpr = orderByItem.getExpr();
                    if (columnExpr instanceof SQLIdentifierExpr) {
                        String keyColumName = ((SQLIdentifierExpr) columnExpr).getName();
                        keyColumName = SQLUtils.normalize(keyColumName);
                        if (keyColumName.equalsIgnoreCase(columnName)) {
                            return element;
                        }
                    }
                }
            }

        }

        return null;
    }

    public void forEachColumn(Consumer<SQLColumnDefinition> columnConsumer) {
        if (columnConsumer == null) {
            return;
        }

        for (SQLTableElement element : this.tableElementList) {
            if (element instanceof SQLColumnDefinition) {
                columnConsumer.accept((SQLColumnDefinition) element);
            }
        }
    }

    public SQLPrimaryKey findPrimaryKey() {
        for (SQLTableElement element : this.tableElementList) {
            if (element instanceof SQLPrimaryKey) {
                return (SQLPrimaryKey) element;
            }
        }

        return null;
    }

    public List<SQLForeignKeyConstraint> findForeignKey() {
        List<SQLForeignKeyConstraint> fkList = new ArrayList<SQLForeignKeyConstraint>();
        for (SQLTableElement element : this.tableElementList) {
            if (element instanceof SQLForeignKeyConstraint) {
                fkList.add((SQLForeignKeyConstraint) element);
            }
        }
        return fkList;
    }

    public boolean hashForeignKey() {
        for (SQLTableElement element : this.tableElementList) {
            if (element instanceof SQLForeignKeyConstraint) {
                return true;
            }
        }
        return false;
    }

    public boolean isReferenced(SQLName tableName) {
        if (tableName == null) {
            return false;
        }

        return isReferenced(tableName.getSimpleName());
    }

    public boolean isReferenced(String tableName) {
        if (tableName == null) {
            return false;
        }

        tableName = SQLUtils.normalize(tableName);

        for (SQLTableElement element : this.tableElementList) {
            if (element instanceof SQLForeignKeyConstraint) {
                SQLForeignKeyConstraint fk = (SQLForeignKeyConstraint) element;
                String refTableName = fk.getReferencedTableName().getSimpleName();

                if (SQLUtils.nameEquals(tableName, refTableName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public SQLAlterTableStatement foreignKeyToAlterTable() {
        SQLAlterTableStatement stmt = new SQLAlterTableStatement();
        for (int i = this.tableElementList.size() - 1; i >= 0; --i) {
            SQLTableElement element = this.tableElementList.get(i);
            if (element instanceof SQLForeignKeyConstraint) {
                SQLForeignKeyConstraint fk = (SQLForeignKeyConstraint) element;
                this.tableElementList.remove(i);
                stmt.addItem(new SQLAlterTableAddConstraint(fk));
            }
        }

        if (stmt.getItems().size() == 0) {
            return null;
        }

        stmt.setDbType(getDbType());
        stmt.setTableSource(this.tableSource.clone());

        Collections.reverse(stmt.getItems());

        return stmt;
    }

    public static void sort(List<SQLStatement> stmtList) {
        Map<String, SQLCreateTableStatement> tables = new HashMap<String, SQLCreateTableStatement>();
        Map<String, List<SQLCreateTableStatement>> referencedTables = new HashMap<String, List<SQLCreateTableStatement>>();

        for (SQLStatement stmt : stmtList) {
            if (stmt instanceof SQLCreateTableStatement) {
                SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) stmt;
                String tableName = createTableStmt.getName().getSimpleName();
                tableName = SQLUtils.normalize(tableName).toLowerCase();
                tables.put(tableName, createTableStmt);
            }
        }

        List<ListDG.Edge> edges = new ArrayList<ListDG.Edge>();

        for (SQLCreateTableStatement stmt : tables.values()) {
            for (SQLTableElement element : stmt.getTableElementList()) {
                if (element instanceof SQLForeignKeyConstraint) {
                    SQLForeignKeyConstraint fk = (SQLForeignKeyConstraint) element;
                    String refTableName = fk.getReferencedTableName().getSimpleName();
                    refTableName = SQLUtils.normalize(refTableName).toLowerCase();

                    SQLCreateTableStatement refTable = tables.get(refTableName);
                    if (refTable != null) {
                        edges.add(new ListDG.Edge(stmt, refTable));
                    }

                    List<SQLCreateTableStatement> referencedList = referencedTables.get(refTableName);
                    if (referencedList == null) {
                        referencedList = new ArrayList<SQLCreateTableStatement>();
                        referencedTables.put(refTableName, referencedList);
                    }
                    referencedList.add(stmt);
                }
            }
        }

        for (SQLStatement stmt : stmtList) {
            if (stmt instanceof OracleCreateSynonymStatement) {
                OracleCreateSynonymStatement createSynonym = (OracleCreateSynonymStatement) stmt;
                SQLName object = createSynonym.getObject();
                String refTableName = object.getSimpleName();
                SQLCreateTableStatement refTable = tables.get(refTableName);
                if (refTable != null) {
                    edges.add(new ListDG.Edge(stmt, refTable));
                }
            }
        }

        ListDG dg = new ListDG(stmtList, edges);

        SQLStatement[] tops = new SQLStatement[stmtList.size()];
        if (dg.topologicalSort(tops)) {
            for (int i = 0, size = stmtList.size(); i < size; ++i) {
                stmtList.set(i, tops[size - i - 1]);
            }
            return;
        }

        List<SQLAlterTableStatement> alterList = new ArrayList<SQLAlterTableStatement>();

        for (int i = edges.size() - 1; i >= 0; --i) {
            ListDG.Edge edge = edges.get(i);
            SQLCreateTableStatement from = (SQLCreateTableStatement) edge.from;
            String fromTableName = from.getName().getSimpleName();
            fromTableName = SQLUtils.normalize(fromTableName).toLowerCase();
            if (referencedTables.containsKey(fromTableName)) {
                edges.remove(i);

                Arrays.fill(tops, null);
                tops = new SQLStatement[stmtList.size()];

                dg = new ListDG(stmtList, edges);
                if (dg.topologicalSort(tops)) {
                    for (int j = 0, size = stmtList.size(); j < size; ++j) {
                        SQLStatement stmt = tops[size - j - 1];
                        stmtList.set(j, stmt);
                    }

                    SQLAlterTableStatement alter = from.foreignKeyToAlterTable();
                    alterList.add(alter);

                    stmtList.add(alter);
                    return;
                }
                edges.add(i, edge);
            }
        }

        for (int i = edges.size() - 1; i >= 0; --i) {
            ListDG.Edge edge = edges.get(i);
            SQLCreateTableStatement from = (SQLCreateTableStatement) edge.from;
            String fromTableName = from.getName().getSimpleName();
            fromTableName = SQLUtils.normalize(fromTableName).toLowerCase();
            if (referencedTables.containsKey(fromTableName)) {
                SQLAlterTableStatement alter = from.foreignKeyToAlterTable();

                edges.remove(i);
                if (alter != null) {
                    alterList.add(alter);
                }

                Arrays.fill(tops, null);
                tops = new SQLStatement[stmtList.size()];

                dg = new ListDG(stmtList, edges);
                if (dg.topologicalSort(tops)) {
                    for (int j = 0, size = stmtList.size(); j < size; ++j) {
                        SQLStatement stmt = tops[size - j - 1];
                        stmtList.set(j, stmt);
                    }

                    stmtList.addAll(alterList);
                    return;
                }
            }
        }
    }

    public void simplify() {
        SQLName name = getName();
        if (name instanceof SQLPropertyExpr) {
            String tableName = ((SQLPropertyExpr) name).getName();
            tableName = SQLUtils.normalize(tableName);

            String normalized = SQLUtils.normalize(tableName, dbType);
            if (tableName != normalized) {
                this.setName(normalized);
                name = getName();
            }
        }

        if (name instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) name;
            String tableName = identExpr.getName();
            String normalized = SQLUtils.normalize(tableName, dbType);
            if (normalized != tableName) {
                setName(normalized);
            }
        }

        for (SQLTableElement element : this.tableElementList) {
            if (element instanceof SQLColumnDefinition) {
                SQLColumnDefinition column = (SQLColumnDefinition) element;
                column.simplify();
            } else if (element instanceof SQLConstraint) {
                ((SQLConstraint) element).simplify();
            }
        }
    }

    public boolean apply(SQLDropIndexStatement x) {
        long indexNameHashCode64 = x.getIndexName().nameHashCode64();

        for (int i = tableElementList.size() - 1; i >= 0; i--) {
            SQLTableElement e = tableElementList.get(i);
            if (e instanceof SQLUniqueConstraint) {
                SQLUniqueConstraint unique = (SQLUniqueConstraint) e;
                if (unique.getName().nameHashCode64() == indexNameHashCode64) {
                    tableElementList.remove(i);
                    return true;
                }

            } else if (e instanceof MySqlTableIndex) {
                MySqlTableIndex tableIndex = (MySqlTableIndex) e;
                if (SQLUtils.nameEquals(tableIndex.getName(), x.getIndexName())) {
                    tableElementList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean apply(SQLCommentStatement x) {
        SQLName on = x.getOn().getName();
        SQLExpr comment = x.getComment();
        if (comment == null) {
            return false;
        }

        SQLCommentStatement.Type type = x.getType();
        if (type == SQLCommentStatement.Type.TABLE) {
            if (!SQLUtils.nameEquals(getName(), on)) {
                return false;
            }

            setComment(comment.clone());

            return true;
        } else if (type == SQLCommentStatement.Type.COLUMN) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) on;
            if (!SQLUtils.nameEquals(getName(), (SQLName) propertyExpr.getOwner())) {
                return false;
            }

            SQLColumnDefinition column
                    = this.findColumn(
                        propertyExpr.nameHashCode64());

            if (column != null) {
                column.setComment(comment.clone());
            }
            return true;
        }

        return false;
    }

    public boolean apply(SQLAlterTableStatement alter) {
        if (!SQLUtils.nameEquals(alter.getName(), this.getName())) {
            return false;
        }

        int applyCount = 0;
        for (SQLAlterTableItem item : alter.getItems()) {
            if (alterApply(item)) {
                applyCount++;
            }
        }

        return applyCount > 0;
    }

    protected boolean alterApply(SQLAlterTableItem item) {
        if (item instanceof SQLAlterTableDropColumnItem) {
            return apply((SQLAlterTableDropColumnItem) item);

        } else if (item instanceof SQLAlterTableAddColumn) {
            return apply((SQLAlterTableAddColumn) item);

        } else if (item instanceof SQLAlterTableAddConstraint) {
            return apply((SQLAlterTableAddConstraint) item);

        } else if (item instanceof SQLAlterTableDropPrimaryKey) {
            return apply((SQLAlterTableDropPrimaryKey) item);

        } else if (item instanceof SQLAlterTableDropIndex) {
            return apply((SQLAlterTableDropIndex) item);

        } else if (item instanceof SQLAlterTableDropConstraint) {
            return apply((SQLAlterTableDropConstraint) item);

        } else if (item instanceof SQLAlterTableDropKey) {
            return apply((SQLAlterTableDropKey) item);

        } else if (item instanceof SQLAlterTableDropForeignKey) {
            return apply((SQLAlterTableDropForeignKey) item);

        } else if (item instanceof SQLAlterTableRename) {
            return apply((SQLAlterTableRename) item);

        } else if (item instanceof SQLAlterTableRenameColumn) {
            return apply((SQLAlterTableRenameColumn) item);

        } else if (item instanceof SQLAlterTableAddIndex) {
            return apply((SQLAlterTableAddIndex) item);
        }

        return false;
    }

    // SQLAlterTableRenameColumn

    private boolean apply(SQLAlterTableRenameColumn item) {
        int columnIndex = columnIndexOf(item.getColumn());
        if (columnIndex == -1) {
            return false;
        }

        SQLColumnDefinition column = (SQLColumnDefinition) tableElementList.get(columnIndex);
        column.setName(item.getTo().clone());

        return true;
    }

    public boolean renameColumn(String colummName, String newColumnName) {
        if (colummName == null || newColumnName == null || newColumnName.length() == 0) {
            return false;
        }

        int columnIndex = columnIndexOf(new SQLIdentifierExpr(colummName));
        if (columnIndex == -1) {
            return false;
        }

        SQLColumnDefinition column = (SQLColumnDefinition) tableElementList.get(columnIndex);
        column.setName(new SQLIdentifierExpr(newColumnName));

        return true;
    }

    private boolean apply(SQLAlterTableRename item) {
        SQLName name = item.getToName();
        if (name == null) {
            return false;
        }

        this.setName(name.clone());

        return true;
    }

    private boolean apply(SQLAlterTableDropForeignKey item) {
        for (int i = tableElementList.size() - 1; i >= 0; i--) {
            SQLTableElement e = tableElementList.get(i);
            if (e instanceof SQLUniqueConstraint) {
                SQLForeignKeyConstraint fk = (SQLForeignKeyConstraint) e;
                if (SQLUtils.nameEquals(fk.getName(), item.getIndexName())) {
                    tableElementList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean apply(SQLAlterTableDropKey item) {
        for (int i = tableElementList.size() - 1; i >= 0; i--) {
            SQLTableElement e = tableElementList.get(i);
            if (e instanceof SQLUniqueConstraint) {
                SQLUniqueConstraint unique = (SQLUniqueConstraint) e;
                if (SQLUtils.nameEquals(unique.getName(), item.getKeyName())) {
                    tableElementList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean apply(SQLAlterTableDropConstraint item) {
        for (int i = tableElementList.size() - 1; i >= 0; i--) {
            SQLTableElement e = tableElementList.get(i);
            if (e instanceof SQLConstraint) {
                SQLConstraint constraint = (SQLConstraint) e;
                if (SQLUtils.nameEquals(constraint.getName(), item.getConstraintName())) {
                    tableElementList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean apply(SQLAlterTableDropIndex item) {
        for (int i = tableElementList.size() - 1; i >= 0; i--) {
            SQLTableElement e = tableElementList.get(i);
            if (e instanceof SQLUniqueConstraint) {
                SQLUniqueConstraint unique = (SQLUniqueConstraint) e;
                if (SQLUtils.nameEquals(unique.getName(), item.getIndexName())) {
                    tableElementList.remove(i);
                    return true;
                }

            } else if (e instanceof MySqlTableIndex) {
                MySqlTableIndex tableIndex = (MySqlTableIndex) e;
                if (SQLUtils.nameEquals(tableIndex.getName(), item.getIndexName())) {
                    tableElementList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean apply(SQLAlterTableDropPrimaryKey item) {
        for (int i = tableElementList.size() - 1; i >= 0; i--) {
            SQLTableElement e = tableElementList.get(i);
            if (e instanceof SQLPrimaryKey) {
                tableElementList.remove(i);
                return true;
            }
        }
        return false;
    }

    private boolean apply(SQLAlterTableAddConstraint item) {
        tableElementList.add((SQLTableElement) item.getConstraint());
        return true;
    }

    private boolean apply(SQLAlterTableDropColumnItem item) {
        for (SQLName column : item.getColumns()) {
            String columnName = column.getSimpleName();
            for (int i = tableElementList.size() - 1; i >= 0; --i) {
                SQLTableElement e = tableElementList.get(i);
                if (e instanceof SQLColumnDefinition) {
                    if (SQLUtils.nameEquals(columnName, ((SQLColumnDefinition) e).getName().getSimpleName())) {
                        tableElementList.remove(i);
                    }
                }
            }

            for (int i = tableElementList.size() - 1; i >= 0; --i) {
                SQLTableElement e = tableElementList.get(i);
                if (e instanceof SQLUnique) {
                    SQLUnique unique = (SQLUnique) e;
                    unique.applyDropColumn(column);
                    if (unique.getColumns().size() == 0) {
                        tableElementList.remove(i);
                    }
                } else if (e instanceof MySqlTableIndex) {
                    MySqlTableIndex index = (MySqlTableIndex) e;
                    index.applyDropColumn(column);
                    if (index.getColumns().size() == 0) {
                        tableElementList.remove(i);
                    }
                }
            }
        }



        return true;
    }

    protected boolean apply(SQLAlterTableAddIndex item) {
        return false;
    }

    private boolean apply(SQLAlterTableAddColumn item) {
        int startIndex = tableElementList.size();
        if (item.isFirst()) {
            startIndex = 0;
        }

        int afterIndex = columnIndexOf(item.getAfterColumn());
        if (afterIndex != -1) {
            startIndex = afterIndex + 1;
        }

        int beforeIndex = columnIndexOf(item.getFirstColumn());
        if (beforeIndex != -1) {
            startIndex = beforeIndex;
        }

        for (int i = 0; i < item.getColumns().size(); i++) {
            SQLColumnDefinition column = item.getColumns().get(i);
            tableElementList.add(i + startIndex, column);
            column.setParent(this);
        }

        return true;
    }

    protected int columnIndexOf(SQLName column) {
        if (column == null) {
            return -1;
        }

        String columnName = column.getSimpleName();
        for (int i = tableElementList.size() - 1; i >= 0; --i) {
            SQLTableElement e = tableElementList.get(i);
            if (e instanceof SQLColumnDefinition) {
                if (SQLUtils.nameEquals(columnName, ((SQLColumnDefinition) e).getName().getSimpleName())) {
                    return i;
                }
            }
        }

        return -1;
    }

    public void cloneTo(SQLCreateTableStatement x) {
        x.ifNotExiists = ifNotExiists;
        x.type = type;
        if (tableSource != null) {
            x.setTableSource(tableSource.clone());
        }
        for (SQLTableElement e : tableElementList) {
            SQLTableElement e2 = e.clone();
            e2.setParent(x);
            x.tableElementList.add(e2);
        }
        if (inherits != null) {
            x.setInherits(inherits.clone());
        }
        if (select != null) {
            x.setSelect(select.clone());
        }
        if (comment != null) {
            x.setComment(comment.clone());
        }

        x.onCommitPreserveRows = onCommitPreserveRows;
        x.onCommitDeleteRows = onCommitDeleteRows;

        if (tableOptions != null) {
            for (Map.Entry<String, SQLObject> entry : tableOptions.entrySet()) {
                SQLObject entryVal = entry.getValue().clone();
                x.tableOptions.put(entry.getKey(), entryVal);
            }
        }
    }

    public SQLName getStoredAs() {
        return storedAs;
    }

    public void setStoredAs(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.storedAs = x;
    }

    public SQLCreateTableStatement clone() {
        SQLCreateTableStatement x = new SQLCreateTableStatement(dbType);
        cloneTo(x);
        return x;
    }

    public String toString() {
        return SQLUtils.toSQLString(this, dbType);
    }

    public boolean isOnCommitPreserveRows() {
        return onCommitPreserveRows;
    }

    public void setOnCommitPreserveRows(boolean onCommitPreserveRows) {
        this.onCommitPreserveRows = onCommitPreserveRows;
    }

    public List<SQLName> getClusteredBy() {
        return clusteredBy;
    }

    public List<SQLSelectOrderByItem> getSortedBy() {
        return sortedBy;
    }

    public void addSortedByItem(SQLSelectOrderByItem item) {
        item.setParent(this);
        this.sortedBy.add(item);
    }

    public int getBuckets() {
        return buckets;
    }

    public void setBuckets(int buckets) {
        this.buckets = buckets;
    }

    public List<SQLColumnDefinition> getPartitionColumns() {
        return partitionColumns;
    }

    public void addPartitionColumn(SQLColumnDefinition column) {
        if (column != null) {
            column.setParent(this);
        }
        this.partitionColumns.add(column);
    }

    public SQLExternalRecordFormat getRowFormat() {
        return rowFormat;
    }

    public void setRowFormat(SQLExternalRecordFormat x) {
        if (x != null) {
            x.setParent(this);
        }
        this.rowFormat = x;
    }

    public boolean isPrimaryColumn(long columnNameHash) {
        SQLPrimaryKey pk = this.findPrimaryKey();
        if (pk == null) {
            return false;
        }

        return pk.containsColumn(columnNameHash);
    }
}
