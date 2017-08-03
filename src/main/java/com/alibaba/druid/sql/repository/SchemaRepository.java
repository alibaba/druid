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
package com.alibaba.druid.sql.repository;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by wenshao on 03/06/2017.
 */
public class SchemaRepository {
    private Schema defaultSchema;
    protected String dbType;
    protected SQLASTVisitor consoleVisitor;
    protected SQLASTVisitor resolveVisitor;

    public SchemaRepository() {

    }

    public SchemaRepository(String dbType) {
        this.dbType = dbType;

        if (JdbcConstants.MYSQL.equals(dbType)) {
            consoleVisitor = new MySqlConsoleSchemaVisitor();
            resolveVisitor = new MySqlResolveVisitor();
        } else if (JdbcConstants.ORACLE.equals(dbType)) {
            consoleVisitor = new OracleConsoleSchemaVisitor();
            resolveVisitor = new OracleResolveVisitor();
        } else {
            consoleVisitor = new DefaultConsoleSchemaVisitor();
        }
    }

    private Map<String, Schema> schemas = new LinkedHashMap<String, Schema>();

    public String getDefaultSchemaName() {
        return getDefaultSchema().getName();
    }

    public void setDefaultSchema(String name) {
        if (name == null) {
            defaultSchema = null;
            return;
        }

        String normalizedName = SQLUtils.normalize(name)
                .toLowerCase();

        Schema defaultSchema = schemas.get(normalizedName);
        if (defaultSchema != null) {
            this.defaultSchema = defaultSchema;
            return;
        }

        if (defaultSchema == null) {
            if (this.defaultSchema != null
                    && this.defaultSchema.getName() == null) {
                this.defaultSchema.setName(name);

                schemas.put(normalizedName, this.defaultSchema);
                return;
            }

            defaultSchema = new Schema(this);
            defaultSchema.setName(name);
            schemas.put(normalizedName, defaultSchema);
            this.defaultSchema = defaultSchema;
        }
    }

    public Schema findSchema(String schema) {
        return findSchema(schema, false);
    }

    protected Schema findSchema(String name, boolean create) {
        if (name == null || name.length() == 0) {
            return getDefaultSchema();
        }

        name = SQLUtils.normalize(name);
        String normalizedName = name.toLowerCase();

        if (getDefaultSchema() != null && defaultSchema.getName() == null) {
            defaultSchema.setName(name);
            schemas.put(normalizedName, defaultSchema);
            return defaultSchema;
        }

        Schema schema = schemas.get(normalizedName);
        if (schema == null) {
            schema = new Schema(this, name);
            schemas.put(normalizedName, schema);
        }
        return schema;
    }

    public Schema getDefaultSchema() {
        if (defaultSchema == null) {
            defaultSchema = new Schema(this);
        }

        return defaultSchema;
    }

    public void setDefaultSchema(Schema schema) {
        this.defaultSchema = schema;
    }

    public SchemaObject findTable(String tableName) {
        return getDefaultSchema().findTable(tableName);
    }

    public SchemaObject findTableOrView(String tableName) {
        return findTableOrView(tableName, true);
    }

    public SchemaObject findTableOrView(String tableName, boolean onlyCurrent) {
        Schema schema = getDefaultSchema();

        SchemaObject object = schema.findTableOrView(tableName);
        if (object != null) {
            return object;
        }

        for (Schema s : this.schemas.values()) {
            if (s == schema) {
                continue;
            }

            object = schema.findTableOrView(tableName);
            if (object != null) {
                return object;
            }
        }

        return null;
    }

    public Collection<Schema> getSchemas() {
        return schemas.values();
    }

    public SchemaObject findFunction(String functionName) {
        return getDefaultSchema().findFunction(functionName);
    }

    public void acceptDDL(String ddl) {
        acceptDDL(ddl, dbType);
    }

    public void acceptDDL(String ddl, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(ddl, dbType);
        for (SQLStatement stmt : stmtList) {
            accept(stmt);
        }
    }

    public void accept(SQLStatement stmt) {
        stmt.accept(consoleVisitor);
    }

