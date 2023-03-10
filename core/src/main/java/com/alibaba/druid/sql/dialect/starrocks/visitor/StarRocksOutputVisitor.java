package com.alibaba.druid.sql.dialect.starrocks.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class StarRocksOutputVisitor extends SQLASTOutputVisitor implements StarRocksASTVisitor{

    {
        this.dbType = DbType.starrocks;
        this.shardingSupport = true;
        this.quote = '`';
    }


    public StarRocksOutputVisitor(Appendable appender) {
        super(appender);
    }

    public StarRocksOutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public StarRocksOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }




    public boolean visit(StarRocksCreateTableStatement x) {
        super.visit((SQLCreateTableStatement) x);

        SQLName model = x.getModelKey();
        if (model != null) {
            println();
            String modelName = model.getSimpleName().toLowerCase();
            switch (modelName) {
                case "duplicate" :
                    print0(ucase ? "DUPLICATE" : "duplicate");
                    break;
                case "aggregate" :
                    print0(ucase ? "AGGREGATE" : "aggregate");
                    break;
                case "unique" :
                    print0(ucase ? "UNIQUE" : "unique");
                    break;
                case "primary" :
                    print0(ucase ? "PRIMARY" : "primary");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported data model type ");
            }
            print(' ');
            print0(ucase ? "KEY" : "key");
            if (x.getParameters().size() > 0) {
                for (int i = 0; i < x.getParameters().size(); ++i) {
                    if (i != 0) {
                        println(", ");
                    }
                    x.getParameters().get(i).accept(this);
                }
            }
        }

        SQLExpr partitionBy = x.getPartitionBy();
        if (partitionBy != null) {
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            partitionBy.accept(this);
        }

        return false;
    }
















}
