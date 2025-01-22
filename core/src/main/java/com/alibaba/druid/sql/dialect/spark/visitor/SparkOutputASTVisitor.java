/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.spark.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;
import com.alibaba.druid.sql.dialect.spark.ast.SparkCreateTableStatement;
import com.alibaba.druid.sql.dialect.spark.ast.stmt.SparkCacheTableStatement;
import com.alibaba.druid.sql.dialect.spark.ast.stmt.SparkCreateScanStatement;
import com.alibaba.druid.sql.visitor.ExportParameterVisitorUtils;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author peiheng.qph
 * @version $Id: SparkOutputVisitor.java, v 0.1 2018年09月17日 10:40 peiheng.qph Exp $
 */
public class SparkOutputASTVisitor extends HiveOutputVisitor implements SparkASTVisitor {
    public SparkOutputASTVisitor(StringBuilder appender, DbType dbType) {
        super(appender, dbType);
    }

    public SparkOutputASTVisitor(StringBuilder appender) {
        super(appender);
    }

    //add using statment

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        if (x instanceof SparkCreateTableStatement) {
            return visit((SparkCreateTableStatement) x);
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(SparkCreateTableStatement x) {
        print0(ucase ? "CREATE " : "create ");

        printCreateTableFeatures(x);

        if (x.isIfNotExists()) {
            print0(ucase ? "TABLE IF NOT EXISTS " : "table if not exists ");
        } else {
            print0(ucase ? "TABLE " : "table ");
        }

        x.getName().accept(this);
        printCreateTableLike(x);
        printTableElementsWithComment(x);
        printUsing(x);

        printComment(x.getComment());
        printPartitionedBy(x);
        printClusteredBy(x);
        printSortedBy(x.getSortedBy());
        printIntoBuckets(x.getBuckets());
        printStoredAs(x);
        printSelectAs(x, true);
        printTableOptions(x);
        printLocation(x);
        return false;
    }

    protected void printUsing(SparkCreateTableStatement x) {
        if (x.getDatasource() != null) {
            println();
            print0(ucase ? "USING " : "using ");
            print0(x.getDatasource().toString());
        }
    }

    protected void printTableOptions(SparkCreateTableStatement x) {
        Map<String, SQLObject> serdeProperties = x.getSerdeProperties();
        if (serdeProperties.size() > 0) {
            println();
            print0(ucase ? "TBLPROPERTIES (" : "tblproperties (");
            String seperator = "";
            for (Entry<String, SQLObject> entry : serdeProperties.entrySet()) {
                print0("'" + entry.getKey() + "'='");
                entry.getValue().accept(this);
                print0("'" + seperator);
                seperator = ",";
            }
            print(')');
        }
    }

    @Override
    public boolean visit(SQLHexExpr x) {
        if (this.parameterized) {
            print('?');
            incrementReplaceCunt();

            if (this.parameters != null) {
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return false;
        }

        print0("x'");
        print0(x.getHex());
        print('\'');
        return false;
    }

    public boolean visit(SparkCreateScanStatement x) {
        print0(ucase ? "CREATE " : "create ");
        print0(ucase ? "SCAN " : "scan ");

        x.getName().accept(this);
        if (x.getOn() != null) {
            print0(ucase ? " ON " : " on ");
            x.getOn().accept(this);
        }

        SQLExpr using = x.getUsing();
        if (using != null) {
            println();
            print0(ucase ? "USING " : "using ");
            printExpr(using);
        }

        if (x.getOptions().size() > 0) {
            print0(ucase ? " OPTIONS (" : " options (");
            printAndAccept(x.getOptions(), ", ");
            print(')');
        }

        return false;
    }

    public boolean visit(SparkCacheTableStatement x) {
        print0(ucase ? "CACHE " : "cache ");
        if (x.isLazy()) {
            print0(ucase ? " LAZY " : " lazy ");
        }
        print0(ucase ? "TABLE " : "table ");
        x.getName().accept(this);

        if (x.getOptions().size() > 0) {
            print0(ucase ? " OPTIONS (" : " options (");
            printAndAccept(x.getOptions(), ", ");
            print(')');
        }

        if (x.isAs()) {
            print0(ucase ? " AS " : " as ");
        }

        SQLSelect query = x.getQuery();
        if (query != null) {
            print(' ');
            query.accept(this);
        }

        return false;
    }
}
