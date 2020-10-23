/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.antspark.parser;

import com.alibaba.druid.sql.ast.SQLCurrentTimeExpr;
import com.alibaba.druid.sql.ast.SQLCurrentUserExpr;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

/**
 *
 * @author peiheng.qph
 * @version $Id: AntsparkExprParser.java, v 0.1 2018年09月14日 15:04 peiheng.qph Exp $
 */
public class AntsparkExprParser  extends SQLExprParser {
    private final static String[] AGGREGATE_FUNCTIONS;
    private final static long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER",
                "ROWNUMBER" };

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public AntsparkExprParser(String sql){
        this(new AntsparkLexer(sql));
        this.lexer.nextToken();
    }

    public AntsparkExprParser(Lexer lexer){
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        //        if(lexer.token() == Token.COLON) {
        //            lexer.nextToken();
        //            expr = dotRest(expr);
        //            return expr;
        //        }

        if (lexer.token() == Token.LBRACKET) {
            SQLArrayExpr array = new SQLArrayExpr();
            array.setExpr(expr);
            lexer.nextToken();
            this.exprList(array.getValues(), array);
            accept(Token.RBRACKET);
            return primaryRest(array);
        }

        return super.primaryRest(expr);
    }

    public SQLExpr primary() {
        final Token tok = lexer.token();
        switch (tok) {
            case IDENTIFIER:
                final long hash_lower = lexer.hash_lower();
                if (hash_lower == FnvHash.Constants.OUTLINE) {
                    lexer.nextToken();
                    SQLExpr file = primary();
                    SQLExpr expr = new MySqlOutFileExpr(file);

                    return primaryRest(expr);
                }

                SQLCurrentTimeExpr currentTimeExpr = null;
                if (hash_lower == FnvHash.Constants.CURRENT_TIMESTAMP) {
                    currentTimeExpr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.CURRENT_TIMESTAMP);
                } else if (hash_lower == FnvHash.Constants.CURRENT_DATE) {
                    currentTimeExpr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.CURRENT_DATE);
                } else if (hash_lower == FnvHash.Constants.CURRENT_USER && isEnabled(SQLParserFeature.EnableCurrentUserExpr)) {
                    lexer.nextToken();
                    return primaryRest(new SQLCurrentUserExpr());
                }

                if (currentTimeExpr != null) {
                    String methodName = lexer.stringVal();
                    lexer.nextToken();

                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        if (lexer.token() == Token.LPAREN) {
                            lexer.nextToken();
                        } else {
                            return primaryRest(
                                    methodRest(new SQLIdentifierExpr(methodName), false)
                            );
                        }
                    }

                    return primaryRest(currentTimeExpr);
                }
                break;
            default:
                break;
        }

        return super.primary();
    }

    public SQLExternalRecordFormat parseRowFormat() {
        lexer.nextToken();
        acceptIdentifier("FORMAT");

        if (lexer.identifierEquals(FnvHash.Constants.DELIMITED)) {
            lexer.nextToken();
        }

        SQLExternalRecordFormat format = new SQLExternalRecordFormat();

        if (lexer.identifierEquals(FnvHash.Constants.FIELDS)) {
            lexer.nextToken();
            acceptIdentifier("TERMINATED");
            accept(Token.BY);

            format.setTerminatedBy(this.expr());
        } else if (lexer.identifierEquals("FIELD")) {
            throw new ParserException("syntax error, expect FIELDS, " + lexer.info());
        }

        if (lexer.identifierEquals(FnvHash.Constants.LINES)) {
            lexer.nextToken();
            acceptIdentifier("TERMINATED");
            accept(Token.BY);

            format.setLinesTerminatedBy(this.expr());
        }

        if (lexer.token() == Token.ESCAPE || lexer.identifierEquals(FnvHash.Constants.ESCAPED)) {
            lexer.nextToken();
            accept(Token.BY);
            format.setEscapedBy(this.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.COLLECTION)) {
            lexer.nextToken();
            acceptIdentifier("ITEMS");
            acceptIdentifier("TERMINATED");
            accept(Token.BY);
            format.setCollectionItemsTerminatedBy(this.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.MAP)) {
            lexer.nextToken();
            acceptIdentifier("KEYS");
            acceptIdentifier("TERMINATED");
            accept(Token.BY);
            format.setMapKeysTerminatedBy(this.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.SERDE)) {
            lexer.nextToken();
            format.setSerde(this.expr());
        }

        return format;
    }

    protected SQLExpr parseAliasExpr(String alias) {
        String chars = alias.substring(1, alias.length() - 1);
        return new SQLCharExpr(chars);
    }
    protected SQLExpr parseDatasource(String alias) {
        String chars = alias.substring(1, alias.length() - 1);
        return new SQLCharExpr(chars);
    }
    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.identifierEquals(FnvHash.Constants.MAPPED)) {
            lexer.nextToken();
            accept(Token.BY);
            this.parseAssignItem(column.getMappedBy(), column);
        }

        if (lexer.identifierEquals(FnvHash.Constants.COLPROPERTIES)) {
            lexer.nextToken();
            this.parseAssignItem(column.getColProperties(), column);
        }


        return super.parseColumnRest(column);
    }


}