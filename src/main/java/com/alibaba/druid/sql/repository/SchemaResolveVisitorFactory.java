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
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.DB2Object;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.*;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.PGUtils;

import java.util.ArrayList;
import java.util.List;

class SchemaResolveVisitorFactory {
    static class MySqlResolveVisitor extends MySqlASTVisitorAdapter implements SchemaResolveVisitor {
        private SchemaRepository repository;
        private int options;
        private Context context;

        public MySqlResolveVisitor(SchemaRepository repository, int options) {
            this.repository = repository;
            this.options = options;
        }

        public boolean visit(MySqlRepeatStatement x) {
            return true;
        }

        public boolean visit(MySqlDeclareStatement x) {
            for (SQLDeclareItem declareItem : x.getVarList()) {
                visit(declareItem);
            }
            return false;
        }

        public boolean visit(MySqlCursorDeclareStatement x) {
            return true;
        }

        public boolean visit(MysqlForeignKey x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(MySqlSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolveIdent(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(MySqlCreateTableStatement x) {
            resolve(this, x);
            SQLExprTableSource like = x.getLike();
            if (like != null) {
                like.accept(this);
            }
            return false;
        }

        public boolean visit(MySqlUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(MySqlDeleteStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(MySqlInsertStatement x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
        }

        public int getOptions() {
            return options;
        }

        @Override
        public Context getContext() {
            return context;
        }

        public Context createContext(SQLObject object) {
            return this.context = new Context(object, context);
        }

        @Override
        public void popContext() {
            if (context != null) {
                context = context.parent;
            }
        }

        public SchemaRepository getRepository() {
            return repository;
        }
    }

    static class DB2ResolveVisitor extends DB2ASTVisitorAdapter implements SchemaResolveVisitor {
        private SchemaRepository repository;
        private int options;
        private Context context;

        public DB2ResolveVisitor(SchemaRepository repository, int options) {
            this.repository = repository;
            this.options = options;
        }

        public boolean visit(DB2SelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolveIdent(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            long hash64 = x.hashCode64();
            if (hash64 == FnvHash.Constants.CURRENT_DATE || hash64 == DB2Object.Constants.CURRENT_TIME) {
                return false;
            }

            resolveIdent(this, x);
            return true;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
        }

        public int getOptions() {
            return options;
        }

        @Override
        public Context getContext() {
            return context;
        }

        public Context createContext(SQLObject object) {
            return this.context = new Context(object, context);
        }

        @Override
        public void popContext() {
            if (context != null) {
                context = context.parent;
            }
        }

        @Override
        public SchemaRepository getRepository() {
            return repository;
        }
    }

    static class OracleResolveVisitor extends OracleASTVisitorAdapter implements SchemaResolveVisitor {
        private SchemaRepository repository;
        private int options;
        private Context context;

        public OracleResolveVisitor(SchemaRepository repository, int options) {
            this.repository = repository;
            this.options = options;
        }

        public boolean visit(OracleCreatePackageStatement x) {
            Context ctx = createContext(x);

            for (SQLStatement stmt : x.getStatements()) {
                stmt.accept(this);
            }

            popContext();
            return false;
        }

        public boolean visit(OracleForStatement x) {
            Context ctx = createContext(x);

            SQLName index = x.getIndex();
            SQLExpr range = x.getRange();

            if (index != null) {
                SQLDeclareItem declareItem = new SQLDeclareItem(index, null);
                declareItem.setParent(x);

                if (index instanceof SQLIdentifierExpr) {
                    ((SQLIdentifierExpr) index).setResolvedDeclareItem(declareItem);
                }
                declareItem.setResolvedObject(range);
                ctx.declare(declareItem);
                if (range instanceof SQLQueryExpr) {
                    SQLSelect select = ((SQLQueryExpr) range).getSubQuery();
                    SQLSubqueryTableSource tableSource = new SQLSubqueryTableSource(select);
                    declareItem.setResolvedObject(tableSource);
                }

                index.accept(this);
            }


            if (range != null) {
                range.accept(this);
            }

            for (SQLStatement stmt : x.getStatements()) {
                stmt.accept(this);
            }

            popContext();
            return false;
        }

        public boolean visit(OracleForeignKey x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OracleSelectTableReference x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OracleSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolveIdent(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            if (x.nameHashCode64() == FnvHash.Constants.ROWNUM) {
                return false;
            }

            resolveIdent(this, x);
            return true;
        }

        public boolean visit(OracleCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OracleUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OracleDeleteStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OracleMultiInsertStatement x) {
            Context ctx = createContext(x);

            SQLSelect select = x.getSubQuery();
            visit(select);

            OracleSelectSubqueryTableSource tableSource = new OracleSelectSubqueryTableSource(select);
            tableSource.setParent(x);
            ctx.setTableSource(tableSource);

            for (OracleMultiInsertStatement.Entry entry : x.getEntries()) {
                entry.accept(this);
            }

            popContext();
            return false;
        }

        public boolean visit(OracleMultiInsertStatement.InsertIntoClause x) {
            for (SQLExpr column : x.getColumns()) {
                if (column instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr identColumn = (SQLIdentifierExpr) column;
                    identColumn.setResolvedTableSource(x.getTableSource());
                }
            }
            return true;
        }

        public boolean visit(OracleInsertStatement x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
        }

        public int getOptions() {
            return options;
        }

        @Override
        public Context getContext() {
            return context;
        }

        public Context createContext(SQLObject object) {
            return this.context = new Context(object, context);
        }

        @Override
        public void popContext() {
            if (context != null) {
                context = context.parent;
            }
        }

        public SchemaRepository getRepository() {
            return repository;
        }
    }

    static class OdpsResolveVisitor extends OdpsASTVisitorAdapter implements SchemaResolveVisitor {
        private int options;
        private SchemaRepository repository;
        private Context context;

        public OdpsResolveVisitor(SchemaRepository repository, int options) {
            this.repository = repository;
            this.options = options;
        }

        public boolean visit(OdpsSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolveIdent(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(OdpsCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(HiveInsert x) {
            Context ctx = createContext(x);

            SQLExprTableSource tableSource = x.getTableSource();
            if (tableSource != null) {
                ctx.setTableSource(x.getTableSource());
                visit(tableSource);
            }

            List<SQLAssignItem> partitions = x.getPartitions();
            if (partitions != null) {
                for (SQLAssignItem item : partitions) {
                    item.accept(this);
                }
            }

            SQLSelect select = x.getQuery();
            if (select != null) {
                visit(select);
            }

            popContext();
            return false;
        }

        public boolean visit(HiveInsertStatement x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
        }

        public int getOptions() {
            return options;
        }

        @Override
        public Context getContext() {
            return context;
        }

        public Context createContext(SQLObject object) {
            return this.context = new Context(object, context);
        }

        @Override
        public void popContext() {
            if (context != null) {
                context = context.parent;
            }
        }

        public SchemaRepository getRepository() {
            return repository;
        }
    }

    static class HiveResolveVisitor extends HiveASTVisitorAdapter implements SchemaResolveVisitor {
        private int options;
        private SchemaRepository repository;
        private Context context;

        public HiveResolveVisitor(SchemaRepository repository, int options) {
            this.repository = repository;
            this.options = options;
        }

        public boolean visit(OdpsSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolveIdent(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(HiveCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(HiveInsert x) {
            Context ctx = createContext(x);

            SQLExprTableSource tableSource = x.getTableSource();
            if (tableSource != null) {
                ctx.setTableSource(x.getTableSource());
                visit(tableSource);
            }

            List<SQLAssignItem> partitions = x.getPartitions();
            if (partitions != null) {
                for (SQLAssignItem item : partitions) {
                    item.accept(this);
                }
            }

            SQLSelect select = x.getQuery();
            if (select != null) {
                visit(select);
            }

            popContext();
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
        }

        public int getOptions() {
            return options;
        }

        @Override
        public Context getContext() {
            return context;
        }

        public Context createContext(SQLObject object) {
            return this.context = new Context(object, context);
        }

        @Override
        public void popContext() {
            if (context != null) {
                context = context.parent;
            }
        }

        public SchemaRepository getRepository() {
            return repository;
        }
    }

    static class PGResolveVisitor extends PGASTVisitorAdapter implements SchemaResolveVisitor {
        private int options;
        private SchemaRepository repository;
        private Context context;

        public PGResolveVisitor(SchemaRepository repository, int options) {
            this.repository = repository;
            this.options = options;
        }

        public boolean visit(PGSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(PGFunctionTableSource x) {
            for (SQLParameter parameter : x.getParameters()) {
                SQLName name = parameter.getName();
                if (name instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr identName = (SQLIdentifierExpr) name;
                    identName.setResolvedTableSource(x);
                }
            }

            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolveIdent(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            if (PGUtils.isPseudoColumn(x.nameHashCode64())) {
                return false;
            }

            resolveIdent(this, x);
            return true;
        }

        public boolean visit(PGUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(PGDeleteStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(PGSelectStatement x) {
            createContext(x);
            visit(x.getSelect());
            popContext();
            return false;
        }

        public boolean visit(PGInsertStatement x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
        }

        public int getOptions() {
            return options;
        }

        @Override
        public Context getContext() {
            return context;
        }

        public Context createContext(SQLObject object) {
            return this.context = new Context(object, context);
        }

        @Override
        public void popContext() {
            if (context != null) {
                context = context.parent;
            }
        }

        public SchemaRepository getRepository() {
            return repository;
        }
    }

    static class SQLServerResolveVisitor extends SQLServerASTVisitorAdapter implements SchemaResolveVisitor {
        private int options;
        private SchemaRepository repository;
        private Context context;

        public SQLServerResolveVisitor(SchemaRepository repository, int options) {
            this.repository = repository;
            this.options = options;
        }

        public boolean visit(SQLServerSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolveIdent(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLServerUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLServerInsertStatement x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
        }

        public int getOptions() {
            return options;
        }

        @Override
        public Context getContext() {
            return context;
        }

        public Context createContext(SQLObject object) {
            return this.context = new Context(object, context);
        }

        @Override
        public void popContext() {
            if (context != null) {
                context = context.parent;
            }
        }

        public SchemaRepository getRepository() {
            return repository;
        }
    }

    static class SQLResolveVisitor extends SQLASTVisitorAdapter implements SchemaResolveVisitor {
        private int options;
        private SchemaRepository repository;
        private Context context;

        public SQLResolveVisitor(SchemaRepository repository, int options) {
            this.repository = repository;
            this.options = options;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolveIdent(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
        }

        public int getOptions() {
            return options;
        }

        @Override
        public Context getContext() {
            return context;
        }

        public Context createContext(SQLObject object) {
            return this.context = new Context(object, context);
        }

        @Override
        public void popContext() {
            if (context != null) {
                context = context.parent;
            }
        }

        public SchemaRepository getRepository() {
            return repository;
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLCreateTableStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLExprTableSource table = x.getTableSource();
        ctx.setTableSource(table);

        table.accept(visitor);

        List<SQLTableElement> elements = x.getTableElementList();
        for (int i = 0; i < elements.size(); i++) {
            SQLTableElement e = elements.get(i);
            if (e instanceof SQLColumnDefinition) {
                SQLColumnDefinition columnn = (SQLColumnDefinition) e;
                SQLName columnnName = columnn.getName();
                if (columnnName instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) columnnName;
                    identifierExpr.setResolvedTableSource(table);
                    identifierExpr.setResolvedColumn(columnn);
                }
            } else if (e instanceof SQLUniqueConstraint) {
                List<SQLSelectOrderByItem> columns = ((SQLUniqueConstraint) e).getColumns();
                for (SQLSelectOrderByItem orderByItem : columns) {
                    SQLExpr orderByItemExpr = orderByItem.getExpr();
                    if (orderByItemExpr instanceof SQLIdentifierExpr) {
                        SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) orderByItemExpr;
                        identifierExpr.setResolvedTableSource(table);

                        SQLColumnDefinition column = x.findColumn(identifierExpr.nameHashCode64());
                        if (column != null) {
                            identifierExpr.setResolvedColumn(column);
                        }
                    }
                }
            } else {
                e.accept(visitor);
            }
        }

        SQLSelect select = x.getSelect();
        if (select != null) {
            visitor.visit(select);
        }

        SchemaRepository repository = visitor.getRepository();
        if (repository != null) {
            repository.acceptCreateTable(x);
        }

        visitor.popContext();

        SQLExprTableSource like = x.getLike();
        if (like != null) {
            like.accept(visitor);
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLUpdateStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            with.accept(visitor);
        }

        SQLTableSource table = x.getTableSource();
        SQLTableSource from = x.getFrom();

        ctx.setTableSource(table);
        ctx.setFrom(from);

        table.accept(visitor);
        if (from != null) {
            from.accept(visitor);
        }

        List<SQLUpdateSetItem> items = x.getItems();
        for (SQLUpdateSetItem item : items) {
            SQLExpr column = item.getColumn();
            if (column instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) column;
                identifierExpr.setResolvedTableSource(table);
                visitor.visit(identifierExpr);
            } else if (column instanceof SQLListExpr) {
                SQLListExpr columnGroup = (SQLListExpr) column;
                for (SQLExpr columnGroupItem : columnGroup.getItems()) {
                    if (columnGroupItem instanceof SQLIdentifierExpr) {
                        SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) columnGroupItem;
                        identifierExpr.setResolvedTableSource(table);
                        visitor.visit(identifierExpr);
                    } else {
                        columnGroupItem.accept(visitor);
                    }
                }
            } else {
                column.accept(visitor);
            }
            SQLExpr value = item.getValue();
            if (value != null) {
                value.accept(visitor);
            }
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            where.accept(visitor);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            orderBy.accept(visitor);
        }

        for (SQLExpr sqlExpr : x.getReturning()) {
            sqlExpr.accept(visitor);
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLDeleteStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            visitor.visit(with);
        }

        SQLTableSource table = x.getTableSource();
        SQLTableSource from = x.getFrom();

        if (from == null) {
            from = x.getUsing();
        }

        if (table == null && from != null) {
            table = from;
            from = null;
        }

        if (from != null) {
            ctx.setFrom(from);
            from.accept(visitor);
        }

        if (table != null) {
            if (from != null && table instanceof SQLExprTableSource) {
                SQLExpr tableExpr = ((SQLExprTableSource) table).getExpr();
                if (tableExpr instanceof SQLPropertyExpr
                        && ((SQLPropertyExpr) tableExpr).getName().equals("*")) {
                    String alias = ((SQLPropertyExpr) tableExpr).getOwnernName();
                    SQLTableSource refTableSource = from.findTableSource(alias);
                    if (refTableSource != null) {
                        ((SQLPropertyExpr) tableExpr).setResolvedTableSource(refTableSource);
                    }
                }
            }
            table.accept(visitor);
            ctx.setTableSource(table);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            where.accept(visitor);
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLInsertStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            visitor.visit(with);
        }

        SQLTableSource table = x.getTableSource();

        ctx.setTableSource(table);

        if (table != null) {
            table.accept(visitor);
        }

        for (SQLExpr column : x.getColumns()) {
            column.accept(visitor);
        }

        if (x instanceof HiveInsertStatement) {
            for (SQLAssignItem item : ((HiveInsertStatement) x).getPartitions()) {
                item.accept(visitor);
            }
        }

        for (SQLInsertStatement.ValuesClause valuesClause : x.getValuesList()) {
            valuesClause.accept(visitor);
        }

        SQLSelect query = x.getQuery();
        if (query != null) {
            visitor.visit(query);
        }

        visitor.popContext();
    }

    static void resolveIdent(SchemaResolveVisitor visitor, SQLIdentifierExpr x) {
        SchemaResolveVisitor.Context ctx = visitor.getContext();
        if (ctx == null) {
            return;
        }

        String ident = x.getName();
        long hash = x.nameHashCode64();
        SQLTableSource tableSource = null;

        if ((hash == FnvHash.Constants.LEVEL || hash == FnvHash.Constants.CONNECT_BY_ISCYCLE)
                && ctx.object instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) ctx.object;
            if (queryBlock.getStartWith() != null
                    || queryBlock.getConnectBy() != null) {
                return;
            }
        }

        SQLTableSource ctxTable = ctx.getTableSource();

        if (ctxTable instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) ctxTable;
            tableSource = join.findTableSourceWithColumn(hash, ident, visitor.getOptions());
            if (tableSource == null) {
                final SQLTableSource left = join.getLeft(), right = join.getRight();

                if (left instanceof SQLSubqueryTableSource
                        && right instanceof SQLExprTableSource) {
                    SQLSelect leftSelect = ((SQLSubqueryTableSource) left).getSelect();
                    if (leftSelect.getQuery() instanceof SQLSelectQueryBlock) {
                        boolean hasAllColumn = ((SQLSelectQueryBlock) leftSelect.getQuery()).selectItemHasAllColumn();
                        if (!hasAllColumn) {
                            tableSource = right;
                        }
                    }
                } else if (right instanceof SQLSubqueryTableSource
                        && left instanceof SQLExprTableSource) {
                    SQLSelect rightSelect = ((SQLSubqueryTableSource) right).getSelect();
                    if (rightSelect.getQuery() instanceof SQLSelectQueryBlock) {
                        boolean hasAllColumn = ((SQLSelectQueryBlock) rightSelect.getQuery()).selectItemHasAllColumn();
                        if (!hasAllColumn) {
                            tableSource = left;
                        }
                    }
                } else if (left instanceof SQLExprTableSource && right instanceof SQLExprTableSource) {
                    SQLExprTableSource leftExprTableSource = (SQLExprTableSource) left;
                    SQLExprTableSource rightExprTableSource = (SQLExprTableSource) right;

                    if (leftExprTableSource.getSchemaObject() != null
                            && rightExprTableSource.getSchemaObject() == null) {
                        tableSource = rightExprTableSource;

                    } else if (rightExprTableSource.getSchemaObject() != null
                            && leftExprTableSource.getSchemaObject() == null) {
                        tableSource = leftExprTableSource;
                    }
                }
            }
        } else if (ctxTable instanceof SQLSubqueryTableSource) {
            tableSource = ctxTable.findTableSourceWithColumn(hash, ident, visitor.getOptions());
        } else if (ctxTable instanceof SQLLateralViewTableSource) {
            tableSource = ctxTable.findTableSourceWithColumn(hash, ident, visitor.getOptions());

            if (tableSource == null) {
                tableSource = ((SQLLateralViewTableSource) ctxTable).getTableSource();
            }
        } else {
            for (SchemaResolveVisitor.Context parentCtx = ctx;
                 parentCtx != null;
                 parentCtx = parentCtx.parent)
            {
                SQLDeclareItem declareItem = parentCtx.findDeclare(hash);
                if (declareItem != null) {
                    x.setResolvedDeclareItem(declareItem);
                    return;
                }

                if (parentCtx.object instanceof SQLBlockStatement) {
                    SQLBlockStatement block = (SQLBlockStatement) parentCtx.object;
                    SQLParameter parameter = block.findParameter(hash);
                    if (parameter != null) {
                        x.setResolvedParameter(parameter);
                        return;
                    }
                } else if (parentCtx.object instanceof SQLCreateProcedureStatement) {
                    SQLCreateProcedureStatement createProc = (SQLCreateProcedureStatement) parentCtx.object;
                    SQLParameter parameter = createProc.findParameter(hash);
                    if (parameter != null) {
                        x.setResolvedParameter(parameter);
                        return;
                    }
                }
            }

            tableSource = ctxTable;
            if (tableSource instanceof SQLExprTableSource) {
                SchemaObject table = ((SQLExprTableSource) tableSource).getSchemaObject();
                if (table != null) {
                    if (table.findColumn(hash) == null) {
                        SQLCreateTableStatement createStmt = null;
                        {
                            SQLStatement smt = table.getStatement();
                            if (smt instanceof SQLCreateTableStatement) {
                                createStmt = (SQLCreateTableStatement) smt;
                            }
                        }

                        if (createStmt != null && createStmt.getTableElementList().size() > 0) {
                            tableSource = null; // maybe parent
                        }
                    }
                }
            }
        }

        if (tableSource instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();

            if (expr instanceof SQLMethodInvokeExpr) {
                SQLMethodInvokeExpr func = (SQLMethodInvokeExpr) expr;
                if (func.methodNameHashCode64() == FnvHash.Constants.ANN) {
                    expr = func.getArguments().get(0);
                }
            }

            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                long identHash = identExpr.nameHashCode64();

                tableSource = unwrapAlias(ctx, tableSource, identHash);
            }
        }

        if (tableSource != null) {
            x.setResolvedTableSource(tableSource);

            SQLColumnDefinition column = tableSource.findColumn(hash);
            if (column != null) {
                x.setResolvedColumn(column);
            }

            if (ctxTable instanceof SQLJoinTableSource ) {
                String alias = tableSource.computeAlias();
                if (alias == null || tableSource instanceof SQLWithSubqueryClause.Entry) {
                    return;
                }

                if (visitor.isEnabled(SchemaResolveVisitor.Option.ResolveIdentifierAlias)) {
                    SQLPropertyExpr propertyExpr = new SQLPropertyExpr(new SQLIdentifierExpr(alias), ident, hash);
                    propertyExpr.setResolvedColumn(x.getResolvedColumn());
                    propertyExpr.setResolvedTableSource(x.getResolvedTableSource());
                    SQLUtils.replaceInParent(x, propertyExpr);
                }
            }
        }

        if (x.getResolvedColumn() == null
                && x.getResolvedTableSource() == null) {
            for (SchemaResolveVisitor.Context parentCtx = ctx;
                 parentCtx != null;
                 parentCtx = parentCtx.parent)
            {
                SQLDeclareItem declareItem = parentCtx.findDeclare(hash);
                if (declareItem != null) {
                    x.setResolvedDeclareItem(declareItem);
                    return;
                }

                if (parentCtx.object instanceof SQLBlockStatement) {
                    SQLBlockStatement block = (SQLBlockStatement) parentCtx.object;
                    SQLParameter parameter = block.findParameter(hash);
                    if (parameter != null) {
                        x.setResolvedParameter(parameter);
                        return;
                    }
                } else if (parentCtx.object instanceof SQLCreateProcedureStatement) {
                    SQLCreateProcedureStatement createProc = (SQLCreateProcedureStatement) parentCtx.object;
                    SQLParameter parameter = createProc.findParameter(hash);
                    if (parameter != null) {
                        x.setResolvedParameter(parameter);
                        return;
                    }
                }
            }
        }

        if (x.getResolvedColumnObject() == null && ctx.object instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) ctx.object;
            boolean having = false;
            for (SQLObject current = x, parent = x.getParent(); parent != null;current = parent, parent = parent.getParent()) {
                if (parent instanceof SQLSelectGroupByClause && parent.getParent() == queryBlock) {
                    SQLSelectGroupByClause groupBy = (SQLSelectGroupByClause) parent;
                    if (current == groupBy.getHaving()) {
                        having = true;
                    }
                    break;
                }
            }
            if (having) {
                SQLSelectItem selectItem = queryBlock.findSelectItem(x.hashCode64());
                if (selectItem != null) {
                    x.setResolvedColumn(selectItem);
                }
            }
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLPropertyExpr x) {
        SchemaResolveVisitor.Context ctx = visitor.getContext();
        if (ctx == null) {
            return;
        }

        long owner_hash = 0;
        {
            SQLExpr ownerObj = x.getOwner();
            if (ownerObj instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr owner = (SQLIdentifierExpr) ownerObj;
                owner_hash = owner.nameHashCode64();
            } else if (ownerObj instanceof SQLPropertyExpr) {
                owner_hash = ((SQLPropertyExpr) ownerObj).hashCode64();
            }
        }

        SQLTableSource tableSource = null;
        SQLTableSource ctxTable = ctx.getTableSource();

        if (ctxTable != null) {
            tableSource = ctxTable.findTableSource(owner_hash);
        }

        if (tableSource == null) {
            SQLTableSource ctxFrom = ctx.getFrom();
            if (ctxFrom != null) {
                tableSource = ctxFrom.findTableSource(owner_hash);
            }
        }

        if (tableSource == null) {
            for (SchemaResolveVisitor.Context parentCtx = ctx;
                 parentCtx != null;
                 parentCtx = parentCtx.parent) {

                SQLTableSource parentCtxTable = parentCtx.getTableSource();

                if (parentCtxTable != null) {
                    tableSource = parentCtxTable.findTableSource(owner_hash);
                    if (tableSource == null) {
                        SQLTableSource ctxFrom = parentCtx.getFrom();
                        if (ctxFrom != null) {
                            tableSource = ctxFrom.findTableSource(owner_hash);
                        }
                    }

                    if (tableSource != null) {
                        break;
                    }
                } else {
                    if (parentCtx.object instanceof SQLBlockStatement) {
                        SQLBlockStatement block = (SQLBlockStatement) parentCtx.object;
                        SQLParameter parameter = block.findParameter(owner_hash);
                        if (parameter != null) {
                            x.setResolvedOwnerObject(parameter);
                            return;
                        }
                    } else if (parentCtx.object instanceof SQLMergeStatement) {
                        SQLMergeStatement mergeStatement = (SQLMergeStatement) parentCtx.object;
                        SQLTableSource into = mergeStatement.getInto();
                        if (into instanceof SQLSubqueryTableSource
                                && into.aliasHashCode64() == owner_hash) {
                            x.setResolvedOwnerObject(into);
                        }
                    }

                    SQLDeclareItem declareItem = parentCtx.findDeclare(owner_hash);
                    if (declareItem != null) {
                        SQLObject resolvedObject = declareItem.getResolvedObject();
                        if (resolvedObject instanceof SQLCreateProcedureStatement
                                || resolvedObject instanceof SQLCreateFunctionStatement
                                || resolvedObject instanceof SQLTableSource) {
                            x.setResolvedOwnerObject(resolvedObject);
                        }
                        break;
                    }
                }
            }
        }

        if (tableSource != null) {
            x.setResolvedTableSource(tableSource);
            SQLObject column = tableSource.resolveColum(
                    x.nameHashCode64());
            if (column instanceof SQLColumnDefinition) {
                x.setResolvedColumn((SQLColumnDefinition) column);
            } else if (column instanceof SQLSelectItem) {
                x.setResolvedColumn((SQLSelectItem) column);
            }
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLBinaryOpExpr x) {
        final SQLBinaryOperator op = x.getOperator();
        final SQLExpr left = x.getLeft();

        if ((op == SQLBinaryOperator.BooleanAnd || op == SQLBinaryOperator.BooleanOr)
                && left instanceof SQLBinaryOpExpr
                && ((SQLBinaryOpExpr) left).getOperator() == op) {
            List<SQLExpr> groupList = SQLBinaryOpExpr.split(x, op);
            for (int i = 0; i < groupList.size(); i++) {
                SQLExpr item = groupList.get(i);
                item.accept(visitor);
            }
            return;
        }

        if (left != null) {
            if (left instanceof SQLBinaryOpExpr) {
                resolve(visitor, (SQLBinaryOpExpr) left);
            } else {
                left.accept(visitor);
            }
        }

        SQLExpr right = x.getRight();
        if (right != null) {
            right.accept(visitor);
        }
    }

    static SQLTableSource unwrapAlias(SchemaResolveVisitor.Context ctx, SQLTableSource tableSource, long identHash) {
        if (ctx == null) {
            return tableSource;
        }

        if (ctx.object instanceof SQLDeleteStatement
                && (ctx.getTableSource() == null || tableSource == ctx.getTableSource())
                && ctx.getFrom() != null) {
            SQLTableSource found = ctx.getFrom().findTableSource(identHash);
            if (found != null) {
                return found;
            }
        }

        for (SchemaResolveVisitor.Context parentCtx = ctx;
             parentCtx != null;
             parentCtx = parentCtx.parent) {

            SQLWithSubqueryClause with = null;
            if (parentCtx.object instanceof SQLSelect) {
                SQLSelect select = (SQLSelect) parentCtx.object;
                with = select.getWithSubQuery();
            } else if (parentCtx.object instanceof SQLDeleteStatement) {
                SQLDeleteStatement delete = (SQLDeleteStatement) parentCtx.object;
                with = delete.getWith();
            } else if (parentCtx.object instanceof SQLInsertStatement) {
                SQLInsertStatement insertStmt = (SQLInsertStatement) parentCtx.object;
                with = insertStmt.getWith();
            } else if (parentCtx.object instanceof SQLUpdateStatement) {
                SQLUpdateStatement updateStmt = (SQLUpdateStatement) parentCtx.object;
                with = updateStmt.getWith();
            }

            if (with != null) {
                SQLWithSubqueryClause.Entry entry = with.findEntry(identHash);
                if (entry != null) {
                    return entry;
                }
            }
        }
        return tableSource;
    }

    static void resolve(SchemaResolveVisitor visitor, SQLSelectQueryBlock x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);
        if (ctx != null && ctx.level >= 32) {
            return;
        }

        SQLTableSource from = x.getFrom();

        if (from != null) {
            ctx.setTableSource(from);

            Class fromClass = from.getClass();
            if (fromClass == SQLExprTableSource.class) {
                visitor.visit((SQLExprTableSource) from);
            } else {
                from.accept(visitor);
            }
        } else if (x.getParent() != null && x.getParent().getParent() instanceof HiveInsert
                && x.getParent().getParent().getParent() instanceof HiveMultiInsertStatement){
            HiveMultiInsertStatement insert = (HiveMultiInsertStatement) x.getParent().getParent().getParent();
            if (insert.getFrom() instanceof SQLExprTableSource) {
                from = insert.getFrom();
                ctx.setTableSource(from);
            }
        }

        List<SQLSelectItem> selectList = x.getSelectList();

        List<SQLSelectItem> columns = new ArrayList<SQLSelectItem>();
        for (int i = selectList.size() - 1; i >= 0; i--) {
            SQLSelectItem selectItem = selectList.get(i);
            SQLExpr expr = selectItem.getExpr();
            if (expr instanceof SQLAllColumnExpr) {
                SQLAllColumnExpr allColumnExpr = (SQLAllColumnExpr) expr;
                allColumnExpr.setResolvedTableSource(from);

                visitor.visit(allColumnExpr);

                if (visitor.isEnabled(SchemaResolveVisitor.Option.ResolveAllColumn)) {
                    extractColumns(visitor, from, null, columns);
                }
            } else if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                visitor.visit(propertyExpr);

                String ownerName = propertyExpr.getOwnernName();
                if (propertyExpr.getName().equals("*")) {
                    if (visitor.isEnabled(SchemaResolveVisitor.Option.ResolveAllColumn)) {
                        SQLTableSource tableSource = x.findTableSource(ownerName);
                        extractColumns(visitor, tableSource, ownerName, columns);
                    }
                }

                SQLColumnDefinition column = propertyExpr.getResolvedColumn();
                if (column != null) {
                    continue;
                }
                SQLTableSource tableSource = x.findTableSource(propertyExpr.getOwnernName());
                if (tableSource != null) {
                    column = tableSource.findColumn(propertyExpr.nameHashCode64());
                    if (column != null) {
                        propertyExpr.setResolvedColumn(column);
                    }
                }
            } else if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                visitor.visit(identExpr);

                long name_hash = identExpr.nameHashCode64();

                SQLColumnDefinition column = identExpr.getResolvedColumn();
                if (column != null) {
                    continue;
                }
                if (from == null) {
                    continue;
                }
                column = from.findColumn(name_hash);
                if (column != null) {
                    identExpr.setResolvedColumn(column);
                }
            } else {
                expr.accept(visitor);
            }

            if (columns.size() > 0) {
                for (SQLSelectItem column : columns) {
                    column.setParent(x);
                    column.getExpr().accept(visitor);
                }

                selectList.remove(i);
                selectList.addAll(i, columns);
                columns.clear();
            }
        }

        SQLExprTableSource into = x.getInto();
        if (into != null) {
            visitor.visit(into);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            if (where instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) where;
                resolveExpr(visitor, binaryOpExpr.getLeft());
                resolveExpr(visitor, binaryOpExpr.getRight());
            } else if (where instanceof SQLBinaryOpExprGroup) {
                SQLBinaryOpExprGroup binaryOpExprGroup = (SQLBinaryOpExprGroup) where;
                for (SQLExpr item : binaryOpExprGroup.getItems()) {
                    if (item instanceof SQLBinaryOpExpr) {
                        SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) item;
                        resolveExpr(visitor, binaryOpExpr.getLeft());
                        resolveExpr(visitor, binaryOpExpr.getRight());
                    } else {
                        item.accept(visitor);
                    }
                }
            } else {
                where.accept(visitor);
            }
        }

        SQLExpr startWith = x.getStartWith();
        if (startWith != null) {
            startWith.accept(visitor);
        }

        SQLExpr connectBy = x.getConnectBy();
        if (connectBy != null) {
            connectBy.accept(visitor);
        }

        SQLSelectGroupByClause groupBy = x.getGroupBy();
        if (groupBy != null) {
            groupBy.accept(visitor);
        }

        List<SQLWindow> windows = x.getWindows();
        if (windows != null) {
            for (SQLWindow window : windows) {
                window.accept(visitor);
            }
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            for (SQLSelectOrderByItem orderByItem : orderBy.getItems()) {
                SQLExpr orderByItemExpr = orderByItem.getExpr();

                if (orderByItemExpr instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr orderByItemIdentExpr = (SQLIdentifierExpr) orderByItemExpr;
                    long hash = orderByItemIdentExpr.nameHashCode64();
                    SQLSelectItem selectItem = x.findSelectItem(hash);

                    if (selectItem != null) {
                        orderByItem.setResolvedSelectItem(selectItem);

                        SQLExpr selectItemExpr = selectItem.getExpr();
                        if (selectItemExpr instanceof SQLIdentifierExpr) {
                            orderByItemIdentExpr.setResolvedTableSource(((SQLIdentifierExpr) selectItemExpr).getResolvedTableSource());
                            orderByItemIdentExpr.setResolvedColumn(((SQLIdentifierExpr) selectItemExpr).getResolvedColumn());
                        } else if (selectItemExpr instanceof SQLPropertyExpr) {
                            orderByItemIdentExpr.setResolvedTableSource(((SQLPropertyExpr) selectItemExpr).getResolvedTableSource());
                            orderByItemIdentExpr.setResolvedColumn(((SQLPropertyExpr) selectItemExpr).getResolvedColumn());
                        }
                        continue;
                    }
                }

                orderByItemExpr.accept(visitor);
            }
        }

        int forUpdateOfSize = x.getForUpdateOfSize();
        if (forUpdateOfSize > 0) {
            for (SQLExpr sqlExpr : x.getForUpdateOf()) {
                sqlExpr.accept(visitor);
            }
        }

        List<SQLSelectOrderByItem> distributeBy = x.getDistributeBy();
        if (distributeBy != null) {
            for (SQLSelectOrderByItem item : distributeBy) {
                item.accept(visitor);
            }
        }

        List<SQLSelectOrderByItem> sortBy = x.getSortBy();
        if (sortBy != null) {
            for (SQLSelectOrderByItem item : sortBy) {
                item.accept(visitor);
            }
        }

        visitor.popContext();
    }

    static void extractColumns(SchemaResolveVisitor visitor, SQLTableSource from, String ownerName, List<SQLSelectItem> columns) {
        if (from instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) from).getExpr();

            SchemaRepository repository = visitor.getRepository();
            if (repository == null) {
                return;
            }

            String alias = from.getAlias();

            SchemaObject table = repository.findTable((SQLExprTableSource) from);
            if (table != null) {
                SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) table.getStatement();
                for (SQLTableElement e : createTableStmt.getTableElementList()) {
                    if (e instanceof SQLColumnDefinition) {
                        SQLColumnDefinition column = (SQLColumnDefinition) e;

                        if (alias != null) {
                            SQLPropertyExpr name = new SQLPropertyExpr(alias, column.getName().getSimpleName());
                            name.setResolvedColumn(column);
                            columns.add(new SQLSelectItem(name));
                        } else if (ownerName != null) {
                            SQLPropertyExpr name = new SQLPropertyExpr(ownerName, column.getName().getSimpleName());
                            name.setResolvedColumn(column);
                            columns.add(new SQLSelectItem(name));
                        } else if (from.getParent() instanceof SQLJoinTableSource
                                && from instanceof SQLExprTableSource
                                && expr instanceof SQLName) {
                            String tableName = expr.toString();
                            SQLPropertyExpr name = new SQLPropertyExpr(tableName, column.getName().getSimpleName());
                            name.setResolvedColumn(column);
                            columns.add(new SQLSelectItem(name));
                        } else {
                            SQLIdentifierExpr name = (SQLIdentifierExpr) column.getName().clone();
                            name.setResolvedColumn(column);
                            columns.add(new SQLSelectItem(name));
                        }
                    }
                }
                return;
            }

            if (expr instanceof SQLIdentifierExpr) {
                SQLTableSource resolvedTableSource = ((SQLIdentifierExpr) expr).getResolvedTableSource();
                if (resolvedTableSource instanceof SQLWithSubqueryClause.Entry) {
                    SQLWithSubqueryClause.Entry entry = (SQLWithSubqueryClause.Entry) resolvedTableSource;
                    SQLSelect select = ((SQLWithSubqueryClause.Entry) resolvedTableSource).getSubQuery();
                    SQLSelectQueryBlock firstQueryBlock = select.getFirstQueryBlock();
                    if (firstQueryBlock != null) {
                        for (SQLSelectItem item : firstQueryBlock.getSelectList()) {
                            String itemAlias = item.computeAlias();
                            if (itemAlias != null) {
                                SQLIdentifierExpr columnExpr = new SQLIdentifierExpr(itemAlias);
                                columnExpr.setResolvedColumn(item);
                                columns.add(
                                        new SQLSelectItem(columnExpr));
                            }
                        }
                    }
                }
            }

        } else if (from instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) from;
            extractColumns(visitor, join.getLeft(), ownerName, columns);
            extractColumns(visitor, join.getRight(), ownerName, columns);
        } else if (from instanceof SQLSubqueryTableSource) {
            SQLSelectQueryBlock subQuery = ((SQLSubqueryTableSource) from).getSelect().getQueryBlock();
            if (subQuery == null) {
                return;
            }
            final List<SQLSelectItem> subSelectList = subQuery.getSelectList();
            for (SQLSelectItem subSelectItem : subSelectList) {
                if (subSelectItem.getAlias() != null) {
                    continue;
                }

                if (!(subSelectItem.getExpr() instanceof SQLName)) {
                    return; // skip
                }
            }

            for (SQLSelectItem subSelectItem : subSelectList) {
                String alias = subSelectItem.computeAlias();
                columns.add(new SQLSelectItem(new SQLIdentifierExpr(alias)));
            }
        } else if (from instanceof SQLUnionQueryTableSource) {
            SQLSelectQueryBlock firstQueryBlock = ((SQLUnionQueryTableSource) from).getUnion().getFirstQueryBlock();
            if (firstQueryBlock == null) {
                return;
            }

            final List<SQLSelectItem> subSelectList = firstQueryBlock.getSelectList();
            for (SQLSelectItem subSelectItem : subSelectList) {
                if (subSelectItem.getAlias() != null) {
                    continue;
                }

                if (!(subSelectItem.getExpr() instanceof SQLName)) {
                    return; // skip
                }
            }

            for (SQLSelectItem subSelectItem : subSelectList) {
                String alias = subSelectItem.computeAlias();
                columns.add(new SQLSelectItem(new SQLIdentifierExpr(alias)));
            }
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLAllColumnExpr x) {
        SQLTableSource tableSource = x.getResolvedTableSource();

        if (tableSource == null) {
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

            SQLTableSource from = queryBlock.getFrom();
            if (from == null || from instanceof SQLJoinTableSource) {
                return;
            }

            x.setResolvedTableSource(from);
            tableSource = from;
        }

        if (tableSource instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                SQLTableSource resolvedTableSource = ((SQLIdentifierExpr) expr).getResolvedTableSource();
                if (resolvedTableSource != null) {
                    x.setResolvedTableSource(resolvedTableSource);
                }
            }
        }
    }

    static void resolve(SchemaResolveVisitor v, SQLMethodInvokeExpr x) {
        SQLExpr owner = x.getOwner();
        if (owner != null) {
            resolveExpr(v, owner);
        }

        for (SQLExpr arg : x.getArguments()) {
            resolveExpr(v, arg);
        }

        SQLExpr from = x.getFrom();
        if (from != null) {
            resolveExpr(v, from);
        }

        SQLExpr using = x.getUsing();
        if (using != null) {
            resolveExpr(v, using);
        }

        SQLExpr _for = x.getFor();
        if (_for != null) {
            resolveExpr(v, _for);
        }

        long nameHash = x.methodNameHashCode64();
        SchemaRepository repository = v.getRepository();
        if (repository != null) {
            SQLDataType dataType = repository.findFuntionReturnType(nameHash);
            if (dataType != null) {
                x.setResolvedReturnDataType(dataType);
            }
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLSelect x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLWithSubqueryClause with = x.getWithSubQuery();
        if (with != null) {
            visitor.visit(with);
        }

        SQLSelectQuery query = x.getQuery();
        if (query != null) {
            if (query instanceof SQLSelectQueryBlock) {
                visitor.visit((SQLSelectQueryBlock) query);
            } else {
                query.accept(visitor);
            }
        }

        SQLSelectQueryBlock queryBlock = x.getFirstQueryBlock();

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            for (SQLSelectOrderByItem orderByItem : orderBy.getItems()) {
                SQLExpr orderByItemExpr = orderByItem.getExpr();

                if (orderByItemExpr instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr orderByItemIdentExpr = (SQLIdentifierExpr) orderByItemExpr;
                    long hash = orderByItemIdentExpr.nameHashCode64();

                    SQLSelectItem selectItem = null;
                    if (queryBlock != null) {
                        selectItem = queryBlock.findSelectItem(hash);
                    }

                    if (selectItem != null) {
                        orderByItem.setResolvedSelectItem(selectItem);

                        SQLExpr selectItemExpr = selectItem.getExpr();
                        if (selectItemExpr instanceof SQLIdentifierExpr) {
                            orderByItemIdentExpr.setResolvedTableSource(((SQLIdentifierExpr) selectItemExpr).getResolvedTableSource());
                            orderByItemIdentExpr.setResolvedColumn(((SQLIdentifierExpr) selectItemExpr).getResolvedColumn());
                        } else if (selectItemExpr instanceof SQLPropertyExpr) {
                            orderByItemIdentExpr.setResolvedTableSource(((SQLPropertyExpr) selectItemExpr).getResolvedTableSource());
                            orderByItemIdentExpr.setResolvedColumn(((SQLPropertyExpr) selectItemExpr).getResolvedColumn());
                        }
                        continue;
                    }
                }

                orderByItemExpr.accept(visitor);
            }
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLWithSubqueryClause x) {
        List<SQLWithSubqueryClause.Entry> entries = x.getEntries();
        final SchemaResolveVisitor.Context context = visitor.getContext();
        for (SQLWithSubqueryClause.Entry entry : entries) {
            SQLSelect query = entry.getSubQuery();
            if (query != null) {
                visitor.visit(query);

                final long alias_hash = entry.aliasHashCode64();
                if (context != null && alias_hash != 0) {
                    context.addTableSource(alias_hash, entry);
                }
            } else {
                entry.getReturningStatement().accept(visitor);
            }
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLExprTableSource x) {
        SQLExpr expr = x.getExpr();

        SQLExpr annFeature = null;
        if (expr instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr func = (SQLMethodInvokeExpr) expr;
            if (func.methodNameHashCode64() == FnvHash.Constants.ANN) {
                expr = func.getArguments().get(0);

                annFeature = func.getArguments().get(1);
                if (annFeature instanceof SQLIdentifierExpr) {
                    ((SQLIdentifierExpr) annFeature).setResolvedTableSource(x);
                } else  if (annFeature instanceof SQLPropertyExpr) {
                    ((SQLPropertyExpr) annFeature).setResolvedTableSource(x);
                }
            }
        }

        if (expr instanceof SQLName) {
            if (x.getSchemaObject() != null) {
                return;
            }

            SQLIdentifierExpr identifierExpr = null;

            if (expr instanceof SQLIdentifierExpr) {
                identifierExpr = (SQLIdentifierExpr) expr;
            } else if (expr instanceof SQLPropertyExpr) {
                SQLExpr owner = ((SQLPropertyExpr) expr).getOwner();
                if (owner instanceof SQLIdentifierExpr) {
                    identifierExpr = (SQLIdentifierExpr) owner;
                }
            }

            if (identifierExpr != null) {
                checkParameter(visitor, identifierExpr);

                SQLTableSource tableSource = unwrapAlias(visitor.getContext(), null, identifierExpr.nameHashCode64());
                if (tableSource != null) {
                    identifierExpr.setResolvedTableSource(tableSource);
                    return;
                }
            }

            SchemaRepository repository = visitor.getRepository();
            if (repository != null) {
                SchemaObject table = repository.findTable((SQLName) expr);
                if (table != null) {
                    x.setSchemaObject(table);

                    if (annFeature != null) {
                        if (annFeature instanceof SQLIdentifierExpr) {
                            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) annFeature;
                            SQLColumnDefinition column = table.findColumn(identExpr.nameHashCode64());
                            if (column != null) {
                                identExpr.setResolvedColumn(column);
                            }
                        }
                    }
                }
            }

        } else if (expr instanceof SQLMethodInvokeExpr) {
            visitor.visit((SQLMethodInvokeExpr) expr);
        } else if (expr instanceof SQLQueryExpr) {
            SQLSelect select =
            ((SQLQueryExpr) expr)
                    .getSubQuery();

            visitor.visit(select);

            SQLSelectQueryBlock queryBlock = select.getQueryBlock();
            if (queryBlock != null && annFeature instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) annFeature;
                SQLObject columnDef = queryBlock.resolveColum(identExpr.nameHashCode64());
                if (columnDef instanceof SQLColumnDefinition) {
                    identExpr.setResolvedColumn((SQLColumnDefinition) columnDef);
                } else if (columnDef instanceof SQLSelectItem) {
                    identExpr.setResolvedColumn((SQLSelectItem) columnDef);
                }
            }
            //if (queryBlock.findColumn())
        } else {
            expr.accept(visitor);
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLAlterTableStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLTableSource tableSource = x.getTableSource();
        ctx.setTableSource(tableSource);

        for (SQLAlterTableItem item : x.getItems()) {
            item.accept(visitor);
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLMergeStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLTableSource into = x.getInto();
        if (into instanceof SQLExprTableSource) {
            ctx.setTableSource(into);
        } else {
            into.accept(visitor);
        }

        SQLTableSource using = x.getUsing();
        if (using != null) {
            using.accept(visitor);
            ctx.setFrom(using);
        }

        SQLExpr on = x.getOn();
        if (on != null) {
            on.accept(visitor);
        }

        SQLMergeStatement.MergeUpdateClause updateClause  = x.getUpdateClause();
        if (updateClause != null) {
            for (SQLUpdateSetItem item : updateClause.getItems()) {
                SQLExpr column = item.getColumn();

                if (column instanceof SQLIdentifierExpr) {
                    ((SQLIdentifierExpr) column).setResolvedTableSource(into);
                } else if (column instanceof SQLPropertyExpr) {
                    ((SQLPropertyExpr) column).setResolvedTableSource(into);
                } else {
                    column.accept(visitor);
                }

                SQLExpr value = item.getValue();
                if (value != null) {
                    value.accept(visitor);
                }
            }

            SQLExpr where = updateClause.getWhere();
            if (where != null) {
                where.accept(visitor);
            }

            SQLExpr deleteWhere = updateClause.getDeleteWhere();
            if (deleteWhere != null) {
                deleteWhere.accept(visitor);
            }
        }

        SQLMergeStatement.MergeInsertClause insertClause = x.getInsertClause();
        if (insertClause != null) {
            for (SQLExpr column : insertClause.getColumns()) {
                if (column instanceof SQLIdentifierExpr) {
                    ((SQLIdentifierExpr) column).setResolvedTableSource(into);
                } else if (column instanceof SQLPropertyExpr) {
                    ((SQLPropertyExpr) column).setResolvedTableSource(into);
                }
                column.accept(visitor);
            }
            for (SQLExpr value : insertClause.getValues()) {
                value.accept(visitor);
            }
            SQLExpr where = insertClause.getWhere();
            if (where != null) {
                where.accept(visitor);
            }
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLCreateFunctionStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        {
            SQLDeclareItem declareItem = new SQLDeclareItem(x.getName().clone(), null);
            declareItem.setResolvedObject(x);

            SchemaResolveVisitor.Context parentCtx = visitor.getContext();
            if (parentCtx != null) {
                parentCtx.declare(declareItem);
            } else {
                ctx.declare(declareItem);
            }
        }

        for (SQLParameter parameter : x.getParameters()) {
            parameter.accept(visitor);
        }

        SQLStatement block = x.getBlock();
        if (block != null) {
            block.accept(visitor);
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLCreateProcedureStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        {
            SQLDeclareItem declareItem = new SQLDeclareItem(x.getName().clone(), null);
            declareItem.setResolvedObject(x);


            SchemaResolveVisitor.Context parentCtx = visitor.getContext();
            if (parentCtx != null) {
                parentCtx.declare(declareItem);
            } else {
                ctx.declare(declareItem);
            }
        }

        for (SQLParameter parameter : x.getParameters()) {
            parameter.accept(visitor);
        }

        SQLStatement block = x.getBlock();
        if (block != null) {
            block.accept(visitor);
        }

        visitor.popContext();
    }

    static boolean resolve(SchemaResolveVisitor visitor, SQLIfStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLExpr condition = x.getCondition();
        if (condition != null) {
            condition.accept(visitor);
        }

        for (SQLStatement stmt : x.getStatements()) {
            stmt.accept(visitor);
        }

        for (SQLIfStatement.ElseIf elseIf : x.getElseIfList()) {
            elseIf.accept(visitor);
        }

        SQLIfStatement.Else e = x.getElseItem();
        if (e != null) {
            e.accept(visitor);
        }

        visitor.popContext();
        return false;
    }

    static void resolve(SchemaResolveVisitor visitor, SQLBlockStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        for (SQLParameter parameter : x.getParameters()) {
            visitor.visit(parameter);
        }

        for (SQLStatement stmt : x.getStatementList()) {
            stmt.accept(visitor);
        }

        SQLStatement exception = x.getException();
        if (exception != null) {
            exception.accept(visitor);
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLParameter x) {
        SQLName name = x.getName();
        if (name instanceof SQLIdentifierExpr) {
            ((SQLIdentifierExpr) name).setResolvedParameter(x);
        }

        SQLExpr expr = x.getDefaultValue();

        SchemaResolveVisitor.Context ctx = null;
        if (expr != null) {
            if (expr instanceof SQLQueryExpr) {
                ctx = visitor.createContext(x);

                SQLSubqueryTableSource tableSource = new SQLSubqueryTableSource(((SQLQueryExpr) expr).getSubQuery());
                tableSource.setParent(x);
                tableSource.setAlias(x.getName().getSimpleName());

                ctx.setTableSource(tableSource);
            }

            expr.accept(visitor);
        }

        if (ctx != null) {
            visitor.popContext();
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLDeclareItem x) {
        SchemaResolveVisitor.Context ctx = visitor.getContext();
        if (ctx != null) {
            ctx.declare(x);
        }

        SQLName name = x.getName();
        if (name instanceof SQLIdentifierExpr) {
            ((SQLIdentifierExpr) name).setResolvedDeclareItem(x);
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLOver x) {
        SQLName of = x.getOf();
        SQLOrderBy orderBy = x.getOrderBy();
        List<SQLExpr> partitionBy = x.getPartitionBy();


        if (of == null // skip if of is not null
                && orderBy != null) {
            orderBy.accept(visitor);
        }

        if (partitionBy != null) {
            for (SQLExpr expr : partitionBy) {
                expr.accept(visitor);
            }
        }
    }

    private static boolean checkParameter(SchemaResolveVisitor visitor, SQLIdentifierExpr x) {
        if (x.getResolvedParameter() != null) {
            return true;
        }

        SchemaResolveVisitor.Context ctx = visitor.getContext();
        if (ctx == null) {
            return false;
        }

        long hash = x.hashCode64();
        for (SchemaResolveVisitor.Context parentCtx = ctx;
             parentCtx != null;
             parentCtx = parentCtx.parent) {

            if (parentCtx.object instanceof SQLBlockStatement) {
                SQLBlockStatement block = (SQLBlockStatement) parentCtx.object;
                SQLParameter parameter = block.findParameter(hash);
                if (parameter != null) {
                    x.setResolvedParameter(parameter);
                    return true;
                }
            }

            if (parentCtx.object instanceof SQLCreateProcedureStatement) {
                SQLCreateProcedureStatement createProc = (SQLCreateProcedureStatement) parentCtx.object;
                SQLParameter parameter = createProc.findParameter(hash);
                if (parameter != null) {
                    x.setResolvedParameter(parameter);
                    return true;
                }
            }

            if (parentCtx.object instanceof SQLSelect) {
                SQLSelect select = (SQLSelect) parentCtx.object;
                SQLWithSubqueryClause with = select.getWithSubQuery();
                if (with != null) {
                    SQLWithSubqueryClause.Entry entry = with.findEntry(hash);
                    if (entry != null) {
                        x.setResolvedTableSource(entry);
                        return true;
                    }
                }
            }

            SQLDeclareItem declareItem = parentCtx.findDeclare(hash);
            if (declareItem != null) {
                x.setResolvedDeclareItem(declareItem);
                break;
            }
        }
        return false;
    }

    static void resolve(SchemaResolveVisitor visitor, SQLReplaceStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLExprTableSource tableSource = x.getTableSource();
        ctx.setTableSource(tableSource);
        visitor.visit(tableSource);

        for (SQLExpr column : x.getColumns()) {
            column.accept(visitor);
        }

        SQLQueryExpr queryExpr = x.getQuery();
        if (queryExpr != null) {
            visitor.visit(queryExpr.getSubQuery());
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLFetchStatement x) {
        resolveExpr(visitor, x.getCursorName());
        for (SQLExpr expr : x.getInto()) {
            resolveExpr(visitor, expr);
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLForeignKeyConstraint x) {
        SchemaRepository repository = visitor.getRepository();
        SQLObject parent = x.getParent();

        if (parent instanceof SQLCreateTableStatement) {
            SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) parent;
            SQLTableSource table = createTableStmt.getTableSource();
            for (SQLName item : x.getReferencingColumns()) {
                SQLIdentifierExpr columnName = (SQLIdentifierExpr) item;
                columnName.setResolvedTableSource(table);

                SQLColumnDefinition column = createTableStmt.findColumn(columnName.nameHashCode64());
                if (column != null) {
                    columnName.setResolvedColumn(column);
                }
            }
        } else if (parent instanceof SQLAlterTableAddConstraint) {
            SQLAlterTableStatement stmt = (SQLAlterTableStatement) parent.getParent();
            SQLTableSource table = stmt.getTableSource();
            for (SQLName item : x.getReferencingColumns()) {
                SQLIdentifierExpr columnName = (SQLIdentifierExpr) item;
                columnName.setResolvedTableSource(table);
            }
        }


        if (repository == null) {
            return;
        }

        SQLExprTableSource table = x.getReferencedTable();
        for (SQLName item : x.getReferencedColumns()) {
            SQLIdentifierExpr columnName = (SQLIdentifierExpr) item;
            columnName.setResolvedTableSource(table);
        }

        SQLName tableName = table.getName();

        SchemaObject tableObject = repository.findTable(tableName);
        if (tableObject == null) {
            return;
        }

        SQLStatement tableStmt = tableObject.getStatement();
        if (tableStmt instanceof SQLCreateTableStatement) {
            SQLCreateTableStatement refCreateTableStmt = (SQLCreateTableStatement) tableStmt;
            for (SQLName item : x.getReferencedColumns()) {
                SQLIdentifierExpr columnName = (SQLIdentifierExpr) item;
                SQLColumnDefinition column = refCreateTableStmt.findColumn(columnName.nameHashCode64());
                if (column != null) {
                    columnName.setResolvedColumn(column);
                }
            }
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLCreateViewStatement x) {
        x.getSubQuery()
                .accept(visitor);
    }

    // for performance
    static void resolveExpr(SchemaResolveVisitor visitor, SQLExpr x) {
        if (x == null) {
            return;
        }

        Class<?> clazz = x.getClass();
        if (clazz == SQLIdentifierExpr.class) {
            visitor.visit((SQLIdentifierExpr) x);
            return;
        } else if (clazz == SQLIntegerExpr.class || clazz == SQLCharExpr.class) {
            // skip
            return;
        }

        x.accept(visitor);
    }

    static void resolveUnion(SchemaResolveVisitor visitor, SQLUnionQuery x) {
        SQLUnionOperator operator = x.getOperator();
        List<SQLSelectQuery> relations = x.getRelations();
        if (relations.size() > 2) {
            for (SQLSelectQuery relation : relations) {
                relation.accept(visitor);
            }
            return;
        }
        SQLSelectQuery left = x.getLeft();
        SQLSelectQuery right = x.getRight();

        boolean bracket = x.isBracket() && !(x.getParent() instanceof SQLUnionQueryTableSource);

        if ((!bracket)
                && left instanceof SQLUnionQuery
                && ((SQLUnionQuery) left).getOperator() == operator
                && !right.isBracket()
                && x.getOrderBy() == null) {

            SQLUnionQuery leftUnion = (SQLUnionQuery) left;

            List<SQLSelectQuery> rights = new ArrayList<SQLSelectQuery>();
            rights.add(right);

            for (; ; ) {
                SQLSelectQuery leftLeft = leftUnion.getLeft();
                SQLSelectQuery leftRight = leftUnion.getRight();

                if ((!leftUnion.isBracket())
                        && leftUnion.getOrderBy() == null
                        && (!leftLeft.isBracket())
                        && (!leftRight.isBracket())
                        && leftLeft instanceof SQLUnionQuery
                        && ((SQLUnionQuery) leftLeft).getOperator() == operator) {
                    rights.add(leftRight);
                    leftUnion = (SQLUnionQuery) leftLeft;
                    continue;
                } else {
                    rights.add(leftRight);
                    rights.add(leftLeft);
                }
                break;
            }

            for (int i = rights.size() - 1; i >= 0; i--) {
                SQLSelectQuery item = rights.get(i);
                item.accept(visitor);
            }
            return;
        }

        if (left != null) {
            left.accept(visitor);
        }

        if (right != null) {
            right.accept(visitor);
        }
    }
}
