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
package com.alibaba.druid.sql.repository;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcConstants;

import java.io.IOException;
import java.util.*;

/**
 * Created by wenshao on 03/06/2017.
 */
public class SchemaRepository {
    private static Log LOG = LogFactory.getLog(SchemaRepository.class);
    private Schema defaultSchema;
    protected String dbType;
    protected SQLASTVisitor consoleVisitor;

    public SchemaRepository() {

    }

    public SchemaRepository(String dbType) {
        this.dbType = dbType;

        if (JdbcConstants.MYSQL.equals(dbType)) {
            consoleVisitor = new MySqlConsoleSchemaVisitor();
        } else if (JdbcConstants.ORACLE.equals(dbType)) {
            consoleVisitor = new OracleConsoleSchemaVisitor();
        } else {
            consoleVisitor = new DefaultConsoleSchemaVisitor();
        }
    }

    private Map<String, Schema> schemas = new LinkedHashMap<String, Schema>();

    public String getDbType() {
        return dbType;
    }

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

    public Collection<SchemaObject> getObjects() {
        return getDefaultSchema().getObjects();
    }

    public int getViewCount() {
        return getDefaultSchema().getViewCount();
    }

    public void resolve(SQLSelectStatement stmt, SchemaResolveVisitor.Option... options) {
        if (stmt == null) {
            return;
        }

        SchemaResolveVisitor resolveVisitor = createResolveVisitor(options);
        resolveVisitor.visit(stmt);
    }

    public void resolve(SQLStatement stmt, SchemaResolveVisitor.Option... options) {
        if (stmt == null) {
            return;
        }

        SchemaResolveVisitor resolveVisitor = createResolveVisitor(options);
        stmt.accept(resolveVisitor);
    }

