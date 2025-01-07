/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.spark.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLShowFunctionsStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.spark.ast.stmt.SparkCacheTableStatement;
import com.alibaba.druid.sql.dialect.spark.ast.stmt.SparkCreateScanStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

/**
 * @author peiheng.qph
 * @version $Id: AntsparkStateMentParser.java, v 0.1 2018年09月14日 15:07 peiheng.qph Exp $
 */
public class SparkStatementParser extends HiveStatementParser {
    public SparkStatementParser(String sql) {
        super(new SparkExprParser(sql));
    }

    public SparkStatementParser(Lexer lexer) {
        super(lexer);
    }

    public SparkStatementParser(SQLExprParser sqlExprParser) {
        super(sqlExprParser);
    }
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new SparkCreateTableParser(this.exprParser);
    }

    protected void alterTableUnset(SQLAlterTableStatement stmt) {
        acceptIdentifier("TBLPROPERTIES");
        accept(Token.LPAREN);
        exprParser.names(stmt.getUnsetTableOptions(), stmt);
        accept(Token.RPAREN);
    }

    public SQLStatement parseCreateScan() {
        SparkCreateScanStatement stmt = new SparkCreateScanStatement();
        accept(Token.CREATE);
        acceptIdentifier("SCAN");
        stmt.setName(exprParser.name());
        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            stmt.setOn(this.exprParser.name());
        }
        if (lexer.token() == Token.USING) {
            lexer.nextToken();
            stmt.setUsing(this.exprParser.expr());
        }
        if (lexer.identifierEquals("OPTIONS")) {
            lexer.nextToken();
            accept(Token.LPAREN);

            for (; ; ) {
                SQLAssignItem item = this.exprParser.parseAssignItem();
                stmt.addOption(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }

    public SparkCacheTableStatement parseCache() {
        accept(Token.CACHE);
        SparkCacheTableStatement stmt = new SparkCacheTableStatement();
        if (lexer.identifierEquals("LAZY")) {
            lexer.nextToken();
            stmt.setLazy(true);
        }
        accept(Token.TABLE);
        stmt.setName(exprParser.name());
        if (lexer.identifierEquals("OPTIONS")) {
            lexer.nextToken();
            accept(Token.LPAREN);

            for (; ; ) {
                SQLAssignItem item = this.exprParser.parseAssignItem();
                stmt.addOption(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
            accept(Token.RPAREN);
        }
        if (lexer.token() == Token.AS) {
            lexer.nextToken();
            stmt.setAs(true);
        }
        if (!lexer.isEOF()) {
            SQLSelectParser selectParser = createSQLSelectParser();
            SQLSelect query = selectParser.select();
            stmt.setQuery(query);
        }

        return stmt;
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.identifierEquals(FnvHash.Constants.SHOW)) {
            Lexer.SavePoint savePoint = this.lexer.mark();
            lexer.nextToken();
            if (lexer.token() == Token.USER
                || lexer.identifierEquals(FnvHash.Constants.SYSTEM)
                || lexer.token() == Token.ALL
                || lexer.identifierEquals(FnvHash.Constants.FUNCTIONS)) {
                SQLShowFunctionsStatement stmt = new SQLShowFunctionsStatement();

                if (!lexer.identifierEquals(FnvHash.Constants.FUNCTIONS)) {
                    SQLName name = this.exprParser.name();
                    stmt.setKind(name);
                }
                if (lexer.identifierEquals(FnvHash.Constants.FUNCTIONS)) {
                    lexer.nextToken();

                    if (lexer.token() == Token.LIKE) {
                        lexer.nextToken();
                        SQLExpr like = this.exprParser.expr();
                        stmt.setLike(like);
                    }
                    statementList.add(stmt);
                    return true;
                }
            }
            lexer.reset(savePoint);
        }
        return super.parseStatementListDialect(statementList);
    }
}