    public boolean isSequence(String name) {
        return getDefaultSchema().isSequence(name);
    }

    public SchemaObject findTable(SQLTableSource tableSource, String alias) {
        return getDefaultSchema().findTable(tableSource, alias);
    }

    public SQLColumnDefinition findColumn(SQLTableSource tableSource, SQLSelectItem selectItem) {
        return getDefaultSchema().findColumn(tableSource, selectItem);
    }

    public SQLColumnDefinition findColumn(SQLTableSource tableSource, SQLExpr expr) {
        return getDefaultSchema().findColumn(tableSource, expr);
    }

    public SchemaObject findTable(SQLTableSource tableSource, SQLSelectItem selectItem) {
        return getDefaultSchema().findTable(tableSource, selectItem);
    }

    public SchemaObject findTable(SQLTableSource tableSource, SQLExpr expr) {
        return getDefaultSchema().findTable(tableSource, expr);
    }

    public Map<String, SchemaObject> getTables(SQLTableSource x) {
        return getDefaultSchema().getTables(x);
    }

    public int getTableCount() {
        return getDefaultSchema().getTableCount();
    }

    public Map<String, SchemaObject> getObjects() {
        return getDefaultSchema().getObjects();
    }

    public int getViewCount() {
        return getDefaultSchema().getViewCount();
    }

