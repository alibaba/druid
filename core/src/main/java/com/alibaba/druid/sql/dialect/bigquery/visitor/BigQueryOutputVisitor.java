package com.alibaba.druid.sql.dialect.bigquery.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.bigquery.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

public class BigQueryOutputVisitor extends SQLASTOutputVisitor
        implements BigQueryVisitor {
    public BigQueryOutputVisitor(StringBuilder appender) {
        super(appender, DbType.db2);
    }

    public BigQueryOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        this.dbType = DbType.bigquery;
    }

    protected void printPartitionedBy(SQLCreateTableStatement x) {
        List<SQLExpr> partitionBy;
        if (!(x instanceof BigQueryCreateTableStatement)) {
            return;
        } else {
            partitionBy = ((BigQueryCreateTableStatement) x).getPartitionBy();
        }
        if (partitionBy.isEmpty()) {
            return;
        }
        println();
        print0(ucase ? "PARTITION BY " : "partition by ");
        printAndAccept(((BigQueryCreateTableStatement) x).getPartitionBy(), ",");
    }

    protected void printPartitionedByColumn(SQLColumnDefinition column) {
        String function = (String) column.getName().getAttribute("function");
        if (function != null) {
            print0(function);
            print('(');
        }
        column.accept(this);
        if (function != null) {
            print(')');
        }
    }

    protected void printCreateTableLike(SQLCreateTableStatement x) {
        SQLExprTableSource like = x.getLike();
        if (like == null) {
            return;
        }
        println();
        print0(ucase ? "CLONE " : "clone ");
        like.accept(this);
    }

    public boolean visit(BigQuerySelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    protected void printSelectListBefore(SQLSelectQueryBlock x) {
        if (x instanceof BigQuerySelectQueryBlock) {
            printSelectListBefore((BigQuerySelectQueryBlock) x);
            return;
        }

        super.printSelectListBefore(x);
    }

    protected void printSelectListBefore(BigQuerySelectQueryBlock x) {
        BigQuerySelectQueryBlock.DifferentialPrivacy privacy = x.getDifferentialPrivacy();
        if (privacy != null) {
            incrementIndent();
            println();
            privacy.accept(this);
            decrementIndent();
        } else {
            print(' ');
        }
    }

    public boolean visit(BigQuerySelectQueryBlock.DifferentialPrivacy x) {
        print0(ucase ? "WITH DIFFERENTIAL_PRIVACY" : "with differential_privacy");
        println();
        print0(ucase ? "OPTIONS (" : "options (");
        printAndAccept(x.getOptions(), ",");
        print(')');
        println();
        return false;
    }

    protected void printColumnProperties(SQLColumnDefinition x) {
        List<SQLAssignItem> colProperties = x.getColPropertiesDirect();
        if (colProperties == null || colProperties.isEmpty()) {
            return;
        }
        print0(ucase ? " OPTIONS (" : " options (");
        printAndAccept(colProperties, ", ");
        print0(ucase ? ")" : ")");
    }

    @Override
    public boolean visit(SQLStructDataType.Field x) {
        SQLName name = x.getName();
        if (name != null) {
            name.accept(this);
        }
        SQLDataType dataType = x.getDataType();

        if (dataType != null) {
            print(' ');
            dataType.accept(this);
        }

        if (!x.getConstraints().isEmpty()) {
            print(' ');
            printAndAccept(x.getConstraints(), ",");
        }

        if (!x.getOptions().isEmpty()) {
            print(' ');
            print0(ucase ? "OPTIONS (" : "options (");
            printAndAccept(x.getOptions(), ",");
            print0(ucase ? ")" : ")");
        }
        return false;
    }

    protected void printClusteredBy(SQLCreateTableStatement x) {
        List<SQLSelectOrderByItem> clusteredBy = x.getClusteredBy();
        if (clusteredBy.isEmpty()) {
            return;
        }
        println();
        print0(ucase ? "CLUSTER BY " : "cluster by ");
        printAndAccept(clusteredBy, ",");
    }

    @Override
    protected void printCreateFunctionBody(SQLCreateFunctionStatement x) {
        printCreateFunctionReturns(x);

        String language = x.getLanguage();
        if (language != null) {
            println();
            print0(ucase ? "LANGUAGE " : "language ");
            print0(language);
        }
        List<SQLAssignItem> options = x.getOptions();
        if (!options.isEmpty()) {
            println();
            print0(ucase ? "OPTIONS (" : "options (");
            printAndAccept(options, ",");
            print(')');
        }

        String wrappedSource = x.getWrappedSource();
        if (wrappedSource != null) {
            println();
            print0("AS \"\"\"");
            print0(wrappedSource);
            print0("\"\"\"");
        } else {
            SQLStatement block = x.getBlock();
            if (block != null) {
                println();
                print0(ucase ? "AS (" : "as (");
                block.accept(this);
                print(')');
            }
        }
    }

    protected void printCreateFunctionReturns(SQLCreateFunctionStatement x) {
        SQLDataType returnDataType = x.getReturnDataType();
        if (returnDataType == null) {
            return;
        }
        println();
        print(ucase ? "RETURNS " : "returns ");
        returnDataType.accept(this);
    }

    public boolean visit(BigQuerySelectAsStruct x) {
        print0(ucase ? "SELECT AS STRUCT " : "select as struct ");
        printlnAndAccept(x.getItems(), ", ");
        printFrom(x);
        return false;
    }

    protected void printFrom(BigQuerySelectAsStruct x) {
        SQLTableSource from = x.getFrom();
        if (from == null) {
            return;
        }

        println();
        print(ucase ? "FROM " : "from ");
        printTableSource(from);
    }

    protected void printFetchFirst(SQLSelectQueryBlock x) {
        SQLLimit limit = x.getLimit();
        if (limit == null) {
            return;
        }
        println();
        limit.accept(this);
    }

    protected void printLifeCycle(SQLExpr lifeCycle) {
        if (lifeCycle == null) {
            return;
        }
        println();
        print0(ucase ? "LIFECYCLE = " : "lifecycle = ");
        lifeCycle.accept(this);
    }

    public boolean visit(BigQueryAssertStatement x) {
        print0(ucase ? "ASSERT " : "assert ");
        x.getExpr().accept(this);
        SQLCharExpr as = x.getAs();
        if (as != null) {
            println();
            print0(ucase ? "AS " : "as ");
            as.accept(this);
        }
        return false;
    }

    public boolean visit(SQLCastExpr x) {
        tryPrintLparen(x);
        if (x.isTry()) {
            print0(ucase ? "SAFE_CAST(" : "safe_cast(");
        } else {
            print0(ucase ? "CAST(" : "cast(");
        }
        x.getExpr().accept(this);
        print0(ucase ? " AS " : " as ");
        x.getDataType().accept(this);
        print0(")");
        tryPrintRparen(x);
        return false;
    }

    protected void printTableOption(SQLExpr name, SQLExpr value, int index) {
        if (index != 0) {
            print(",");
            println();
        }
        String key = name.toString();
        print0(key);
        print0(" = ");
        value.accept(this);
    }

    @Override
    public boolean visit(SQLTimestampExpr x) {
        print0(ucase ? "TIMESTAMP " : "timestamp ");
        print0(x.getLiteral());
        return false;
    }

    protected void printCollate(SQLCreateTableStatement x) {
        if (x instanceof BigQueryCreateTableStatement) {
            BigQueryCreateTableStatement bigQueryCreateTableStatement = (BigQueryCreateTableStatement) x;
            if (bigQueryCreateTableStatement.getCollate() != null) {
                println();
                print0(ucase ? "DEFAULT COLLATE " : "default collate ");
                bigQueryCreateTableStatement.getCollate().accept(this);
            }
        }
    }

    @Override
    public boolean visit(BigQueryDateTimeExpr x) {
        x.getExpr().accept(this);
        SQLExpr timeZone = x.getTimeZone();
        print0(ucase ? " AT TIME ZONE " : " at time zone ");
        timeZone.accept(this);
        return false;
    }
}
