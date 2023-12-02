package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByHash;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.ast.SQLPartitionByRange;
import com.alibaba.druid.sql.ast.SQLPartitionOf;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

public class PGCreateTableParser extends SQLCreateTableParser {
    public PGCreateTableParser(Lexer lexer) {
        super(new PGExprParser(lexer));
    }

    public PGCreateTableParser(String sql) {
        super(new PGExprParser(sql));
    }

    public PGCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLPartitionBy parsePartitionBy() {
        lexer.nextToken();
        accept(Token.BY);

        if (lexer.identifierEquals("LIST")) {
            lexer.nextToken();
            SQLPartitionByList list = new SQLPartitionByList();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                list.addColumn(this.exprParser.expr());
                accept(Token.RPAREN);
            } else {
                acceptIdentifier("COLUMNS");
                accept(Token.LPAREN);
                for (; ; ) {
                    list.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }

            return list;
        } else if (lexer.identifierEquals("HASH") || lexer.identifierEquals("UNI_HASH")) {
            SQLPartitionByHash hash = new SQLPartitionByHash();

            if (lexer.identifierEquals("UNI_HASH")) {
                hash.setUnique(true);
            }

            lexer.nextToken();

            if (lexer.token() == Token.KEY) {
                lexer.nextToken();
                hash.setKey(true);
            }

            accept(Token.LPAREN);
            this.exprParser.exprList(hash.getColumns(), hash);
            accept(Token.RPAREN);
            return hash;
        } else if (lexer.token() == Token.IDENTIFIER) {
            SQLPartitionByRange clause = partitionByRange();
            return clause;
        }

        throw new ParserException("TODO " + lexer.info());
    }
    protected SQLPartitionByRange partitionByRange() {
        SQLPartitionByRange clause = new SQLPartitionByRange();
        if (lexer.identifierEquals(FnvHash.Constants.RANGE)) {
            lexer.nextToken();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                clause.addColumn(this.exprParser.expr());
                accept(Token.RPAREN);
            } else {
                acceptIdentifier("COLUMNS");
                accept(Token.LPAREN);
                for (; ; ) {
                    clause.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }
        } else {
            SQLExpr expr = this.exprParser.expr();
            if (lexer.identifierEquals(FnvHash.Constants.STARTWITH)) {
                lexer.nextToken();
                SQLExpr start = this.exprParser.primary();
                acceptIdentifier("ENDWITH");
                SQLExpr end = this.exprParser.primary();
                expr = new SQLBetweenExpr(expr, start, end);
            }
            clause.setInterval(expr);
        }

        return clause;
    }

    public SQLPartitionOf parsePartitionOf() {
        lexer.nextToken();
        accept(Token.OF);
        SQLPartitionOf partitionOf = new SQLPartitionOf();
        SQLName tableNameTmp = this.exprParser.name();
        SQLExprTableSource sqlExprTableSource = new SQLExprTableSource(tableNameTmp);
        partitionOf.setParentTable(sqlExprTableSource);
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            if (lexer.token() == Token.CONSTRAINT) {
                lexer.nextToken();
                SQLName constraintName = this.exprParser.name();
                partitionOf.setConstraintName(constraintName);
                accept(Token.CHECK);
                SQLExpr checkExpr = this.exprParser.expr();
                partitionOf.setCheckExpr(checkExpr);
            } else {
                SQLName columnName = this.exprParser.name();
                partitionOf.setColumnName(columnName);
                if (lexer.token() == Token.DEFAULT) {
                    accept(Token.DEFAULT);
                    SQLExpr defaultExpr = this.exprParser.primary();
                    partitionOf.setDefaultExpr(defaultExpr);
                }
            }
            accept(Token.RPAREN);
        }
        if (lexer.token() == Token.DEFAULT) {
            accept(Token.DEFAULT);
            partitionOf.setUseDefault(true);
            return partitionOf;
        }
        accept(Token.FOR);
        accept(Token.VALUES);
        if (lexer.token() == Token.FROM) {
            accept(Token.FROM);
            accept(Token.LPAREN);
            List<SQLExpr> sqlExprBetweens = new ArrayList<>();
            this.exprParser.exprList(sqlExprBetweens, partitionOf);
            partitionOf.setForValuesFrom(sqlExprBetweens);
            accept(Token.RPAREN);
            accept(Token.TO);
            accept(Token.LPAREN);
            List<SQLExpr> sqlExprAnds = new ArrayList<>();
            this.exprParser.exprList(sqlExprAnds, partitionOf);
            partitionOf.setForValuesTo(sqlExprAnds);
            accept(Token.RPAREN);
            return partitionOf;
        } else if (lexer.token() == Token.IN) {
            accept(Token.IN);
            accept(Token.LPAREN);
            List<SQLExpr> sqlExprBetweens = new ArrayList<>();
            this.exprParser.exprList(sqlExprBetweens, partitionOf);
            partitionOf.setForValuesIn(sqlExprBetweens);
            accept(Token.RPAREN);
            return partitionOf;
        }
        if (lexer.token() == Token.WITH) {
            accept(Token.WITH);
            accept(Token.LPAREN);
            acceptIdentifier("MODULUS");
            SQLExpr modulus = this.exprParser.primary();
            partitionOf.setForValuesModulus(modulus);
            accept(Token.COMMA);
            acceptIdentifier("REMAINDER");
            SQLExpr remainder = (SQLIntegerExpr) this.exprParser.primary();
            partitionOf.setForValuesRemainder(remainder);
            accept(Token.RPAREN);
            return partitionOf;
        } else {
            throw new ParserException("TODO " + lexer.info());
        }
    }
}
