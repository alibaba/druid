/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlForceIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlIndexHintImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUseIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateTableSource;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

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
        if (lexer.token() != Token.FROM) {
            return;
        }
        
        lexer.nextTokenIdent();

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
        
        queryBlock.setFrom(parseTableSource());
    }


    @Override
    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            select.setBracket(true);
            accept(Token.RPAREN);

            return queryRest(select, acceptUnion);
        }

        MySqlSelectQueryBlock queryBlock = new MySqlSelectQueryBlock();
        queryBlock.setParent(parent);

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
            }

            if (lexer.identifierEquals(FnvHash.Constants.HIGH_PRIORITY)) {
                queryBlock.setHignPriority(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.STRAIGHT_JOIN)) {
                queryBlock.setStraightJoin(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.SQL_SMALL_RESULT)) {
                queryBlock.setSmallResult(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.SQL_BIG_RESULT)) {
                queryBlock.setBigResult(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.SQL_BUFFER_RESULT)) {
                queryBlock.setBufferResult(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.SQL_CACHE)) {
                queryBlock.setCache(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.SQL_NO_CACHE)) {
                queryBlock.setCache(false);
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.SQL_CALC_FOUND_ROWS)) {
                queryBlock.setCalcFoundRows(true);
                lexer.nextToken();
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

        parseGroupBy(queryBlock);

        queryBlock.setOrderBy(this.exprParser.parseOrderBy());

        if (lexer.token() == Token.LIMIT) {
            queryBlock.setLimit(this.exprParser.parseLimit());
        }

        if (lexer.token() == Token.PROCEDURE) {
            lexer.nextToken();
            throw new ParserException("TODO. " + lexer.info());
        }

        parseInto(queryBlock);

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();
            accept(Token.UPDATE);

            queryBlock.setForUpdate(true);

            if (lexer.identifierEquals(FnvHash.Constants.NO_WAIT) || lexer.identifierEquals(FnvHash.Constants.NOWAIT)) {
                lexer.nextToken();
                queryBlock.setNoWait(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.WAIT)) {
                lexer.nextToken();
                SQLExpr waitTime = this.exprParser.primary();
                queryBlock.setWaitTime(waitTime);
            }
        }

        if (lexer.token() == Token.LOCK) {
            lexer.nextToken();
            accept(Token.IN);
            acceptIdentifier("SHARE");
            acceptIdentifier("MODE");
            queryBlock.setLockInShareMode(true);
        }

        return queryRest(queryBlock, acceptUnion);
    }
    
    public SQLTableSource parseTableSource() {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            SQLTableSource tableSource;
            if (lexer.token() == Token.SELECT || lexer.token() == Token.WITH) {
                SQLSelect select = select();

                accept(Token.RPAREN);

                SQLSelectQuery query = queryRest(select.getQuery());
                if (query instanceof SQLUnionQuery && select.getWithSubQuery() == null) {
                    select.getQuery().setBracket(true);
                    tableSource = new SQLUnionQueryTableSource((SQLUnionQuery) query);
                } else {
                    tableSource = new SQLSubqueryTableSource(select);
                }
            } else if (lexer.token() == Token.LPAREN) {
                tableSource = parseTableSource();
                accept(Token.RPAREN);
            } else {
                tableSource = parseTableSource();
                accept(Token.RPAREN);
            }

            return parseTableSourceRest(tableSource);
        }
        
        if(lexer.token() == Token.UPDATE) {
            SQLTableSource tableSource = new MySqlUpdateTableSource(parseUpdateStatment());
            return parseTableSourceRest(tableSource);
        }

        if (lexer.token() == Token.SELECT) {
            throw new ParserException("TODO. " + lexer.info());
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
        if (lexer.token() == (Token.INTO)) {
            lexer.nextToken();

            if (lexer.identifierEquals("OUTFILE")) {
                lexer.nextToken();

                MySqlOutFileExpr outFile = new MySqlOutFileExpr();
                outFile.setFile(expr());

                queryBlock.setInto(outFile);

                if (lexer.identifierEquals("FIELDS") || lexer.identifierEquals("COLUMNS")) {
                    lexer.nextToken();

                    if (lexer.identifierEquals("TERMINATED")) {
                        lexer.nextToken();
                        accept(Token.BY);
                    }
                    outFile.setColumnsTerminatedBy(expr());

                    if (lexer.identifierEquals("OPTIONALLY")) {
                        lexer.nextToken();
                        outFile.setColumnsEnclosedOptionally(true);
                    }

                    if (lexer.identifierEquals("ENCLOSED")) {
                        lexer.nextToken();
                        accept(Token.BY);
                        outFile.setColumnsEnclosedBy((SQLLiteralExpr) expr());
                    }

                    if (lexer.identifierEquals("ESCAPED")) {
                        lexer.nextToken();
                        accept(Token.BY);
                        outFile.setColumnsEscaped((SQLLiteralExpr) expr());
                    }
                }

                if (lexer.identifierEquals("LINES")) {
                    lexer.nextToken();

                    if (lexer.identifierEquals("STARTING")) {
                        lexer.nextToken();
                        accept(Token.BY);
                        outFile.setLinesStartingBy((SQLLiteralExpr) expr());
                    } else {
                        lexer.identifierEquals("TERMINATED");
                        lexer.nextToken();
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
    }

    protected SQLTableSource primaryTableSourceRest(SQLTableSource tableSource) {
        parseIndexHintList(tableSource);

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.LPAREN);
            this.exprParser.names(((SQLExprTableSource) tableSource).getPartitions(), tableSource);
            accept(Token.RPAREN);
        }

        return tableSource;
    }

    protected SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
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
        if (lexer.token() == Token.PRIMARY) {
            lexer.nextToken();
            hint.getIndexList().add(new SQLIdentifierExpr("PRIMARY"));
        } else {
            this.exprParser.names(hint.getIndexList());
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
