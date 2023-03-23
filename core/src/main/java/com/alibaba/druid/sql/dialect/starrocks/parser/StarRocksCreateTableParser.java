package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.expr.StarRocksCharExpr;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StarRocksCreateTableParser extends SQLCreateTableParser {
    public StarRocksCreateTableParser(Lexer lexer) {
        super(new StarRocksExprParser(lexer));
    }

    public StarRocksCreateTableParser(String sql) {
        super(new StarRocksExprParser(sql));
    }

    public StarRocksCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public void parseCreateTableRest(SQLCreateTableStatement stmt) {
        StarRocksCreateTableStatement srStmt = (StarRocksCreateTableStatement) stmt;

        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.setEngine(
                    this.exprParser.expr()
            );
        }

        if (lexer.identifierEquals(FnvHash.Constants.DUPLICATE) || lexer.identifierEquals(FnvHash.Constants.AGGREGATE)
                || lexer.identifierEquals(FnvHash.Constants.UNIQUE) || lexer.identifierEquals(FnvHash.Constants.PRIMARY)) {
            SQLName model = this.exprParser.name();
            srStmt.setModelKey(model);
            accept(Token.KEY);
            this.exprParser.exprList(srStmt.getModelKeyParameters(), srStmt);
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.BY);
            SQLExpr expr = this.exprParser.expr();
            srStmt.setPartitionBy(expr);
            accept(Token.LPAREN);

            if (lexer.token() == Token.PARTITION) {
                for (; ; ) {
                    Map<SQLExpr, SQLExpr> lessThanMap = srStmt.getLessThanMap();
                    Map<SQLExpr, List<SQLExpr>> fixedRangeMap = srStmt.getFixedRangeMap();
                    lexer.nextToken();
                    SQLExpr area = this.exprParser.expr();
                    accept(Token.VALUES);
                    if (lexer.identifierEquals(FnvHash.Constants.LESS)) {
                        srStmt.setLessThan(true);
                        lexer.nextToken();
                        if (lexer.identifierEquals(FnvHash.Constants.THAN)) {
                            lexer.nextToken();
                            SQLExpr value = this.exprParser.expr();
                            lessThanMap.put(area, value);
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                            } else if (lexer.token() == Token.RPAREN) {
                                lexer.nextToken();
                                srStmt.setLessThanMap(lessThanMap);
                                break;
                            }
                        }
                    } else if (lexer.token() == Token.LBRACKET) {
                        lexer.nextToken();
                        srStmt.setFixedRange(true);
                        List<SQLExpr> valueList = new ArrayList<>();

                        for (; ; ) {
                            SQLExpr value = this.exprParser.expr();
                            valueList.add(value);
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                            } else if (lexer.token() == Token.RPAREN) {
                                lexer.nextToken();
                                fixedRangeMap.put(area, valueList);
                                break;
                            }
                        }

                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                        } else if (lexer.token() == Token.RPAREN) {
                            lexer.nextToken();
                            srStmt.setFixedRangeMap(fixedRangeMap);
                            break;
                        }
                    }
                }
            } else if (lexer.identifierEquals(FnvHash.Constants.START)) {
                srStmt.setStartEnd(true);
                lexer.nextToken();
                SQLExpr start = this.exprParser.expr();
                srStmt.setStart(start);
                accept(Token.END);

                SQLExpr end = this.exprParser.expr();
                srStmt.setEnd(end);

                if (lexer.identifierEquals(FnvHash.Constants.EVERY)) {
                    lexer.nextToken();
                    SQLExpr every = this.exprParser.expr();
                    srStmt.setEvery(every);
                    accept(Token.RPAREN);
                }
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.DISTRIBUTED)) {
            lexer.nextToken();
            accept(Token.BY);
            SQLExpr hash = this.exprParser.expr();
            srStmt.setDistributedBy(hash);
            if (lexer.identifierEquals(FnvHash.Constants.BUCKETS)) {
                lexer.nextToken();
                int bucket = lexer.integerValue().intValue();
                stmt.setBuckets(bucket);
                lexer.nextToken();
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.PROPERTIES)) {
            lexer.nextToken();
            accept(Token.LPAREN);
            Map<SQLCharExpr, SQLCharExpr> properties = srStmt.getPropertiesMap();
            Map<SQLCharExpr, SQLCharExpr> lBracketProperties = srStmt.getlBracketPropertiesMap();
            for (; ; ) {
                if (lexer.token() == Token.LBRACKET) {
                    lexer.nextToken();
                    parseProperties(lBracketProperties);
                } else {
                    parseProperties(properties);
                }
                lexer.nextToken();
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
                if (lexer.token() == Token.RBRACKET) {
                    lexer.nextToken();
                }
                if (lexer.token() == Token.RPAREN) {
                    lexer.nextToken();
                    srStmt.setPropertiesMap(properties);
                    srStmt.setlBracketPropertiesMap(lBracketProperties);
                    break;
                }
            }
        }
    }

    private void parseProperties(Map<SQLCharExpr, SQLCharExpr> propertiesType) {
        String keyText = lexer.stringVal();
        SQLCharExpr key = new StarRocksCharExpr(keyText);
        lexer.nextToken();
        accept(Token.EQ);
        String valueText = lexer.stringVal();
        SQLCharExpr value = new StarRocksCharExpr(valueText);
        propertiesType.put(key, value);
    }

    protected StarRocksCreateTableStatement newCreateStatement() {
        return new StarRocksCreateTableStatement();
    }

}
