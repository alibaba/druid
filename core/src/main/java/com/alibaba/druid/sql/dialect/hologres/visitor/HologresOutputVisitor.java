package com.alibaba.druid.sql.dialect.hologres.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.parser.CharTypes;
import com.alibaba.druid.sql.visitor.VisitorFeature;

import java.util.List;

public class HologresOutputVisitor extends PGOutputVisitor {
    public HologresOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, DbType.hologres, parameterized);
    }

    public HologresOutputVisitor(StringBuilder appender) {
        super(appender, DbType.hologres);
    }

    @Override
    protected void printPartitionBy(SQLCreateTableStatement x) {
        SQLPartitionBy partitionBy = x.getPartitioning();
        if (partitionBy == null) {
            return;
        }
        println();
        if (partitionBy.getLogical() != null && partitionBy.getLogical()) {
            print0(ucase ? "LOGICAL " : "logical ");
        }
        print0(ucase ? "PARTITION BY " : "partition by ");
        partitionBy.accept(this);
    }

    @Override
    protected void printFrom(SQLSelectQueryBlock x) {
        SQLTableSource from = x.getFrom();
        if (from == null) {
            return;
        }

        List<String> beforeComments = from.getBeforeCommentsDirect();
        if (beforeComments != null) {
            for (String comment : beforeComments) {
                println();
                print0(comment);
            }
        }

        super.printFrom(x);
        if (x instanceof CKSelectQueryBlock && ((CKSelectQueryBlock) x).isFinal()) {
            print0(ucase ? " FINAL" : " final");
        }
    }

    @Override
    public void printComment(String comment) {
        if (comment == null) {
            return;
        }

        if (isEnabled(VisitorFeature.OutputSkipMultilineComment) && comment.startsWith("/*")) {
            return;
        }

        if (isEnabled(VisitorFeature.OutputSkipSingleLineComment)
                && (comment.startsWith("-") || comment.startsWith("#"))) {
            return;
        }

        if (comment.startsWith("--")
                && comment.length() > 2
                && comment.charAt(2) != ' '
                && comment.charAt(2) != '-') {
            print0("-- ");
            print0(comment.substring(2));
        } else if (comment.startsWith("#")
                && comment.length() > 1
                && comment.charAt(1) != ' '
                && comment.charAt(1) != '#') {
            print0("# ");
            print0(comment.substring(1));
        } else if (comment.startsWith("/*")) {
            println();
            print0(comment);
        } else if (comment.startsWith("--")) {
            print0(comment);
        }

        char first = '\0';
        for (int i = 0; i < comment.length(); i++) {
            char c = comment.charAt(i);
            if (CharTypes.isWhitespace(c)) {
                continue;
            }
            first = c;
            break;
        }

        if (first == '-' || first == '#') {
            endLineComment = true;
        }
    }

    public void printArrayExprPrefix() {
        print0(ucase ? "ARRAY" : "array");
    }
}
