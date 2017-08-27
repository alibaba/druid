package com.alibaba.druid.sql.repository;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

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

        public boolean visit(SQLExprTableSource x) {
            repository.resolve(this, x);
            return false;
        }

        public boolean visit(MySqlSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolve(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            resolve(this, x);
            return true;
        }

        public boolean visit(SQLPropertyExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLAllColumnExpr x) {
            resolve(this, x);
            return false;
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

        public boolean visit(SQLSelect x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLWithSubqueryClause x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
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

        public boolean visit(SQLExprTableSource x) {
            repository.resolve(this, x);
            return false;
        }

        public boolean visit(DB2SelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolve(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            resolve(this, x);
            return true;
        }

        public boolean visit(SQLPropertyExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLAllColumnExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLDeleteStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelect x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLWithSubqueryClause x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
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

        public boolean visit(OracleSelectTableReference x) {
            repository.resolve(this, x);
            return false;
        }

        public boolean visit(SQLExprTableSource x) {
            repository.resolve(this, x);
            return false;
        }

        public boolean visit(OracleSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolve(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            resolve(this, x);
            return true;
        }

        public boolean visit(SQLPropertyExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLAllColumnExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OracleCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OracleUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLDeleteStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OracleDeleteStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelect x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLWithSubqueryClause x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
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

        public boolean visit(SQLExprTableSource x) {
            repository.resolve(this, x);
            return false;
        }

        public boolean visit(OdpsSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolve(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            resolve(this, x);
            return true;
        }

        public boolean visit(SQLPropertyExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLAllColumnExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(OdpsCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLDeleteStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelect x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLWithSubqueryClause x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
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

        public boolean visit(SQLExprTableSource x) {
            repository.resolve(this, x);
            return false;
        }

        public boolean visit(PGSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolve(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            resolve(this, x);
            return true;
        }

        public boolean visit(SQLPropertyExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLAllColumnExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(PGUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLDeleteStatement x) {
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

        public boolean visit(SQLSelect x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLWithSubqueryClause x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
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

        public boolean visit(SQLExprTableSource x) {
            repository.resolve(this, x);
            return false;
        }

        public boolean visit(SQLServerSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolve(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            resolve(this, x);
            return true;
        }

        public boolean visit(SQLPropertyExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLAllColumnExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLServerUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLDeleteStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelect x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLWithSubqueryClause x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
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

        public boolean visit(SQLExprTableSource x) {
            repository.resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectQueryBlock x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelectItem x) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                resolve(this, (SQLIdentifierExpr) expr);
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                resolve(this, (SQLPropertyExpr) expr);
                return false;
            }

            return true;
        }

        public boolean visit(SQLIdentifierExpr x) {
            resolve(this, x);
            return true;
        }

        public boolean visit(SQLPropertyExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLAllColumnExpr x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLUpdateStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLDeleteStatement x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLSelect x) {
            resolve(this, x);
            return false;
        }

        public boolean visit(SQLWithSubqueryClause x) {
            resolve(this, x);
            return false;
        }

        @Override
        public boolean isEnabled(Option option) {
            return (options & option.mask) != 0;
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

                        SQLColumnDefinition column = x.findColumn(identifierExpr.name_hash_lower());
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

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLUpdateStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

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

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLDeleteStatement x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLTableSource table = x.getTableSource();
        SQLTableSource from = x.getFrom();

        if (table == null && from != null) {
            table = from;
            from = null;
        }

        ctx.setTableSource(table);
        ctx.setFrom(from);

        if (table != null) {
            table.accept(visitor);
        }

        if (from != null) {
            from.accept(visitor);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            where.accept(visitor);
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLIdentifierExpr x) {
        SchemaResolveVisitor.Context ctx = visitor.getContext();
        if (ctx == null) {
            return;
        }

        String ident = x.getName();
        long hash = x.name_hash_lower();
        SQLTableSource tableSource = null;

        SQLTableSource ctxTable = ctx.getTableSource();

        if (ctxTable instanceof SQLJoinTableSource
                || ctxTable instanceof SQLSubqueryTableSource) {
            tableSource = ctxTable.findTableSourceWithColumn(hash);
        } else {
            tableSource = ctxTable;
            if (tableSource instanceof SQLExprTableSource) {
                SchemaObject table = ((SQLExprTableSource) tableSource).getSchemaObject();
                if (table != null) {
                    if (table.findColumn(hash) == null) {
                        tableSource = null; // maybe parent
                    }
                }
            }
        }

        if (tableSource instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                long identHash = identExpr.name_hash_lower();

                for (SchemaResolveVisitor.Context parentCtx = ctx.parent;
                     parentCtx != null;
                     parentCtx = parentCtx.parent) {

                    if (parentCtx.object instanceof SQLSelect) {
                        SQLSelect select = (SQLSelect) parentCtx.object;
                        SQLWithSubqueryClause with = select.getWithSubQuery();
                        if (with != null) {
                            SQLWithSubqueryClause.Entry entry = with.findEntry(identHash);
                            if (entry != null) {
                                tableSource = entry;
                                break;
                            }
                        }
                    } else if (parentCtx.object instanceof PGSelectStatement) {

                    }
                }
            }
        }

        if (tableSource != null) {
            x.setResolvedTableSource(tableSource);

            SQLColumnDefinition column = tableSource.findColumn(hash);
            if (column != null) {
                x.setResolvedColumn(column);
            }

            if (ctxTable instanceof SQLJoinTableSource) {
                String alias = tableSource.computeAlias();
                SQLPropertyExpr propertyExpr = new SQLPropertyExpr(new SQLIdentifierExpr(alias), ident, hash);
                SQLUtils.replaceInParent(x, propertyExpr);
            }
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLSelectQueryBlock x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLTableSource from = x.getFrom();
        if (from != null) {
            ctx.setTableSource(from);

            from.accept(visitor);
        }

        List<SQLSelectItem> selectList = x.getSelectList();

        List<SQLSelectItem> columns = new ArrayList<SQLSelectItem>();
        for (int i = selectList.size() - 1; i >= 0; i--) {
            SQLSelectItem selectItem = selectList.get(i);
            SQLExpr expr = selectItem.getExpr();
            if (expr instanceof SQLAllColumnExpr) {
                if (visitor.isEnabled(SchemaResolveVisitor.Option.ResolveAllColumn)) {
                    extractColumns(visitor, from, columns);
                }
            } else if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                visitor.visit(propertyExpr);

                String ownerName = propertyExpr.getOwnernName();
                if (propertyExpr.getName().equals("*")) {
                    if (visitor.isEnabled(SchemaResolveVisitor.Option.ResolveAllColumn)) {
                        SQLTableSource tableSource = x.findTableSource(ownerName);
                        extractColumns(visitor, tableSource, columns);
                    }
                }

                SQLColumnDefinition column = propertyExpr.getResolvedColumn();
                if (column != null) {
                    continue;
                }
                SQLTableSource tableSource = x.findTableSource(propertyExpr.getOwnernName());
                if (tableSource != null) {
                    column = tableSource.findColumn(propertyExpr.name_hash_lower());
                    if (column != null) {
                        propertyExpr.setResolvedColumn(column);
                    }
                }
            } else if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                visitor.visit(identExpr);

                long name_hash = identExpr.name_hash_lower();

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
            }
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            where.accept(visitor);
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

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            orderBy.accept(visitor);
        }

        visitor.popContext();
    }

    static void extractColumns(SchemaResolveVisitor visitor, SQLTableSource from, List<SQLSelectItem> columns) {
        if (from instanceof SQLExprTableSource) {
            SchemaRepository repository = visitor.getRepository();
            if (repository == null) {
                return;
            }

            SchemaObject table = repository.findTable((SQLExprTableSource) from);
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

    static void resolve(SchemaResolveVisitor visitor, SQLPropertyExpr x) {
        SchemaResolveVisitor.Context ctx = visitor.getContext();
        if (ctx == null) {
            return;
        }

        SQLIdentifierExpr owner;
        {
            SQLExpr ownerObj = x.getOwner();
            if (ownerObj instanceof SQLIdentifierExpr) {
                owner = (SQLIdentifierExpr) ownerObj;
            } else {
                return;
            }
        }
        long owner_hash = owner.name_hash_lower();

        SQLTableSource ctxTable = ctx.getTableSource();

        SQLTableSource tableSource = ctxTable.findTableSource(owner_hash);
        if (tableSource == null) {
            SQLTableSource ctxFrom = ctx.getFrom();
            if (ctxFrom != null) {
                tableSource = ctxFrom.findTableSource(owner_hash);
            }
        }

        if (tableSource != null) {
            x.setResolvedTableSource(tableSource);
            SQLColumnDefinition column = tableSource.findColumn(x.name_hash_lower());
            if (column != null) {
                x.setResolvedColumn(column);
            }
        }
    }

    static void resolve(SchemaResolveVisitor visitor, SQLAllColumnExpr x) {
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
    }

    static void resolve(SchemaResolveVisitor visitor, SQLSelect x) {
        SchemaResolveVisitor.Context ctx = visitor.createContext(x);

        SQLWithSubqueryClause with = x.getWithSubQuery();
        if (with != null) {
            visitor.visit(with);
        }

        SQLSelectQuery query = x.getQuery();
        if (query != null) {
            query.accept(visitor);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            visitor.visit(orderBy);
        }

        visitor.popContext();
    }

    static void resolve(SchemaResolveVisitor visitor, SQLWithSubqueryClause x) {
        List<SQLWithSubqueryClause.Entry> entries = x.getEntries();
        for (SQLWithSubqueryClause.Entry entry : entries) {
            SQLSelect query = entry.getSubQuery();
            if (query != null) {
                visitor.visit(query);
            } else {
                entry.getReturningStatement().accept(visitor);
            }
        }
    }

}
