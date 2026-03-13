package com.alibaba.druid.sql.dialect.dm.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.dm.Dm;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.*;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.DmSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.DmSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

public class DmOutputVisitor extends SQLASTOutputVisitor implements DmASTVisitor, OracleASTVisitor {
    public DmOutputVisitor(StringBuilder appender) {
        super(appender, DbType.dm, Dm.DIALECT);
    }

    public DmOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, DbType.dm, Dm.DIALECT, parameterized);
    }

    @Override
    public boolean visit(FetchClause x) {
        print0(ucase ? "FETCH " : "fetch ");
        if (FetchClause.Option.FIRST.equals(x.getOption())) {
            print0(ucase ? "FIRST " : "first ");
        } else if (FetchClause.Option.NEXT.equals(x.getOption())) {
            print0(ucase ? "NEXT " : "next ");
        }
        x.getCount().accept(this);
        print0(ucase ? " ROWS ONLY" : " rows only");
        return false;
    }

    @Override
    public boolean visit(ForClause x) {
        print0(ucase ? "FOR " : "for ");
        if (ForClause.Option.UPDATE.equals(x.getOption())) {
            print0(ucase ? "UPDATE" : "update");
        } else if (ForClause.Option.SHARE.equals(x.getOption())) {
            print0(ucase ? "SHARE" : "share");
        }

        if (x.getOf().size() > 0) {
            print(' ');
            for (int i = 0; i < x.getOf().size(); ++i) {
                if (i != 0) {
                    println(", ");
                }
                x.getOf().get(i).accept(this);
            }
        }

        if (x.isNoWait()) {
            print0(ucase ? " NOWAIT" : " nowait");
        } else if (x.getWaitTimeout() != null) {
            print0(ucase ? " WAIT " : " wait ");
            x.getWaitTimeout().accept(this);
        } else if (x.isSkipLocked()) {
            print0(ucase ? " SKIP LOCKED" : " skip locked");
        }

        return false;
    }

    public boolean visit(DmSelectQueryBlock x) {
        if ((!isParameterized()) && isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        final boolean bracket = x.isParenthesized();
        if (bracket) {
            print('(');
        }

        print0(ucase ? "SELECT " : "select ");

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
            print0(ucase ? "UNIQUE " : "unique ");
        }

        SQLTop top = x.getTop();
        if (top != null) {
            top.accept(this);
            print(' ');
        }

        printSelectList(x.getSelectList());

        SQLExprTableSource into = x.getInto();
        if (into != null) {
            println();
            print0(ucase ? "INTO " : "into ");
            into.accept(this);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            printTableSource(from);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            printExpr(where, parameterized);
        }

        printHierarchical(x);

        SQLSelectGroupByClause groupBy = x.getGroupBy();
        if (groupBy != null) {
            println();
            visit(groupBy);
        }

        List<SQLWindow> windows = x.getWindows();
        if (windows != null && windows.size() > 0) {
            println();
            print0(ucase ? "WINDOW " : "window ");
            printAndAccept(windows, ", ");
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            orderBy.accept(this);
        }

        SQLLimit limit = x.getLimit();
        if (limit != null) {
            println();
            visit(limit);
        }

        FetchClause fetch = x.getFetch();
        if (fetch != null) {
            println();
            fetch.accept(this);
        }

        ForClause forClause = x.getForClause();
        if (forClause != null) {
            println();
            forClause.accept(this);
        }

        if (bracket) {
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(DmDeleteStatement x) {
        print0(ucase ? "DELETE " : "delete ");

        print0(ucase ? "FROM " : "from ");

        printTableSource(x.getTableSource());

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            printExpr(where, parameterized);
        }

        return false;
    }

    @Override
    public boolean visit(DmInsertStatement x) {
        print0(ucase ? "INSERT INTO " : "insert into ");

        x.getTableSource().accept(this);

        printInsertColumns(x.getColumns());

        if (!x.getValuesList().isEmpty()) {
            println();
            print0(ucase ? "VALUES" : "values");
            for (int i = 0, size = x.getValuesList().size(); i < size; ++i) {
                if (i != 0) {
                    print(',');
                }
                println();
                print('(');
                SQLInsertStatement.ValuesClause values = x.getValuesList().get(i);
                printAndAccept(values.getValues(), ", ");
                print(')');
            }
        }

        if (x.getQuery() != null) {
            println();
            x.getQuery().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(DmUpdateStatement x) {
        print0(ucase ? "UPDATE " : "update ");

        printTableSource(x.getTableSource());

        println();
        print0(ucase ? "SET " : "set ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            SQLUpdateSetItem item = x.getItems().get(i);
            visit(item);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            printTableSource(from);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            printExpr(where, parameterized);
        }

        List<SQLExpr> returning = x.getReturning();
        if (returning != null && returning.size() > 0) {
            println();
            print0(ucase ? "RETURNING " : "returning ");
            printAndAccept(returning, ", ");
        }

        return false;
    }

    @Override
    public boolean visit(DmSelectStatement x) {
        return visit(x.getSelect());
    }

    @Override
    public void endVisit(DmSelectStatement x) {
    }

    // Oracle AST visitor default implementations for DM Oracle-compatibility
    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

}
