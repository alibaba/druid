/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.mysql.parser;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.parser.HiveCreateTableParser;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.*;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateTableSource;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

public class MySqlSelectParser extends SQLSelectParser {

    protected boolean              returningFlag = false;
    protected MySqlUpdateStatement updateStmt;

    public MySqlSelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public MySqlSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache){
        super(exprParser, selectListCache);
    }

    public MySqlSelectParser(String sql){
        this(new MySqlExprParser(sql));
    }
    
    public void parseFrom(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.EOF
                || lexer.token() == Token.SEMI
                || lexer.token() == Token.ORDER
                || lexer.token() == Token.RPAREN
                || lexer.token() == Token.UNION
        ) {
            return;
        }

        if (lexer.token() != Token.FROM) {
            for (SQLSelectItem item : queryBlock.getSelectList()) {
                SQLExpr expr = item.getExpr();
                if (expr instanceof SQLAggregateExpr) {
                    throw new ParserException("syntax error, expect " + Token.FROM + ", actual " + lexer.token() + ", " + lexer.info());
                }
            }
            return;
        }
        
        lexer.nextTokenIdent();

        while (lexer.token() == Token.HINT) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.TABLE) {
            HiveCreateTableParser createTableParser = new HiveCreateTableParser(lexer);
            HiveCreateTableStatement stmt = (HiveCreateTableStatement) createTableParser
                    .parseCreateTable(false);
            SQLAdhocTableSource tableSource = new SQLAdhocTableSource(stmt);
            queryBlock.setFrom(
                    parseTableSourceRest(tableSource)
            );
            return;
        }

        if (lexer.token() == Token.UPDATE) { // taobao returning to urgly syntax
            updateStmt = this.parseUpdateStatment();
            List<SQLExpr> returnning = updateStmt.getReturning();
            for (SQLSelectItem item : queryBlock.getSelectList()) {
                SQLExpr itemExpr = item.getExpr();
                itemExpr.setParent(updateStmt);
                returnning.add(itemExpr);
            }
            returningFlag = true;
            return;
        }

        SQLTableSource from = parseTableSource(queryBlock);
        queryBlock.setFrom(from);
    }

  
    @Override
    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        List<SQLCommentHint> hints = null;
        if (lexer.token() == Token.HINT) {
            hints = this.exprParser.parseHints();
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            select.setBracket(true);
            accept(Token.RPAREN);

            return queryRest(select, acceptUnion);
        }

        if (lexer.token() == Token.VALUES) {
            return valuesQuery(acceptUnion);
        }

        MySqlSelectQueryBlock queryBlock = new MySqlSelectQueryBlock();
        queryBlock.setParent(parent);

        class QueryHintHandler implements Lexer.CommentHandler {
            private MySqlSelectQueryBlock queryBlock;
            private Lexer lexer;

            QueryHintHandler(MySqlSelectQueryBlock queryBlock, Lexer lexer) {
                this.queryBlock = queryBlock;
                this.lexer = lexer;
            }

            @Override
            public boolean handle(Token lastToken, String comment) {
                if (lexer.isEnabled(SQLParserFeature.TDDLHint)
                        && (comment.startsWith("+ TDDL")
                        || comment.startsWith("+TDDL")
                        || comment.startsWith("!TDDL")
                        || comment.startsWith("TDDL"))) {
                    SQLCommentHint hint = new TDDLHint(comment);

                    if (lexer.getCommentCount() > 0) {
                        hint.addBeforeComment(lexer.getComments());
                    }

                    queryBlock.getHints().add(hint);

                    lexer.nextToken();
                }
                return false;
            }
        }

        this.lexer.setCommentHandler(new QueryHintHandler(queryBlock, this.lexer));

        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }

        if (lexer.token() == Token.SELECT) {
            if (selectListCache != null) {
                selectListCache.match(lexer, queryBlock);
            }
        }

        if (lexer.token() == Token.SELECT) {
            lexer.nextTokenValue();

            for(;;) {
                if (lexer.token() == Token.HINT) {
                    this.exprParser.parseHints(queryBlock.getHints());
                } else {
                    break;
                }
            }


            while (true) {
                Token token = lexer.token();
                if (token == (Token.DISTINCT)) {
                    queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.DISTINCTROW)) {
                    queryBlock.setDistionOption(SQLSetQuantifier.DISTINCTROW);
                    lexer.nextToken();
                } else if (token == (Token.ALL)) {
                    queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                    lexer.nextToken();
                } else if (token == (Token.UNIQUE)) {
                    queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.HIGH_PRIORITY)) {
                    queryBlock.setHignPriority(true);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.STRAIGHT_JOIN)) {
                    queryBlock.setStraightJoin(true);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.SQL_SMALL_RESULT)) {
                    queryBlock.setSmallResult(true);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.SQL_BIG_RESULT)) {
                    queryBlock.setBigResult(true);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.SQL_BUFFER_RESULT)) {
                    queryBlock.setBufferResult(true);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.SQL_CACHE)) {
                    queryBlock.setCache(true);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.SQL_NO_CACHE)) {
                    queryBlock.setCache(false);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.SQL_CALC_FOUND_ROWS)) {
                    queryBlock.setCalcFoundRows(true);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.TOP)) {
                    Lexer.SavePoint mark = lexer.mark();

                    lexer.nextToken();
                    if (lexer.token() == Token.LITERAL_INT) {
                        SQLLimit limit = new SQLLimit(lexer.integerValue().intValue());
                        queryBlock.setLimit(limit);
                        lexer.nextToken();
                    } else if (lexer.token() == Token.DOT) {
                        lexer.reset(mark);
                        break;
                    }
                } else {
                    break;
                }
            }

            parseSelectList(queryBlock);

            if (lexer.identifierEquals(FnvHash.Constants.FORCE)) {
                lexer.nextToken();
                accept(Token.PARTITION);
                SQLName partition = this.exprParser.name();
                queryBlock.setForcePartition(partition);
            }
            
            parseInto(queryBlock);
        }

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        if (lexer.token() == Token.GROUP || lexer.token() == Token.HAVING) {
            parseGroupBy(queryBlock);
        }

        if (lexer.identifierEquals(FnvHash.Constants.WINDOW)) {
            parseWindow(queryBlock);
        }

        if (lexer.token() == Token.ORDER) {
            queryBlock.setOrderBy(this.exprParser.parseOrderBy());
        }

        if (lexer.token() == Token.LIMIT) {
            queryBlock.setLimit(this.exprParser.parseLimit());
        }

        if (lexer.token() == Token.FETCH) {
            final Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.NEXT)) {
                lexer.nextToken();
                SQLExpr rows = this.exprParser.primary();
                queryBlock.setLimit(
                        new SQLLimit(rows));
                acceptIdentifier("ROWS");
                acceptIdentifier("ONLY");
            } else {
                lexer.reset(mark);
            }
        }

        if (lexer.token() == Token.PROCEDURE) {
            lexer.nextToken();
            throw new ParserException("TODO. " + lexer.info());
        }

        if (lexer.token() == Token.INTO) {
            parseInto(queryBlock);
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();

            if (lexer.token() == Token.UPDATE) {
                lexer.nextToken();
                queryBlock.setForUpdate(true);

                if (lexer.identifierEquals(FnvHash.Constants.NO_WAIT)
                        || lexer.identifierEquals(FnvHash.Constants.NOWAIT)) {
                    lexer.nextToken();
                    queryBlock.setNoWait(true);
                } else if (lexer.identifierEquals(FnvHash.Constants.WAIT)) {
                    lexer.nextToken();
                    SQLExpr waitTime = this.exprParser.primary();
                    queryBlock.setWaitTime(waitTime);
                }

                if (lexer.identifierEquals(FnvHash.Constants.SKIP)) {
                    lexer.nextToken();
                    acceptIdentifier("LOCKED");
                    queryBlock.setSkipLocked(true);
                }
            } else {
                acceptIdentifier("SHARE");
                queryBlock.setForShare(true);
            }
        }

        if (lexer.token() == Token.LOCK) {
            lexer.nextToken();
            accept(Token.IN);
            acceptIdentifier("SHARE");
            acceptIdentifier("MODE");
            queryBlock.setLockInShareMode(true);
        }

        if (hints != null) {
            queryBlock.setHints(hints);
        }

        return queryRest(queryBlock, acceptUnion);
    }

    public SQLTableSource parseTableSource() {
        return parseTableSource(null);
    }
    
    public SQLTableSource parseTableSource(SQLObject parent) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            List hints = null;
            if (lexer.token() == Token.HINT) {
                hints = new ArrayList();
                this.exprParser.parseHints(hints);
            }

            SQLTableSource tableSource;
            if (lexer.token() == Token.SELECT || lexer.token() == Token.WITH) {
                SQLSelect select = select();

                accept(Token.RPAREN);

                SQLSelectQueryBlock innerQuery = select.getQueryBlock();

                boolean noOrderByAndLimit = innerQuery instanceof SQLSelectQueryBlock
                        && ((SQLSelectQueryBlock) innerQuery).getOrderBy() == null
                        && ((SQLSelectQueryBlock) select.getQuery()).getLimit() == null;

                if (lexer.token() == Token.LIMIT) {
                    SQLLimit limit = this.exprParser.parseLimit();
                    if (parent != null && parent instanceof SQLSelectQueryBlock) {
                       ((SQLSelectQueryBlock) parent).setLimit(limit);
                    }
                    if (parent == null && noOrderByAndLimit) {
                        innerQuery.setLimit(limit);
                    }
                } else if (lexer.token() == Token.ORDER) {
                    SQLOrderBy orderBy = this.exprParser.parseOrderBy();
                    if (parent != null && parent instanceof SQLSelectQueryBlock) {
                        ((SQLSelectQueryBlock) parent).setOrderBy(orderBy);
                    }
                    if (parent == null && noOrderByAndLimit) {
                        innerQuery.setOrderBy(orderBy);
                    }
                }

                SQLSelectQuery query = queryRest(select.getQuery(), false);
                if (query instanceof SQLUnionQuery && select.getWithSubQuery() == null) {
                    select.getQuery().setBracket(true);
                    tableSource = new SQLUnionQueryTableSource((SQLUnionQuery) query);
                } else {
                    tableSource = new SQLSubqueryTableSource(select);
                }

                if (hints != null) {
                    tableSource.getHints().addAll(hints);
                }
                
            } else if (lexer.token() == Token.LPAREN) {
                tableSource = parseTableSource();
                if (lexer.token() != Token.RPAREN && tableSource instanceof SQLSubqueryTableSource) {
                    SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) tableSource;
                    SQLSelect select = sqlSubqueryTableSource.getSelect();

                    SQLSelectQuery query = queryRest(select.getQuery(), true);
                    if (query instanceof SQLUnionQuery && select.getWithSubQuery() == null) {
                        select.getQuery().setBracket(true);
                        tableSource = new SQLUnionQueryTableSource((SQLUnionQuery) query);
                    } else {
                        tableSource = new SQLSubqueryTableSource(select);
                    }

                    if (hints != null) {
                        tableSource.getHints().addAll(hints);
                    }
                } else if (lexer.token() != Token.RPAREN && tableSource instanceof SQLUnionQueryTableSource) {
                    SQLUnionQueryTableSource unionQueryTableSource = (SQLUnionQueryTableSource) tableSource;
                    SQLUnionQuery unionQuery = unionQueryTableSource.getUnion();

                    SQLSelectQuery query = queryRest(unionQuery, true);
                    if (query instanceof SQLUnionQuery) {
                        unionQuery.setBracket(true);
                        tableSource = new SQLUnionQueryTableSource((SQLUnionQuery) query);
                    } else {
                        tableSource = new SQLSubqueryTableSource(unionQuery);
                    }

                    if (hints != null) {
                        tableSource.getHints().addAll(hints);
                    }
                }
                accept(Token.RPAREN);
            } else {
                tableSource = parseTableSource();
                accept(Token.RPAREN);
                if (lexer.token() == Token.AS
                        && tableSource instanceof SQLValuesTableSource) {
                    lexer.nextToken();
                    String alias = lexer.stringVal();
                    lexer.nextToken();
                    tableSource.setAlias(alias);
                    accept(Token.LPAREN);
                    SQLValuesTableSource values = (SQLValuesTableSource) tableSource;
                    this.exprParser.names(values.getColumns(), tableSource);
                    accept(Token.RPAREN);
                }
            }

            return parseTableSourceRest(tableSource);
        } else if (lexer.token() == Token.LBRACE) {
            accept(Token.LBRACE);
            acceptIdentifier("OJ");

            SQLTableSource tableSrc = parseTableSource();

            accept(Token.RBRACE);

            tableSrc = parseTableSourceRest(tableSrc);

            if (lexer.hasComment() && lexer.isKeepComments()) {
                tableSrc.addAfterComment(lexer.readAndResetComments());
            }

            return tableSrc;
        }

        if (lexer.token() == Token.VALUES) {
            return parseValues();
        }
        
        if(lexer.token() == Token.UPDATE) {
            SQLTableSource tableSource = new MySqlUpdateTableSource(parseUpdateStatment());
            return parseTableSourceRest(tableSource);
        }

        if (lexer.token() == Token.SELECT) {
            throw new ParserException("TODO. " + lexer.info());
        }

        if (lexer.identifierEquals(FnvHash.Constants.UNNEST)) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();

            if (lexer.token() == Token.LPAREN){
                lexer.nextToken();
                SQLUnnestTableSource unnest = new SQLUnnestTableSource();
                this.exprParser.exprList(unnest.getItems(), unnest);
                accept(Token.RPAREN);

                if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    acceptIdentifier("ORDINALITY");
                    unnest.setOrdinality(true);
                }

                String alias = this.tableAlias();
                unnest.setAlias(alias);

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    this.exprParser.names(unnest.getColumns(), unnest);
                    accept(Token.RPAREN);
                }

                SQLTableSource tableSrc = parseTableSourceRest(unnest);
                return tableSrc;
            } else {
                lexer.reset(mark);
            }
        }

        SQLExprTableSource tableReference = new SQLExprTableSource();

        parseTableSourceQueryTableExpr(tableReference);

        SQLTableSource tableSrc = parseTableSourceRest(tableReference);
        
        if (lexer.hasComment() && lexer.isKeepComments()) {
            tableSrc.addAfterComment(lexer.readAndResetComments());
        }
        
        return tableSrc;
    }

    protected MySqlUpdateStatement parseUpdateStatment() {
        MySqlUpdateStatement update = new MySqlUpdateStatement();

        lexer.nextToken();

        if (lexer.identifierEquals(FnvHash.Constants.LOW_PRIORITY)) {
            lexer.nextToken();
            update.setLowPriority(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.IGNORE)) {
            lexer.nextToken();
            update.setIgnore(true);
        }
        
        if (lexer.identifierEquals(FnvHash.Constants.COMMIT_ON_SUCCESS)) {
            lexer.nextToken();
            update.setCommitOnSuccess(true);
        }
        
        if (lexer.identifierEquals(FnvHash.Constants.ROLLBACK_ON_FAIL)) {
            lexer.nextToken();
            update.setRollBackOnFail(true);
        }
        
        if (lexer.identifierEquals(FnvHash.Constants.QUEUE_ON_PK)) {
            lexer.nextToken();
            update.setQueryOnPk(true);
        }
        
        if (lexer.identifierEquals(FnvHash.Constants.TARGET_AFFECT_ROW)) {
            lexer.nextToken();
            SQLExpr targetAffectRow = this.exprParser.expr();
            update.setTargetAffectRow(targetAffectRow);
        }

        if (lexer.identifierEquals(FnvHash.Constants.FORCE)) {
            lexer.nextToken();

            if (lexer.token() == Token.ALL) {
                lexer.nextToken();
                acceptIdentifier("PARTITIONS");
                update.setForceAllPartitions(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.PARTITIONS)){
                lexer.nextToken();
                update.setForceAllPartitions(true);
            } else if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                SQLName partition = this.exprParser.name();
                update.setForcePartition(partition);
            } else {
                throw new ParserException("TODO. " + lexer.info());
            }
        }

        while (lexer.token() == Token.HINT) {
            this.exprParser.parseHints(update.getHints());
        }

        SQLSelectParser selectParser = this.exprParser.createSelectParser();
        SQLTableSource updateTableSource = selectParser.parseTableSource();
        update.setTableSource(updateTableSource);

        accept(Token.SET);

        for (;;) {
            SQLUpdateSetItem item = this.exprParser.parseUpdateSetItem();
            update.addItem(item);

            if (lexer.token() != Token.COMMA) {
                break;
            }

            lexer.nextToken();
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            update.setWhere(this.exprParser.expr());
        }

        update.setOrderBy(this.exprParser.parseOrderBy());
        update.setLimit(this.exprParser.parseLimit());
        
        return update;
    }
    
    protected void parseInto(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() != Token.INTO) {
            return;
        }

        lexer.nextToken();

        if (lexer.identifierEquals(FnvHash.Constants.OUTFILE)) {
            lexer.nextToken();

            MySqlOutFileExpr outFile = new MySqlOutFileExpr();
            outFile.setFile(expr());

            queryBlock.setInto(outFile);

            if (lexer.identifierEquals(FnvHash.Constants.FIELDS) || lexer.identifierEquals(FnvHash.Constants.COLUMNS)) {
                lexer.nextToken();

                if (lexer.identifierEquals(FnvHash.Constants.TERMINATED)) {
                    lexer.nextToken();
                    accept(Token.BY);
                }
                outFile.setColumnsTerminatedBy(expr());

                if (lexer.identifierEquals(FnvHash.Constants.OPTIONALLY)) {
                    lexer.nextToken();
                    outFile.setColumnsEnclosedOptionally(true);
                }

                if (lexer.identifierEquals(FnvHash.Constants.ENCLOSED)) {
                    lexer.nextToken();
                    accept(Token.BY);
                    outFile.setColumnsEnclosedBy((SQLLiteralExpr) expr());
                }

                if (lexer.identifierEquals(FnvHash.Constants.ESCAPED)) {
                    lexer.nextToken();
                    accept(Token.BY);
                    outFile.setColumnsEscaped((SQLLiteralExpr) expr());
                }
            }

            if (lexer.identifierEquals(FnvHash.Constants.LINES)) {
                lexer.nextToken();

                if (lexer.identifierEquals(FnvHash.Constants.STARTING)) {
                    lexer.nextToken();
                    accept(Token.BY);
                    outFile.setLinesStartingBy((SQLLiteralExpr) expr());
                } else {
                    if (lexer.identifierEquals(FnvHash.Constants.TERMINATED)) {
                        lexer.nextToken();
                    }
                    accept(Token.BY);
                    outFile.setLinesTerminatedBy((SQLLiteralExpr) expr());
                }
            }
        } else {
            SQLExpr intoExpr = this.exprParser.name();
            if (lexer.token() == Token.COMMA) {
                SQLListExpr list = new SQLListExpr();
                list.addItem(intoExpr);

                while (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    SQLName name = this.exprParser.name();
                    list.addItem(name);
                }

                intoExpr = list;
            }
            queryBlock.setInto(intoExpr);
        }
    }

    protected SQLTableSource primaryTableSourceRest(SQLTableSource tableSource) {
        if (lexer.token() == Token.USE) {
            lexer.nextToken();
            MySqlUseIndexHint hint = new MySqlUseIndexHint();
            parseIndexHint(hint);
            tableSource.getHints().add(hint);
        }

        if (lexer.identifierEquals(FnvHash.Constants.IGNORE)) {
            lexer.nextToken();
            MySqlIgnoreIndexHint hint = new MySqlIgnoreIndexHint();
            parseIndexHint(hint);
            tableSource.getHints().add(hint);
        }

        if (lexer.identifierEquals(FnvHash.Constants.FORCE)) {
            lexer.nextToken();
            MySqlForceIndexHint hint = new MySqlForceIndexHint();
            parseIndexHint(hint);
            tableSource.getHints().add(hint);
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            // 兼容jsqlparser 和presto
            if (lexer.token() == Token.ON) {
                tableSource.setAlias("partition");
            } else {
                accept(Token.LPAREN);
                this.exprParser.names(((SQLExprTableSource) tableSource).getPartitions(), tableSource);
                accept(Token.RPAREN);
            }
        }

        return tableSource;
    }


    public SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        if (lexer.identifierEquals(FnvHash.Constants.TABLESAMPLE) && tableSource instanceof SQLExprTableSource) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();

            SQLTableSampling sampling = new SQLTableSampling();

            if (lexer.identifierEquals(FnvHash.Constants.BERNOULLI)) {
                lexer.nextToken();
                sampling.setBernoulli(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.SYSTEM)) {
                lexer.nextToken();
                sampling.setSystem(true);
            }

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                if (lexer.identifierEquals(FnvHash.Constants.BUCKET)) {
                    lexer.nextToken();
                    SQLExpr bucket = this.exprParser.primary();
                    sampling.setBucket(bucket);

                    if (lexer.token() == Token.OUT) {
                        lexer.nextToken();
                        accept(Token.OF);
                        SQLExpr outOf = this.exprParser.primary();
                        sampling.setOutOf(outOf);
                    }

                    if (lexer.token() == Token.ON) {
                        lexer.nextToken();
                        SQLExpr on = this.exprParser.expr();
                        sampling.setOn(on);
                    }
                }

                if (lexer.token() == Token.LITERAL_INT || lexer.token() == Token.LITERAL_FLOAT) {
                    SQLExpr val = this.exprParser.primary();

                    if (lexer.identifierEquals(FnvHash.Constants.ROWS)) {
                        lexer.nextToken();
                        sampling.setRows(val);
                    } else if (lexer.token() == Token.RPAREN) {
                        sampling.setRows(val);
                    } else {
                        acceptIdentifier("PERCENT");
                        sampling.setPercent(val);
                    }
                }

                if (lexer.token() == Token.IDENTIFIER) {
                    String strVal = lexer.stringVal();
                    char first = strVal.charAt(0);
                    char last = strVal.charAt(strVal.length() - 1);
                    if (last >= 'a' && last <= 'z') {
                        last -= 32; // to upper
                    }

                    boolean match = false;
                    if ((first == '.' || (first >= '0' && first <= '9'))) {
                        switch (last) {
                            case 'B':
                            case 'K':
                            case 'M':
                            case 'G':
                            case 'T':
                            case 'P':
                                match = true;
                                break;
                            default:
                                break;
                        }
                    }
                    SQLSizeExpr size = new SQLSizeExpr(strVal.substring(0, strVal.length() - 2), last);
                    sampling.setByteLength(size);
                    lexer.nextToken();
                }

                final SQLExprTableSource table = (SQLExprTableSource) tableSource;
                table.setSampling(sampling);

                accept(Token.RPAREN);
            } else {
                lexer.reset(mark);
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.USING)) {
            return tableSource;
        }

        parseIndexHintList(tableSource);

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.LPAREN);
            this.exprParser.names(((SQLExprTableSource) tableSource).getPartitions(), tableSource);
            accept(Token.RPAREN);
        }

        return super.parseTableSourceRest(tableSource);
    }

    private void parseIndexHintList(SQLTableSource tableSource) {
        if (lexer.token() == Token.USE) {
            lexer.nextToken();
            MySqlUseIndexHint hint = new MySqlUseIndexHint();
            parseIndexHint(hint);
            tableSource.getHints().add(hint);
            parseIndexHintList(tableSource);
        }

        if (lexer.identifierEquals(FnvHash.Constants.IGNORE)) {
            lexer.nextToken();
            MySqlIgnoreIndexHint hint = new MySqlIgnoreIndexHint();
            parseIndexHint(hint);
            tableSource.getHints().add(hint);
            parseIndexHintList(tableSource);
        }

        if (lexer.identifierEquals(FnvHash.Constants.FORCE)) {
            lexer.nextToken();
            MySqlForceIndexHint hint = new MySqlForceIndexHint();
            parseIndexHint(hint);
            tableSource.getHints().add(hint);
            parseIndexHintList(tableSource);
        }
    }

    private void parseIndexHint(MySqlIndexHintImpl hint) {
        if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
        } else {
            accept(Token.KEY);
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();

            if (lexer.token() == Token.JOIN) {
                lexer.nextToken();
                hint.setOption(MySqlIndexHint.Option.JOIN);
            } else if (lexer.token() == Token.ORDER) {
                lexer.nextToken();
                accept(Token.BY);
                hint.setOption(MySqlIndexHint.Option.ORDER_BY);
            } else {
                accept(Token.GROUP);
                accept(Token.BY);
                hint.setOption(MySqlIndexHint.Option.GROUP_BY);
            }
        }

        accept(Token.LPAREN);
        while (lexer.token() != Token.RPAREN && lexer.token() != Token.EOF) {
            if (lexer.token() == Token.PRIMARY) {
                lexer.nextToken();
                hint.getIndexList().add(new SQLIdentifierExpr("PRIMARY"));
            } else {
                SQLName name = this.exprParser.name();
                name.setParent(hint);
                hint.getIndexList().add(name);
            }
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
            } else {
                break;
            }
        }
        accept(Token.RPAREN);
    }

    public SQLUnionQuery unionRest(SQLUnionQuery union) {
        if (lexer.token() == Token.LIMIT) {
            union.setLimit(this.exprParser.parseLimit());
        }
        return super.unionRest(union);
    }

    public MySqlExprParser getExprParser() {
        return (MySqlExprParser) exprParser;
    }
}
