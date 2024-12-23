package com.alibaba.druid.sql.dialect.doris.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPivot;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLUnpivot;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSampling;
import com.alibaba.druid.sql.dialect.doris.ast.DorisExprTableSource;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksOutputVisitor;

import java.util.List;

public class DorisOutputVisitor extends StarRocksOutputVisitor implements DorisASTVisitor {
    public DorisOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.doris;
    }

    public DorisOutputVisitor(StringBuilder appender, DbType dbType) {
        super(appender, dbType);
        dbType = DbType.doris;
    }

    public DorisOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.doris;
    }

    public void printSqlSetQuantifier(SQLSelectQueryBlock x) {
        final int distinctOption = x.getDistionOption();
        if (SQLSetQuantifier.ALL == distinctOption) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == distinctOption) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.UNIQUE == distinctOption) {
            print0(ucase ? "UNIQUE " : "unique ");
        } else if (SQLSetQuantifier.ALL_EXCEPT == distinctOption) {
            print0(ucase ? "ALL EXCEPT " : "all except ");
        }
    }

    @Override
    public boolean visit(DorisExprTableSource x) {
        printTableSourceExpr(x.getExpr());

        if (!x.getPartitions().isEmpty()) {
            println();
            print(ucase ? "PARTITION(" : "partition(");
            printAndAccept(x.getPartitions(), ", ");
            print(")");
        }

        if (!x.getTablets().isEmpty()) {
            println();
            print(ucase ? "TABLET(" : "tablet(");
            printAndAccept(x.getTablets(), ", ");
            print(")");
        }

        final SQLTableSampling sampling = x.getSampling();
        if (sampling != null) {
            println();
            sampling.accept(this);
        }

        if (x.getRepeatable() != null) {
            println();
            print0(ucase ? "REPEATABLE " : "repeatable ");
            x.getRepeatable().accept(this);
        }

        String alias = x.getAlias();
        List<SQLName> columns = x.getColumnsDirect();
        if (alias != null) {
            print(' ');
            if (columns != null && columns.size() > 0) {
                print0(ucase ? " AS " : " as ");
            } else if (x.isNeedAsTokenForAlias()) {
                print0(ucase ? "AS " : "as ");
            }
            print0(alias);
        }

        if (columns != null && columns.size() > 0) {
            print(" (");
            printAndAccept(columns, ", ");
            print(')');
        }

        SQLPivot pivot = x.getPivot();
        if (pivot != null) {
            println();
            pivot.accept(this);
        }

        SQLUnpivot unpivot = x.getUnpivot();
        if (unpivot != null) {
            println();
            unpivot.accept(this);
        }

        if (isPrettyFormat() && x.hasAfterComment()) {
            print(' ');
            printlnComment(x.getAfterCommentsDirect());
        }

        return false;
    }

    @Override
    public boolean visit(SQLTableSampling x) {
        print0(ucase ? "TABLESAMPLE" : "tablesample");
        print('(');

        final SQLExpr percent = x.getPercent();
        if (percent != null) {
            percent.accept(this);
            print0(ucase ? " PERCENT" : " percent");
        }

        final SQLExpr rows = x.getRows();
        if (rows != null) {
            rows.accept(this);
            print0(ucase ? " ROWS" : " rows");
        }

        final SQLExpr size = x.getByteLength();
        if (size != null) {
            size.accept(this);
        }

        print(')');
        return false;
    }

    @Override
    public void printInsertOverWrite(SQLInsertStatement x) {
        print0(ucase ? "INSERT OVERWRITE TABLE " : "insert overwrite table ");
    }
}
