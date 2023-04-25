package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleSelectParser;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.LinkedList;
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

    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        SQLCreateTableStatement createTable = newCreateStatement();
        createTable.setDbType(getDbType());

        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                createTable.addBeforeComment(lexer.readAndResetComments());
            }

            accept(Token.CREATE);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            createTable.setExternal(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.DIMENSION)) {
            lexer.nextToken();
            createTable.setDimension(true);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            createTable.setIfNotExiists(true);
        }

        createTable.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (; ; ) {
                Token token = lexer.token();
                if (lexer.identifierEquals(FnvHash.Constants.SUPPLEMENTAL)
                        && DbType.oracle == dbType) {
                    SQLTableElement element = this.parseCreateTableSupplementalLogingProps();
                    element.setParent(createTable);
                    createTable.getTableElementList().add(element);
                } else if (token == Token.IDENTIFIER //
                        || token == Token.LITERAL_ALIAS) {
                    SQLColumnDefinition column = this.exprParser.parseColumn(createTable);
                    column.setParent(createTable);
                    createTable.getTableElementList().add(column);
                } else if (token == Token.PRIMARY //
                        || token == Token.UNIQUE //
                        || token == Token.CHECK //
                        || token == Token.CONSTRAINT
                        || token == Token.FOREIGN) {
                    SQLConstraint constraint = this.exprParser.parseConstaint();
                    constraint.setParent(createTable);
                    createTable.getTableElementList().add((SQLTableElement) constraint);
                } else if (token == Token.TABLESPACE) {
                    throw new ParserException("TODO " + lexer.info());
                } else {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    createTable.getTableElementList().add(column);
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

            if (lexer.identifierEquals(FnvHash.Constants.INHERITS)) {
                lexer.nextToken();
                accept(Token.LPAREN);
                SQLName inherits = this.exprParser.name();
                createTable.setInherits(new SQLExprTableSource(inherits));
                accept(Token.RPAREN);
            }
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            SQLSelect select = null;
            if (DbType.oracle == dbType) {
                select = new OracleSelectParser(this.exprParser).select();
            } else {
                select = this.createSQLSelectParser().select();
            }
            createTable.setSelect(select);
        }

        if (lexer.token() == Token.WITH && DbType.postgresql == dbType) {
            lexer.nextToken();
            accept(Token.LPAREN);
            parseAssignItems(createTable.getTableOptions(), createTable, false);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.TABLESPACE) {
            lexer.nextToken();
            createTable.setTablespace(
                    this.exprParser.name()
            );
        }

        if (lexer.token() == Token.PARTITION) {
            SQLPartitionBy partitionClause = parsePartitionBy();
            createTable.setPartitioning(partitionClause);
        }

        parseCreateTableRest(createTable);

        return createTable;
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
            accept(Token.KEY);
            SQLIndexDefinition modelKey = new SQLIndexDefinition();
            modelKey.setType(model.getSimpleName());
            modelKey.setKey(true);
            srStmt.setModelKey(modelKey);
            this.exprParser.parseIndexRest(modelKey, srStmt);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            srStmt.setComment(new SQLCharExpr(lexer.stringVal()));
            accept(lexer.token());
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
            srStmt.getStarRocksProperties()
                    .addAll(parseProperties(srStmt));
        }
    }

    protected StarRocksCreateTableStatement newCreateStatement() {
        return new StarRocksCreateTableStatement();
    }

    public List<SQLExpr> parseProperties(SQLObject parent) {
        List<SQLExpr> starRocksProperties = new LinkedList<>();
        SQLArrayExpr arrayExpr;
        for (; ; ) {
            if (lexer.token() == Token.LBRACKET) {
                accept(Token.LBRACKET);
                arrayExpr = new SQLArrayExpr();
                arrayExpr.setParent(parent);
                arrayExpr.getValues().add(
                        this.exprParser.parseAssignItem(true, arrayExpr)
                );
                starRocksProperties.add(arrayExpr);

                if (lexer.token() == Token.COMMA) {
                    accept(Token.COMMA);
                }
                accept(Token.RBRACKET);
            } else {
                starRocksProperties.add(this.exprParser.parseAssignItem(true, parent));
            }

            if (lexer.token() == Token.COMMA) {
                accept(Token.COMMA);
            }

            if (lexer.token() == Token.RPAREN) {
                accept(Token.RPAREN);
                break;
            }
        }

        return starRocksProperties;
    }
}
