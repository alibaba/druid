package com.alibaba.druid.sql.dialect.redshift.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.dialect.redshift.stmt.*;

public class RedshiftOutputVisitor extends PGOutputVisitor implements RedshiftASTVisitor {
    public RedshiftOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.hologres;
    }

    public RedshiftOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.hologres;
    }

    public boolean visit(RedshiftSelectQueryBlock x) {
        print0(ucase ? "SELECT " : "select ");

        RedshiftTop top = x.getTop();
        if (top != null) {
            visit(top);
            print(' ');
        }

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print0(ucase ? "DISTINCT " : "distinct ");
        }

        printSelectList(x.getSelectList());
        printInto(x);
        printFrom(x);
        printWhere(x);
        printHierarchical(x);
        printGroupBy(x);
        printQualify(x);
        printOrderBy(x);
        printFetchFirst(x);
        printAfterFetch(x);

        return false;
    }

    @Override
    public boolean visit(RedshiftTop x) {
        boolean parameterized = this.parameterized;
        this.parameterized = false;

        print0(ucase ? "TOP " : "top ");

        x.getExpr().accept(this);

        this.parameterized = parameterized;
        return false;
    }

    @Override
    protected void printInto(SQLSelectQueryBlock x) {
        if (x instanceof RedshiftSelectQueryBlock) {
            RedshiftSelectQueryBlock queryBlock = (RedshiftSelectQueryBlock) x;
            SQLExprTableSource into = x.getInto();
            if (into != null) {
                println();
                print0(ucase ? "INTO " : "into ");
                if (queryBlock.isInsertTemp()) {
                    print0(ucase ? "TEMP " : "temp ");
                }
                if (queryBlock.isInsertTemporary()) {
                    print0(ucase ? "TEMPORARY " : "temporary ");
                }
                if (queryBlock.isInsertTable()) {
                    print0(ucase ? "TABLE " : "table ");
                }
                into.accept(this);
            }
        } else {
            super.printInto(x);
        }
    }

    @Override
    public boolean visit(RedshiftCreateTableStatement x) {
        printCreateTable(x, false);
        println();

        if (x.getSelect() != null) {
            println();
            print0(ucase ? "AS" : "as");
            println();
            x.getSelect().accept(this);
        }

        if (x.getBackup() != null) {
            print0(ucase ? "BACKUP " : "backup ");
            x.getBackup().accept(this);
            println();
        }
        if (x.getDistStyle() != null) {
            print0(ucase ? "DISTSTYLE " : "diststyle ");
            x.getDistStyle().accept(this);
            println();
        }
        if (x.getDistKey() != null) {
            print0(ucase ? "DISTKEY(" : "distkey(");
            x.getDistKey().accept(this);
            print0(")");
            println();
        }
        if (x.getSortKey() != null) {
            if (x.getSortKey().isCompound()) {
                print0(ucase ? "COMPOUND " : "compound ");
            } else if (x.getSortKey().isInterleaved()) {
                print0(ucase ? "INTERLEAVED " : "interleaved ");
            }

            if (!x.getSortKey().getColumns().isEmpty()) {
                print0(ucase ? "SORTKEY(" : "sortkey(");
                printAndAccept(x.getSortKey().getColumns(), ", ");
                print0(")");
            } else if (x.getSortKey().isAuto()) {
                print0(ucase ? "SORTKEY AUTO" : "sortkey auto");
            }
            println();
        }
        if (x.isEncodeAuto()) {
            print0(ucase ? "ENCODE AUTO" : "encode auto");
        }
        return false;
    }

    @Override
    public boolean visit(RedshiftColumnEncode x) {
        print0(ucase ? "ENCODE " : "encode ");
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public boolean visit(RedshiftColumnKey x) {
        if (x.isDistKey()) {
            print0(ucase ? "DISTKEY" : "distkey");
        }
        if (x.isSortKey()) {
            print0(ucase ? "SORTKEY" : "sortkey");
        }
        return false;
    }

    protected void printGeneratedAlways(SQLColumnDefinition x, boolean parameterized) {
        SQLExpr generatedAlwaysAs = x.getGeneratedAlwaysAs();
        if (generatedAlwaysAs != null) {
            print0(ucase ? " GENERATED BY DEFAULT AS " : " generated by default as ");
            printExpr(generatedAlwaysAs, parameterized);
        }

        SQLColumnDefinition.Identity identity = x.getIdentity();
        if (identity != null) {
            print(' ');
            identity.accept(this);
        }
    }

    @Override
    public boolean visit(SQLColumnDefinition.Identity x) {
        print0(ucase ? "IDENTITY" : "identity");
        Integer seed = x.getSeed();
        if (seed != null) {
            print0(" (");
            print(seed);
            print0(", ");
            print(x.getIncrement());
            print(')');
        }
        return false;
    }
}
