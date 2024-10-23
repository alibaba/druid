package com.alibaba.druid.sql.dialect.teradata.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLTop;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.ast.TDCreateTableStatement;
import com.alibaba.druid.sql.dialect.teradata.ast.TDDateDataType;
import com.alibaba.druid.sql.dialect.teradata.ast.TDNormalize;
import com.alibaba.druid.sql.dialect.teradata.ast.TDSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class TDOutputVisitor extends SQLASTOutputVisitor implements TDASTVisitor {
    public TDOutputVisitor(StringBuilder appender) {
        super(appender, DbType.teradata);
    }

    protected void printSelectListBefore(SQLSelectQueryBlock x) {
        if (x instanceof TDSelectQueryBlock) {
            print(' ');
            TDSelectQueryBlock tdSelectQueryBlock = (TDSelectQueryBlock) x;
            if (tdSelectQueryBlock.isWithDeletedRows()) {
                print0(ucase ? "WITH DELETED ROWS " : " with deleted rows ");
            }
            if (tdSelectQueryBlock.isAsJson()) {
                print0(ucase ? "AS JSON " : " as json ");
            }
        }
    }
    @Override
    public void printSqlSetQuantifier(SQLSelectQueryBlock x) {
        final int distinctOption = x.getDistionOption();
        if (SQLSetQuantifier.ALL == distinctOption) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == distinctOption) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (x instanceof TDSelectQueryBlock && (((TDSelectQueryBlock) x).getNormalize() != null)) {
            TDNormalize normalize = ((TDSelectQueryBlock) x).getNormalize();
            print0(ucase ? "NORMALIZE " : "normalize ");
            if (normalize.isMeets() || normalize.isOverlaps()) {
                print0(ucase ? "ON " : "on ");
                if (normalize.isMeetsFirst()) {
                    print0(ucase ? "MEETS " : "meets ");
                } else {
                    print0(ucase ? "OVERLAPS " : "overlaps ");
                }
                if (normalize.isMeets() && normalize.isOverlaps()) {
                    print0(ucase ? "OR " : "or ");
                    if (!normalize.isMeetsFirst()) {
                        print0(ucase ? "MEETS " : "meets ");
                    } else {
                        print0(ucase ? "OVERLAPS " : "overlaps ");
                    }
                }
            }
        }
    }

    @Override
    public void printTop(SQLSelectQueryBlock x) {
        if (x instanceof TDSelectQueryBlock) {
            SQLTop top = ((TDSelectQueryBlock) x).getTop();
            if (top != null) {
                print(' ');
                visit(top);
            }
        }
    }

    @Override
    public boolean visit(SQLDataType x) {
        super.visit(x);
        if (x instanceof TDDateDataType) {
            TDDateDataType dataType = (TDDateDataType) x;
            if (dataType.getFormat() != null) {
                print(ucase ? " FORMAT " : " format ");
                dataType.getFormat().accept(this);
            }
        }
        return false;
    }

    protected void printCreateTableRest(SQLCreateTableStatement x) {
        if (x instanceof TDCreateTableStatement) {
            TDCreateTableStatement stmt = (TDCreateTableStatement) x;
            if (stmt.getPrimaryKey() != null) {
                visit((SQLPrimaryKeyImpl) stmt.getPrimaryKey());
            }
            if (stmt.getOnCommitRows() != null) {
                print0(ucase ? "ON COMMIT " : "on commit ");
                print0(ucase ? stmt.getOnCommitRows().name().toUpperCase() : stmt.getOnCommitRows().name().toLowerCase());
                print0(ucase ? " ROWS" : " rows");
            }
        }
    }

    @Override
    public boolean visit(SQLPrimaryKeyImpl x) {
        println();
        print0(ucase ? "PRIMARY INDEX " : "primary index ");

        print('(');
        printAndAccept(x.getColumns(), ", ");
        print(')');
        println();
        return false;
    }
}
