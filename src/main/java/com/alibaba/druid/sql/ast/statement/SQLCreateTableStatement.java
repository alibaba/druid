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

import java.io.IOException;
import java.util.*;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlShowColumnOutpuVisitor;
import com.alibaba.druid.util.ListDG;
import com.alibaba.druid.util.lang.Consumer;

public class SQLCreateTableStatement extends SQLStatementImpl implements SQLDDLStatement {

    protected boolean            ifNotExiists = false;
    protected Type               type;
    protected SQLExprTableSource tableSource;

    protected List<SQLTableElement> tableElementList = new ArrayList<SQLTableElement>();

    // for postgresql
    private SQLExprTableSource inherits;

    protected SQLSelect select;

    public SQLCreateTableStatement(){

    }

    public SQLCreateTableStatement(String dbType){
        super(dbType);
    }

    public SQLName getName() {
        if (tableSource == null) {
            return null;
        }

        return (SQLName) tableSource.getExpr();
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
        this.select = select;
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
        columName = SQLUtils.normalize(columName);

        for (SQLTableElement element : tableElementList) {
            if (element instanceof SQLColumnDefinition) {
                SQLColumnDefinition column = (SQLColumnDefinition) element;
                String name = column.computeAlias();
                if (columName.equalsIgnoreCase(name)) {
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

                SQLExpr column = unique.getColumns().get(0);
                if (column instanceof SQLIdentifierExpr
                        && SQLUtils.nameEquals(columnName, ((SQLIdentifierExpr) column).getName())) {
                    return unique.columns.size() > 1;
                }
            } else if (element instanceof MySqlKey) {
                MySqlKey unique = (MySqlKey) element;

                SQLExpr column = unique.getColumns().get(0);
                if (column instanceof SQLIdentifierExpr
                        && SQLUtils.nameEquals(columnName, ((SQLIdentifierExpr) column).getName())) {
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

                SQLExpr column = unique.getColumns().get(0);
                if (column instanceof SQLIdentifierExpr
                        && SQLUtils.nameEquals(columnName, ((SQLIdentifierExpr) column).getName())) {
                    return unique.columns.size() == 1;
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
            List<SQLExpr> keyColumns = null;
            if (element instanceof SQLUniqueConstraint) {
                SQLUniqueConstraint unique = (SQLUniqueConstraint) element;
                keyColumns = unique.getColumns();
            } else if (element instanceof MySqlTableIndex) {
                keyColumns = ((MySqlTableIndex) element).getColumns();
            }

            if (keyColumns == null) {
                continue;
            }

            for (SQLExpr columnExpr : keyColumns) {
                if (columnExpr instanceof SQLIdentifierExpr) {
                    String keyColumName = ((SQLIdentifierExpr) columnExpr).getName();
                    keyColumName = SQLUtils.normalize(keyColumName);
                    if (keyColumName.equalsIgnoreCase(columnName)) {
                        return element;
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
            setName(tableName);
            name = getName();
        }

        if (name instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) name;
            String tableName = identExpr.getName();
            tableName = SQLUtils.normalize(tableName, dbType);
            identExpr.setName(tableName);
        }

        if (name instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) name;
            String tableName = identExpr.getName();
            tableName = SQLUtils.normalize(tableName);
            identExpr.setName(tableName);
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

    public boolean apply(SQLAlterTableStatement alter) {
        if (!SQLUtils.nameEquals(alter.getName(), this.getName())) {
            return false;
        }

        int applyCount = 0;
        for (SQLAlterTableItem item : alter.getItems()) {
            if (item instanceof SQLAlterTableDropColumnItem) {
                if (apply((SQLAlterTableDropColumnItem) item)) {
                    applyCount++;
                }
            } else if (item instanceof SQLAlterTableAddColumn) {
                if (apply((SQLAlterTableAddColumn) item)) {
                    applyCount++;
                }
            }
        }

        return applyCount > 0;
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
        }
        return true;
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

    private int columnIndexOf(SQLName column) {
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
}
