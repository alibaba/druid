package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByList;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByValue;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksIndexDefinition;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

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

    @Override
    public StarRocksExprParser getExprParser() {
        return (StarRocksExprParser) exprParser;
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
                    if (lexer.token() == Token.INDEX) {
                        StarRocksIndexDefinition index = new StarRocksIndexDefinition();
                        lexer.nextToken();
                        index.setIndexName(this.exprParser.name());
                        accept(Token.LPAREN);
                        for (; ; ) {
                            index.getColumns().add(this.exprParser.name());
                            if (!(lexer.token() == (Token.COMMA))) {
                                break;
                            } else {
                                lexer.nextToken();
                            }
                        }
                        accept(Token.RPAREN);
                        if (lexer.token() == Token.USING) {
                            lexer.nextToken();
                            accept(Token.BITMAP);
                            index.setUsingBitmap(true);
                        }
                        if (lexer.token() == Token.COMMENT) {
                            lexer.nextToken();
                            index.setComment(StringUtils.removeNameQuotes(lexer.stringVal()));
                            lexer.nextToken();
                        }
                        index.setParent(createTable);
                        createTable.getTableElementList().add(index);
                    } else {
                        SQLColumnDefinition column = this.exprParser.parseColumn();
                        createTable.getTableElementList().add(column);
                    }
                }

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
            SQLSelect select = this.createSQLSelectParser().select();
            createTable.setSelect(select);
        }

        if (lexer.token() == Token.WITH) {
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
        for (; ; ) {
            if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.setEngine(
                        this.exprParser.expr()
                );
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.DUPLICATE) || lexer.identifierEquals(FnvHash.Constants.AGGREGATE)
                    || lexer.token() == Token.UNIQUE || lexer.token() == Token.PRIMARY) {
                SQLName model = this.exprParser.name();
                accept(Token.KEY);
                SQLIndexDefinition modelKey = new SQLIndexDefinition();
                modelKey.setType(model.getSimpleName());
                modelKey.setKey(true);
                srStmt.setModelKey(modelKey);
                this.exprParser.parseIndexRest(modelKey, srStmt);
                continue;
            }

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                srStmt.setComment(new SQLCharExpr(StringUtils.removeNameQuotes(lexer.stringVal())));
                accept(lexer.token());
                continue;
            }

            if (lexer.token() == Token.PARTITION) {
                SQLPartitionBy clause = parsePartitionBy();
                srStmt.setPartitioning(clause);
                continue;
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
                continue;
            }

            if (lexer.token() == Token.ORDER) {
                parseOrderBy(stmt);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.PROPERTIES)) {
                lexer.nextToken();
                accept(Token.LPAREN);
                srStmt.getStarRocksProperties()
                        .addAll(parseProperties(srStmt));
                continue;
            }

            break;
        }
    }

    public SQLPartitionBy parsePartitionBy() {
        lexer.nextToken();
        accept(Token.BY);

        SQLPartitionBy partitionClause;

        boolean linera = false;
        if (lexer.identifierEquals(FnvHash.Constants.LINEAR)) {
            lexer.nextToken();
            linera = true;
        }

        if (lexer.token() == Token.KEY) {
            MySqlPartitionByKey clause = new MySqlPartitionByKey();
            lexer.nextToken();

            if (linera) {
                clause.setLinear(true);
            }

            if (lexer.identifierEquals(FnvHash.Constants.ALGORITHM)) {
                lexer.nextToken();
                accept(Token.EQ);
                clause.setAlgorithm(lexer.integerValue().shortValue());
                lexer.nextToken();
            }

            accept(Token.LPAREN);
            if (lexer.token() != Token.RPAREN) {
                for (; ; ) {
                    clause.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
            }
            accept(Token.RPAREN);

            partitionClause = clause;

            partitionClauseRest(clause);
        } else if (lexer.identifierEquals("HASH") || lexer.identifierEquals("UNI_HASH")) {
            SQLPartitionByHash clause = new SQLPartitionByHash();

            if (lexer.identifierEquals("UNI_HASH")) {
                clause.setUnique(true);
            }

            lexer.nextToken();

            if (linera) {
                clause.setLinear(true);
            }

            if (lexer.token() == Token.KEY) {
                lexer.nextToken();
                clause.setKey(true);
            }

            accept(Token.LPAREN);
            this.exprParser.exprList(clause.getColumns(), clause);
            accept(Token.RPAREN);
            partitionClause = clause;

            partitionClauseRest(clause);

        } else if (lexer.identifierEquals("RANGE")) {
            SQLPartitionByRange clause = partitionByRange();
            partitionClause = clause;

            partitionClauseRest(clause);

        } else if (lexer.identifierEquals("VALUE")) {
            SQLPartitionByValue clause = partitionByValue();
            partitionClause = clause;

            partitionClauseRest(clause);

        } else if (lexer.identifierEquals("LIST")) {
            lexer.nextToken();
            SQLPartitionByList clause = new SQLPartitionByList();

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
            partitionClause = clause;

            partitionClauseRest(clause);
        } else if (lexer.token() == Token.IDENTIFIER) {
            SQLPartitionByRange clause = partitionByRange();
            partitionClause = clause;

            partitionClauseRest(clause);
        } else {
            throw new ParserException("TODO. " + lexer.info());
        }

        if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
            lexer.nextToken();
            partitionClause.setLifeCycle((SQLIntegerExpr) exprParser.expr());
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            for (; ; ) {
                SQLPartition partitionDef = this.getExprParser()
                    .parsePartition();

                partitionClause.addPartition(partitionDef);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }
            accept(Token.RPAREN);
        }
        return partitionClause;
    }

    protected void partitionClauseRest(SQLPartitionBy clause) {
        if (lexer.identifierEquals(FnvHash.Constants.PARTITIONS)
            || lexer.identifierEquals(FnvHash.Constants.TBPARTITIONS)
            || lexer.identifierEquals(FnvHash.Constants.DBPARTITIONS)) {
            lexer.nextToken();
            SQLIntegerExpr countExpr = this.exprParser.integerExpr();
            clause.setPartitionsCount(countExpr);
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();

            if (lexer.identifierEquals("NUM")) {
                lexer.nextToken();
            }

            clause.setPartitionsCount(this.exprParser.expr());

            clause.putAttribute("ads.partition", Boolean.TRUE);
        }

        if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
            lexer.nextToken();
            clause.setLifeCycle((SQLIntegerExpr) exprParser.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.SUBPARTITION)) {
            lexer.nextToken();
            accept(Token.BY);

            SQLSubPartitionBy subPartitionByClause = null;

            boolean linear = false;
            if (lexer.identifierEquals("LINEAR")) {
                lexer.nextToken();
                linear = true;
            }

            if (lexer.token() == Token.KEY) {
                MySqlSubPartitionByKey subPartitionKey = new MySqlSubPartitionByKey();
                lexer.nextToken();

                if (linear) {
                    clause.setLinear(true);
                }

                if (lexer.identifierEquals(FnvHash.Constants.ALGORITHM)) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    subPartitionKey.setAlgorithm(lexer.integerValue().shortValue());
                    lexer.nextToken();
                }

                accept(Token.LPAREN);
                for (; ; ) {
                    subPartitionKey.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);

                subPartitionByClause = subPartitionKey;

            } else if (lexer.identifierEquals("VALUE")) {
                MySqlSubPartitionByValue subPartitionByValue = new MySqlSubPartitionByValue();
                lexer.nextToken();
                accept(Token.LPAREN);
                for (; ; ) {
                    subPartitionByValue.addColumn(this.exprParser.expr());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);

                subPartitionByClause = subPartitionByValue;

            } else if (lexer.identifierEquals("HASH")) {
                lexer.nextToken();
                SQLSubPartitionByHash subPartitionHash = new SQLSubPartitionByHash();

                if (linear) {
                    clause.setLinear(true);
                }

                if (lexer.token() == Token.KEY) {
                    lexer.nextToken();
                    subPartitionHash.setKey(true);
                }

                accept(Token.LPAREN);
                subPartitionHash.setExpr(this.exprParser.expr());
                accept(Token.RPAREN);
                subPartitionByClause = subPartitionHash;

            } else if (lexer.identifierEquals("LIST")) {
                lexer.nextToken();
                MySqlSubPartitionByList subPartitionList = new MySqlSubPartitionByList();

                //for ads
                if (lexer.token() == Token.KEY) {
                    lexer.nextToken();
                    accept(Token.LPAREN);

                    for (; ; ) {
                        SQLExpr expr = this.exprParser.expr();

                        if (expr instanceof SQLIdentifierExpr
                            && (lexer.identifierEquals("bigint") || lexer.identifierEquals("long"))) {
                            String dataType = lexer.stringVal();
                            lexer.nextToken();

                            SQLColumnDefinition column = this.exprParser.createColumnDefinition();
                            column.setName((SQLIdentifierExpr) expr);
                            column.setDataType(new SQLDataTypeImpl(dataType));
                            subPartitionList.addColumn(column);

                            subPartitionList.putAttribute("ads.subPartitionList", Boolean.TRUE);
                        }

                        subPartitionList.addKey(expr);
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    subPartitionList.putAttribute("ads.subPartitionList", Boolean.TRUE);
                    accept(Token.RPAREN);
                } else if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();

                    SQLExpr expr;
                    if (lexer.token() == Token.LITERAL_ALIAS) {
                        expr = new SQLIdentifierExpr(lexer.stringVal());
                        lexer.nextToken();
                    } else {
                        expr = this.exprParser.expr();
                    }

                    if (expr instanceof SQLIdentifierExpr
                        && (lexer.identifierEquals("bigint") || lexer.identifierEquals("long"))) {
                        String dataType = lexer.stringVal();
                        lexer.nextToken();

                        SQLColumnDefinition column = this.exprParser.createColumnDefinition();
                        column.setName((SQLIdentifierExpr) expr);
                        column.setDataType(new SQLDataTypeImpl(dataType));
                        subPartitionList.addColumn(column);

                        subPartitionList.putAttribute("ads.subPartitionList", Boolean.TRUE);
                    } else {
                        subPartitionList.addKey(expr);
                    }
                    accept(Token.RPAREN);
                } else {
                    acceptIdentifier("COLUMNS");
                    accept(Token.LPAREN);
                    for (; ; ) {
                        subPartitionList.addColumn(this.exprParser.parseColumn());
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                }
                subPartitionByClause = subPartitionList;
            } else if (lexer.identifierEquals(FnvHash.Constants.RANGE)) {
                lexer.nextToken();
                SQLSubPartitionByRange range = new SQLSubPartitionByRange();

                accept(Token.LPAREN);
                this.exprParser.exprList(range.getColumns(), range);
                accept(Token.RPAREN);
                subPartitionByClause = range;
            }

            if (lexer.identifierEquals(FnvHash.Constants.SUBPARTITION)) {
                lexer.nextToken();
                acceptIdentifier("OPTIONS");
                this.exprParser.parseAssignItem(subPartitionByClause.getOptions(), subPartitionByClause);
            }

            if (lexer.identifierEquals(FnvHash.Constants.SUBPARTITIONS)) {
                lexer.nextToken();
                Number intValue = lexer.integerValue();
                SQLNumberExpr numExpr = new SQLNumberExpr(intValue);
                subPartitionByClause.setSubPartitionsCount(numExpr);
                lexer.nextToken();
            } else if (lexer.identifierEquals(FnvHash.Constants.PARTITIONS)) { // ADB
                lexer.nextToken();
                subPartitionByClause.setSubPartitionsCount((SQLIntegerExpr) exprParser.expr());
                subPartitionByClause.getAttributes().put("adb.partitons", true);
            }

            if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
                lexer.nextToken();
                subPartitionByClause.setLifecycle((SQLIntegerExpr) exprParser.expr());
            }

            if (subPartitionByClause != null) {
                subPartitionByClause.setLinear(linear);

                clause.setSubPartitionBy(subPartitionByClause);
            }
        }
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

    protected SQLPartitionByValue partitionByValue() {
        SQLPartitionByValue clause = new SQLPartitionByValue();
        if (lexer.identifierEquals(FnvHash.Constants.VALUE)) {
            lexer.nextToken();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                clause.addColumn(this.exprParser.expr());
                accept(Token.RPAREN);
            }
        }
        return clause;
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

    private void parseOrderBy(SQLCreateTableStatement stmt) {
        lexer.nextToken();
        accept(Token.BY);
        accept(Token.LPAREN);
        for (; ; ) {
            SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
            stmt.addSortedByItem(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);
    }
}