    private SchemaResolveVisitor createResolveVisitor(SchemaResolveVisitor.Option... options) {
        int optionsValue = SchemaResolveVisitor.Option.of(options);

        SchemaResolveVisitor resolveVisitor;
        if (JdbcConstants.MYSQL.equals(dbType)
                || JdbcConstants.SQLITE.equals(dbType)) {
            resolveVisitor = new SchemaResolveVisitorFactory.MySqlResolveVisitor(this, optionsValue);
        } else if (JdbcConstants.ORACLE.equals(dbType)) {
            resolveVisitor = new SchemaResolveVisitorFactory.OracleResolveVisitor(this, optionsValue);
        } else if (JdbcConstants.DB2.equals(dbType)) {
            resolveVisitor = new SchemaResolveVisitorFactory.DB2ResolveVisitor(this, optionsValue);
        } else if (JdbcConstants.ODPS.equals(dbType)) {
            resolveVisitor = new SchemaResolveVisitorFactory.OdpsResolveVisitor(this, optionsValue);
        } else if (JdbcConstants.HIVE.equals(dbType)) {
            resolveVisitor = new SchemaResolveVisitorFactory.HiveResolveVisitor(this, optionsValue);
        } else if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            resolveVisitor = new SchemaResolveVisitorFactory.PGResolveVisitor(this, optionsValue);
        } else if (JdbcConstants.SQL_SERVER.equals(dbType)) {
            resolveVisitor = new SchemaResolveVisitorFactory.SQLServerResolveVisitor(this, optionsValue);
        } else {
            resolveVisitor = new SchemaResolveVisitorFactory.SQLResolveVisitor(this, optionsValue);
        }
        return resolveVisitor;
    }

    public String resolve(String input) {
        SchemaResolveVisitor visitor
                = createResolveVisitor(
                    SchemaResolveVisitor.Option.ResolveAllColumn,
                    SchemaResolveVisitor.Option.ResolveIdentifierAlias);

        List<SQLStatement> stmtList = SQLUtils.parseStatements(input, dbType);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
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
                    SQLName db = showColumns.getDatabase();
                    Schema schema;
                    if (db == null) {
                        schema = getDefaultSchema();
                    } else {
                        schema = findSchema(db.getSimpleName());
                    }

                    SQLName table = null;
                    SchemaObject schemaObject = null;
                    if (schema != null) {
                        table = showColumns.getTable();
                        schemaObject = schema.findTable(table.nameHashCode64());
                    }

                    if (schemaObject == null) {
                        buf.append("ERROR 1146 (42S02): Table '" + table + "' doesn't exist\n");
                    } else {
                        MySqlCreateTableStatement createTableStmt = (MySqlCreateTableStatement) schemaObject.getStatement();
                        createTableStmt.showCoumns(buf);
                    }
                } else if (stmt instanceof MySqlShowCreateTableStatement) {
                    MySqlShowCreateTableStatement showCreateTableStmt = (MySqlShowCreateTableStatement) stmt;
                    SQLName table = showCreateTableStmt.getName();
                    SchemaObject schemaObject = findTable(table);
                    if (schemaObject == null) {
                        buf.append("ERROR 1146 (42S02): Table '" + table + "' doesn't exist\n");
                    } else {
                        MySqlCreateTableStatement createTableStmt = (MySqlCreateTableStatement) schemaObject.getStatement();
                        createTableStmt.output(buf);
                    }
                } else if (stmt instanceof MySqlRenameTableStatement) {
                    MySqlRenameTableStatement renameStmt = (MySqlRenameTableStatement) stmt;
                    for (MySqlRenameTableStatement.Item item : renameStmt.getItems()) {
                        renameTable(item.getName(), item.getTo());
                    }
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
            long tableHashCode64 = propertyExpr.nameHashCode64();

            Schema schemaObj = findSchema(schema);
            if (schemaObj == null) {
                return null;
            }

            return schemaObj.findTable(tableHashCode64);
        }

        return null;
    }

    private boolean renameTable(SQLName name, SQLName to) {
        Schema schema;
        if (name instanceof SQLPropertyExpr) {
            String schemaName = ((SQLPropertyExpr) name).getOwnernName();
            schema = findSchema(schemaName);
        } else {
            schema = getDefaultSchema();
        }

        if (schema == null) {
            return false;
        }

        long nameHashCode64 = name.nameHashCode64();
        SchemaObject schemaObject = schema.findTable(nameHashCode64);
        if (schemaObject != null) {
            MySqlCreateTableStatement createTableStmt = (MySqlCreateTableStatement) schemaObject.getStatement();
            if (createTableStmt != null) {
                createTableStmt.setName(to.clone());
            }

            schema.objects.put(to.hashCode64(), schemaObject);
            schema.objects.remove(nameHashCode64);
        }
        return true;
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

        public boolean visit(SQLAlterViewStatement x) {
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

        public boolean visit(SQLDropIndexStatement x) {
            acceptDropIndex(x);
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

        public boolean visit(SQLAlterViewStatement x) {
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

        public boolean visit(SQLDropIndexStatement x) {
            acceptDropIndex(x);
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

        public boolean visit(SQLAlterViewStatement x) {
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

        public boolean visit(SQLDropIndexStatement x) {
            acceptDropIndex(x);
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
        SQLCreateTableStatement x1 = x.clone();
        String schemaName = x1.getSchema();

        Schema schema = findSchema(schemaName, true);

        SQLSelect select = x1.getSelect();
        if (select != null) {
            select.accept(createResolveVisitor(SchemaResolveVisitor.Option.ResolveAllColumn));

            SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
            if (queryBlock != null) {
                List<SQLSelectItem> selectList = queryBlock.getSelectList();
                for (SQLSelectItem selectItem : selectList) {
                    SQLExpr selectItemExpr = selectItem.getExpr();
                    if (selectItemExpr instanceof SQLAllColumnExpr
                            || (selectItemExpr instanceof SQLPropertyExpr && ((SQLPropertyExpr) selectItemExpr).getName().equals("*"))) {
                        continue;
                    }

                    String name = selectItem.computeAlias();
                    SQLDataType dataType = selectItem.computeDataType();
                    SQLColumnDefinition column = new SQLColumnDefinition();
                    column.setName(name);
                    column.setDataType(dataType);
                    column.setDbType(dbType);
                    x1.getTableElementList().add(column);
                }
                if (x1.getTableElementList().size() > 0) {
                    x1.setSelect(null);
                }
            }
        }

        SQLExprTableSource like = x1.getLike();
        if (like != null) {
            SchemaObject tableObject = null;

            SQLName name = like.getName();
            if (name != null) {
                tableObject = findTable(name);
            }

            SQLCreateTableStatement tableStmt = null;
            if (tableObject != null) {
                SQLStatement stmt = tableObject.getStatement();
                if (stmt instanceof SQLCreateTableStatement) {
                    tableStmt = (SQLCreateTableStatement) stmt;
                }
            }

            if (tableStmt != null) {
                SQLName tableName = x1.getName();
                tableStmt.cloneTo(x1);
                x1.setName(tableName);
                x1.setLike((SQLExprTableSource) null);
            }
        }

        x1.setSchema(null);

        String name = x1.computeName();
        SchemaObject table = schema.findTableOrView(name);
        if (table != null) {
            LOG.info("replaced table '" + name + "'");
        }

        table = new SchemaObjectImpl(name, SchemaObjectType.Table, x1);
        schema.objects.put(table.nameHashCode64(), table);
        return true;
    }

    boolean acceptDropTable(SQLDropTableStatement x) {
        for (SQLExprTableSource table : x.getTableSources()) {
            String schemaName = table.getSchema();
            Schema schema = findSchema(schemaName, false);
            if (schema == null) {
                continue;
            }
            long nameHashCode64 = table.getName().nameHashCode64();
            schema.objects.remove(nameHashCode64);
        }
        return true;
    }

    boolean acceptView(SQLCreateViewStatement x) {
        String schemaName = x.getSchema();

        Schema schema = findSchema(schemaName, true);

        String name = x.computeName();
        SchemaObject view = schema.findTableOrView(name);
        if (view != null) {
            return false;
        }

        SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.View, x.clone());
        schema.objects.put(object.nameHashCode64(), object);
        return true;
    }

    boolean acceptView(SQLAlterViewStatement x) {
        String schemaName = x.getSchema();

        Schema schema = findSchema(schemaName, true);

        String name = x.computeName();
        SchemaObject view = schema.findTableOrView(name);
        if (view != null) {
            return false;
        }

        SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.View, x.clone());
        schema.objects.put(object.nameHashCode64(), object);
        return true;
    }

    boolean acceptDropIndex(SQLDropIndexStatement x) {
        SQLName table = x.getTableName().getName();
        SchemaObject object = findTable(table);

        if (object != null) {
            SQLCreateTableStatement stmt = (SQLCreateTableStatement) object.getStatement();
            if (stmt != null) {
                stmt.apply(x);
                return true;
            }
        }

        return false;
    }

    boolean acceptCreateIndex(SQLCreateIndexStatement x) {
        String schemaName = x.getSchema();

        Schema schema = findSchema(schemaName, true);

        String name = x.getName().getSimpleName();
        SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.Index, x.clone());
        schema.objects.put(object.nameHashCode64(), object);

        return true;
    }

    boolean acceptCreateFunction(SQLCreateFunctionStatement x) {
        String schemaName = x.getSchema();
        Schema schema = findSchema(schemaName, true);

        String name = x.getName().getSimpleName();
        SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.Function, x.clone());
        schema.functions.put(object.nameHashCode64(), object);

        return true;
    }

    boolean acceptAlterTable(SQLAlterTableStatement x) {
        String schemaName = x.getSchema();
        Schema schema = findSchema(schemaName, true);

        SchemaObject object = schema.findTable(x.nameHashCode64());
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
        schema.objects.put(object.nameHashCode64(), object);
        return false;
    }

    public boolean acceptDropSequence(SQLDropSequenceStatement x) {
        String schemaName = x.getSchema();
        Schema schema = findSchema(schemaName, true);

        long nameHashCode64 = x.getName().nameHashCode64();
        schema.objects.remove(nameHashCode64);
        return false;
    }
}
