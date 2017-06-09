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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by wenshao on 03/06/2017.
 */
public class SchemaRepository {
    private final Map<String, SchemaObject> objects = new ConcurrentSkipListMap<String, SchemaObject>();

    private final Map<String, SchemaObject> functions  = new ConcurrentSkipListMap<String, SchemaObject>();

    private final SchemaVisitor visitor = new SchemaVisitor();

    public SchemaObject findTable(String tableName) {
        String lowerName = tableName.toLowerCase();
        SchemaObject object = objects.get(lowerName);

        if (object != null && object.getType() == SchemaObjectType.Table) {
            return object;
        }

        return null;
    }

    public SchemaObject findFunction(String functionName) {
        String lowerName = functionName.toLowerCase();
        return functions.get(lowerName);
    }

    public void acceptDDL(String ddl, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(ddl, dbType);
        for (SQLStatement stmt : stmtList) {
            accept(stmt);
        }
    }

    public void accept(SQLStatement stmt) {
        stmt.accept(visitor);
    }

    public boolean isSequence(String name) {
        SchemaObject object = objects.get(name);
        return object != null
                && object.getType() == SchemaObjectType.Sequence;
    }

    public class SchemaVisitor extends OracleASTVisitorAdapter {

        public boolean visit(SQLDropSequenceStatement x) {
            String name = x.getName().getSimpleName();
            objects.remove(name);
            return false;
        }

        public boolean visit(SQLCreateSequenceStatement x) {
            String name = x.getName().getSimpleName();
            SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.Sequence);

            objects.put(name.toLowerCase(), object);
            return false;
        }

        public boolean visit(OracleCreateTableStatement x) {
            visit((SQLCreateTableStatement) x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            String name = x.computeName();
            SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.Table, x);

            objects.put(name.toLowerCase(), object);

            return false;
        }

        public boolean visit(SQLCreateIndexStatement x) {
            String name = x.getName().getSimpleName();
            SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.Index);

            objects.put(name.toLowerCase(), object);

            return false;
        }

        public boolean visit(SQLCreateFunctionStatement x) {
            String name = x.getName().getSimpleName();
            SchemaObject object = new SchemaObjectImpl(name, SchemaObjectType.Function, x);

            functions.put(name.toLowerCase(), object);

            return false;
        }
    }

    public SchemaObject findTable(SQLTableSource tableSource, String alias) {
        if (tableSource instanceof SQLExprTableSource) {
            if (alias.equalsIgnoreCase(tableSource.computeAlias())) {
                SQLExprTableSource exprTableSource = (SQLExprTableSource) tableSource;

                SchemaObject tableObject = exprTableSource.getSchemaObject();
                if (tableObject !=  null) {
                    return tableObject;
                }

                SQLExpr expr = exprTableSource.getExpr();
                if (expr instanceof SQLIdentifierExpr) {
                    String tableName = ((SQLIdentifierExpr) expr).getName();

                    tableObject = findTable(tableName);
                    if (tableObject != null) {
                        exprTableSource.setSchemaObject(tableObject);
                    }
                    return tableObject;
                }
            }
            return null;
        }

        if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) tableSource;
            SQLTableSource left = join.getLeft();

            SchemaObject tableObject = findTable(left, alias);
            if (tableObject != null) {
                return tableObject;
            }

            SQLTableSource right = join.getRight();
            tableObject = findTable(right, alias);
            return tableObject;
        }

        return null;
    }

    public SQLColumnDefinition findColumn(SQLTableSource tableSource, SQLSelectItem selectItem) {
        if (selectItem == null) {
            return null;
        }

        return findColumn(tableSource, selectItem.getExpr());
    }

    public SQLColumnDefinition findColumn(SQLTableSource tableSource, SQLExpr expr) {
        SchemaObject object = findTable(tableSource, expr);
        if (object != null) {
            if (expr instanceof SQLAggregateExpr) {
                SQLAggregateExpr aggregateExpr = (SQLAggregateExpr) expr;
                String function = aggregateExpr.getMethodName();
                if ("min".equalsIgnoreCase(function)
                        || "max".equalsIgnoreCase(function)) {
                    SQLExpr arg = aggregateExpr.getArguments().get(0);
                    expr = arg;
                }
            }

            if (expr instanceof SQLName) {
                return object.findColumn(((SQLName) expr).getSimpleName());
            }
        }

        return null;
    }

    public SchemaObject findTable(SQLTableSource tableSource, SQLSelectItem selectItem) {
        if (selectItem == null) {
            return null;
        }

        return findTable(tableSource, selectItem.getExpr());
    }

    public SchemaObject findTable(SQLTableSource tableSource, SQLExpr expr) {
        if (expr instanceof SQLAggregateExpr) {
            SQLAggregateExpr aggregateExpr = (SQLAggregateExpr) expr;
            String function = aggregateExpr.getMethodName();
            if ("min".equalsIgnoreCase(function)
                    || "max".equalsIgnoreCase(function)) {
                SQLExpr arg = aggregateExpr.getArguments().get(0);
                return findTable(tableSource, arg);
            }
        }

        if (expr instanceof SQLPropertyExpr) {
            String ownerName = ((SQLPropertyExpr) expr).getOwnernName();
            return findTable(tableSource, ownerName);
        }

        if (expr instanceof SQLAllColumnExpr || expr instanceof SQLIdentifierExpr) {
            if (tableSource instanceof SQLExprTableSource) {
                return findTable(tableSource, tableSource.computeAlias());
            }
            return null;
        }

        return null;
    }

    public Map<String, SchemaObject> getTables(SQLTableSource x) {
        Map<String, SchemaObject> tables = new LinkedHashMap<String, SchemaObject>();
        computeTables(x, tables);
        return tables;
    }

    private void computeTables(SQLTableSource x, Map<String, SchemaObject> tables) {
        if (x == null) {
            return;
        }

        if (x instanceof SQLExprTableSource) {
            SQLExprTableSource exprTableSource = (SQLExprTableSource) x;

            SQLExpr expr = exprTableSource.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) expr).getName();

                SchemaObject table = exprTableSource.getSchemaObject();
                if (table == null) {
                    table = findTable(tableName);

                    if (table != null) {
                        exprTableSource.setSchemaObject(table);
                    }
                }

                if (table != null) {
                    tables.put(tableName, table);

                    String alias = x.getAlias();
                    if (alias != null && !alias.equalsIgnoreCase(tableName)) {
                        tables.put(alias, table);
                    }
                }
            }

            return;
        }

        if (x instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) x;
            computeTables(join.getLeft(), tables);
            computeTables(join.getRight(), tables);
        }
    }

    public int getTableCount() {
        int count = 0;
        for (SchemaObject object : this.objects.values()) {
            if (object.getType() == SchemaObjectType.Table) {
                count++;
            }
        }
        return count;
    }

    public Map<String, SchemaObject> getObjects() {
        return this.objects;
    }

    public int getViewCount() {
        int count = 0;
        for (SchemaObject object : this.objects.values()) {
            if (object.getType() == SchemaObjectType.View) {
                count++;
            }
        }
        return count;
    }
}
