/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.antspark.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.antspark.ast.AntsparkCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInputOutputFormat;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

/**
 *
 * @author peiheng.qph
 * @version $Id: AntsparkCreateTableParser.java, v 0.1 2018年09月14日 15:03 peiheng.qph Exp $
 */
public class AntsparkCreateTableParser extends SQLCreateTableParser {
    public AntsparkCreateTableParser(String sql) {
        super(new AntsparkExprParser(sql));
    }

    public AntsparkCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }
    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        AntsparkCreateTableStatement stmt = new AntsparkCreateTableStatement();

        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                stmt.addBeforeComment(lexer.readAndResetComments());
            }

            accept(Token.CREATE);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            stmt.setExternal(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.TEMPORARY)) {
            lexer.nextToken();
            stmt.setType(SQLCreateTableStatement.Type.TEMPORARY);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals(FnvHash.Constants.IF)) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExiists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (; ; ) {
                Token token = lexer.token();
                if (token == Token.IDENTIFIER //
                        || token == Token.LITERAL_ALIAS) {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    stmt.getTableElementList().add(column);
                } else if (token == Token.PRIMARY //
                        || token == Token.UNIQUE //
                        || token == Token.CHECK //
                        || token == Token.CONSTRAINT
                        || token == Token.FOREIGN) {
                    SQLConstraint constraint = this.exprParser.parseConstaint();
                    constraint.setParent(stmt);
                    stmt.getTableElementList().add((SQLTableElement) constraint);
                } else if (token == Token.TABLESPACE) {
                    throw new ParserException("TODO "  + lexer.info());
                } else {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    stmt.getTableElementList().add(column);
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();

                    if (lexer.token() == Token.RPAREN) { // compatible for sql server
                        break;
                    }
                    continue;
                }

                break;
            }

            accept(Token.RPAREN);
        }
        //add using
        if(lexer.token()== Token.USING){
            lexer.nextToken();
            SQLName expr=this.exprParser.name();
            stmt.setDatasource(expr);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

        if (lexer.identifierEquals(FnvHash.Constants.MAPPED)) {
            lexer.nextToken();
            accept(Token.BY);
            this.exprParser.parseAssignItem(stmt.getMappedBy(), stmt);
        }

        if (lexer.token() == Token.PARTITIONED) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);

            for (;;) {
                if (lexer.token() != Token.IDENTIFIER) {
                    throw new ParserException("expect identifier. " + lexer.info());
                }

                SQLColumnDefinition column = this.exprParser.parseColumn();
                stmt.addPartitionColumn(column);

                if (lexer.isKeepComments() && lexer.hasComment()) {
                    column.addAfterComment(lexer.readAndResetComments());
                }

                if (lexer.token() != Token.COMMA) {
                    break;
                } else {
                    lexer.nextToken();
                    if (lexer.isKeepComments() && lexer.hasComment()) {
                        column.addAfterComment(lexer.readAndResetComments());
                    }
                }
            }

            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);
            for (; ; ) {
                SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                stmt.addClusteredByItem(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.SKEWED)) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);
            this.exprParser.exprList(stmt.getSkewedBy(), stmt);
            accept(Token.RPAREN);
            accept(Token.ON);
            accept(Token.LPAREN);
            for (;;) {
                if (lexer.token() == Token.LPAREN) {
                    SQLListExpr list = new SQLListExpr();
                    lexer.nextToken();
                    this.exprParser.exprList(list.getItems(), list);
                    accept(Token.RPAREN);
                    stmt.addSkewedByOn(list);
                } else {
                    SQLExpr expr = this.exprParser.expr();
                    stmt.addSkewedByOn(expr);
                }
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.SORTED)) {
            parseSortedBy(stmt);
        }

        if (lexer.token() == Token.ROW
                || lexer.identifierEquals(FnvHash.Constants.ROW)) {
            parseRowFormat(stmt);
        }

        if (lexer.identifierEquals(FnvHash.Constants.SORTED)) {
            parseSortedBy(stmt);
        }

        if (stmt.getClusteredBy().size() > 0 || stmt.getSortedBy().size() > 0) {
            accept(Token.INTO);
            if (lexer.token() == Token.LITERAL_INT) {
                stmt.setBuckets(lexer.integerValue().intValue());
                lexer.nextToken();
            } else {
                throw new ParserException("into buckets must be integer. " + lexer.info());
            }
            acceptIdentifier("BUCKETS");
        }

        if (lexer.token() == Token.ROW
                || lexer.identifierEquals(FnvHash.Constants.ROW)) {
            parseRowFormat(stmt);
        }

        if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
            lexer.nextToken();
            accept(Token.AS);

            if (lexer.identifierEquals(FnvHash.Constants.INPUTFORMAT)) {
                HiveInputOutputFormat format = new HiveInputOutputFormat();
                lexer.nextToken();
                format.setInput(this.exprParser.primary());

                if (lexer.identifierEquals(FnvHash.Constants.OUTPUTFORMAT)) {
                    lexer.nextToken();
                    format.setOutput(this.exprParser.primary());
                }
                stmt.setStoredAs(format);
            } else {
                SQLName name = this.exprParser.name();
                stmt.setStoredAs(name);
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOCATION)) {
            lexer.nextToken();
            SQLExpr location = this.exprParser.expr();
            stmt.setLocation(location);
        }

        if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
            lexer.nextToken();
            accept(Token.LPAREN);

            for (;;) {
                String name = lexer.stringVal();
                lexer.nextToken();
                accept(Token.EQ);
                SQLExpr value = this.exprParser.primary();
                stmt.addOption(name, value);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    if (lexer.token() == Token.RPAREN) {
                        break;
                    }
                    continue;
                }
                break;
            }

            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.META)) {
            lexer.nextToken();
            acceptIdentifier("LIFECYCLE");
            stmt.setMetaLifeCycle(this.exprParser.primary());
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();
            SQLSelect select = this.createSQLSelectParser().select();
            stmt.setSelect(select);
        }

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setLike(name);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

        return stmt;
    }

    private void parseRowFormat(AntsparkCreateTableStatement stmt) {
    }

    private void parseSortedBy(AntsparkCreateTableStatement stmt) {
    }
}