    public String resolve(String input) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(input, dbType);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(resolveVisitor);
        }

        return SQLUtils.toSQLString(stmtList, dbType);
    }

    public String console(String input) {
        try {
            StringBuffer buf = new StringBuffer();

            List<SQLStatement> stmtList = SQLUtils.parseStatements(input, dbType);

            for (SQLStatement stmt : stmtList) {
                if (stmt instanceof MySqlShowColumnsStatement) {
                    MySqlShowColumnsStatement showColumns = ((MySqlShowColumnsStatement) stmt);
                    SQLName table = showColumns.getTable();
                    SchemaObject schemaObject = findTable(table);
                    MySqlCreateTableStatement createTableStmt = (MySqlCreateTableStatement) schemaObject.getStatement();
                    createTableStmt.showCoumns(buf);
                } else if (stmt instanceof MySqlShowCreateTableStatement) {
                    MySqlShowCreateTableStatement showCreateTableStmt = (MySqlShowCreateTableStatement) stmt;
                    SQLName table = showCreateTableStmt.getName();
                    SchemaObject schemaObject = findTable(table);
                    MySqlCreateTableStatement createTableStmt = (MySqlCreateTableStatement) schemaObject.getStatement();
                    createTableStmt.output(buf);
                } else if (stmt instanceof SQLShowTablesStatement) {
                    SQLShowTablesStatement showTables = (SQLShowTablesStatement) stmt;
                    SQLName database = showTables.getDatabase();

                    Schema schema;
                    if (database == null) {
                        schema = getDefaultSchema();
                    } else {
                        schema = findSchema(database.getSimpleName());
                    }
                    if (schema != null) {
                        for (String table : schema.showTables()) {
                            buf.append(table);
                            buf.append('\n');
                        }
                    }

                } else {
                    stmt.accept(consoleVisitor);
                }
            }

            if (buf.length() == 0) {
                return "\n";
            }

            return buf.toString();
        } catch (IOException ex) {
            throw new DruidRuntimeException("exeucte command error.", ex);
        }
    }

    public SchemaObject findTable(SQLName name) {
        if (name instanceof SQLIdentifierExpr) {
            return findTable(((SQLIdentifierExpr) name).getName());
        }

        if (name instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) name;
            String schema = propertyExpr.getOwnernName();
            String table = propertyExpr.getName();

            Schema schemaObj = findSchema(schema);
            if (schemaObj == null) {
                return null;
            }

            return schemaObj.findTable(table);
        }

        return null;
    }

    public class MySqlResolveVisitor extends MySqlASTVisitorAdapter {

        public boolean visit(SQLExprTableSource x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(MySqlSelectQueryBlock x) {
            x.getFrom().accept(this);

            resolve(this, x);
            return super.visit(x);
        }
    }

    public class OracleResolveVisitor extends OracleASTVisitorAdapter {
        public boolean visit(OracleSelectTableReference x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLExprTableSource x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OracleSelectQueryBlock x) {
            x.getFrom().accept(this);

            resolve(this, x);
            return super.visit(x);
        }

        public boolean visit(SQLIdentifierExpr x) {
            resolve(this, x);
            return true;
        }
    }

    public class OdpsResolveVisitor extends OdpsASTVisitorAdapter {

        public boolean visit(SQLExprTableSource x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OdpsSelectQueryBlock x) {
            resolve(this, x);
            return super.visit(x);
        }
    }

    private void resolve(SQLASTVisitor visitor, SQLIdentifierExpr x) {
        SQLSelectQueryBlock queryBlock = null;
        for (SQLObject parent = x.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof SQLTableSource) {
                return;
            }
            if (parent instanceof SQLSelectQueryBlock) {
                queryBlock = (SQLSelectQueryBlock) parent;
                break;
            }
        }

        if (queryBlock == null) {
            return;
        }

        String ident = x.getName();
        SQLTableSource tableSource = queryBlock.findTableSourceWithColumn(ident);
        if (tableSource != null) {
            String alias = tableSource.computeAlias();
            SQLPropertyExpr propertyExpr = new SQLPropertyExpr(alias, ident);
            SQLUtils.replaceInParent(x, propertyExpr);
        }
    }

    private void resolve(SQLASTVisitor visitor, SQLExprTableSource x) {
        if (x.getSchemaObject() != null) {
            return;
        }

        SQLExpr expr = x.getExpr();
        if (expr instanceof SQLName) {
            SchemaObject table = findTable((SQLName) expr);
            if (table != null) {
                x.setSchemaObject(table);
            }
        }
    }

    private void resolve(SQLASTVisitor visitor, SQLSelectQueryBlock x) {
        SQLTableSource from = x.getFrom();
        from.accept(visitor);

        List<SQLSelectItem> selectList = x.getSelectList();

        List<SQLSelectItem> columns = new ArrayList<SQLSelectItem>();
        for (int i = selectList.size() - 1; i >= 0; i--) {
            SQLSelectItem selectItem = selectList.get(i);
            SQLExpr expr = selectItem.getExpr();
            if (expr instanceof SQLAllColumnExpr) {
                extractColumns(from, columns);
            } else if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                String ownerName = propertyExpr.getOwnernName();
                if (propertyExpr.getName().equals("*")) {
                    SQLTableSource tableSource = x.findTableSource(ownerName);
                    extractColumns(tableSource, columns);
                }

                SQLColumnDefinition column = propertyExpr.getResolvedColumn();
                if (column != null) {
                    continue;
                }
                SQLTableSource tableSource = x.findTableSource(propertyExpr.getOwnernName());
                if (tableSource != null) {
                    column = tableSource.findColumn(propertyExpr.getName());
                    if (column != null) {
                        propertyExpr.setResolvedColumn(column);
                    }
                }
            } else if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                SQLColumnDefinition column = identExpr.getResolvedColumn();
                if (column != null) {
                    continue;
                }
                column = from.findColumn(identExpr.getName());
                if (column != null) {
                    identExpr.setResolvedColumn(column);
                }
            }

            if (columns.size() > 0) {
                for (SQLSelectItem column : columns) {
                    column.setParent(x);
                }

                selectList.remove(i);
                selectList.addAll(i, columns);
            }
        }
    }

    private void extractColumns(SQLTableSource from, List<SQLSelectItem> columns) {
        if (from instanceof SQLExprTableSource) {
            SchemaObject table = findTable((SQLExprTableSource) from);
            if (table != null) {
                SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) table.getStatement();
                for (SQLTableElement e : createTableStmt.getTableElementList()) {
                    if (e instanceof SQLColumnDefinition) {
                        SQLColumnDefinition column = (SQLColumnDefinition) e;
                        SQLIdentifierExpr name = (SQLIdentifierExpr) column.getName().clone();
                        name.setResolvedColumn(column);
                        columns.add(new SQLSelectItem(name));
                    }
                }
            }
        }
    }

    public SchemaObject findTable(SQLExprTableSource x) {
        if (x == null) {
            return null;
        }

        SQLExpr expr = x.getExpr();
        if (expr instanceof SQLName) {
            return findTable((SQLName) expr);
        }

        return null;
    }

    public class MySqlConsoleSchemaVisitor extends MySqlASTVisitorAdapter {
        public boolean visit(SQLDropSequenceStatement x) {
            acceptDropSequence(x);
            return false;
        }

        public boolean visit(SQLCreateSequenceStatement x) {
            acceptCreateSequence(x);
            return false;
        }

        public boolean visit(MySqlCreateTableStatement x) {
            acceptCreateTable(x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            acceptCreateTable(x);
            return false;
        }

        public boolean visit(SQLDropTableStatement x) {
            acceptDropTable(x);
            return false;
        }

        public boolean visit(SQLCreateViewStatement x) {
            acceptView(x);
            return false;
        }

        public boolean visit(SQLCreateIndexStatement x) {
            acceptCreateIndex(x);
            return false;
        }

        public boolean visit(SQLCreateFunctionStatement x) {
            acceptCreateFunction(x);
            return false;
        }

        public boolean visit(SQLAlterTableStatement x) {
            acceptAlterTable(x);
            return false;
        }


        public boolean visit(SQLUseStatement x) {
            String schema = x.getDatabase().getSimpleName();
            setDefaultSchema(schema);
            return false;
        }
    }

    public class OracleConsoleSchemaVisitor extends OracleASTVisitorAdapter {
        public boolean visit(SQLDropSequenceStatement x) {
            acceptDropSequence(x);
            return false;
        }

        public boolean visit(SQLCreateSequenceStatement x) {
            acceptCreateSequence(x);
            return false;
        }

        public boolean visit(OracleCreateTableStatement x) {
            visit((SQLCreateTableStatement) x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            acceptCreateTable(x);
            return false;
        }

        public boolean visit(SQLDropTableStatement x) {
            acceptDropTable(x);
            return false;
        }

        public boolean visit(SQLCreateViewStatement x) {
            acceptView(x);
            return false;
        }

        public boolean visit(SQLCreateIndexStatement x) {
            acceptCreateIndex(x);
            return false;
        }

        public boolean visit(SQLCreateFunctionStatement x) {
            acceptCreateFunction(x);
            return false;
        }

        public boolean visit(SQLAlterTableStatement x) {
            acceptAlterTable(x);
            return false;
        }

        public boolean visit(SQLUseStatement x) {
            String schema = x.getDatabase().getSimpleName();
            setDefaultSchema(schema);
            return false;
        }
    }

    public class DefaultConsoleSchemaVisitor extends SQLASTVisitorAdapter {
        public boolean visit(SQLDropSequenceStatement x) {
            acceptDropSequence(x);
            return false;
        }

        public boolean visit(SQLCreateSequenceStatement x) {
            acceptCreateSequence(x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            acceptCreateTable(x);
            return false;
        }

        public boolean visit(SQLDropTableStatement x) {
            acceptDropTable(x);
            return false;
        }

        public boolean visit(SQLCreateViewStatement x) {
            acceptView(x);
            return false;
        }

        public boolean visit(SQLCreateIndexStatement x) {
            acceptCreateIndex(x);
            return false;
        }

        public boolean visit(SQLCreateFunctionStatement x) {
            acceptCreateFunction(x);
            return false;
        }

        public boolean visit(SQLAlterTableStatement x) {
            acceptAlterTable(x);
            return false;
        }
    }

    boolean acceptCreateTable(MySqlCreateTableStatement x) {
        SQLExprTableSource like = x.getLike();
        if (like != null) {
            SchemaObject table = findTable((SQLName) like.getExpr());
            if (table != null) {
                MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) table.getStatement();
                MySqlCreateTableStatement stmtCloned = stmt.clone();
                stmtCloned.setName(x.getName().clone());
                acceptCreateTable((SQLCreateTableStatement) stmtCloned);
                return false;
            }
        }

        return acceptCreateTable((SQLCreateTableStatement) x);
    }

    boolean acceptCreateTable(SQLCreateTableStatement x) {
        String schemaName = x.getSchema();

        Schema schema = findSchema(schemaName, true);

        SQLSelect select = x.getSelect();
        if (select != null) {
            select.accept(resolveVisitor);

            SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
            if (queryBlock != null) {
                List<SQLSelectItem> selectList = queryBlock.getSelectList();
                for (SQLSelectItem selectItem : selectList) {
                    String name = selectItem.computeAlias();
                    SQLDataType dataType = selectItem.computeDataType();
                    SQLColumnDefinition column = new SQLColumnDefinition();
                    column.setName(name);
                    column.setDataType(dataType);
                    column.setDbType(dbType);
                    x.getTableElementList().add(column);
                }
                x.setSelect(null);
            }
        }

        x.setSchema(null);

        String name = x.computeName();
        SchemaObject table = schema.findTableOrView(name);
        if (table != null) {
            return false;
        }

        table = new SchemaObjectImpl(name, SchemaObjectType.Table, x.clone());
        String name_lower = SQLUtils.normalize(name).toLowerCase();
        schema.objects.put(name_lower, table);
        return true;
    }

    boolean acceptDropTable(SQLDropTableStatement x) {
        for (SQLExprTableSource table : x.getTableSources()) {
            String schemaName = table.getSchema();
            Schema schema = findSchema(schemaName, false);
            if (schema == null) {
                continue;
            }
            String name = table.getName().getSimpleName();
            String name_lower = SQLUtils.normalize(name).toLowerCase();
            schema.objects.remove(name_lower);
        }
        return true;
    }

    public boolean acceptView(SQLCreateViewStatement x) {
        String schemaName = x.getSchema();

        Schema schema = findSchema(schemaName, true);

        String name = x.computeName();
        SchemaObject view = schema.findTableOrView(name);
        if (view != null) {
            return false;
        }

        SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.View, x.clone());
        String name_lower = SQLUtils.normalize(name).toLowerCase();
        schema.objects.put(name_lower, object);
        return true;
    }

    public boolean acceptCreateIndex(SQLCreateIndexStatement x) {
        String schemaName = x.getSchema();

        Schema schema = findSchema(schemaName, true);

        String name = x.getName().getSimpleName();
        SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.Index, x.clone());
        String name_lower = SQLUtils.normalize(name).toLowerCase();
        schema.objects.put(name_lower, object);

        return true;
    }

    public boolean acceptCreateFunction(SQLCreateFunctionStatement x) {
        String schemaName = x.getSchema();
        Schema schema = findSchema(schemaName, true);

        String name = x.getName().getSimpleName();
        SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.Function, x.clone());
        String name_lower = SQLUtils.normalize(name).toLowerCase();
        schema.functions.put(name_lower, object);

        return true;
    }

    public boolean acceptAlterTable(SQLAlterTableStatement x) {
        String schemaName = x.getSchema();
        Schema schema = findSchema(schemaName, true);

        SchemaObject object = schema.findTable(x.getTableName());
        if (object != null) {
            SQLCreateTableStatement stmt = (SQLCreateTableStatement) object.getStatement();
            if (stmt != null) {
                stmt.apply(x);
                return true;
            }
        }

        return false;
    }

    public boolean acceptCreateSequence(SQLCreateSequenceStatement x) {
        String schemaName = x.getSchema();
        Schema schema = findSchema(schemaName, true);

        String name = x.getName().getSimpleName();
        SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.Sequence);
        String name_lower = SQLUtils.normalize(name).toLowerCase();
        schema.objects.put(name_lower, object);
        return false;
    }

    public boolean acceptDropSequence(SQLDropSequenceStatement x) {
        String schemaName = x.getSchema();
        Schema schema = findSchema(schemaName, true);

        String name = x.getName().getSimpleName();
        String name_lower = SQLUtils.normalize(name).toLowerCase();
        schema.objects.remove(name_lower);
        return false;
    }
}
