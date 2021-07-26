package com.alibaba.druid.sql.dialect.dm.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLWindow;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.dm.ast.DMSQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

/**
 * @author two brother
 * @date 2021/7/26 11:31
 */
public class DmSqlOutputVisitor  extends SQLASTOutputVisitor implements DMASTVisitor {
    public DmSqlOutputVisitor(Appendable appender) {
        super(appender);
    }

    public DmSqlOutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public DmSqlOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        if (x instanceof DMSQLSelectQueryBlock) {
            return visit((DMSQLSelectQueryBlock) x);
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(DMSQLSelectQueryBlock x) {
        if (isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "SELECT " : "select ");

        if (x.getHintsSize() > 0) {
            printAndAccept(x.getHints(), ", ");
            print(' ');
        }

        final boolean informix =DbType.informix == dbType;
        if (informix) {
            printFetchFirst(x);
        }

        final int distinctOption = x.getDistionOption();
        if (SQLSetQuantifier.ALL == distinctOption) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == distinctOption) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.UNIQUE == distinctOption) {
            print0(ucase ? "UNIQUE " : "unique ");
        }

        if (x.hasTop()) {
            print0(ucase ? "TOP " : "top ");
            print0(x.getArg0() + " ");
            if (x.getArg1() != -1) {
                print0("," + x.getArg1() + " ");
            }
            if (x.isPERCENT()) {
                print0(ucase ? "PERCENT " : "percent ");
            }
            if (x.isWITH()) {
                print0(ucase ? "WITH " : "with ");
            }
            if (x.isTIES()) {
                print0(ucase ? "TIES " : "ties ");
            }
        }

        printSelectList(
                x.getSelectList());

        SQLExprTableSource into = x.getInto();
        if (into != null) {
            println();
            print0(ucase ? "INTO " : "into ");
            into.accept(this);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();

            boolean printFrom = from instanceof SQLLateralViewTableSource
                    && ((SQLLateralViewTableSource) from).getTableSource() == null;
            if (!printFrom) {
                print0(ucase ? "FROM " : "from ");
            }
            printTableSource(from);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            printExpr(where, parameterized);

            if (where.hasAfterComment() && isPrettyFormat()) {
                print(' ');
                printlnComment(x.getWhere().getAfterCommentsDirect());
            }
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

        final List<SQLSelectOrderByItem> distributeBy = x.getDistributeByDirect();
        if (distributeBy != null && distributeBy.size() > 0) {
            println();
            print0(ucase ? "DISTRIBUTE BY " : "distribute by ");
            printAndAccept(distributeBy, ", ");
        }

        List<SQLSelectOrderByItem> sortBy = x.getSortByDirect();
        if (sortBy != null && sortBy.size() > 0) {
            println();
            print0(ucase ? "SORT BY " : "sort by ");
            printAndAccept(sortBy, ", ");
        }

        final List<SQLSelectOrderByItem> clusterBy = x.getClusterByDirect();
        if (clusterBy != null && clusterBy.size() > 0) {
            println();
            print0(ucase ? "CLUSTER BY " : "cluster by ");
            printAndAccept(clusterBy, ", ");
        }

        if (!informix) {
            printFetchFirst(x);
        }

        if (x.isForUpdate()) {
            println();
            print0(ucase ? "FOR UPDATE" : "for update");
        }

        return false;
    }
}
