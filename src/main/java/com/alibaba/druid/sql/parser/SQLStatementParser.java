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
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerType;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveMsckRepairStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.FullTextType;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsAlterTableSetChangeLogs;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.FnvHash.Constants;
import com.alibaba.druid.util.MySqlUtils;
import com.alibaba.druid.util.StringUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static com.alibaba.druid.sql.parser.Token.*;
import static com.alibaba.druid.sql.parser.Token.ALL;
import static com.alibaba.druid.sql.parser.Token.ALTER;
import static com.alibaba.druid.sql.parser.Token.CASCADE;
import static com.alibaba.druid.sql.parser.Token.COMMA;
import static com.alibaba.druid.sql.parser.Token.GROUP;
import static com.alibaba.druid.sql.parser.Token.LINE_COMMENT;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;
import static com.alibaba.druid.sql.parser.Token.LPAREN;
import static com.alibaba.druid.sql.parser.Token.MULTI_LINE_COMMENT;
import static com.alibaba.druid.sql.parser.Token.RESTRICT;
import static com.alibaba.druid.sql.parser.Token.RPAREN;
import static com.alibaba.druid.sql.parser.Token.SELECT;
import static com.alibaba.druid.sql.parser.Token.TABLE;

public class SQLStatementParser extends SQLParser {
    protected SchemaRepository   repository;
    protected SQLExprParser      exprParser;
    protected boolean            parseCompleteValues = true;
    protected int                parseValuesSize     = 3;
    protected SQLSelectListCache selectListCache     = null;
    protected InsertColumnsCache insertColumnsCache  = null;

    protected java.sql.Timestamp now;
    protected java.sql.Date      currentDate;

    public SQLStatementParser(String sql){
        this(sql, null);
    }

    public SQLStatementParser(String sql, DbType dbType){
        this(new SQLExprParser(sql, dbType));
    }

    public SQLStatementParser(SQLExprParser exprParser){
        super(exprParser.getLexer(), exprParser.getDbType());
        this.exprParser = exprParser;
        this.dbType = exprParser.dbType;
    }

    protected SQLStatementParser(Lexer lexer, DbType dbType){
        super(lexer, dbType);
    }

    public boolean isKeepComments() {
        return lexer.isKeepComments();
    }

    public void setKeepComments(boolean keepComments) {
        this.lexer.setKeepComments(keepComments);
    }

    public SQLExprParser getExprParser() {
        return exprParser;
    }

    public SchemaRepository getRepository() {
        return repository;
    }

    public void setRepository(SchemaRepository repository) {
        this.repository = repository;
    }

    public List<SQLStatement> parseStatementList() {
        List<SQLStatement> statementList = new ArrayList<SQLStatement>();
        parseStatementList(statementList, -1, null);
        return statementList;
    }

    public List<SQLStatement> parseStatementList(SQLObject parent) {
        List<SQLStatement> statementList = new ArrayList<SQLStatement>();
        parseStatementList(statementList, -1, parent);
        return statementList;
    }

    public void parseStatementList(List<SQLStatement> statementList) {
        parseStatementList(statementList, -1, null);
    }

    public void parseStatementList(List<SQLStatement> statementList, int max) {
        parseStatementList(statementList, max, null);
    }

    public void parseStatementList(List<SQLStatement> statementList, int max, SQLObject parent) {
        if ("select @@session.tx_read_only".equals(lexer.text)
                && lexer.token == Token.SELECT) {
            SQLSelect select = new SQLSelect();
            MySqlSelectQueryBlock queryBlock = new MySqlSelectQueryBlock();
            queryBlock.addSelectItem(new SQLPropertyExpr(new SQLVariantRefExpr("@@session"), "tx_read_only"));
            select.setQuery(queryBlock);

            SQLSelectStatement stmt = new SQLSelectStatement(select);
            statementList.add(stmt);

            lexer.reset(29, '\u001A', Token.EOF);
            return;
        }

        boolean semi = false;
        for (int i = 0;;i++) {
            if (max != -1) {
                if (statementList.size() >= max) {
                    return;
                }
            }
            while (lexer.token == MULTI_LINE_COMMENT || lexer.token == LINE_COMMENT) {
                lexer.nextToken();
            }

            switch (lexer.token) {
                case EOF:
                case END:
                case UNTIL:
                case ELSE:
                case WHEN:
                    if (lexer.isKeepComments() && lexer.hasComment() && statementList.size() > 0) {
                        SQLStatement stmt = statementList.get(statementList.size() - 1);
                        stmt.addAfterComment(lexer.readAndResetComments());
                    }
                    return;
                case SEMI: {
                    int line0 = lexer.getLine();
                    lexer.nextToken();
                    int line1 = lexer.getLine();

                    if(statementList.size() > 0) {
                        SQLStatement lastStmt = statementList.get(statementList.size() - 1);
                        lastStmt.setAfterSemi(true);

                        if (lexer.isKeepComments()) {
                            SQLStatement stmt = statementList.get(statementList.size() - 1);
                            if (line1 - line0 <= 1) {
                                stmt.addAfterComment(lexer.readAndResetComments());
                            }
                        }
                    }

                    semi = true;

                    continue;
                }
                case WITH: {
                    SQLStatement stmt = parseWith();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case SELECT: {
                    MySqlHintStatement hintStatement = null;
                    if (i == 1
                            && statementList.size() > 0
                            && statementList.get(statementList.size() - i) instanceof MySqlHintStatement) {
                        hintStatement = (MySqlHintStatement) statementList.get(statementList.size() - i);
                    } else if (i > 0 && !semi) {
                        throw new ParserException("syntax error. " + lexer.info());
                    }
                    SQLStatement stmt = parseSelect();
                    stmt.setParent(parent);
                    if (hintStatement != null && stmt instanceof SQLStatementImpl) {
                        SQLStatementImpl stmtImpl = (SQLStatementImpl) stmt;
                        List<SQLCommentHint> hints = stmtImpl.getHeadHintsDirect();
                        if (hints == null) {
                            stmtImpl.setHeadHints(hintStatement.getHints());
                        } else {
                            hints.addAll(hintStatement.getHints());
                        }
                        statementList.set(statementList.size() - 1, stmt);
                    } else {
                        statementList.add(stmt);
                    }
                    semi = false;
                    continue;
                }
                case UPDATE: {
                    //FOR ADS
                    Lexer.SavePoint savePoint = lexer.mark();
                    lexer.nextToken();
                    if(dbType == DbType.mysql && lexer.identifierEquals("PLANCACHE")) {
                        lexer.nextToken();
                        if (lexer.token == Token.SELECT) {
                            MySqlUpdatePlanCacheStatement stmt = new MySqlUpdatePlanCacheStatement();

                            SQLSelect fromSelect = createSQLSelectParser().select();
                            accept(Token.TO);
                            SQLSelect toSelect = createSQLSelectParser().select();

                            stmt.setFormSelect(fromSelect);
                            stmt.setToSelect(toSelect);

                            statementList.add(stmt);
                            continue;
                        }
                    }

                    lexer.reset(savePoint);
                    SQLStatement stmt = parseUpdateStatement();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case CREATE: {
                    SQLStatement stmt = parseCreate();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case INSERT: {
                    SQLStatement stmt = parseInsert();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case DELETE: {
                    SQLStatement stmt = parseDeleteStatement();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case EXPLAIN: {
                    lexer.computeRowAndColumn();
                    int sourceLine = lexer.posLine;
                    int sourceColumn = lexer.posColumn;

                    //FOR ADS
                    Lexer.SavePoint savePoint = lexer.mark();
                    lexer.nextToken();

                    if (lexer.identifierEquals("PLANCACHE")) {
                        lexer.nextToken();
                        MySqlExplainPlanCacheStatement stmt = new MySqlExplainPlanCacheStatement();
                        stmt.setSourceLine(sourceLine);
                        stmt.setSourceLine(sourceColumn);
                        statementList.add(stmt);
//                    } else if(lexer.token ==  Token.ANALYZE) {
//                        lexer.nextToken();
//
//                        SQLExplainAnalyzeStatement stmt = new SQLExplainAnalyzeStatement();
//                        stmt.setSelect(createSQLSelectParser().select());
//                        statementList.add(stmt);
                    } else {
                        lexer.reset(savePoint);
                        SQLExplainStatement stmt = parseExplain();
                        stmt.setSourceLine(sourceLine);
                        stmt.setSourceLine(sourceColumn);
                        stmt.setParent(parent);
                        statementList.add(stmt);
                    }
                    continue;
                }
                case SET: {
                    SQLStatement stmt = parseSet();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case ALTER: {
                    SQLStatement stmt = parseAlter();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case TRUNCATE: {
                    SQLStatement stmt = parseTruncate();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case USE: {
                    SQLStatement stmt = parseUse();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case GRANT: {
                    SQLStatement stmt = parseGrant();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case REVOKE: {
                    SQLStatement stmt = parseRevoke();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case SHOW: {
                    SQLStatement stmt = parseShow();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case MERGE: {
                    SQLStatement stmt = parseMerge();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case REPEAT: {
                    SQLStatement stmt = parseRepeat();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case DECLARE: {
                    SQLStatement stmt = parseDeclare();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case WHILE: {
                    SQLStatement stmt = parseWhile();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case IF: {
                    SQLStatement stmt = parseIf();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case CASE: {
                    SQLStatement stmt = parseCase();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case OPEN: {
                    SQLStatement stmt = parseOpen();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case FETCH: {
                    SQLStatement stmt = parseFetch();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case DROP: {
                    SQLStatement stmt = parseDrop();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case COMMENT: {
                    SQLStatement stmt = parseComment();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case KILL: {
                    SQLStatement stmt = parseKill();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case CLOSE: {
                    SQLStatement stmt = parseClose();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case RETURN: {
                    SQLStatement stmt = parseReturn();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case UPSERT: {
                    SQLStatement stmt = parseUpsert();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                case LEAVE: {
                    SQLStatement stmt = parseLeave();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                default:
                    break;
            }

            if (lexer.token == Token.LBRACE || lexer.identifierEquals("CALL")) {
                SQLCallStatement stmt = parseCall();
                statementList.add(stmt);
                continue;
            }


            if (lexer.identifierEquals("UPSERT")) {
                SQLStatement stmt = parseUpsert();
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals("LIST")) {
                Lexer.SavePoint mark = lexer.mark();

                SQLStatement stmt = parseList();
                if (stmt != null) {
                    statementList.add(stmt);
                    continue;
                } else {
                    lexer.reset(mark);
                }
            }

            if (lexer.identifierEquals("RENAME")) {
                SQLStatement stmt = parseRename();
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals("RELEASE")) {
                SQLStatement stmt = parseReleaseSavePoint();
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals("SAVEPOINT")) {
                SQLStatement stmt = parseSavePoint();
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals("REFRESH")) {
                SQLStatement stmt = parseRefresh();
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.COPY)) {
                SQLStatement stmt = parseCopy();
                statementList.add(stmt);
                continue;
            }

            if (lexer.token == Token.DESC || lexer.identifierEquals(FnvHash.Constants.DESCRIBE)) {
                SQLStatement stmt = parseDescribe();
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals("ROLLBACK")) {
                SQLStatement stmt = parseRollback();
                statementList.add(stmt);

                if (parent instanceof SQLBlockStatement
                        && DbType.mysql == dbType) {
                    return;
                }

                continue;
            }

            if (lexer.identifierEquals("DUMP")) {
                SQLStatement stmt = parseDump();
                statementList.add(stmt);

                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.COMMIT)) {
                SQLStatement stmt = parseCommit();

                statementList.add(stmt);

                if (parent instanceof SQLBlockStatement
                        && DbType.mysql == dbType) {
                    return;
                }

                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.RETURN)) {
                SQLStatement stmt = parseReturn();
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.PURGE)) {
                SQLStatement stmt = parsePurge();
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.FLASHBACK)) {
                SQLStatement stmt = parseFlashback();
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.WHO)) {
                SQLStatement stmt = parseWhoami();
                statementList.add(stmt);
                continue;
            }

            if (lexer.token == Token.FOR) {
                SQLStatement stmt = parseFor();
                statementList.add(stmt);
                stmt.setParent(parent);
                continue;
            }

            if (lexer.token == Token.LPAREN) {
                char markChar = lexer.current();
                int markBp = lexer.bp();

                do {
                    lexer.nextToken();
                } while (lexer.token == Token.LPAREN);

                if (lexer.token == Token.SELECT) {
                    lexer.reset(markBp, markChar, Token.LPAREN);
                    SQLStatement stmt = parseSelect();
                    statementList.add(stmt);
                    continue;
                } else {
                    throw new ParserException("TODO " + lexer.info());
                }
            }

            if (lexer.token == Token.VALUES) {
                SQLValuesTableSource values = this.createSQLSelectParser().parseValues();
                SQLSelectStatement stmt = new SQLSelectStatement();
                stmt.setSelect(
                    new SQLSelect(values)
                );
                statementList.add(stmt);
                stmt.setParent(parent);
                continue;
            }

            int size = statementList.size();
            if (parseStatementListDialect(statementList)) {
                if (parent != null) {
                    for (int j = size; j < statementList.size(); ++j) {
                        SQLStatement dialectStmt = statementList.get(j);
                        dialectStmt.setParent(parent);
                    }
                }

                continue;
            }

            // throw new ParserException("syntax error, " + lexer.token + " "
            // + lexer.stringVal() + ", pos "
            // + lexer.pos());
            //throw new ParserException("not supported." + lexer.info());
            printError(lexer.token);
        }


    }

    public SQLStatement parseCopy() {
        throw new ParserException("TODO : " + lexer.info());
    }

    public SQLStatement parseFor() {
        accept(Token.FOR);

        SQLForStatement stmt = new SQLForStatement();
        stmt.setDbType(dbType);

        stmt.setIndex(this.exprParser.name());
        accept(Token.IN);
        stmt.setRange(this.exprParser.expr());

        accept(Token.LOOP);

        this.parseStatementList(stmt.getStatements(), -1, stmt);
        accept(Token.END);
        accept(Token.LOOP);

        accept(Token.SEMI);
        stmt.setAfterSemi(true);

        return stmt;
    }

    public SQLStatement parseFlashback() {
        throw new ParserException("not supported. " + lexer.info());
    }

    public SQLStatement parseDump() {
        SQLDumpStatement stmt = new SQLDumpStatement();
        acceptIdentifier("DUMP");
        acceptIdentifier("DATA");

        if (lexer.identifierEquals(FnvHash.Constants.OVERWRITE)) {
            lexer.nextToken();
            stmt.setOverwrite(true);
        }

        if (lexer.token == Token.INTO) {
            lexer.nextToken();
            if (lexer.token == LITERAL_CHARS) {
                stmt.setInto(new SQLCharExpr(lexer.stringVal));
                lexer.nextToken();
            } else {
                stmt.setInto(this.exprParser.expr());
            }
        }

        SQLSelect select = createSQLSelectParser().select();
        stmt.setSelect(select);
        return stmt;
    }

    public SQLStatement parseDrop() {
        List<String> beforeComments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            beforeComments = lexer.readAndResetComments();
        }

        Lexer.SavePoint mark = lexer.mark();
        lexer.nextToken();

        final SQLStatement stmt;

        List<SQLCommentHint> hints = null;
        if (lexer.token == Token.HINT) {
            hints = this.exprParser.parseHints();
        }

        boolean temporary = false;

        if (lexer.token == Token.TEMPORARY || lexer.identifierEquals(FnvHash.Constants.TEMPORARY)) {
            lexer.nextToken();
            temporary = true;
        }

        boolean physical = false;
        if (lexer.identifierEquals(FnvHash.Constants.PHYSICAL)) {
            lexer.nextToken();
            physical = true;
        }

        switch (lexer.token) {
            case USER:
                stmt = parseDropUser();
                break;
            case INDEX:
                stmt = parseDropIndex();
                break;
            case VIEW:
                stmt = parseDropView(false);
                break;
            case TRIGGER:
                stmt = parseDropTrigger(false);
                break;
            case DATABASE:
                stmt = parseDropDatabaseOrSchema(false);
                if (physical) {
                    ((SQLDropDatabaseStatement) stmt).setPhysical(physical);
                }
                break;
            case SCHEMA:
                if (dbType == DbType.postgresql) {
                    stmt = parseDropSchema();
                } else {
                    stmt = parseDropDatabaseOrSchema(false);
                    if (physical) {
                        ((SQLDropDatabaseStatement) stmt).setPhysical(physical);
                    }
                }
                break;
            case FUNCTION:
                SQLDropFunctionStatement dropFunc = parseDropFunction(false);
                if (temporary) {
                    dropFunc.setTemporary(true);
                }
                stmt = dropFunc;
                break;
            case TABLESPACE:
                stmt = parseDropTablespace(false);
                break;
            case PROCEDURE:
                stmt = parseDropProcedure(false);
                break;
            case SEQUENCE:
                stmt = parseDropSequence(false);
                break;
            case TABLE: {
                SQLDropTableStatement dropTable = parseDropTable(false);
                if (temporary) {
                    dropTable.setTemporary(true);
                }
                if (hints != null) {
                    dropTable.setHints(hints);
                }
                stmt = dropTable;
                break;
            }
            default:
                if (lexer.token == Token.TABLE || lexer.identifierEquals("TEMPORARY")  || lexer.identifierEquals(FnvHash.Constants.PARTITIONED)) {
                    SQLDropTableStatement dropTable = parseDropTable(false);
                    if (hints != null) {
                        dropTable.setHints(hints);
                    }
                    stmt = dropTable;
                } else if (lexer.identifierEquals(Constants.TABLES)) {
                    stmt = parseDropTable(false);
                } else if (lexer.identifierEquals(FnvHash.Constants.EVENT)) {
                    stmt = parseDropEvent();
                } else if (lexer.identifierEquals(FnvHash.Constants.RESOURCE)) {
                    stmt = parseDropResource();

                } else if (lexer.identifierEquals(FnvHash.Constants.LOGFILE)) {
                    stmt = parseDropLogFileGroup();

                } else if (lexer.identifierEquals(FnvHash.Constants.SERVER)) {
                    stmt = parseDropServer();

                } else if (lexer.identifierEquals(FnvHash.Constants.TABLEGROUP)) {
                    stmt = parseDropTableGroup();
                } else if (lexer.identifierEquals(FnvHash.Constants.ROLE)) {
                    lexer.reset(mark);
                    stmt = parseDropRole();
                } else if (lexer.identifierEquals(FnvHash.Constants.OUTLINE)) {
                    lexer.reset(mark);
                    stmt = parseDropOutline();

                } else if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
                    lexer.nextToken();
                    if (lexer.token == Token.TABLE) {
                        lexer.reset(mark);
                        stmt = parseDropTable(true);
                    } else if (lexer.identifierEquals(FnvHash.Constants.CATALOG)) {
                        lexer.reset(mark);
                        stmt = parseDropCatalog();
                    } else {
                        throw new ParserException("TODO " + lexer.info());
                    }
                } else if (lexer.token() == Token.FULLTEXT) {
                    lexer.nextToken();

                    FullTextType type = parseFullTextType();
                    SQLName name = this.exprParser.name();

                    MysqlDropFullTextStatement x = new MysqlDropFullTextStatement();
                    x.setName(name);
                    x.setType(type);
                    stmt = x;
                } else if (lexer.identifierEquals("INSTANCE_GROUP")) {
                    lexer.nextToken();
                    MySqlManageInstanceGroupStatement x = new MySqlManageInstanceGroupStatement();
                    x.setOperation(new SQLIdentifierExpr("DROP"));

                    for (; ; ) {
                        x.getGroupNames().add(exprParser.expr());
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    stmt = x;
                } else if (lexer.identifierEquals("MATERIALIZED")) {
                    stmt = parseDropMaterializedView();
                } else {
                    throw new ParserException("TODO " + lexer.info());
                }

                break;
        }

        if (beforeComments != null) {
            stmt.addBeforeComment(beforeComments);
        }


        return stmt;
    }

    protected FullTextType parseFullTextType() {
        FullTextType textType;

        if (lexer.identifierEquals(FnvHash.Constants.CHARFILTER)) {
            textType = FullTextType.CHARFILTER;
        } else if (lexer.identifierEquals(FnvHash.Constants.TOKENIZER)) {
            textType = FullTextType.TOKENIZER;
        } else if (lexer.identifierEquals(FnvHash.Constants.TOKENFILTER)) {
            textType = FullTextType.TOKENFILTER;
        } else if (lexer.identifierEquals(FnvHash.Constants.ANALYZER)) {
            textType = FullTextType.ANALYZER;
        } else if (lexer.identifierEquals(FnvHash.Constants.DICTIONARY)) {
            textType = FullTextType.DICTIONARY;
        } else {
            throw new ParserException("type of full text must be [CHARFILTER/TOKENIZER/TOKENFILTER/ANALYZER/DICTIONARY] .");
        }
        lexer.nextToken();
        return textType;
    }

    protected SQLStatement parseWhoami() {
        lexer.nextToken();
        acceptIdentifier("AM");
        acceptIdentifier("I");
        return new SQLWhoamiStatement();
    }

    protected SQLStatement parseDropOutline() {
        accept(Token.DROP);

        SQLDropOutlineStatement stmt = new SQLDropOutlineStatement();
        stmt.setDbType(dbType);

        acceptIdentifier("OUTLINE");
        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    public SQLStatement parseRefresh() {
        if (lexer.identifierEquals("REFRESH")) {
            lexer.nextToken();
        }
        SQLRefreshMaterializedViewStatement stmt = new SQLRefreshMaterializedViewStatement();
        stmt.setDbType(dbType);

        acceptIdentifier("MATERIALIZED");

        accept(Token.VIEW);

        stmt.setName(this.exprParser.name());
        return stmt;
    }

    public SQLStatement parseShowMaterializedView() {
        if (lexer.token() == Token.SHOW) {
            lexer.nextToken();
        }
        SQLShowMaterializedViewStatement stmt = new SQLShowMaterializedViewStatement();
        stmt.setDbType(dbType);

        acceptIdentifier("MATERIALIZED");

        acceptIdentifier("VIEWS");

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            stmt.setLike(this.exprParser.charExpr());
        }

        return stmt;
    }

    public SQLStatement parseDropMaterializedView() {
        if (lexer.token() == Token.DROP) {
            lexer.nextToken();
        }
        SQLDropMaterializedViewStatement stmt = new SQLDropMaterializedViewStatement();
        stmt.setDbType(dbType);

        acceptIdentifier("MATERIALIZED");

        accept(Token.VIEW);

        stmt.setName(this.exprParser.name());
        return stmt;
    }

    protected SQLStatement parseDropCatalog() {
        accept(Token.DROP);

        SQLDropCatalogStatement stmt = new SQLDropCatalogStatement(dbType);

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            stmt.setExternal(true);
            lexer.nextToken();
        }

        acceptIdentifier("CATALOG");
        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    protected SQLStatement parseDropRole() {
        accept(Token.DROP);
        acceptIdentifier("ROLE");

        SQLDropRoleStatement stmt = new SQLDropRoleStatement();
        stmt.setDbType(dbType);

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    protected SQLStatement parseDropTableGroup() {
        if (lexer.token == Token.DROP) {
            lexer.nextToken();
        }

        acceptIdentifier("TABLEGROUP");

        SQLDropTableGroupStatement stmt = new SQLDropTableGroupStatement();
        stmt.setDbType(dbType);

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    protected SQLStatement parseDropServer() {
        if (lexer.token == Token.DROP) {
            lexer.nextToken();
        }

        acceptIdentifier("SERVER");

        SQLDropServerStatement stmt = new SQLDropServerStatement();
        stmt.setDbType(dbType);

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    protected SQLStatement parseDropLogFileGroup() {
        if (lexer.token == Token.DROP) {
            lexer.nextToken();
        }

        acceptIdentifier("LOGFILE");
        accept(Token.GROUP);

        SQLDropLogFileGroupStatement stmt = new SQLDropLogFileGroupStatement();
        stmt.setDbType(dbType);

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr engine = this.exprParser.primary();
            stmt.setEngine(engine);
        }

        return stmt;
    }

    protected SQLStatement parseDropEvent() {
        if (lexer.token == Token.DROP) {
            lexer.nextToken();
        }

        acceptIdentifier("EVENT");

        SQLDropEventStatement stmt = new SQLDropEventStatement();
        stmt.setDbType(dbType);

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    protected SQLStatement parseDropResource() {
        if (lexer.token == Token.DROP) {
            lexer.nextToken();
        }

        acceptIdentifier("RESOURCE");

        if (lexer.token == GROUP) {
            lexer.nextToken();
            SQLDropResourceGroupStatement stmt = new SQLDropResourceGroupStatement();
            stmt.setDbType(dbType);

            if (lexer.token == Token.IF) {
                lexer.nextToken();
                accept(Token.EXISTS);
                stmt.setIfExists(true);
            }

            SQLName name = this.exprParser.name();
            stmt.setName(name);

            return stmt;
        }

        SQLDropResourceStatement stmt = new SQLDropResourceStatement();
        stmt.setDbType(dbType);

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    protected SQLStatement parseAlterFunction() {
        throw new ParserException("TODO " + lexer.info());
    }

    public SQLStatement parseKill() {
        throw new ParserException("not supported. " + lexer.info());
    }

    public SQLStatement parseCase() {
        throw new ParserException("not supported. " + lexer.info());
    }

    public SQLStatement parseIf() {
        throw new ParserException("not supported. " + lexer.info());
    }

    public SQLStatement parseWhile() {
        throw new ParserException("not supported. " + lexer.info());
    }

    public SQLStatement parseDeclare() {
        throw new ParserException("not supported. " + lexer.info());
    }

    public SQLStatement parseRepeat() {
        throw new ParserException("not supported. " + lexer.info());
    }

    public SQLStatement parseLeave() {
        throw new ParserException("not supported. " + lexer.info());
    }

    public SQLStatement parsePurge() {
        acceptIdentifier("PURGE");

        if (lexer.token == Token.TABLE) {
            lexer.nextToken();
            SQLName tableName = this.exprParser.name();

            SQLPurgeTableStatement stmt = new SQLPurgeTableStatement();
            stmt.setTable(tableName);

            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.RECYCLEBIN)) {
            lexer.nextToken();
            SQLPurgeRecyclebinStatement stmt = new SQLPurgeRecyclebinStatement();
            return stmt;
        }

        SQLPurgeLogsStatement stmt = new SQLPurgeLogsStatement();

        if (lexer.token == Token.BINARY) {
            lexer.nextToken();
            stmt.setBinary(true);
        } else if (lexer.identifierEquals(FnvHash.Constants.MASTER)) {
            lexer.nextToken();
            stmt.setMaster(true);
        }

        if (lexer.token == ALL) {
            lexer.nextToken();
            stmt.setAll(true);
            return stmt;
        }

        acceptIdentifier("LOGS");

        if (lexer.token == Token.TO) {
            lexer.nextToken();
            SQLExpr to = this.exprParser.expr();
            stmt.setTo(to);
        }

        if (lexer.identifierEquals(FnvHash.Constants.BEFORE)) {
            lexer.nextToken();
            SQLExpr before = this.exprParser.expr();
            stmt.setBefore(before);
        }

        return stmt;
    }

    public SQLStatement parseReturn() {
        if (lexer.token == Token.RETURN
                || lexer.identifierEquals("RETURN")) {
            lexer.nextToken();
        }

        SQLReturnStatement stmt = new SQLReturnStatement();
        if (lexer.token != Token.SEMI) {
            SQLExpr expr = this.exprParser.expr();
            stmt.setExpr(expr);
        }

        if (lexer.token == Token.SEMI) {
            accept(Token.SEMI);
            stmt.setAfterSemi(true);
        }

        return stmt;
    }

    public SQLStatement parseUpsert() {
        SQLInsertStatement insertStatement = new SQLInsertStatement();

        if (lexer.token == Token.UPSERT || lexer.identifierEquals("UPSERT")) {
            lexer.nextToken();
            insertStatement.setUpsert(true);
        }

        parseInsert0(insertStatement);
        return insertStatement;
    }

    public SQLStatement parseRollback() {
        lexer.nextToken();

        if (lexer.identifierEquals("WORK")) {
            lexer.nextToken();
        }

        SQLRollbackStatement stmt = new SQLRollbackStatement(getDbType());

        if (lexer.token == Token.TO) {
            lexer.nextToken();

            if (lexer.identifierEquals("SAVEPOINT") || lexer.token == Token.SAVEPOINT) {
                lexer.nextToken();
            }

            stmt.setTo(this.exprParser.name());
        }
        return stmt;
    }

    public SQLStatement parseCommit() {
        throw new ParserException("TODO " + lexer.info());
    }

    public SQLStatement parseShow() {
        throw new ParserException("TODO " + lexer.info());
    }

    public SQLUseStatement parseUse() {
        accept(Token.USE);
        SQLUseStatement stmt = new SQLUseStatement(getDbType());
        stmt.setDatabase(this.exprParser.name());
        return stmt;
    }

    protected SQLExpr parseUser() {
        SQLExpr user = this.exprParser.expr();
        return user;
    }

    public SQLGrantStatement parseGrant() {
        accept(Token.GRANT);
        SQLGrantStatement stmt = new SQLGrantStatement(getDbType());

        parsePrivileages(stmt.getPrivileges(), stmt);

        if (lexer.token == Token.ON) {
            lexer.nextToken();

            switch (lexer.token) {
                case PROCEDURE:
                    lexer.nextToken();
                    stmt.setResourceType(SQLObjectType.PROCEDURE);
                    break;
                case FUNCTION:
                    lexer.nextToken();
                    stmt.setResourceType(SQLObjectType.FUNCTION);
                    break;
                case TABLE:
                    lexer.nextToken();
                    stmt.setResourceType(SQLObjectType.TABLE);
                    break;
                case USER:
                    lexer.nextToken();
                    stmt.setResourceType(SQLObjectType.USER);
                    break;
                case DATABASE:
                    lexer.nextToken();
                    stmt.setResourceType(SQLObjectType.DATABASE);
                    break;
                case IDENTIFIER:
                    if (lexer.identifierEquals("SYSTEM")) {
                        lexer.nextToken();
                        stmt.setResourceType(SQLObjectType.SYSTEM);
                    }
                    break;
                default:
                    break;
            }


            if (stmt.getResourceType() != null && lexer.token == Token.COLONCOLON) {
                lexer.nextToken(); // sql server
            }

            SQLExpr expr;
            if (lexer.token == Token.DOT) {
                expr = new SQLAllColumnExpr();
                lexer.nextToken();
            } else {
                expr = this.exprParser.expr();
            }

            if (stmt.getResourceType() == SQLObjectType.TABLE || stmt.getResourceType() == null) {
                stmt.setResource(new SQLExprTableSource(expr));
            } else {
                stmt.setResource(expr);
            }
        }

        if (lexer.token == Token.TO) {
            lexer.nextToken();
            for(;;) {
                SQLExpr user = parseUser();
                stmt.getUsers().add(user);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
        }

        if (lexer.token == Token.WITH) {
            lexer.nextToken();

            if (lexer.token == Token.GRANT) {
                lexer.nextToken();
                acceptIdentifier("OPTION");
                stmt.setWithGrantOption(true);
            }

            for (;;) {
                if (lexer.identifierEquals("MAX_QUERIES_PER_HOUR")) {
                    lexer.nextToken();
                    stmt.setMaxQueriesPerHour(this.exprParser.primary());
                    continue;
                }

                if (lexer.identifierEquals("MAX_UPDATES_PER_HOUR")) {
                    lexer.nextToken();
                    stmt.setMaxUpdatesPerHour(this.exprParser.primary());
                    continue;
                }

                if (lexer.identifierEquals("MAX_CONNECTIONS_PER_HOUR")) {
                    lexer.nextToken();
                    stmt.setMaxConnectionsPerHour(this.exprParser.primary());
                    continue;
                }

                if (lexer.identifierEquals("MAX_USER_CONNECTIONS")) {
                    lexer.nextToken();
                    stmt.setMaxUserConnections(this.exprParser.primary());
                    continue;
                }

                break;
            }
        }

        if (lexer.identifierEquals("ADMIN")) {
            lexer.nextToken();
            acceptIdentifier("OPTION");
            stmt.setAdminOption(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.IDENTIFIED)) {
            lexer.nextToken();
            accept(Token.BY);

            if (lexer.identifierEquals("PASSWORD")) {
                lexer.nextToken();
                String password = lexer.stringVal();
                accept(Token.LITERAL_CHARS);
                stmt.setIdentifiedByPassword(password);
            } else {
                stmt.setIdentifiedBy(this.exprParser.expr());
            }
        }

        if (lexer.token == Token.WITH) {
            lexer.nextToken();
            if (lexer.token == Token.GRANT) {
                lexer.nextToken();
                acceptIdentifier("OPTION");
                stmt.setWithGrantOption(true);
            }
        }

        return stmt;
    }

    protected void parsePrivileages(List<SQLPrivilegeItem> privileges, SQLObject parent) {
        for (;;) {
            String privilege = null;
            if (lexer.token == Token.ALL) {
                lexer.nextToken();
                if (lexer.identifierEquals("PRIVILEGES")) {
                    privilege = "ALL PRIVILEGES";
                    lexer.nextToken();
                } else {
                    privilege = "ALL";
                }
            } else if (lexer.token == Token.SELECT) {
                privilege = "SELECT";
                lexer.nextToken();
            } else if (lexer.token == Token.UPDATE) {
                privilege = "UPDATE";
                lexer.nextToken();
            } else if (lexer.token == Token.DELETE) {
                privilege = "DELETE";
                lexer.nextToken();
            } else if (lexer.token == Token.INSERT) {
                privilege = "INSERT";
                lexer.nextToken();
            } else if (lexer.token == Token.INDEX) {
                lexer.nextToken();
                privilege = "INDEX";
            } else if (lexer.token == Token.TRIGGER) {
                lexer.nextToken();
                privilege = "TRIGGER";
            } else if (lexer.token == Token.REFERENCES) {
                privilege = "REFERENCES";
                lexer.nextToken();
            } else if (lexer.token == Token.DESC) {
                privilege = "DESCRIBE";
                lexer.nextToken();
            } else if (lexer.token == Token.CREATE) {
                lexer.nextToken();

                if (lexer.token == Token.TABLE) {
                    privilege = "CREATE TABLE";
                    lexer.nextToken();
                } else if (lexer.token == Token.SESSION) {
                    privilege = "CREATE SESSION";
                    lexer.nextToken();
                } else if (lexer.token == Token.TABLESPACE) {
                    privilege = "CREATE TABLESPACE";
                    lexer.nextToken();
                } else if (lexer.token == Token.USER) {
                    privilege = "CREATE USER";
                    lexer.nextToken();
                } else if (lexer.token == Token.VIEW) {
                    privilege = "CREATE VIEW";
                    lexer.nextToken();
                } else if (lexer.token == Token.PROCEDURE) {
                    privilege = "CREATE PROCEDURE";
                    lexer.nextToken();
                } else if (lexer.token == Token.SEQUENCE) {
                    privilege = "CREATE SEQUENCE";
                    lexer.nextToken();
                } else if (lexer.token == Token.ANY) {
                    lexer.nextToken();

                    if (lexer.token == Token.TABLE) {
                        lexer.nextToken();
                        privilege = "CREATE ANY TABLE";
                    } else if (lexer.identifierEquals("MATERIALIZED")) {
                        lexer.nextToken();
                        accept(Token.VIEW);
                        privilege = "CREATE ANY MATERIALIZED VIEW";
                    } else {
                        throw new ParserException("TODO : " + lexer.token + " " + lexer.stringVal());
                    }
                } else if (lexer.identifierEquals("SYNONYM")) {
                    privilege = "CREATE SYNONYM";
                    lexer.nextToken();
                } else if (lexer.identifierEquals("ROUTINE")) {
                    privilege = "CREATE ROUTINE";
                    lexer.nextToken();
                } else if (lexer.identifierEquals("TEMPORARY")) {
                    lexer.nextToken();
                    acceptIdentifier("TABLES");
                    privilege = "CREATE TEMPORARY TABLES";
                } else if (lexer.token == Token.ON) {
                    privilege = "CREATE";
                } else if (lexer.token == Token.COMMA) {
                    privilege = "CREATE";
                } else {
                    throw new ParserException("TODO : " + lexer.token + " " + lexer.stringVal());
                }
            } else if (lexer.token == Token.ALTER) {
                lexer.nextToken();
                if (lexer.token == Token.TABLE) {
                    privilege = "ALTER TABLE";
                    lexer.nextToken();
                } else if (lexer.token == Token.SESSION) {
                    privilege = "ALTER SESSION";
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.ROUTINE)) {
                    privilege = "ALTER ROUTINE";
                    lexer.nextToken();
                } else if (lexer.token == Token.ANY) {
                    lexer.nextToken();

                    if (lexer.token == Token.TABLE) {
                        lexer.nextToken();
                        privilege = "ALTER ANY TABLE";
                    } else if (lexer.identifierEquals("MATERIALIZED")) {
                        lexer.nextToken();
                        accept(Token.VIEW);
                        privilege = "ALTER ANY MATERIALIZED VIEW";
                    } else {
                        throw new ParserException("TODO : " + lexer.token + " " + lexer.stringVal());
                    }
                } else if (lexer.token == Token.ON || lexer.token == Token.COMMA) {
                    privilege = "ALTER";
                } else {
                    throw new ParserException("TODO : " + lexer.token + " " + lexer.stringVal());
                }
            } else if (lexer.token == Token.DROP) {
                lexer.nextToken();
                if (lexer.token == Token.DROP) {
                    privilege = "DROP TABLE";
                    lexer.nextToken();
                } else if (lexer.token == Token.SESSION) {
                    privilege = "DROP SESSION";
                    lexer.nextToken();
                } else if (lexer.token == Token.ANY) {
                    lexer.nextToken();

                    if (lexer.token == Token.TABLE) {
                        lexer.nextToken();
                        privilege = "DROP ANY TABLE";
                    } else if (lexer.identifierEquals("MATERIALIZED")) {
                        lexer.nextToken();
                        accept(Token.VIEW);
                        privilege = "DROP ANY MATERIALIZED VIEW";
                    } else {
                        throw new ParserException("TODO : " + lexer.token + " " + lexer.stringVal());
                    }
                } else {
                    privilege = "DROP";
                }
            } else if (lexer.identifierEquals("USAGE")) {
                privilege = "USAGE";
                lexer.nextToken();
            } else if (lexer.identifierEquals("EXECUTE")) {
                privilege = "EXECUTE";
                lexer.nextToken();
            } else if (lexer.identifierEquals("PROXY")) {
                privilege = "PROXY";
                lexer.nextToken();
            } else if (lexer.identifierEquals("QUERY")) {
                lexer.nextToken();
                acceptIdentifier("REWRITE");
                privilege = "QUERY REWRITE";
            } else if (lexer.identifierEquals("GLOBAL")) {
                lexer.nextToken();
                acceptIdentifier("QUERY");
                acceptIdentifier("REWRITE");
                privilege = "GLOBAL QUERY REWRITE";
            } else if (lexer.identifierEquals("INHERIT")) {
                lexer.nextToken();
                acceptIdentifier("PRIVILEGES");
                privilege = "INHERIT PRIVILEGES";
            } else if (lexer.identifierEquals("EVENT")) {
                lexer.nextToken();
                privilege = "EVENT";
            } else if (lexer.identifierEquals("FILE")) {
                lexer.nextToken();
                privilege = "FILE";
            } else if (lexer.identifierEquals("DESCRIBE")) {
                lexer.nextToken();
                privilege = "DESCRIBE";
            } else if (lexer.token == Token.GRANT) {
                lexer.nextToken();
                acceptIdentifier("OPTION");

                if (lexer.token == Token.FOR) {
                    privilege = "GRANT OPTION FOR";
                    lexer.nextToken();
                } else {
                    privilege = "GRANT OPTION";
                }
            } else if (lexer.token == Token.LOCK) {
                lexer.nextToken();
                acceptIdentifier("TABLES");
                privilege = "LOCK TABLES";
            } else if (lexer.identifierEquals("PROCESS")) {
                lexer.nextToken();
                privilege = "PROCESS";
            } else if (lexer.identifierEquals("RELOAD")) {
                lexer.nextToken();
                privilege = "RELOAD";
            } else if (lexer.identifierEquals("CONNECT")) {
                privilege = "CONNECT";
                lexer.nextToken();
            } else if (lexer.identifierEquals("RESOURCE")) {
                lexer.nextToken();
                privilege = "RESOURCE";
            } else if (lexer.token == Token.CONNECT) {
                lexer.nextToken();
                privilege = "CONNECT";
            } else if (lexer.identifierEquals("REPLICATION")) {
                lexer.nextToken();
                if (lexer.identifierEquals("SLAVE")) {
                    lexer.nextToken();
                    privilege = "REPLICATION SLAVE";
                } else {
                    acceptIdentifier("CLIENT");
                    privilege = "REPLICATION CLIENT";
                }
            } else if (lexer.token == Token.SHOW) {
                lexer.nextToken();

                if (lexer.token == Token.VIEW) {
                    lexer.nextToken();
                    privilege = "SHOW VIEW";
                } else if (lexer.identifierEquals("DATABASES")) {
                    acceptIdentifier("DATABASES");
                    privilege = "SHOW DATABASES";
                } else {
                    privilege = "SHOW";
                }
            } else if (lexer.identifierEquals("SHUTDOWN")) {
                lexer.nextToken();
                privilege = "SHUTDOWN";
            } else if (lexer.identifierEquals("SUPER")) {
                lexer.nextToken();
                privilege = "SUPER";

            } else if (lexer.identifierEquals("CONTROL")) { // sqlserver
                lexer.nextToken();
                privilege = "CONTROL";
            } else if (lexer.identifierEquals("IMPERSONATE")) { // sqlserver
                lexer.nextToken();
                privilege = "IMPERSONATE";
            } else if (lexer.identifierEquals("LOAD")) { // sqlserver
                lexer.nextToken();
                if (lexer.identifierEquals("DATA")) {
                    lexer.nextToken();
                    privilege = "LOAD DATA";
                }
            } else if (lexer.identifierEquals("DUMP")) { // sqlserver
                lexer.nextToken();
                if (lexer.identifierEquals("DATA")) {
                    lexer.nextToken();
                    privilege = "DUMP DATA";
                }
            }

            if (privilege != null) {
                SQLExpr expr = new SQLIdentifierExpr(privilege);

                SQLPrivilegeItem privilegeItem = new SQLPrivilegeItem();
                privilegeItem.setAction(expr);

                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();
                    for(;;) {
                        privilegeItem.getColumns().add(this.exprParser.name());

                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                }


                expr.setParent(parent);
                privileges.add(privilegeItem);
            }

            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
    }

    public SQLRevokeStatement parseRevoke() {
        accept(Token.REVOKE);

        SQLRevokeStatement stmt = new SQLRevokeStatement(dbType);

        if (lexer.token == Token.GRANT) {
            lexer.nextToken();
            acceptIdentifier("OPTION");

            stmt.setGrantOption(true);

            if (lexer.token == Token.FOR) {
                lexer.nextToken();
            }
        }

        parsePrivileages(stmt.getPrivileges(), stmt);

        if (lexer.token == Token.ON) {
            lexer.nextToken();

            switch (lexer.token) {
                case PROCEDURE:
                    lexer.nextToken();
                    stmt.setResourceType(SQLObjectType.PROCEDURE);
                    break;
                case FUNCTION:
                    lexer.nextToken();
                    stmt.setResourceType(SQLObjectType.FUNCTION);
                    break;
                case TABLE:
                    lexer.nextToken();
                    stmt.setResourceType(SQLObjectType.TABLE);
                    break;
                case USER:
                    lexer.nextToken();
                    stmt.setResourceType(SQLObjectType.USER);
                    break;
                case IDENTIFIER:
                    if (lexer.identifierEquals("SYSTEM")) {
                        lexer.nextToken();
                        stmt.setResourceType(SQLObjectType.SYSTEM);
                    } else if (lexer.identifierEquals("PROJECT")) {
                        lexer.nextToken();
                        stmt.setResourceType(SQLObjectType.PROJECT);
                    }

                    break;
                default:
                    break;
            }


            SQLExpr expr = this.exprParser.expr();
            if (stmt.getResourceType() == SQLObjectType.TABLE || stmt.getResourceType() == null) {
                stmt.setResource(new SQLExprTableSource(expr));
            } else {
                stmt.setResource(expr);
            }
        }

        if (lexer.token == Token.FROM) {
            lexer.nextToken();
            for (;;) {
                if (lexer.token() == Token.USER && this.dbType == DbType.odps) {
                    lexer.nextToken();
                }
                SQLExpr user = parseUser();
                stmt.getUsers().add(user);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        }

        return stmt;
    }

    public SQLStatement parseSavePoint() {
        acceptIdentifier("SAVEPOINT");
        SQLSavePointStatement stmt = new SQLSavePointStatement(getDbType());
        stmt.setName(this.exprParser.name());
        return stmt;
    }

    public SQLStatement parseReleaseSavePoint() {
        acceptIdentifier("RELEASE");
        acceptIdentifier("SAVEPOINT");
        SQLReleaseSavePointStatement stmt = new SQLReleaseSavePointStatement(getDbType());
        stmt.setName(this.exprParser.name());
        return stmt;
    }

    public SQLStatement parseAlter() {
        Lexer.SavePoint mark = lexer.mark();
        accept(Token.ALTER);

        if (lexer.token == Token.TABLE) {
            lexer.nextToken();

            SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());

            if (lexer.token == Token.IF) {
                lexer.nextToken();
                accept(Token.EXISTS);
                stmt.setIfExists(true);
            }

            stmt.setName(this.exprParser.name());

            for (;;) {
                if (lexer.token == Token.DROP) {
                    parseAlterDrop(stmt);
                } else if (lexer.identifierEquals(FnvHash.Constants.ADD)) {
                    lexer.nextToken();

                    boolean ifNotExists = false;

                    if (lexer.token == Token.IF) {
                        lexer.nextToken();
                        accept(Token.NOT);
                        accept(Token.EXISTS);
                        ifNotExists = true;
                    }

                    if (lexer.token == Token.PRIMARY) {
                        SQLPrimaryKey primaryKey = this.exprParser.parsePrimaryKey();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(primaryKey);
                        stmt.addItem(item);
                    } else if (lexer.token == UNIQUE) {
                        SQLUnique unique = this.exprParser.parseUnique();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(unique);
                        stmt.addItem(item);
                    } else if (lexer.token == Token.IDENTIFIER) {
                        SQLAlterTableAddColumn item = parseAlterTableAddColumn();
                        stmt.addItem(item);
                    } else if (lexer.token == Token.COLUMN) {
                        lexer.nextToken();
                        SQLAlterTableAddColumn item = parseAlterTableAddColumn();
                        stmt.addItem(item);
                    } else if (lexer.token == Token.CHECK) {
                        SQLCheck check = this.exprParser.parseCheck();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(check);
                        stmt.addItem(item);
                    } else if (lexer.token == Token.CONSTRAINT) {
                        SQLConstraint constraint = this.exprParser.parseConstaint();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(constraint);
                        stmt.addItem(item);
                    } else if (lexer.token == Token.FOREIGN) {
                        SQLConstraint constraint = this.exprParser.parseForeignKey();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(constraint);
                        stmt.addItem(item);
                    } else if (lexer.token == Token.PARTITION) {
                        for (;;) {
                            lexer.nextToken();
                            SQLAlterTableAddPartition addPartition = new SQLAlterTableAddPartition();

                            addPartition.setIfNotExists(ifNotExists);

                            accept(Token.LPAREN);

                            parseAssignItems(addPartition.getPartitions(), addPartition, false);

                            accept(Token.RPAREN);

                            if (lexer.identifierEquals(FnvHash.Constants.LOCATION)) {
                                lexer.nextToken();
                                SQLExpr location = this.exprParser.primary();
                                addPartition.setLocation(location);
                            }

                            stmt.addItem(addPartition);

                            if (lexer.token == Token.PARTITION) {
                                continue;
                            }
                            if (lexer.token == Token.COMMA) {
                                lexer.nextToken();

                                if(lexer.identifierEquals("ADD") || lexer.token == Token.PARTITION) {
                                    continue;
                                }
                            }


                            break;
                        }
                    } else if (lexer.token == DEFAULT) {
                        SQLConstraint constraint = this.exprParser.parseConstaint();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(constraint);
                        stmt.addItem(item);
                    } else {
                        throw new ParserException("TODO " + lexer.info());
                    }
                } else if (lexer.token == Token.DISABLE) {
                    lexer.nextToken();

                    if (lexer.token == Token.CONSTRAINT) {
                        lexer.nextToken();
                        SQLAlterTableDisableConstraint item = new SQLAlterTableDisableConstraint();
                        item.setConstraintName(this.exprParser.name());
                        stmt.addItem(item);
                    } else if (lexer.identifierEquals("LIFECYCLE")) {
                        lexer.nextToken();
                        SQLAlterTableDisableLifecycle item = new SQLAlterTableDisableLifecycle();
                        stmt.addItem(item);
                    } else {
                        acceptIdentifier("KEYS");
                        SQLAlterTableDisableKeys item = new SQLAlterTableDisableKeys();
                        stmt.addItem(item);
                    }
                } else if (lexer.token == Token.ENABLE) {
                    lexer.nextToken();
                    if (lexer.token == Token.CONSTRAINT) {
                        lexer.nextToken();
                        SQLAlterTableEnableConstraint item = new SQLAlterTableEnableConstraint();
                        item.setConstraintName(this.exprParser.name());
                        stmt.addItem(item);
                    } else if (lexer.identifierEquals("LIFECYCLE")) {
                        lexer.nextToken();
                        SQLAlterTableEnableLifecycle item = new SQLAlterTableEnableLifecycle();
                        stmt.addItem(item);
                    } else {
                        acceptIdentifier("KEYS");
                        SQLAlterTableEnableKeys item = new SQLAlterTableEnableKeys();
                        stmt.addItem(item);
                    }
                } else if (lexer.token == Token.ALTER) {
                    lexer.nextToken();
                    if (lexer.token == Token.COLUMN) {
                        SQLAlterTableAlterColumn alterColumn = parseAlterColumn();
                        stmt.addItem(alterColumn);
                    } else if (lexer.token == Token.LITERAL_ALIAS) {
                        SQLAlterTableAlterColumn alterColumn = parseAlterColumn();
                        stmt.addItem(alterColumn);
                    } else {
                        throw new ParserException("TODO " + lexer.info());
                    }
                } else if (lexer.token == Token.DELETE) {
                    lexer.nextToken();
                    if (lexer.token == Token.WHERE) {
                        lexer.nextToken();

                        SQLAlterTableDeleteByCondition alterColumn = new SQLAlterTableDeleteByCondition();
                        alterColumn.setWhere(this.exprParser.expr());
                        stmt.addItem(alterColumn);
                    } else {
                        throw new ParserException("TODO " + lexer.info());
                    }
                } else if (lexer.identifierEquals(FnvHash.Constants.CHANGE)) {
                    lexer.nextToken();
                    if (lexer.token == Token.COLUMN) {
                        lexer.nextToken();
                    }
                    SQLName columnName = this.exprParser.name();

                    if (lexer.identifierEquals("RENAME")) {
                        lexer.nextToken();
                        accept(Token.TO);
                        SQLName toName = this.exprParser.name();
                        SQLAlterTableRenameColumn renameColumn = new SQLAlterTableRenameColumn();

                        renameColumn.setColumn(columnName);
                        renameColumn.setTo(toName);

                        stmt.addItem(renameColumn);
                    } else if (lexer.token == Token.COMMENT) {
                        lexer.nextToken();

                        SQLExpr comment;
                        if (lexer.token == Token.LITERAL_ALIAS) {
                            String alias = lexer.stringVal();
                            if (alias.length() > 2 && alias.charAt(0) == '"' && alias.charAt(alias.length() - 1) == '"') {
                                alias = alias.substring(1, alias.length() - 1);
                            }
                            comment = new SQLCharExpr(alias);
                            lexer.nextToken();
                        } else {
                            comment = this.exprParser.primary();
                        }

                        SQLColumnDefinition column = new SQLColumnDefinition();
                        column.setDbType(dbType);
                        column.setName(columnName);
                        column.setComment(comment);

                        SQLAlterTableAlterColumn changeColumn = new SQLAlterTableAlterColumn();

                        changeColumn.setColumn(column);

                        stmt.addItem(changeColumn);
                    } else {
                        SQLColumnDefinition column = this.exprParser.parseColumn();

                        SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
                        alterColumn.setColumn(column);
                        alterColumn.setOriginColumn(columnName);

                        if (lexer.identifierEquals(FnvHash.Constants.AFTER)) {
                            lexer.nextToken();
                            SQLName after = this.exprParser.name();
                            alterColumn.setAfter(after);
                        }

                        stmt.addItem(alterColumn);
                    }
                } else if (lexer.identifierEquals(FnvHash.Constants.EXCHANGE)) {
                    lexer.nextToken();
                    accept(Token.PARTITION);

                    SQLAlterTableExchangePartition item = new SQLAlterTableExchangePartition();

                    accept(Token.LPAREN);
                    for (;;) {
                        SQLExpr partition = this.exprParser.name();
                        if (lexer.token == Token.EQ) {
                            lexer.nextToken();
                            SQLExpr value = this.exprParser.primary();
                            partition = new SQLAssignItem(partition, value);
                        }

                        item.addPartition(partition);

                        if (lexer.token == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }

                        break;
                    }
                    accept(Token.RPAREN);

                    accept(Token.WITH);
                    accept(Token.TABLE);
                    SQLName table = this.exprParser.name();
                    item.setTable(table);

                    if (lexer.token == Token.WITH) {
                        lexer.nextToken();
                        acceptIdentifier("VALIDATION");
                        item.setValidation(true);
                    } else if (lexer.identifierEquals(FnvHash.Constants.WITHOUT)) {
                        lexer.nextToken();
                        acceptIdentifier("VALIDATION");
                        item.setValidation(false);
                    }


                    stmt.addItem(item);
                } else if (lexer.token == Token.WITH) {
                    lexer.nextToken();
                    acceptIdentifier("NOCHECK");
                    acceptIdentifier("ADD");
                    SQLConstraint check = this.exprParser.parseConstaint();

                    SQLAlterTableAddConstraint addCheck = new SQLAlterTableAddConstraint();
                    addCheck.setWithNoCheck(true);
                    addCheck.setConstraint(check);
                    stmt.addItem(addCheck);
                } else if (lexer.identifierEquals("RENAME")) {
                    stmt.addItem(parseAlterTableRename());
                } else if (lexer.token == Token.SET) {
                    lexer.nextToken();

                    if (lexer.token == Token.COMMENT) {
                        lexer.nextToken();
                        SQLAlterTableSetComment setComment = new SQLAlterTableSetComment();
                        setComment.setComment(this.exprParser.primary());
                        stmt.addItem(setComment);
                    } else if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
                        lexer.nextToken();
                        SQLAlterTableSetLifecycle setLifecycle = new SQLAlterTableSetLifecycle();
                        setLifecycle.setLifecycle(this.exprParser.primary());
                        stmt.addItem(setLifecycle);
                    } else if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
                        lexer.nextToken();
                        SQLAlterTableSetOption setOption = new SQLAlterTableSetOption();
                        accept(Token.LPAREN);
                        for (;;) {
                            SQLAssignItem item = this.exprParser.parseAssignItem();
                            setOption.addOption(item);
                            if (lexer.token == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                            break;
                        }
                        accept(Token.RPAREN);
                        stmt.addItem(setOption);
                    } else if (lexer.identifierEquals("CHANGELOGS") && dbType == DbType.odps) {
                        lexer.nextToken();
                        OdpsAlterTableSetChangeLogs item = new OdpsAlterTableSetChangeLogs();
                        item.setValue(this.exprParser.primary());
                        stmt.addItem(item);
                    } else {
                        throw new ParserException("TODO " + lexer.info());
                    }
                } else if (lexer.token == Token.PARTITION) {
                    lexer.nextToken();

                    SQLAlterTableRenamePartition renamePartition = new SQLAlterTableRenamePartition();

                    accept(Token.LPAREN);

                    parseAssignItems(renamePartition.getPartition(), renamePartition);

                    accept(Token.RPAREN);

                    if (lexer.token == Token.ENABLE) {
                        lexer.nextToken();
                        if (lexer.identifierEquals("LIFECYCLE")) {
                            lexer.nextToken();
                        }

                        SQLAlterTableEnableLifecycle enableLifeCycle = new SQLAlterTableEnableLifecycle();
                        for (SQLAssignItem condition : renamePartition.getPartition()) {
                            enableLifeCycle.getPartition().add(condition);
                            condition.setParent(enableLifeCycle);
                        }
                        stmt.addItem(enableLifeCycle);

                        continue;
                    }

                    if (lexer.token == Token.DISABLE) {
                        lexer.nextToken();
                        if (lexer.identifierEquals("LIFECYCLE")) {
                            lexer.nextToken();
                        }

                        SQLAlterTableDisableLifecycle disableLifeCycle = new SQLAlterTableDisableLifecycle();
                        for (SQLAssignItem condition : renamePartition.getPartition()) {
                            disableLifeCycle.getPartition().add(condition);
                            condition.setParent(disableLifeCycle);
                        }
                        stmt.addItem(disableLifeCycle);

                        continue;
                    }

                    if (DbType.odps == dbType)  {
                        if (lexer.identifierEquals("MERGE")) {
                            SQLAlterTablePartition alterTablePartition = new SQLAlterTablePartition();
                            for (SQLAssignItem condition : renamePartition.getPartition()) {
                                alterTablePartition.getPartition().add(condition);
                                condition.setParent(alterTablePartition);
                            }
                            stmt.addItem(alterTablePartition);
                            continue;
                        } else if (lexer.token == Token.SET) {
                            SQLAlterTablePartitionSetProperties alterTablePartition = new SQLAlterTablePartitionSetProperties();
                            for (SQLAssignItem condition : renamePartition.getPartition()) {
                                alterTablePartition.getPartition().add(condition);
                                condition.setParent(alterTablePartition);
                            }

                            lexer.nextToken();
                            acceptIdentifier("PARTITIONPROPERTIES");
                            accept(LPAREN);
                            this.parseAssignItems(alterTablePartition.getPartitionProperties(), alterTablePartition);
                            accept(RPAREN);
                            stmt.addItem(alterTablePartition);
                            continue;
                        }
                    }

                    acceptIdentifier("RENAME");
                    accept(Token.TO);
                    accept(Token.PARTITION);

                    accept(Token.LPAREN);

                    parseAssignItems(renamePartition.getTo(), renamePartition);

                    accept(Token.RPAREN);

                    stmt.addItem(renamePartition);
                } else if (lexer.identifierEquals("TOUCH")) {
                    lexer.nextToken();
                    SQLAlterTableTouch item = new SQLAlterTableTouch();

                    if (lexer.token == Token.PARTITION) {
                        lexer.nextToken();

                        accept(Token.LPAREN);
                        parseAssignItems(item.getPartition(), item);
                        accept(Token.RPAREN);
                    }

                    stmt.addItem(item);
                } else if (lexer.identifierEquals(FnvHash.Constants.ARCHIVE)) {
                    lexer.nextToken();

                    accept(Token.PARTITION);

                    SQLAlterTableArchivePartition item = new SQLAlterTableArchivePartition();
                    accept(Token.LPAREN);
                    parseAssignItems(item.getPartitions(), item, false);
                    accept(Token.RPAREN);

                    stmt.addItem(item);
                } else if (lexer.identifierEquals(FnvHash.Constants.UNARCHIVE)) {
                    lexer.nextToken();

                    accept(Token.PARTITION);

                    SQLAlterTableUnarchivePartition item = new SQLAlterTableUnarchivePartition();
                    accept(Token.LPAREN);
                    parseAssignItems(item.getPartitions(), item, false);
                    accept(Token.RPAREN);

                    stmt.addItem(item);
                } else if (lexer.identifierEquals(FnvHash.Constants.SUBPARTITION_AVAILABLE_PARTITION_NUM)) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    SQLIntegerExpr num = this.exprParser.integerExpr();
                    SQLAlterTableSubpartitionAvailablePartitionNum item = new SQLAlterTableSubpartitionAvailablePartitionNum();
                    item.setNumber(num);
                    stmt.addItem(item);
                } else if (DbType.odps == dbType && lexer.identifierEquals("MERGE")) {
                    lexer.nextToken();

                    boolean ifExists = false;
                    if (lexer.token == Token.IF) {
                        lexer.nextToken();
                        accept(Token.EXISTS);
                        ifExists = true;
                    }

                    if (lexer.token == PARTITION) {
                        SQLAlterTableMergePartition item = new SQLAlterTableMergePartition();
                        for (;;) {
                            item.addPartition(
                                    this.getExprParser().parsePartitionSpec()
                            );
                            if (lexer.token == COMMA) {
                                lexer.nextToken();
                            } else {
                                break;
                            }
                        }

                        accept(OVERWRITE);
                        item.setOverwritePartition(
                                this.getExprParser().parsePartitionSpec()
                        );

                        if (ifExists) {
                            item.setIfExists(true);
                        }

                        stmt.addItem(item);
                    } else {
                        acceptIdentifier("SMALLFILES");
                        stmt.setMergeSmallFiles(true);
                    }
                } else if (DbType.odps == dbType && lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
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
                } else if (DbType.odps == dbType && lexer.identifierEquals(FnvHash.Constants.SORTED)) {
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
                } else if ((stmt.getClusteredBy().size() > 0 || stmt.getSortedBy().size() > 0) && lexer.token == Token.INTO) {
                    lexer.nextToken();

                    int num;
                    if (lexer.token() == Token.LITERAL_INT) {
                        num = lexer.integerValue().intValue();
                        lexer.nextToken();
                    } else {
                        throw new ParserException("into buckets must be integer. " + lexer.info());
                    }

                    if (lexer.identifierEquals(Constants.BUCKETS)) {
                        stmt.setBuckets(num);
                        lexer.nextToken();
                    } else {
                        acceptIdentifier("SHARDS");
                        stmt.setShards(num);
                    }
                } else if (lexer.token == Token.REPLACE) {
                    SQLAlterTableReplaceColumn item = parseAlterTableReplaceColumn();

                    stmt.addItem(item);
                } else if (DbType.hive == dbType && lexer.identifierEquals(FnvHash.Constants.RECOVER)) {
                    lexer.nextToken();
                    acceptIdentifier("PARTITIONS");
                    stmt.addItem(new SQLAlterTableRecoverPartitions());
                } else {
                    break;
                }
            }

            return stmt;
        } else if (lexer.token == Token.VIEW) {
            lexer.nextToken();
            SQLName viewName = this.exprParser.name();

            if (lexer.identifierEquals("RENAME")) {
                lexer.nextToken();
                accept(Token.TO);

                SQLAlterViewRenameStatement stmt = new SQLAlterViewRenameStatement();
                stmt.setName(viewName);

                SQLName newName = this.exprParser.name();

                stmt.setTo(newName);

                return stmt;
            }
            throw new ParserException("TODO " + lexer.info());
        } else if (lexer.token == Token.INDEX) {
            lexer.reset(mark);
            return parseAlterIndex();
        } else if (lexer.token == Token.DATABASE) {
            lexer.reset(mark);
            return parseAlterDatabase();
        } else if (lexer.token == Token.SCHEMA) {
            lexer.reset(mark);
            return parseAlterSchema();
        } else if (lexer.identifierEquals(Constants.RESOURCE)) {
            lexer.reset(mark);
            return parseAlterResourceGroup();
        }
        throw new ParserException("TODO " + lexer.info());
    }

    protected SQLStatement parseAlterDatabase() {
        throw new ParserException("TODO " + lexer.info());
    }

    protected SQLStatement parseAlterSchema() {
        throw new ParserException("TODO " + lexer.info());
    }

    protected SQLAlterTableItem parseAlterTableRename() {
        acceptIdentifier("RENAME");

        if (lexer.token == Token.COLUMN) {
            lexer.nextToken();
            SQLAlterTableRenameColumn renameColumn = new SQLAlterTableRenameColumn();
            renameColumn.setColumn(this.exprParser.name());
            accept(Token.TO);
            renameColumn.setTo(this.exprParser.name());
            return renameColumn;
        }

        if (lexer.token == Token.TO) {
            lexer.nextToken();
            SQLAlterTableRename item = new SQLAlterTableRename();
            item.setTo(this.exprParser.name());
            return item;
        }

        throw new ParserException("TODO " + lexer.info());
    }

    protected SQLAlterTableAlterColumn parseAlterColumn() {
        lexer.nextToken();
        SQLColumnDefinition column = this.exprParser.parseColumn();

        SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
        alterColumn.setColumn(column);
        return alterColumn;
    }

    public void parseAlterDrop(SQLAlterTableStatement stmt) {
        lexer.nextToken();

        boolean ifExists = false;

        if (lexer.token == Token.IF) {
            lexer.nextToken();

            accept(Token.EXISTS);
            ifExists = true;
        }

        if (lexer.token == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLAlterTableDropConstraint item = new SQLAlterTableDropConstraint();
            item.setConstraintName(this.exprParser.name());
            if (lexer.token == RESTRICT) {
                lexer.nextToken();
                item.setRestrict(true);
            } else if (lexer.token == CASCADE) {
                lexer.nextToken();
                item.setCascade(true);
            }
            stmt.addItem(item);
        } else if (lexer.token == Token.COLUMN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());

            if (lexer.token == Token.CASCADE) {
                item.setCascade(true);
                lexer.nextToken();
            }

            stmt.addItem(item);
        } else if (lexer.token == Token.LITERAL_ALIAS) {
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());

            if (lexer.token == Token.CASCADE) {
                item.setCascade(true);
                lexer.nextToken();
            }

            stmt.addItem(item);
        } else if (lexer.token == Token.PARTITION) {
            {
                SQLAlterTableDropPartition dropPartition = parseAlterTableDropPartition(ifExists);
                stmt.addItem(dropPartition);
            }

            while (lexer.token == COMMA) {
                lexer.nextToken();
                Lexer.SavePoint mark = lexer.mark();
                if (lexer.token == Token.PARTITION) {
                    SQLAlterTableDropPartition dropPartition = parseAlterTableDropPartition(ifExists);
                    stmt.addItem(dropPartition);
                } else {
                    lexer.reset(mark);
                }
            }

        } else if (lexer.token == Token.INDEX) {
            lexer.nextToken();
            SQLName indexName = this.exprParser.name();
            SQLAlterTableDropIndex item = new SQLAlterTableDropIndex();
            item.setIndexName(indexName);
            stmt.addItem(item);
        } else if (lexer.token == Token.PRIMARY) {
            lexer.nextToken();
            accept(Token.KEY);
            SQLAlterTableDropPrimaryKey item = new SQLAlterTableDropPrimaryKey();
            stmt.addItem(item);
        } else {
            throw new ParserException("TODO " + lexer.info());
        }
    }

    protected SQLAlterTableDropPartition parseAlterTableDropPartition(boolean ifExists) {
        lexer.nextToken();
        SQLAlterTableDropPartition dropPartition = new SQLAlterTableDropPartition();

        dropPartition.setIfExists(ifExists);

        if (lexer.token == Token.LPAREN) {
            accept(Token.LPAREN);
            this.exprParser.exprList(dropPartition.getPartitions(), dropPartition);
            accept(Token.RPAREN);

            if (lexer.identifierEquals("PURGE")) {
                lexer.nextToken();
                dropPartition.setPurge(true);
            }
        } else {
            for (;;) {
                SQLExpr partition = this.exprParser.expr();
                dropPartition.addPartition(partition);
                if (lexer.token == COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            dropPartition.getAttributes().put("SIMPLE",true);
        }


        return dropPartition;
    }

    protected SQLAlterTableDropSubpartition parseAlterTableDropSubpartition() {
        lexer.nextToken();
        SQLAlterTableDropSubpartition item = new SQLAlterTableDropSubpartition();

        if (lexer.token() == Token.LITERAL_INT) {
            for (; ; ) {
                item.getPartitionIds().add(this.exprParser.integerExpr());
                String pidStr = lexer.stringVal();
                accept(Token.VARIANT);
                String s = pidStr.replaceAll(":", "");
                if (StringUtils.isEmpty(s)) {
                    item.getSubpartitionIds().add(exprParser.integerExpr());
                } else {
                    item.getSubpartitionIds().add(new SQLIntegerExpr(Integer.valueOf(s)));
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
        }

        return item;
    }

    public SQLStatement parseRename() {
        throw new ParserException("TODO " + lexer.info());
    }

    public SQLStatement parseList() {
        if (lexer.identifierEquals(Constants.LIST)) {
            this.lexer.nextToken();
            if (lexer.identifierEquals(Constants.RESOURCE)) {
                this.lexer.nextToken();
                if (lexer.identifierEquals(Constants.GROUP) || lexer.token == GROUP) {
                    this.lexer.nextToken();
                    return new SQLListResourceGroupStatement();
                }
            }
        }

        return null;
    }

    protected SQLDropTableStatement parseDropTable(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropTableStatement stmt = new SQLDropTableStatement(getDbType());

        if (lexer.identifierEquals(FnvHash.Constants.TEMPORARY)) {
            lexer.nextToken();
            stmt.setTemporary(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.PARTITIONED)) {
            lexer.nextToken();
            stmt.setDropPartition(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            stmt.setExternal(true);
        }

        if (lexer.token == TABLE) {
            lexer.nextToken();
        } else if (lexer.identifierEquals(Constants.TABLES) && dbType == DbType.mysql) {
            lexer.nextToken();
        } else {
            throw new ParserException("expected token: TABLE.");
        }


        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        for (;;) {
            SQLName name = this.exprParser.name();
            stmt.addPartition(new SQLExprTableSource(name));
            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        for (;;) {
            if (lexer.identifierEquals("RESTRICT")) {
                lexer.nextToken();
                stmt.setRestrict(true);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.CASCADE)) {
                lexer.nextToken();
                stmt.setCascade(true);

                if (lexer.identifierEquals("CONSTRAINTS")) { // for oracle
                    lexer.nextToken();
                }

                continue;
            }

            if (lexer.token == Token.PURGE || lexer.identifierEquals("PURGE")) {
                lexer.nextToken();
                stmt.setPurge(true);
                continue;
            }

            break;
        }

        if (stmt.isDropPartition()) {
            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                SQLExpr where = this.exprParser.expr();
                stmt.setWhere(where);
            }

        }



        return stmt;
    }

    protected SQLDropSequenceStatement parseDropSequence(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        lexer.nextToken();

        SQLName name = this.exprParser.name();

        SQLDropSequenceStatement stmt = new SQLDropSequenceStatement(getDbType());
        stmt.setName(name);
        return stmt;
    }

    protected SQLDropTriggerStatement parseDropTrigger(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        lexer.nextToken();
        SQLDropTriggerStatement stmt = new SQLDropTriggerStatement(getDbType());

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();


        stmt.setName(name);
        return stmt;
    }

    protected SQLDropViewStatement parseDropView(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropViewStatement stmt = new SQLDropViewStatement(getDbType());

        accept(Token.VIEW);

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        for (;;) {
            SQLName name = this.exprParser.name();
            stmt.addPartition(new SQLExprTableSource(name));
            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        if (lexer.identifierEquals("RESTRICT")) {
            lexer.nextToken();
            stmt.setRestrict(true);
        } else if (lexer.identifierEquals("CASCADE")) {
            lexer.nextToken();

            if (lexer.identifierEquals("CONSTRAINTS")) { // for oracle
                lexer.nextToken();
            }

            stmt.setCascade(true);
        }

        return stmt;
    }

    protected SQLDropStatement parseDropSchema() {
        throw new ParserException("TODO " + lexer.info());
    }

    protected SQLDropStatement parseDropDatabaseOrSchema(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropDatabaseStatement stmt = new SQLDropDatabaseStatement(getDbType());

        if (lexer.token == Token.SCHEMA) {
            lexer.nextToken();
        } else {
            accept(Token.DATABASE);
        }

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setDatabase(name);

        if (lexer.identifierEquals(FnvHash.Constants.RESTRICT)) {
            lexer.nextToken();
            stmt.setRestrict(true);
        } else if (lexer.token == Token.CASCADE || lexer.identifierEquals(FnvHash.Constants.CASCADE)) {
            lexer.nextToken();
            stmt.setCascade(true);
        } else {
            stmt.setCascade(false);
        }

        return stmt;
    }

    protected SQLDropFunctionStatement parseDropFunction(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropFunctionStatement stmt = new SQLDropFunctionStatement(getDbType());

        accept(Token.FUNCTION);

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    protected SQLDropTableSpaceStatement parseDropTablespace(boolean acceptDrop) {
        SQLDropTableSpaceStatement stmt = new SQLDropTableSpaceStatement(getDbType());

        if (lexer.isKeepComments() && lexer.hasComment()) {
            stmt.addBeforeComment(lexer.readAndResetComments());
        }

        if (acceptDrop) {
            accept(Token.DROP);
        }

        accept(Token.TABLESPACE);

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr engine = this.exprParser.primary();
            stmt.setEngine(engine);
        }

        return stmt;
    }

    protected SQLDropProcedureStatement parseDropProcedure(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropProcedureStatement stmt = new SQLDropProcedureStatement(getDbType());

        accept(Token.PROCEDURE);

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    public SQLStatement parseTruncate() {
        accept(Token.TRUNCATE);
        if (lexer.token == Token.TABLE) {
            lexer.nextToken();
        }
        SQLTruncateStatement stmt = new SQLTruncateStatement(getDbType());

        if (lexer.token == Token.ONLY) {
            lexer.nextToken();
            stmt.setOnly(true);
        }

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        for (;;) {
            SQLName name = this.exprParser.name();
            stmt.addTableSource(name);

            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        if (lexer.token == Token.PARTITION) {
            lexer.nextToken();

            if (lexer.token == LPAREN) {
                accept(Token.LPAREN);
                for (; ; ) {
                    SQLAssignItem item = this.exprParser.parseAssignItem();
                    item.setParent(stmt);
                    stmt.getPartitions().add(item);
                    if (lexer.token == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            } else { // for adb
                if (lexer.token == ALL) {
                    lexer.nextToken();
                    stmt.setPartitionAll(true);
                } else {
                    for (; ; ) {
                        stmt.getPartitionsForADB().add(exprParser.integerExpr());
                        if (lexer.token == COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                }
            }
        }

        for (;;) {
            if (lexer.token == Token.PURGE) {
                lexer.nextToken();

                if (lexer.identifierEquals("SNAPSHOT")) {
                    lexer.nextToken();
                    acceptIdentifier("LOG");
                    stmt.setPurgeSnapshotLog(true);
                } else {
                    throw new ParserException("TODO : " + lexer.token + " " + lexer.stringVal());
                }
                continue;
            }

            if (lexer.token == Token.RESTART) {
                lexer.nextToken();
                accept(Token.IDENTITY);
                stmt.setRestartIdentity(Boolean.TRUE);
                continue;
            } else if (lexer.token == Token.SHARE) {
                lexer.nextToken();
                accept(Token.IDENTITY);
                stmt.setRestartIdentity(Boolean.FALSE);
                continue;
            }

            if (lexer.token == Token.CASCADE) {
                lexer.nextToken();
                stmt.setCascade(Boolean.TRUE);
                continue;
            } else if (lexer.token == Token.RESTRICT) {
                lexer.nextToken();
                stmt.setCascade(Boolean.FALSE);
                continue;
            }

            if (lexer.token == Token.DROP) {
                lexer.nextToken();
                acceptIdentifier("STORAGE");
                stmt.setDropStorage(true);
                continue;
            }

            if (lexer.identifierEquals("REUSE")) {
                lexer.nextToken();
                acceptIdentifier("STORAGE");
                stmt.setReuseStorage(true);
                continue;
            }

            if (lexer.identifierEquals("IGNORE")) {
                lexer.nextToken();
                accept(Token.DELETE);
                acceptIdentifier("TRIGGERS");
                stmt.setIgnoreDeleteTriggers(true);
                continue;
            }

            if (lexer.identifierEquals("RESTRICT")) {
                lexer.nextToken();
                accept(Token.WHEN);
                accept(Token.DELETE);
                acceptIdentifier("TRIGGERS");
                stmt.setRestrictWhenDeleteTriggers(true);
                continue;
            }

            if (lexer.token == Token.CONTINUE) {
                lexer.nextToken();
                accept(Token.IDENTITY);
                continue;
            }

            if (lexer.identifierEquals("IMMEDIATE")) {
                lexer.nextToken();
                stmt.setImmediate(true);
                continue;
            }

            break;
        }

        return stmt;
    }

    public SQLStatement parseInsert() {
        SQLInsertStatement stmt = new SQLInsertStatement();

        if (lexer.token == Token.INSERT) {
            accept(Token.INSERT);
        }

        parseInsert0(stmt);
        return stmt;
    }

    protected void parseInsert0(SQLInsertInto insertStatement) {
        parseInsert0(insertStatement, true);
    }

    protected void parseInsert0_hinits(SQLInsertInto insertStatement) {

    }

    protected void parseInsert0(SQLInsertInto insertStatement, boolean acceptSubQuery) {
        if (lexer.token == Token.INTO) {
            lexer.nextToken();

            SQLName tableName = this.exprParser.name();
            insertStatement.setTableName(tableName);

            if (lexer.token == Token.LITERAL_ALIAS) {
                insertStatement.setAlias(tableAlias());
            }

            parseInsert0_hinits(insertStatement);

            if (lexer.token == Token.IDENTIFIER) {
                insertStatement.setAlias(lexer.stringVal());
                lexer.nextToken();
            }
        }

        if (lexer.token == (Token.LPAREN)) {
            lexer.nextToken();
            parseInsertColumns(insertStatement);
            accept(Token.RPAREN);
        }

        if (lexer.token == Token.VALUES) {
            lexer.nextToken();
            for (;;) {
                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();

                    SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
                    this.exprParser.exprList(values.getValues(), values);
                    insertStatement.addValueCause(values);
                    accept(Token.RPAREN);
                } else { // oracle
                    SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
                    SQLExpr value = this.exprParser.expr();
                    values.addValue(value);
                    insertStatement.addValueCause(values);
                }

                if (lexer.token == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }
        } else if (acceptSubQuery && (lexer.token == Token.SELECT || lexer.token == Token.LPAREN)) {
            SQLSelect select = this.createSQLSelectParser().select();
            insertStatement.setQuery(select);
        } else if (lexer.identifierEquals(FnvHash.Constants.VALUE)) {
            throw new ParserException("'values' expected, but 'value'. " + lexer.info());
        }
    }

    protected void parseInsertColumns(SQLInsertInto insert) {
        this.exprParser.exprList(insert.getColumns(), insert);
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        return false;
    }

    public SQLDropUserStatement parseDropUser() {
        accept(Token.USER);

        SQLDropUserStatement stmt = new SQLDropUserStatement(getDbType());
        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }
        for (;;) {
            if (lexer.token == Token.IF) { // skip for adb
                lexer.nextToken();
                accept(Token.EXISTS);
            }
            SQLExpr expr = this.exprParser.expr();
            stmt.addUser(expr);
            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        return stmt;
    }

    public SQLStatement parseDropIndex() {
        accept(Token.INDEX);
        SQLDropIndexStatement stmt = new SQLDropIndexStatement(getDbType());

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        stmt.setIndexName(this.exprParser.name());

        if (lexer.token == Token.ON) {
            lexer.nextToken();
            stmt.setTableName(this.exprParser.name());
        }

        if (lexer.identifierEquals(FnvHash.Constants.ALGORITHM)) {
            lexer.nextToken();
            if (lexer.token == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr algorithm = this.exprParser.primary();
            stmt.setAlgorithm(algorithm);
        }

        if (lexer.token == Token.LOCK) {
            lexer.nextToken();
            if (lexer.token == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr option = this.exprParser.primary();
            stmt.setLockOption(option);
        }
        // for mysql
        return stmt;
    }

    public SQLCallStatement parseCall() {

        boolean brace = false;
        if (lexer.token == Token.LBRACE) {
            lexer.nextToken();
            brace = true;
        }

        SQLCallStatement stmt = new SQLCallStatement(getDbType());

        if (lexer.token == Token.QUES) {
            lexer.nextToken();
            accept(Token.EQ);
            stmt.setOutParameter(new SQLVariantRefExpr("?"));
        }

        acceptIdentifier("CALL");

        stmt.setProcedureName(exprParser.name());

        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();
            exprParser.exprList(stmt.getParameters(), stmt);
            accept(Token.RPAREN);
        }

        if (brace) {
            accept(Token.RBRACE);
            stmt.setBrace(true);
        }

        return stmt;
    }

    public SQLStatement parseSet() {
        accept(Token.SET);
        SQLSetStatement stmt = new SQLSetStatement(getDbType());

        parseAssignItems(stmt.getItems(), stmt);

        return stmt;
    }

    public void parseAssignItems(List<? super SQLAssignItem> items, SQLObject parent) {
        parseAssignItems(items, parent, true);
    }

    public void parseAssignItems(List<? super SQLAssignItem> items, SQLObject parent, boolean variant) {
        for (;;) {
            SQLAssignItem item = exprParser.parseAssignItem(variant);
            item.setParent(parent);
            items.add(item);

            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            } else {
                break;
            }
        }
    }

    public SQLPartitionRef parsePartitionRef() {
        accept(Token.PARTITION);

        SQLPartitionRef partitionRef = new SQLPartitionRef();

        accept(Token.LPAREN);

        for (;;) {
            SQLIdentifierExpr name = (SQLIdentifierExpr) this.exprParser.name();
            if (lexer.token == Token.EQ) {
                lexer.nextToken();
                SQLExpr value = this.exprParser.expr();
                partitionRef.addItem(name, value);
            } else {
                partitionRef.addItem(new SQLPartitionRef.Item(name));
            }

            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            } else {
                break;
            }
        }

        accept(Token.RPAREN);


        return partitionRef;
    }

    public SQLStatement parseCreatePackage() {
        throw new ParserException("TODO " + lexer.info());
    }

    public SQLStatement parseCreate() {
        char markChar = lexer.current();
        int markBp = lexer.bp();

        List<String> comments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            comments = lexer.readAndResetComments();
        }

        accept(Token.CREATE);



        boolean global = false;
        if (lexer.identifierEquals(FnvHash.Constants.GLOBAL)) {
            lexer.nextToken();
            global = true;
        }

        boolean temporary = false;
        if (lexer.identifierEquals(FnvHash.Constants.TEMPORARY) || lexer.token == Token.TEMPORARY) {
            lexer.nextToken();
            temporary = true;
        }

        boolean nonclustered = false;
        if (lexer.identifierEquals(FnvHash.Constants.NONCLUSTERED)) {
            lexer.nextToken();
            nonclustered = true;
        }


        Token token = lexer.token;
        switch (lexer.token) {
            case TABLE: {
                lexer.reset(markBp, markChar, Token.CREATE);
                SQLCreateTableParser createTableParser = getSQLCreateTableParser();
                SQLCreateTableStatement stmt = createTableParser.parseCreateTable();

                if (temporary) {
                    if (global) {
                        stmt.setType(SQLCreateTableStatement.Type.GLOBAL_TEMPORARY);
                    } else {
                        stmt.setType(SQLCreateTableStatement.Type.TEMPORARY);
                    }
                }

                if (comments != null) {
                    stmt.addBeforeComment(comments);
                }

                return stmt;
            }
            case INDEX:
            case UNIQUE: {
                SQLCreateIndexStatement createIndex = parseCreateIndex(false);
                if (nonclustered) {
                    createIndex.setType("NONCLUSTERED");
                }
                return createIndex;
            }
            case SEQUENCE:
                return parseCreateSequence(false);
            case DATABASE: {
                lexer.nextToken();
                if (lexer.identifierEquals("LINK")) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateDbLink();
                }

                lexer.reset(markBp, markChar, Token.CREATE);
                SQLStatement stmt = parseCreateDatabase();

                if (comments != null) {
                    stmt.addBeforeComment(comments);
                    comments = null;
                }

                return stmt;
            }
            case SCHEMA: {
                lexer.nextToken();
                if (lexer.identifierEquals("LINK")) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateDbLink();
                }

                lexer.reset(markBp, markChar, Token.CREATE);
                SQLStatement stmt = parseCreateSchema();

                if (comments != null) {
                    stmt.addBeforeComment(comments);
                    comments = null;
                }

                return stmt;
            }
            case USER:
                lexer.reset(markBp, markChar, Token.CREATE);
                return parseCreateUser();
            case FUNCTION: {
                lexer.reset(markBp, markChar, Token.CREATE);
                SQLStatement createFunct = this.parseCreateFunction();
                SQLStatement stmt = createFunct;
                return stmt;
            }
            default:
                if (token == Token.OR) {
                    lexer.nextToken();
                    accept(Token.REPLACE);

                    if (lexer.identifierEquals(FnvHash.Constants.FORCE)) {
                        lexer.nextToken();
                    }
                    if (lexer.token == Token.PROCEDURE) {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreateProcedure();
                    }

                    if (lexer.token == Token.VIEW) {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreateView();
                    }

                    if (lexer.token == Token.TRIGGER) {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreateTrigger();
                    }

                    if (lexer.token == Token.FUNCTION) {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreateFunction();
                    }

                    if (lexer.identifierEquals(FnvHash.Constants.PACKAGE)) {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreatePackage();
                    }

                    if (lexer.identifierEquals(FnvHash.Constants.TYPE)) {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreateType();
                    }

                    if (lexer.identifierEquals(FnvHash.Constants.PUBLIC)) {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreateSynonym();
                    }

                    if (lexer.identifierEquals(FnvHash.Constants.SYNONYM)) {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreateSynonym();
                    }

                    // lexer.reset(mark_bp, mark_ch, Token.CREATE);
                    throw new ParserException("TODO " + lexer.info());
                } else if (lexer.identifierEquals(FnvHash.Constants.PUBLIC)) {
                    lexer.nextToken();
                    if (lexer.identifierEquals("SYNONYM")) {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreateSynonym();
                    } else {
                        lexer.reset(markBp, markChar, Token.CREATE);
                        return parseCreateDbLink();
                    }
                } else if (lexer.identifierEquals("SHARE")) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateDbLink();
                } else if (lexer.identifierEquals("SYNONYM")) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateSynonym();
                } else if (token == Token.VIEW) {
                    return parseCreateView();
                } else if (token == Token.TRIGGER) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateTrigger();
                } else if (token == Token.PROCEDURE) {
                    SQLCreateProcedureStatement stmt = parseCreateProcedure();
                    stmt.setCreate(true);
                    return stmt;
                } else if (lexer.identifierEquals(FnvHash.Constants.BITMAP)) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateIndex(true);
                } else if (lexer.identifierEquals(FnvHash.Constants.MATERIALIZED)) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateMaterializedView();
                } else if (lexer.identifierEquals(FnvHash.Constants.TYPE)) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateType();
                } else if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    SQLCreateTableStatement createTable = parseCreateTable();
                    if (comments != null) {
                        createTable.addBeforeComment(comments);
                        comments = null;
                    }
                    return createTable;
                } else if (lexer.identifierEquals(FnvHash.Constants.TABLEGROUP)) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateTableGroup();
                } else if (lexer.identifierEquals(FnvHash.Constants.DIMENSION)) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateTable();
                } else if (lexer.identifierEquals(FnvHash.Constants.ROLE)) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateRole();
                } else if (lexer.identifierEquals(Constants.RESOURCE)) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateResourceGroup();
                } else if (lexer.token() == FOREIGN) {
                    lexer.reset(markBp, markChar, Token.CREATE);
                    return parseCreateTable();
                }

                throw new ParserException("TODO " + lexer.info());
        }

    }

    public SQLStatement parseCreateRole() {
        accept(Token.CREATE);
        acceptIdentifier("ROLE");
        SQLName name = this.exprParser.name();

        SQLCreateRoleStatement stmt = new SQLCreateRoleStatement(dbType);
        stmt.setName(name);

        return stmt;
    }

    public SQLStatement parseCreateType() {
        throw new ParserException("TODO " + lexer.token);
    }

    public SQLStatement parseCreateTableGroup() {
        accept(Token.CREATE);
        acceptIdentifier("TABLEGROUP");


        SQLCreateTableGroupStatement stmt = new SQLCreateTableGroupStatement();

        if (lexer.identifierEquals(FnvHash.Constants.IF)) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        if (lexer.token == Token.PARTITION || lexer.identifierEquals("PARTITION")) {
            lexer.nextToken();
            acceptIdentifier("NUM");
            SQLExpr num = this.exprParser.expr();
            stmt.setPartitionNum(num);
        }

        return stmt;
    }

    public SQLStatement parseCreateUser() {
        accept(Token.CREATE);
        accept(Token.USER);

        SQLCreateUserStatement stmt = new SQLCreateUserStatement();
        stmt.setUser(this.exprParser.name());

        acceptIdentifier("IDENTIFIED");
        accept(Token.BY);
        stmt.setPassword(this.exprParser.primary());

        return stmt;
    }

    public SQLCreateFunctionStatement parseCreateFunction() {
        throw new ParserException("TODO " + lexer.token);
    }

    public SQLStatement parseCreateMaterializedView() {
        accept(Token.CREATE);
        acceptIdentifier("MATERIALIZED");
        accept(Token.VIEW);

        SQLCreateMaterializedViewStatement stmt = new SQLCreateMaterializedViewStatement();
        stmt.setName(this.exprParser.name());

        if (this.dbType == DbType.mysql) {
            stmt.setDbType(DbType.mysql);

            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                for (; ; ) {
                    Token token = lexer.token;

                    if (lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
                        lexer.nextToken();
                        if (lexer.token() == Token.KEY) {
                            MySqlKey clsKey = new MySqlKey();
                            this.exprParser.parseIndex(clsKey.getIndexDefinition());
                            clsKey.setIndexType("CLUSTERED");
                            clsKey.setParent(stmt);
                            stmt.getTableElementList().add(clsKey);

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        } else if (lexer.token() == Token.INDEX) {
                            MySqlTableIndex idx = new MySqlTableIndex();
                            this.exprParser.parseIndex(idx.getIndexDefinition());
                            idx.setIndexType("CLUSTERED");
                            idx.setParent(stmt);
                            stmt.getTableElementList().add(idx);

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        }
                    }

                    if (token == Token.IDENTIFIER) {

                        SQLColumnDefinition column = this.exprParser.parseColumn(stmt);
                        stmt.getTableElementList().add((SQLTableElement) column);
                    } else if (token == Token.PRIMARY //
                            || token == Token.UNIQUE //
                            || token == Token.CHECK //
                            || token == Token.CONSTRAINT
                            || token == Token.FOREIGN) {
                        SQLConstraint constraint = this.exprParser.parseConstaint();
                        constraint.setParent(stmt);
                        stmt.getTableElementList().add((SQLTableElement) constraint);
                    } else if (lexer.token() == (Token.INDEX)) {
                        MySqlTableIndex idx = new MySqlTableIndex();
                        this.exprParser.parseIndex(idx.getIndexDefinition());

                        idx.setParent(stmt);
                        stmt.getTableElementList().add(idx);
                    } else if (lexer.token() == (Token.KEY)) {
                        Lexer.SavePoint savePoint = lexer.mark();
                        lexer.nextToken();

                        boolean isColumn = false;
                        if (lexer.identifierEquals(FnvHash.Constants.VARCHAR)) {
                            isColumn = true;
                        }
                        lexer.reset(savePoint);

                        if (isColumn) {
                            stmt.getTableElementList().add(this.exprParser.parseColumn());
                        } else {
                            SQLName name = null;
                            if (lexer.token() == Token.IDENTIFIER) {
                                name = this.exprParser.name();
                            }

                            MySqlKey key = new MySqlKey();
                            this.exprParser.parseIndex(key.getIndexDefinition());

                            if (name != null) {
                                key.setName(name);
                            }
                            stmt.getTableElementList().add(key);
                        }
                        continue;
                    }
                    if (lexer.token == COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }

            for (; ; ) {
                if (lexer.identifierEquals(FnvHash.Constants.DISTRIBUTED)) {
                    lexer.nextToken();
                    accept(Token.BY);
                    if (lexer.identifierEquals(FnvHash.Constants.HASH)) {
                        lexer.nextToken();
                        accept(Token.LPAREN);
                        for (; ; ) {
                            SQLName name = this.exprParser.name();
                            stmt.getDistributedBy().add(name);
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                            break;
                        }
                        accept(Token.RPAREN);
                        stmt.setDistributedByType(new SQLIdentifierExpr("HASH"));
                    } else if (lexer.identifierEquals(FnvHash.Constants.BROADCAST)) {
                        lexer.nextToken();
                        stmt.setDistributedByType(new SQLIdentifierExpr("BROADCAST"));
                    }
                    continue;
                } else if (lexer.identifierEquals("INDEX_ALL")) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    if (lexer.token() == Token.LITERAL_CHARS) {
                        if ("Y".equalsIgnoreCase(lexer.stringVal())) {
                            lexer.nextToken();
                            stmt.addOption("INDEX_ALL", new SQLCharExpr("Y"));
                        } else if ("N".equalsIgnoreCase(lexer.stringVal())) {
                            lexer.nextToken();
                            stmt.addOption("INDEX_ALL", new SQLCharExpr("N"));
                        } else {
                            throw new ParserException("INDEX_ALL accept parameter ['Y' or 'N'] only.");
                        }
                    }
                    continue;
                } else if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
                    lexer.nextToken();
                    if (lexer.token() == Token.EQ) {
                        lexer.nextToken();
                    }
                    SQLExpr expr = this.exprParser.expr();
                    stmt.addOption("ENGINE", expr);
                    continue;
                } else if (lexer.token == Token.PARTITION) {
                    SQLPartitionBy partitionBy = this.exprParser.parsePartitionBy();
                    stmt.setPartitionBy(partitionBy);
                    continue;
                } else if (lexer.token() == Token.COMMENT) {
                    lexer.nextToken();
                    if (lexer.token() == Token.EQ) {
                        lexer.nextToken();
                    }
                    stmt.setComment(this.exprParser.expr());
                    continue;
                }
                break;
            }
        } else {
            if (lexer.token == Token.PARTITION) {
                SQLPartitionBy partitionBy = this.exprParser.parsePartitionBy();
                stmt.setPartitionBy(partitionBy);
            }
        }

        for (;;) {
            if (exprParser instanceof OracleExprParser) {
                ((OracleExprParser) exprParser).parseSegmentAttributes(stmt);
            }

            if (lexer.identifierEquals("REFRESH")) {
                lexer.nextToken();
                boolean refresh = false;
                for (; ; ) {
                    if (lexer.identifierEquals("FAST")) {
                        lexer.nextToken();
                        stmt.setRefreshFast(true);

                        refresh = true;
                    } else if (lexer.identifierEquals("COMPLETE")) {
                        lexer.nextToken();
                        stmt.setRefreshComplete(true);

                        refresh = true;
                    } else if (lexer.identifierEquals(FnvHash.Constants.FORCE)) {
                        lexer.nextToken();
                        stmt.setRefreshForce(true);

                        refresh = true;
                    } else if (lexer.token == Token.ON) {
                        lexer.nextToken();
                        if (lexer.token == Token.COMMIT || lexer.identifierEquals(FnvHash.Constants.COMMIT)) {
                            lexer.nextToken();
                            stmt.setRefreshOnCommit(true);
                            refresh = true;
                        } else if (lexer.identifierEquals(FnvHash.Constants.OVERWRITE)) {
                            lexer.nextToken();
                            stmt.setRefreshOnOverWrite(true);

                            refresh = true;
                        } else {
                            acceptIdentifier("DEMAND");
                            stmt.setRefreshOnDemand(true);

                            refresh = true;
                        }
                    } else if (lexer.identifierEquals(Constants.START)) {
                        lexer.nextToken();
                        accept(Token.WITH);
                        SQLExpr startWith = this.exprParser.expr();
                        stmt.setStartWith(startWith);
                        stmt.setRefreshStartWith(true);

                        refresh = true;
                    } else if (lexer.identifierEquals(Constants.NEXT)) {
                        lexer.nextToken();
                        SQLExpr next = this.exprParser.expr();
                        stmt.setNext(next);
                        stmt.setRefreshNext(true);

                        refresh = true;
                    } else {
                        break;
                    }
                }

                if (!refresh) {
                    throw new ParserException("refresh clause is empty. " + lexer.info());
                }

            } else if (lexer.identifierEquals("BUILD")) {
                lexer.nextToken();

                if (lexer.identifierEquals("IMMEDIATE") || lexer.token == Token.IMMEDIATE) {
                    lexer.nextToken();
                    stmt.setBuildImmediate(true);
                } else {
                    accept(Token.DEFERRED);
                    stmt.setBuildDeferred(true);
                }
            } else if (lexer.identifierEquals("PARALLEL")) {
                lexer.nextToken();
                stmt.setParallel(true);
                if (lexer.token == Token.LITERAL_INT) {
                    stmt.setParallelValue(lexer.integerValue().intValue());
                    lexer.nextToken();
                }
            } else if (lexer.identifierEquals(FnvHash.Constants.NOCACHE) || lexer.token == Token.NOCACHE) {
                lexer.nextToken();
                stmt.setCache(false);
            } else if (lexer.identifierEquals(FnvHash.Constants.NOPARALLEL)) {
                lexer.nextToken();
                stmt.setParallel(false);
            } else if (lexer.token == Token.WITH) {
                lexer.nextToken();
                acceptIdentifier("ROWID");
                stmt.setWithRowId(true);
            } else {
                break;
            }
        }

        Boolean enableQueryRewrite = null;
        if (lexer.token == Token.ENABLE) {
            lexer.nextToken();
            enableQueryRewrite = true;
        }

        if (lexer.token == Token.DISABLE) {
            lexer.nextToken();
            enableQueryRewrite = false;
        }

        if (enableQueryRewrite != null) {
            acceptIdentifier("QUERY");
            acceptIdentifier("REWRITE");
            stmt.setEnableQueryRewrite(enableQueryRewrite);
        }

        accept(Token.AS);
        SQLSelect select = this.createSQLSelectParser().select();
        stmt.setQuery(select);

        return stmt;
    }

    public SQLStatement parseCreateDbLink() {
        throw new ParserException("TODO " + lexer.token);
    }

    public SQLStatement parseCreateSynonym() {
        throw new ParserException("TODO " + lexer.token);
    }

    public SQLStatement parseCreateExternalCatalog() {
        MySqlCreateExternalCatalogStatement stmt = new MySqlCreateExternalCatalogStatement();

        if (lexer.token == Token.CREATE) {
            lexer.nextToken();
        }

        acceptIdentifier("EXTERNAL");
        acceptIdentifier("CATALOG");

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        acceptIdentifier("PROPERTIES");
        accept(Token.LPAREN);

        for (;;) {
            SQLName key = this.exprParser.name();
            accept(Token.EQ);
            SQLName value = this.exprParser.name();

            stmt.getProperties().put(key, value);

            if (lexer.token == Token.RPAREN) {
                accept(Token.RPAREN);
                break;
            }
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLName comment = this.exprParser.name();
            stmt.setComment(comment);
        }

        return stmt;
    }

    public SQLStatement parseCreateTrigger() {
        SQLCreateTriggerStatement stmt = new SQLCreateTriggerStatement(getDbType());

        if (lexer.token == Token.CREATE) {
            lexer.nextToken();

            if (lexer.token == Token.OR) {
                lexer.nextToken();
                accept(Token.REPLACE);

                stmt.setOrReplace(true);
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.DEFINER)) {
            lexer.nextToken();
            accept(Token.EQ);
            SQLName definer = ((MySqlExprParser) this.exprParser).userName();
            stmt.setDefiner(definer);

            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                accept(Token.RPAREN);
            }
        }

        accept(Token.TRIGGER);


        stmt.setName(this.exprParser.name());

        if (lexer.identifierEquals(FnvHash.Constants.BEFORE)) {
            stmt.setTriggerType(TriggerType.BEFORE);
            lexer.nextToken();
        } else if (lexer.identifierEquals(FnvHash.Constants.AFTER)) {
            stmt.setTriggerType(TriggerType.AFTER);
            lexer.nextToken();
        } else if (lexer.identifierEquals(FnvHash.Constants.INSTEAD)) {
            lexer.nextToken();
            accept(Token.OF);
            stmt.setTriggerType(TriggerType.INSTEAD_OF);
        }

        for (;;) {
            if (lexer.token == Token.INSERT) {
                lexer.nextToken();
                stmt.setInsert(true);
            } else if (lexer.token == Token.UPDATE) {
                lexer.nextToken();
                stmt.setUpdate(true);

                if (lexer.token == Token.OF) {
                    lexer.nextToken();
                    this.exprParser.names(stmt.getUpdateOfColumns(), stmt);
                }
            } else if (lexer.token == Token.DELETE) {
                lexer.nextToken();
                stmt.setDelete(true);
            }

            if (lexer.token == Token.COMMA
                    || lexer.token == Token.OR) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        accept(Token.ON);
        stmt.setOn(this.exprParser.name());

        if (lexer.token == Token.FOR) {
            lexer.nextToken();
            acceptIdentifier("EACH");
            accept(Token.ROW);
            stmt.setForEachRow(true);
        }

        if (lexer.token == Token.WHEN) {
            lexer.nextToken();
            SQLExpr condition = this.exprParser.expr();
            stmt.setWhen(condition);
        }

        List<SQLStatement> body = this.parseStatementList();
        if (body == null || body.isEmpty()) {
            throw new ParserException("syntax error");
        }
        stmt.setBody(body.get(0));
        return stmt;
    }

    public SQLStatement parseBlock() {
        throw new ParserException("TODO " + lexer.info());
    }

    public SQLStatement parseCreateSchema() {
        throw new ParserException("TODO " + lexer.info());
    }

    public SQLStatement parseCreateDatabase() {
        SQLCreateDatabaseStatement stmt = new SQLCreateDatabaseStatement(dbType);

        if (lexer.hasComment() && lexer.isKeepComments()) {
            stmt.addBeforeComment(lexer.readAndResetComments());
        }

        if (lexer.token == Token.CREATE) {
            lexer.nextToken();
        }

        if (lexer.token == Token.SCHEMA && dbType == DbType.hive) {
            lexer.nextToken();
        } else {
            accept(Token.DATABASE);
        }



        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token == Token.COMMENT) {
            lexer.nextToken();
            if (lexer.token == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOCATION)) {
            lexer.nextToken();
            SQLExpr location = this.exprParser.expr();
            stmt.setLocation(location);
        }

        if (lexer.token == Token.WITH) {
            lexer.nextToken();

            if (lexer.identifierEquals(FnvHash.Constants.DBPROPERTIES)) {
                lexer.nextToken();
                accept(Token.LPAREN);
                for (;;) {
                    SQLAssignItem assignItem = this.exprParser.parseAssignItem();
                    assignItem.setParent(stmt);
                    stmt.getDbProperties().add(assignItem);

                    if (lexer.token == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            } else {
                throw new ParserException("TODO " + lexer.info());
            }
        }

        if (lexer.token == Token.COMMENT) {
            lexer.nextToken();
            if (lexer.token == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

        return stmt;
    }

    public SQLCreateProcedureStatement parseCreateProcedure() {
        throw new ParserException("TODO " + lexer.token);
    }

    public SQLStatement parseCreateSequence(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        SQLCreateSequenceStatement stmt = new SQLCreateSequenceStatement();

        if(lexer.token == Token.GROUP) {
            lexer.nextToken();
            stmt.setGroup(true);
        } else if (lexer.identifierEquals(FnvHash.Constants.SIMPLE)) {
            lexer.nextToken();
            stmt.setSimple(true);
            if (lexer.token == Token.WITH) {
                lexer.nextToken();
                accept(Token.CACHE);
                stmt.setWithCache(true);
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.TIME)) {
            lexer.nextToken();
            stmt.setTime(true);
        }

        accept(Token.SEQUENCE);


        stmt.setDbType(dbType);
        stmt.setName(this.exprParser.name());

        for (;;) {
            if (lexer.token() == Token.START || lexer.identifierEquals(FnvHash.Constants.START)) {
                lexer.nextToken();
                accept(Token.WITH);
                stmt.setStartWith(this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.INCREMENT)) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setIncrementBy(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.CACHE || lexer.identifierEquals(FnvHash.Constants.CACHE)) {
                lexer.nextToken();
                stmt.setCache(Boolean.TRUE);

                if (lexer.token() == Token.LITERAL_INT) {
                    stmt.setCacheValue(this.exprParser.primary());
                }
                continue;
            } else if (lexer.token == Token.WITH) {
                lexer.nextToken();
                accept(Token.CACHE);
                stmt.setCache(true);
                continue;
            } else if (lexer.token() == Token.NOCACHE || lexer.identifierEquals(FnvHash.Constants.NOCACHE)) {
                lexer.nextToken();
                stmt.setCache(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.ORDER) {
                lexer.nextToken();
                stmt.setOrder(Boolean.TRUE);
                continue;
            } else if (lexer.identifierEquals("NOORDER")) {
                lexer.nextToken();
                stmt.setOrder(Boolean.FALSE);
                continue;
            } else if (lexer.identifierEquals("CYCLE")) {
                lexer.nextToken();
                stmt.setCycle(Boolean.TRUE);
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.NOCYCLE)) {
                lexer.nextToken();
                stmt.setCycle(Boolean.FALSE);
                continue;
            } else if (lexer.identifierEquals("MINVALUE")) {
                lexer.nextToken();
                stmt.setMinValue(this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals("MAXVALUE")) {
                lexer.nextToken();
                stmt.setMaxValue(this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals("NOMAXVALUE")) {
                lexer.nextToken();
                stmt.setNoMaxValue(true);
                continue;
            } else if (lexer.identifierEquals("NOMINVALUE")) {
                lexer.nextToken();
                stmt.setNoMinValue(true);
                continue;
            }
            break;
        }

        if (lexer.identifierEquals(FnvHash.Constants.UNIT)) {
            lexer.nextToken();

            if (lexer.identifierEquals(FnvHash.Constants.COUNT)) {
                lexer.nextToken();
                SQLExpr unitCount = this.exprParser.primary();
                stmt.setUnitCount(unitCount);
            }

            if (lexer.token == Token.INDEX) {
                lexer.nextToken();
                SQLExpr unitIndex = this.exprParser.primary();
                stmt.setUnitIndex(unitIndex);
            }

            if (lexer.hash_lower() == FnvHash.Constants.STEP) {
                lexer.nextToken();
                SQLExpr step = this.exprParser.primary();
                stmt.setStep(step);
            }

        }

        return stmt;
    }

    public SQLCreateIndexStatement parseCreateIndex(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        SQLCreateIndexStatement stmt = new SQLCreateIndexStatement(getDbType());
        if (lexer.token == Token.UNIQUE) {
            lexer.nextToken();
            if (lexer.identifierEquals("CLUSTERED")) {
                lexer.nextToken();
                stmt.setType("UNIQUE CLUSTERED");
            } else if (lexer.identifierEquals("NONCLUSTERED")) {
                stmt.setType("UNIQUE NONCLUSTERED");
                lexer.nextToken();
            } else {
                stmt.setType("UNIQUE");
            }
        } else if (lexer.token() == Token.FULLTEXT) {
            stmt.setType("FULLTEXT");
            lexer.nextToken();
        } else if (lexer.identifierEquals("NONCLUSTERED")) {
            stmt.setType("NONCLUSTERED");
            lexer.nextToken();
        }

        accept(Token.INDEX);

        stmt.setName(this.exprParser.name());

        accept(Token.ON);

        stmt.setTable(this.exprParser.name());

        accept(Token.LPAREN);

        for (;;) {
            SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
            item.setParent(stmt);
            stmt.addItem(item);
            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);

        return stmt;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new SQLCreateTableParser(this.exprParser);
    }

    public SQLStatement parseSelect() {
        SQLSelectParser selectParser = createSQLSelectParser();
        SQLSelect select = selectParser.select();
        return new SQLSelectStatement(select,getDbType());
    }

    public SQLSelectParser createSQLSelectParser() {
        return new SQLSelectParser(this.exprParser, selectListCache);
    }

    public SQLSelectParser createSQLSelectParser(SQLExprParser exprParser) {
        return new SQLSelectParser(exprParser);
    }

    public SQLUpdateStatement parseUpdateStatement() {
        SQLUpdateStatement udpateStatement = createUpdateStatement();

        if (lexer.token == Token.UPDATE) {
            lexer.nextToken();

            SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
            udpateStatement.setTableSource(tableSource);
        }

        parseUpdateSet(udpateStatement);

        if (lexer.token == (Token.WHERE)) {
            lexer.nextToken();
            udpateStatement.setWhere(this.exprParser.expr());
        }

        return udpateStatement;
    }

    protected void parseUpdateSet(SQLUpdateStatement update) {
        accept(Token.SET);

        for (;;) {
            SQLUpdateSetItem item = this.exprParser.parseUpdateSetItem();
            update.addItem(item);

            if (lexer.token != Token.COMMA) {
                break;
            }

            lexer.nextToken();
        }
    }

    protected SQLUpdateStatement createUpdateStatement() {
        return new SQLUpdateStatement(getDbType());
    }

    public SQLDeleteStatement parseDeleteStatement() {
        SQLDeleteStatement deleteStatement = new SQLDeleteStatement(getDbType());

        if (lexer.token == Token.DELETE) {
            lexer.nextToken();
            if (lexer.token == (Token.FROM)) {
                lexer.nextToken();
            }

            if (lexer.token == Token.COMMENT) {
                lexer.nextToken();
            }

            SQLName tableName = exprParser.name();

            deleteStatement.setTableName(tableName);

            if (lexer.token == Token.FROM) {
                lexer.nextToken();
                SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
                deleteStatement.setFrom(tableSource);
            }
        }

        if (lexer.token == (Token.WHERE)) {
            lexer.nextToken();
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        return deleteStatement;
    }

    public SQLCreateTableStatement parseCreateTable() {
        SQLCreateTableParser parser = new SQLCreateTableParser(this.exprParser);
        return parser.parseCreateTable();
    }

    public SQLCreateViewStatement parseCreateView() {
        SQLCreateViewStatement createView = new SQLCreateViewStatement(getDbType());

        if (lexer.token == Token.CREATE) {
            lexer.nextToken();
        }

        if (lexer.token == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            createView.setOrReplace(true);
        }

        if (lexer.identifierEquals("ALGORITHM")) {
            lexer.nextToken();
            accept(Token.EQ);
            String algorithm = lexer.stringVal();
            createView.setAlgorithm(algorithm);
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.DEFINER)) {
            lexer.nextToken();
            accept(Token.EQ);
            SQLName definer = (SQLName) ((MySqlExprParser) this.exprParser).userName();
            createView.setDefiner(definer);
        }

        if (lexer.identifierEquals(FnvHash.Constants.SQL)) {
            lexer.nextToken();
            acceptIdentifier("SECURITY");
            String sqlSecurity = lexer.stringVal();
            createView.setSqlSecurity(sqlSecurity);
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.FORCE)) {
            lexer.nextToken();
            createView.setForce(true);
        }

        this.accept(Token.VIEW);

        if (lexer.token == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            createView.setIfNotExists(true);
        }

        createView.setName(exprParser.name());

        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();

            for (;;) {

                if (lexer.token == Token.CONSTRAINT) {
                    SQLTableConstraint constraint = (SQLTableConstraint) this.exprParser.parseConstaint();
                    createView.addColumn(constraint);
                } else {
                    SQLColumnDefinition column = new SQLColumnDefinition();
                    column.setDbType(dbType);
                    SQLName expr = this.exprParser.name();
                    column.setName(expr);

                    if (dbType == DbType.odps && expr.getSimpleName().startsWith("@")) {
                        column.setDataType(this.exprParser.parseDataType());
                    }

                    this.exprParser.parseColumnRest(column);

                    if (lexer.token == Token.COMMENT) {
                        lexer.nextToken();

                        SQLExpr comment;
                        if (lexer.token == Token.LITERAL_ALIAS) {
                            String alias = lexer.stringVal();
                            if (alias.length() > 2 && alias.charAt(0) == '"' && alias.charAt(alias.length() - 1) == '"') {
                                alias = alias.substring(1, alias.length() - 1);
                            }
                            comment = new SQLCharExpr(alias);
                            lexer.nextToken();
                        } else {
                            comment = this.exprParser.primary();
                        }
                        column.setComment(comment);
                    }

                    column.setParent(createView);
                    createView.addColumn(column);
                }

                if (lexer.token == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }

            accept(Token.RPAREN);
        }

        if (lexer.token == Token.COMMENT) {
            lexer.nextToken();
            SQLCharExpr comment = (SQLCharExpr) exprParser.primary();
            createView.setComment(comment);
        }

        this.accept(Token.AS);

        SQLSelectParser selectParser = this.createSQLSelectParser();
        createView.setSubQuery(selectParser.select());

        if (lexer.token == Token.WITH) {
            lexer.nextToken();

            if (lexer.identifierEquals("CASCADED")) {
                createView.setWithCascaded(true);
                lexer.nextToken();
            } else if (lexer.identifierEquals("LOCAL")){
                createView.setWithLocal(true);
                lexer.nextToken();
            } else if (lexer.identifierEquals("READ")) {
                lexer.nextToken();
                accept(Token.ONLY);
                createView.setWithReadOnly(true);
            }

            if (lexer.token == Token.CHECK) {
                lexer.nextToken();
                acceptIdentifier("OPTION");
                createView.setWithCheckOption(true);
            }
        }

        return createView;
    }

    public SQLCommentStatement parseComment() {
        accept(Token.COMMENT);
        SQLCommentStatement stmt = new SQLCommentStatement();

        accept(Token.ON);

        if (lexer.token == Token.TABLE) {
            stmt.setType(SQLCommentStatement.Type.TABLE);
            lexer.nextToken();
        } else if (lexer.token == Token.COLUMN) {
            stmt.setType(SQLCommentStatement.Type.COLUMN);
            lexer.nextToken();
        }

        stmt.setOn(this.exprParser.name());

        accept(Token.IS);
        stmt.setComment(this.exprParser.expr());

        return stmt;
    }

    protected SQLAlterTableAddColumn parseAlterTableAddColumn() {
        boolean odps = DbType.odps == dbType || DbType.hive == dbType;

        if (odps) {
            acceptIdentifier("COLUMNS");
            accept(Token.LPAREN);
        }

        SQLAlterTableAddColumn item = new SQLAlterTableAddColumn();

        for (;;) {
            SQLColumnDefinition columnDef = this.exprParser.parseColumn();
            item.addColumn(columnDef);

            if (lexer.token == Token.WITH) {
                Lexer.SavePoint mark = lexer.mark();
                lexer.nextToken();
                if (lexer.token == Token.DEFAULT) {
                    lexer.nextToken();
                    SQLExpr defaultExpr = this.exprParser.expr();
                    columnDef.setDefaultExpr(defaultExpr);
                } else {
                    lexer.reset(mark);
                }
            }

            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                if (lexer.identifierEquals("ADD")) {
                    break;
                }
                continue;
            }
            break;
        }

        if (odps) {
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.RESTRICT)) {
            lexer.nextToken();
            item.setRestrict(true);
        } else if (lexer.token() == Token.CASCADE || lexer.identifierEquals(FnvHash.Constants.CASCADE)) {
            lexer.nextToken();
            item.setCascade(true);
        } else {
            item.setCascade(false);
        }

        return item;
    }

    protected SQLAlterTableReplaceColumn parseAlterTableReplaceColumn() {
        accept(Token.REPLACE);
        acceptIdentifier("COLUMNS");

        SQLAlterTableReplaceColumn item = new SQLAlterTableReplaceColumn();

        accept(Token.LPAREN);
        for (;;) {
            SQLColumnDefinition columnDef = this.exprParser.parseColumn();
            item.addColumn(columnDef);
            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                if (lexer.identifierEquals(FnvHash.Constants.ADD)) {
                    break;
                }
                continue;
            }
            break;
        }
        accept(Token.RPAREN);

        return item;
    }

    public SQLStatement parseStatement() {
        if (lexer.token == Token.SELECT) {
            return this.parseSelect();
        }

        if (lexer.token == Token.INSERT) {
            return this.parseInsert();
        }


        if (lexer.token == Token.UPDATE) {
            return this.parseUpdateStatement();
        }

        if (lexer.token == Token.DELETE) {
            return this.parseDeleteStatement();
        }

        List<SQLStatement> list = new ArrayList<SQLStatement>(1);
        this.parseStatementList(list, 1, null);
        return list.get(0);
    }

    /**
     * @param tryBest  - 为true去解析并忽略之后的错误
     *  强制建议除非明确知道可以忽略才传tryBest=true,
     *  不然会忽略语法错误，且截断sql,导致update和delete无where条件下执行！！！
     */
    public SQLStatement parseStatement( final boolean tryBest) {
        List<SQLStatement> list = new ArrayList<SQLStatement>();
        this.parseStatementList(list, 1, null);
        if (tryBest) {
            if (lexer.token != Token.EOF) {
                throw new ParserException("sql syntax error, no terminated. " + lexer.token);
            }
        }
        return list.get(0);
    }

    public SQLExplainStatement parseExplain() {
        accept(Token.EXPLAIN);
        if (lexer.identifierEquals("PLAN")) {
            lexer.nextToken();
        }

        if (lexer.token == Token.FOR) {
            lexer.nextToken();
        }

        SQLExplainStatement explain = new SQLExplainStatement(dbType);

        if (lexer.token == Token.ANALYZE || lexer.identifierEquals(FnvHash.Constants.ANALYZE)) {
            lexer.nextToken();
            explain.setType("ANALYZE");
        }

        if (lexer.token == Token.HINT) {
            explain.setHints(this.exprParser.parseHints());
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTENDED)) {
            lexer.nextToken();
            explain.setExtended(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.DEPENDENCY)) {
            lexer.nextToken();
            explain.setDependency(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.AUTHORIZATION)) {
            lexer.nextToken();
            explain.setAuthorization(true);
        }

        if (DbType.mysql == dbType) {
            if (lexer.identifierEquals("FORMAT")
                    || lexer.identifierEquals("PARTITIONS")) {
                explain.setType(lexer.stringVal);
                lexer.nextToken();
            }
        }

        if (DbType.mysql == dbType || DbType.ads == dbType || DbType.presto == dbType) {
            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();

                if (lexer.identifierEquals("FORMAT")) {
                    lexer.nextToken();
                    String type = "FORMAT " + lexer.stringVal;
                    lexer.nextToken();
                } else if (lexer.identifierEquals("TYPE")) {
                    lexer.nextToken();
                    String type = "TYPE " + lexer.stringVal;
                    lexer.nextToken();
                }

                accept(Token.RPAREN);
            }
        }

        explain.setStatement(parseStatement());

        return explain;
    }

    protected SQLAlterTableAddClusteringKey parseAlterTableAddClusteringKey() {
        lexer.nextToken();
        SQLAlterTableAddClusteringKey  item = new SQLAlterTableAddClusteringKey();
        accept(Token.KEY);
        item.setName(exprParser.name());
        accept(LPAREN);
        for (; ; ) {
            item.getColumns().add(exprParser.name());
            if (lexer.token == COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(RPAREN);
        return item;
    }

    /*
    protected SQLAlterTableAddIndex parseAlterTableAddIndex() {
        SQLAlterTableAddIndex item = new SQLAlterTableAddIndex();

        if (lexer.token() == Token.FULLTEXT) {
            lexer.nextToken();
            item.setType("FULLTEXT");
        } else if (lexer.identifierEquals(FnvHash.Constants.SPATIAL)) {
            lexer.nextToken();
            item.setType("SPATIAL");
        } else if (lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
            lexer.nextToken();
            item.setType("CLUSTERED");
        } else if (lexer.identifierEquals(FnvHash.Constants.ANN)) {
            lexer.nextToken();
            item.setType("ANN");
        }

        if (lexer.identifierEquals(FnvHash.Constants.GLOBAL)) {
            item.setGlobal(true);
            lexer.nextToken();
        }

        if (lexer.token == Token.UNIQUE) {
            item.setUnique(true);
            lexer.nextToken();
            if (lexer.token == Token.INDEX) {
                lexer.nextToken();
            } else if (lexer.token == Token.KEY) {
                item.setKey(true);
                lexer.nextToken();
            }
        } else {
            if (lexer.token == Token.INDEX) {
                accept(Token.INDEX);
            } else if (lexer.token == Token.KEY) {
                item.setKey(true);
                accept(Token.KEY);
            }
        }

        if (lexer.token != Token.LPAREN) {
            item.setName(this.exprParser.name());

            if (DbType.mysql == dbType) {
                if (lexer.identifierEquals("HASHMAP")) {
                    lexer.nextToken();
                    item.setHashMapType(true);
                }
                else if (lexer.identifierEquals(FnvHash.Constants.USING)) {
                    lexer.nextToken();
                    String indexType = lexer.stringVal;
                    item.setType(indexType);
                    accept(Token.IDENTIFIER);
                }
            }


        }

        this.exprParser.parseIndexRest(item);

        if (dbType == DbType.mysql) {
            for (; ; ) {
                if (lexer.identifierEquals(FnvHash.Constants.DISTANCEMEASURE)
                        || lexer.identifierEquals(FnvHash.Constants.ALGORITHM)
                        || lexer.token == Token.LOCK) {
                    String name = lexer.stringVal();
                    lexer.nextToken();
                    if (lexer.token() == Token.EQ) {
                        lexer.nextToken();
                    }
                    SQLExpr option = this.exprParser.primary();
                    item.addOption(name, option);
                } else {
                    break;
                }
            }
        }

        if (DbType.mysql == dbType) {
            if (lexer.identifierEquals(FnvHash.Constants.USING)) {
                lexer.nextToken();
                String indexType = lexer.stringVal;
                item.setType(indexType);
                accept(Token.IDENTIFIER);
            }

            if (lexer.identifierEquals(FnvHash.Constants.DBPARTITION)) {
                SQLPartitionBy partitionClause = this.getSQLCreateTableParser().parsePartitionBy();
                item.setDbPartitionBy(partitionClause);
            }

            if (lexer.identifierEquals(FnvHash.Constants.TBPARTITION)) {
                lexer.nextToken();
                accept(Token.BY);
                SQLExpr expr = this.exprParser.expr();
                if (lexer.identifierEquals(FnvHash.Constants.STARTWITH)) {
                    lexer.nextToken();
                    SQLExpr start = this.exprParser.primary();
                    acceptIdentifier("ENDWITH");
                    SQLExpr end = this.exprParser.primary();
                    expr = new SQLBetweenExpr(expr, start, end);
                }
                item.setTablePartitionBy(expr);
            }

            if (lexer.identifierEquals(FnvHash.Constants.TBPARTITIONS)) {
                lexer.nextToken();
                SQLExpr tbPartitions = this.exprParser.primary();
                item.setTablePartitions(tbPartitions);
            }
        }

        for (;;) {
            if (lexer.token == Token.COMMENT) {
                lexer.nextToken();
                SQLExpr comment = this.exprParser.primary();
                item.setComment(comment);
            } else if (DbType.mysql == dbType) {
                if (lexer.identifierEquals(FnvHash.Constants.KEY_BLOCK_SIZE)) {
                    lexer.nextToken();
                    if (lexer.token() == Token.EQ) {
                        lexer.nextToken();
                    }
                    SQLExpr keyBlockSize = this.exprParser.primary();
                    item.setKeyBlockSize(keyBlockSize);
                } else if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    acceptIdentifier("PARSER");
                    item.setParserName(lexer.stringVal);
                    accept(Token.IDENTIFIER);
                } else if (lexer.identifierEquals(FnvHash.Constants.USING)) {
                    // Or index_type in index_option.
                    lexer.nextToken();
                    item.setType(lexer.stringVal);
                    accept(Token.IDENTIFIER);
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return item;
    }
    */

    /**
     * parse cursor open statement
     *
     * @return
     */
    public SQLOpenStatement parseOpen() {
        SQLOpenStatement stmt = new SQLOpenStatement();
        accept(Token.OPEN);

        final SQLName cursorName;
        if (lexer.token == Token.QUES) {
            lexer.nextToken();
            cursorName = new SQLIdentifierExpr("?");
        } else {
            cursorName = exprParser.name();
        }
        stmt.setCursorName(cursorName);

        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();
            this.exprParser.names(stmt.getColumns(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.token == Token.FOR) {
            lexer.nextToken();
            if (lexer.token == Token.SELECT) {
                SQLSelectParser selectParser = createSQLSelectParser();
                SQLSelect select = selectParser.select();
                SQLQueryExpr queryExpr = new SQLQueryExpr(select);
                stmt.setFor(queryExpr);
            } else if (lexer.token == Token.LITERAL_CHARS) {
                String chars = lexer.stringVal;
                SQLExprParser exprParser = SQLParserUtils.createExprParser(chars, dbType);
                SQLSelectParser selectParser = this.createSQLSelectParser(exprParser);
                SQLSelect select = selectParser.select();
                SQLQueryExpr queryExpr = new SQLQueryExpr(select);
                stmt.setFor(queryExpr);

                lexer.nextToken();
            } else if (lexer.token == Token.QUES) {
                lexer.nextToken();
                stmt.setFor(new SQLVariantRefExpr("?"));
            } else {
                throw new ParserException("TODO " + lexer.info());
            }
        }

        if (lexer.token == Token.USING) {
            lexer.nextToken();
            this.exprParser.exprList(stmt.getUsing(), stmt);
        }

        accept(Token.SEMI);
        stmt.setAfterSemi(true);
        return stmt;
    }

    public SQLFetchStatement parseFetch() {
        accept(Token.FETCH);

        SQLFetchStatement stmt = new SQLFetchStatement();
        stmt.setCursorName(this.exprParser.name());

        if (lexer.identifierEquals("BULK")) {
            lexer.nextToken();
            acceptIdentifier("COLLECT");
            stmt.setBulkCollect(true);
        }

        accept(Token.INTO);
        for (;;) {
            stmt.getInto().add(this.exprParser.name());
            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        if (lexer.token == Token.LIMIT) {
            SQLLimit limit = this.exprParser.parseLimit();
            stmt.setLimit(limit);
        }

        return stmt;
    }

    public SQLStatement parseClose() {
        SQLCloseStatement stmt = new SQLCloseStatement();
        accept(Token.CLOSE);
        stmt.setCursorName(exprParser.name());
        accept(Token.SEMI);
        stmt.setAfterSemi(true);
        return stmt;
    }

    public boolean isParseCompleteValues() {
        return parseCompleteValues;
    }

    public void setParseCompleteValues(boolean parseCompleteValues) {
        this.parseCompleteValues = parseCompleteValues;
    }

    public int getParseValuesSize() {
        return parseValuesSize;
    }

    public void setParseValuesSize(int parseValuesSize) {
        this.parseValuesSize = parseValuesSize;
    }

    public SQLStatement parseMerge() {
        accept(Token.MERGE);

        SQLMergeStatement stmt = new SQLMergeStatement();
        stmt.setDbType(dbType);

        parseHints(stmt.getHints());

        accept(Token.INTO);

        if (lexer.token == Token.LPAREN) {
            lexer.nextToken();
            SQLSelect select = this.createSQLSelectParser().select();
            SQLSubqueryTableSource tableSource = new SQLSubqueryTableSource(select);
            stmt.setInto(tableSource);
            accept(Token.RPAREN);
        } else {
            stmt.setInto(exprParser.name());
        }

        stmt.getInto().setAlias(tableAlias());

        accept(Token.USING);

        SQLTableSource using = this.createSQLSelectParser().parseTableSource();
        stmt.setUsing(using);

        accept(Token.ON);
        stmt.setOn(exprParser.expr());

        for (;;) {
            boolean insertFlag = false;
            if (lexer.token == Token.WHEN) {
                lexer.nextToken();
                if (lexer.token == Token.MATCHED) {
                    SQLMergeStatement.MergeUpdateClause updateClause = new SQLMergeStatement.MergeUpdateClause();
                    lexer.nextToken();

                    if (lexer.token == Token.AND) {
                        lexer.nextToken();
                        SQLExpr where = this.exprParser.expr();
                        updateClause.setWhere(where);
                    }

                    accept(Token.THEN);
                    accept(Token.UPDATE);
                    accept(Token.SET);

                    for (; ; ) {
                        SQLUpdateSetItem item = this.exprParser.parseUpdateSetItem();

                        updateClause.addItem(item);
                        item.setParent(updateClause);

                        if (lexer.token == (Token.COMMA)) {
                            lexer.nextToken();
                            continue;
                        }

                        break;
                    }

                    if (lexer.token == Token.WHERE) {
                        lexer.nextToken();
                        updateClause.setWhere(exprParser.expr());
                    }

                    // for hive

                    SQLExpr deleteWhere = null;
                    if (lexer.token == Token.WHEN) {
                        Lexer.SavePoint savePoint = lexer.mark();
                        lexer.nextToken();
                        if (lexer.token == Token.MATCHED) {
                            lexer.nextToken();

                            if (lexer.token == Token.AND) {
                                lexer.nextToken();
                                deleteWhere = this.exprParser.expr();
                            }

                            if (lexer.token == Token.THEN) {
                                lexer.nextToken();

                                if (lexer.token == Token.DELETE) {
                                    lexer.nextToken();
                                    updateClause.setDeleteWhere(deleteWhere);
                                } else {
                                    deleteWhere = null;
                                }
                            } else {
                                deleteWhere = null;
                            }

                            if (deleteWhere == null) {
                                lexer.reset(savePoint);
                            }
                        }
                    }

                    if (lexer.token == Token.DELETE) {
                        lexer.nextToken();
                        accept(Token.WHERE);
                        updateClause.setDeleteWhere(exprParser.expr());
                    }

                    stmt.setUpdateClause(updateClause);
                } else if (lexer.token == Token.NOT) {
                    lexer.nextToken();
                    insertFlag = true;
                }
            }

            if (!insertFlag) {
                if (lexer.token == Token.WHEN) {
                    lexer.nextToken();
                }

                if (lexer.token == Token.NOT) {
                    lexer.nextToken();
                    insertFlag = true;
                }
            }

            if (insertFlag) {
                SQLMergeStatement.MergeInsertClause insertClause = new SQLMergeStatement.MergeInsertClause();

                accept(Token.MATCHED);
                accept(Token.THEN);
                accept(Token.INSERT);

                if (lexer.token == Token.LPAREN) {
                    accept(Token.LPAREN);
                    exprParser.exprList(insertClause.getColumns(), insertClause);
                    accept(Token.RPAREN);
                }
                accept(Token.VALUES);
                accept(Token.LPAREN);
                exprParser.exprList(insertClause.getValues(), insertClause);
                accept(Token.RPAREN);

                if (lexer.token == Token.WHERE) {
                    lexer.nextToken();
                    insertClause.setWhere(exprParser.expr());
                }

                stmt.setInsertClause(insertClause);
            }

            if (lexer.token == Token.WHEN) {
                continue;
            }

            break;
        }

        SQLErrorLoggingClause errorClause = parseErrorLoggingClause();
        stmt.setErrorLoggingClause(errorClause);

        return stmt;
    }

    protected SQLErrorLoggingClause parseErrorLoggingClause() {
        if (lexer.identifierEquals("LOG")) {
            SQLErrorLoggingClause errorClause = new SQLErrorLoggingClause();

            lexer.nextToken();
            accept(Token.ERRORS);
            if (lexer.token == Token.INTO) {
                lexer.nextToken();
                errorClause.setInto(exprParser.name());
            }

            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                errorClause.setSimpleExpression(exprParser.expr());
                accept(Token.RPAREN);
            }

            if (lexer.token == Token.REJECT) {
                lexer.nextToken();
                accept(Token.LIMIT);
                errorClause.setLimit(exprParser.expr());
            }

            return errorClause;
        }
        return null;
    }

    public void parseHints(List<SQLHint> hints) {
        this.getExprParser().parseHints(hints);
    }

    public SQLStatement parseDescribe() {
        if (lexer.token == Token.DESC || lexer.identifierEquals("DESCRIBE")) {
            lexer.nextToken();
        } else {
            throw new ParserException("expect DESC, actual " + lexer.token);
        }

        SQLDescribeStatement stmt = new SQLDescribeStatement();
        stmt.setDbType(dbType);

        if (lexer.token == Token.DATABASE) {
            lexer.nextToken();
            stmt.setObjectType(SQLObjectType.DATABASE);
        } else if (lexer.token == Token.SCHEMA) {
            lexer.nextToken();
            stmt.setObjectType(SQLObjectType.SCHEMA);
        } else if (lexer.identifierEquals("ROLE")) {
            lexer.nextToken();
            stmt.setObjectType(SQLObjectType.ROLE);
        } else if (lexer.identifierEquals("PACKAGE")) {
            lexer.nextToken();
            stmt.setObjectType(SQLObjectType.PACKAGE);
        } else if (lexer.identifierEquals("INSTANCE")) {
            lexer.nextToken();
            stmt.setObjectType(SQLObjectType.INSTANCE);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTENDED)) {
            lexer.nextToken();
            stmt.setExtended(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.FORMATTED)) {
            lexer.nextToken();
            stmt.setFormatted(true);
        }

        stmt.setObject(this.exprParser.name());

        if (lexer.token == Token.IDENTIFIER) {
            SQLName column = this.exprParser.name();
            stmt.setColumn(column);
        }

        if (lexer.token == Token.PARTITION) {
            lexer.nextToken();
            this.accept(Token.LPAREN);
            for (;;) {
                stmt.getPartition().add(this.exprParser.expr());
                if (lexer.token == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                if (lexer.token == Token.RPAREN) {
                    lexer.nextToken();
                    break;
                }
            }
        }

        if (lexer.token == Token.IDENTIFIER && stmt.getColumn() == null) {
            SQLName column = this.exprParser.name();
            stmt.setColumn(column);
        }

        return stmt;
    }

    public SQLWithSubqueryClause parseWithQuery() {
        SQLWithSubqueryClause withQueryClause = new SQLWithSubqueryClause();
        if (lexer.hasComment() && lexer.isKeepComments()) {
            withQueryClause.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.WITH);

        if (lexer.token == Token.RECURSIVE || lexer.identifierEquals("RECURSIVE")) {
            lexer.nextToken();
            withQueryClause.setRecursive(true);
        }

        for (;;) {
            SQLWithSubqueryClause.Entry entry = new SQLWithSubqueryClause.Entry();
            entry.setParent(withQueryClause);

            String alias = this.lexer.stringVal();
            lexer.nextToken();
            entry.setAlias(alias);

            if (lexer.token == Token.LPAREN) {
                lexer.nextToken();
                exprParser.names(entry.getColumns());
                accept(Token.RPAREN);
            }

            accept(Token.AS);
            accept(Token.LPAREN);

            switch (lexer.token) {
                case VALUES:
                case WITH:
                case SELECT:
                case LPAREN:
                case FROM:
                    entry.setSubQuery(
                            this.createSQLSelectParser()
                                    .select());
                    break;
                case INSERT:
                    entry.setReturningStatement(
                            this.parseInsert()
                    );
                    break;
                case UPDATE:
                    entry.setReturningStatement(
                            this.parseUpdateStatement()
                    );
                    break;
                case DELETE:
                    entry.setReturningStatement(
                            this.parseDeleteStatement()
                    );
                    break;
                default:
                    break;
            }

            accept(Token.RPAREN);

            withQueryClause.addEntry(entry);

            if (lexer.token == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return withQueryClause;
    }

    public SQLStatement parseWith() {
        SQLWithSubqueryClause with = this.parseWithQuery();

        if (lexer.token == Token.SELECT || lexer.token == Token.LPAREN) {
            SQLSelectParser selectParser = createSQLSelectParser();
            SQLSelect select = selectParser.select();
            select.setWithSubQuery(with);
            return new SQLSelectStatement(select, dbType);
        } else if (lexer.token == Token.INSERT) {
            SQLInsertStatement insert = (SQLInsertStatement) this.parseInsert();
            insert.setWith(with);
            return insert;
        } else if (lexer.token == Token.FROM) {
            HiveMultiInsertStatement insert = (HiveMultiInsertStatement) this.parseInsert();
            insert.setWith(with);
            return insert;
        }

        throw new ParserException("TODO. " + lexer.info());
    }

    protected void parseValueClause(List<SQLInsertStatement.ValuesClause> valueClauseList
            , int columnSize
            , SQLObject parent)
    {
        parseValueClause(valueClauseList, null, 0, parent);
    }


    protected void parseValueClauseNative(List<SQLInsertStatement.ValuesClause> valueClauseList
            , List<SQLColumnDefinition> columnDefinitionList
            , int columnSize
            , SQLObject parent)
    {
        final TimeZone timeZone = lexer.getTimeZone();
        SQLInsertStatement.ValuesClause values;
        for (int i = 0; ; ++i) {
            int startPos = lexer.pos - 1;

            if (lexer.token != Token.LPAREN) {
                throw new ParserException("syntax error, expect ')', " + lexer.info());
            }
//            lexer.nextTokenValue();

            if (lexer.ch == '\'') { // for performance
                lexer.bufPos = 0;
                if (dbType == DbType.mysql) {
                    lexer.scanString2();
                } else {
                    lexer.scanString();
                }
            } else if (lexer.ch == '0') {
                lexer.bufPos = 0;
                if (lexer.charAt(lexer.pos + 1) == 'x') {
                    lexer.scanChar();
                    lexer.scanChar();
                    lexer.scanHexaDecimal();
                } else {
                    lexer.scanNumber();
                }
            } else if (lexer.ch > '0' && lexer.ch <= '9') {
                lexer.bufPos = 0;
                lexer.scanNumber();
            } else if (lexer.ch == '-' && lexer.charAt(lexer.pos + 1) != '-') {
                lexer.scanNumber();
            } else {
                lexer.nextTokenValue();
            }

            if (lexer.token() != Token.RPAREN) {
                List valueExprList;
                if (columnSize > 0) {
                    valueExprList = new ArrayList(columnSize);
                } else {
                    valueExprList = new ArrayList();
                }
                values = new SQLInsertStatement.ValuesClause(valueExprList, parent);

                int funcExecCount = 0;
                for (int j = 0; ; ++j) {
                    SQLExpr expr = null;
                    Object value = null;

                    SQLColumnDefinition columnDefinition = null;
                    if (columnDefinitionList != null && j < columnDefinitionList.size()) {
                        columnDefinition = columnDefinitionList.get(j);
                    }

                    SQLDataType dataType = null;
                    if (columnDefinition != null) {
                        dataType = columnDefinition.getDataType();
                    }

                    switch (lexer.token) {
                        case LITERAL_INT: {
                            Number integerValue = lexer.integerValue();

                            if (lexer.ch == ',') {
                                lexer.ch = lexer.charAt(++lexer.pos);
                                lexer.token = COMMA;
                            } else {
                                lexer.nextTokenCommaValue();
                            }

                            if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                                expr = new SQLIntegerExpr(integerValue, values);
                                expr = this.exprParser.exprRest(expr);
                                expr.setParent(values);
                            } else {
                                value = integerValue;
                            }
                            break;
                        }
                        case LITERAL_CHARS: {
                            String strVal = lexer.stringVal();

                            if (lexer.ch == ',') {
                                lexer.ch = lexer.charAt(++lexer.pos);
                                lexer.token = COMMA;
                            } else {
                                lexer.nextTokenCommaValue();
                            }

                            if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                                expr = new SQLCharExpr(strVal, values);
                                expr = this.exprParser.exprRest(expr);
                                expr.setParent(values);
                            } else {
                                value = strVal;
                            }
                            break;
                        }
                        case LITERAL_NCHARS: {
                            String strVal = lexer.stringVal();

                            if (lexer.ch == ',') {
                                lexer.ch = lexer.charAt(++lexer.pos);
                                lexer.token = COMMA;
                            } else {
                                lexer.nextTokenCommaValue();
                            }

                            if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                                expr = new SQLNCharExpr(strVal, values);
                                expr = this.exprParser.exprRest(expr);
                                expr.setParent(values);
                            } else {
                                value = strVal;
                            }
                            break;
                        }
                        case LITERAL_FLOAT: {
                            BigDecimal number = lexer.decimalValue();

                            if (dataType != null
                                    && dataType.nameHashCode64() == FnvHash.Constants.DECIMAL)
                            {

                                int precision = 0, scale = 0;
                                List<SQLExpr> arguments = dataType.getArguments();
                                if (arguments.size() > 0) {
                                    SQLExpr arg0 = arguments.get(0);
                                    if (arg0 instanceof SQLIntegerExpr) {
                                        precision = ((SQLIntegerExpr) arg0).getNumber().intValue();
                                    }
                                }
                                if (arguments.size() > 1) {
                                    SQLExpr arg0 = arguments.get(1);
                                    if (arg0 instanceof SQLIntegerExpr) {
                                        scale = ((SQLIntegerExpr) arg0).getNumber().intValue();
                                    }
                                }

                                if (number instanceof BigDecimal) {
                                    number = MySqlUtils.decimal(number, precision, scale);
                                }
                            }

                            if (lexer.ch == ',') {
                                lexer.ch = lexer.charAt(++lexer.pos);
                                lexer.token = COMMA;
                            } else {
                                lexer.nextTokenCommaValue();
                            }

                            if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                                expr = new SQLDecimalExpr(number);
                                expr = this.exprParser.exprRest(expr);
                                expr.setParent(values);
                            } else {
                                value = number;
                            }
                            break;
                        }
                        case NULL: {
                            lexer.nextTokenCommaValue();
                            if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                                expr = new SQLNullExpr(parent);
                                expr = this.exprParser.exprRest(expr);
                                expr.setParent(values);
                            } else {
                                value = null;
                            }
                            break;
                        }
                        case IDENTIFIER: {
                            long hash = lexer.hash_lower();
                            if (hash == FnvHash.Constants.DATE) {
                                lexer.nextTokenValue();
                                String strVal = lexer.stringVal();
                                value = java.sql.Date.valueOf(strVal);
                                lexer.nextTokenComma();
                            } else if (hash == FnvHash.Constants.TIMESTAMP && timeZone != null) {
                                lexer.nextTokenValue();
                                String strVal = lexer.stringVal();
                                value = new java.sql.Timestamp(MySqlUtils.parseDate(strVal, timeZone)
                                        .getTime());
                                lexer.nextTokenComma();
                            } else if (hash == FnvHash.Constants.CURDATE
                                    || hash == FnvHash.Constants.CUR_DATE
                                    || hash == FnvHash.Constants.CURRENT_DATE) {
                                lexer.nextTokenValue();

                                if (lexer.token == Token.LPAREN) {
                                    lexer.nextToken();
                                    accept(Token.RPAREN);
                                }

                                if (now == null) {
                                    now = new java.sql.Timestamp(System.currentTimeMillis());
                                }

                                if (currentDate == null) {
                                    currentDate = new java.sql.Date(now.getTime());
                                }
                                value = currentDate;
                                funcExecCount++;
                            } else if ((hash == FnvHash.Constants.SYSDATE
                                    || hash == FnvHash.Constants.NOW
                                    || hash == FnvHash.Constants.CURRENT_TIMESTAMP)
                                    && timeZone != null) {
                                lexer.nextTokenValue();

                                if (lexer.token == Token.LPAREN) {
                                    lexer.nextToken();
                                    accept(Token.RPAREN);
                                }

                                if (now == null) {
                                    now = new java.sql.Timestamp(System.currentTimeMillis());
                                }
                                value = now;
                                funcExecCount++;
                            } else if (hash == FnvHash.Constants.UUID) {
                                lexer.nextTokenLParen();
                                accept(Token.LPAREN);
                                accept(Token.RPAREN);
                                value = UUID.randomUUID().toString();
                                funcExecCount++;
                            } else {
                                value = null;
                                Lexer.SavePoint mark = lexer.mark();
                                expr = exprParser.expr();
                                if (expr instanceof SQLName) {
                                    lexer.reset(mark);
                                    lexer.info();
                                    throw new ParserException("insert value error, token " + lexer.stringVal() + ", line " + lexer.posLine + ", column " + lexer.posColumn, lexer.posLine, lexer.posColumn);
                                }
                                expr.setParent(values);
                            }
                            break;
                        }
                        default:
                            value = null;
                            expr = exprParser.expr();
                            expr.setParent(values);
                            break;
                    }

                    if (expr != null) {
                        expr.setParent(values);
                        value = expr;
                    }

                    if (lexer.token == Token.COMMA) {
                        valueExprList.add(value);

                        if (lexer.ch == '\'') { // for performance
                            lexer.bufPos = 0;
                            if (dbType == DbType.mysql) {
                                lexer.scanString2();
                            } else {
                                lexer.scanString();
                            }
                        } else if (lexer.ch == '0') {
                            lexer.bufPos = 0;
                            if (lexer.charAt(lexer.pos + 1) == 'x') {
                                lexer.scanChar();
                                lexer.scanChar();
                                lexer.scanHexaDecimal();
                            } else {
                                lexer.scanNumber();
                            }
                        } else if (lexer.ch > '0' && lexer.ch <= '9') {
                            lexer.bufPos = 0;
                            lexer.scanNumber();
                        } else if (lexer.ch == '-' && lexer.charAt(lexer.pos + 1) != '-') {
                            lexer.bufPos = 0;
                            lexer.scanNumber();
                        } else {
                            lexer.nextTokenValue();
                        }
                        continue;
                    } else if (lexer.token == Token.RPAREN) {
                        valueExprList.add(value);
                        break;
                    } else {
                        expr = this.exprParser.primaryRest(expr);
                        if (lexer.token != Token.COMMA && lexer.token() != Token.RPAREN) {
                            expr = this.exprParser.exprRest(expr);
                        }
                        expr.setParent(values);

                        valueExprList.add(expr);
                        if (lexer.token == Token.COMMA) {
                            lexer.nextTokenValue();
                            continue;
                        } else {
                            break;
                        }
                    }
                }

                if (funcExecCount == 0 && lexer.isEnabled(SQLParserFeature.KeepInsertValueClauseOriginalString)) {
                    int endPos = lexer.pos();
                    String orginalString = lexer.subString(startPos, endPos - startPos);
                    values.setOriginalString(orginalString);
                }
            } else {
                values = new SQLInsertStatement.ValuesClause(new ArrayList<SQLExpr>(0));
            }

            valueClauseList.add(values);

            if (lexer.token != Token.RPAREN) {
                throw new ParserException("syntax error. " + lexer.info());
            }

            if (!parseCompleteValues && valueClauseList.size() >= parseValuesSize) {
                lexer.skipToEOF();
                break;
            }

            lexer.nextTokenComma();
            if (lexer.token == Token.COMMA) {
                lexer.nextTokenLParen();
                if(values != null) {
                    columnSize = values.getValues().size();
                }
                continue;
            } else {
                break;
            }
        }
    }

    public void parseValueClause(SQLInsertValueHandler valueHandler)  throws SQLException {
        for (;;) {
            if (lexer.token != Token.LPAREN) {
                throw new ParserException("syntax error, expect ')', " + lexer.info());
            }

            if (lexer.ch == '\'') { // for performance
                lexer.bufPos = 0;
                if (dbType == DbType.mysql) {
                    lexer.scanString2();
                } else {
                    lexer.scanString();
                }
            } else if (lexer.ch == '0') {
                lexer.bufPos = 0;
                if (lexer.charAt(lexer.pos + 1) == 'x') {
                    lexer.scanChar();
                    lexer.scanChar();
                    lexer.scanHexaDecimal();
                } else {
                    lexer.scanNumber();
                }
            } else if (lexer.ch > '0' && lexer.ch <= '9') {
                lexer.bufPos = 0;
                lexer.scanNumber();
            } else if (lexer.ch == '-' && lexer.charAt(lexer.pos + 1) != '-') {
                lexer.scanNumber();
            } else {
                lexer.nextTokenValue();
            }

            if (lexer.token() != Token.RPAREN) {
                Object row = valueHandler.newRow();

                for (int j = 0; ; ++j) {
                    switch (lexer.token) {
                        case LITERAL_INT: {
                            Number number = lexer.integerValue();

                            if (lexer.ch == ',') {
                                lexer.ch = lexer.charAt(++lexer.pos);
                                lexer.token = COMMA;
                            } else {
                                lexer.nextTokenCommaValue();
                            }

                            if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                                throw new ParserException("insert value error, " + lexer.info());
                            }

                            valueHandler.processInteger(row, j, number);
                            break;
                        }
                        case LITERAL_CHARS:
                        case LITERAL_NCHARS: {
                            String strVal = lexer.stringVal();

                            if (lexer.ch == ',') {
                                lexer.ch = lexer.charAt(++lexer.pos);
                                lexer.token = COMMA;
                            } else {
                                lexer.nextTokenCommaValue();
                            }

                            if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                                throw new ParserException("insert value error, " + lexer.info());
                            }

                            valueHandler.processString(row, j, strVal);
                            break;
                        }
                        case LITERAL_FLOAT: {
                            BigDecimal number = lexer.decimalValue();

                            if (lexer.ch == ',') {
                                lexer.ch = lexer.charAt(++lexer.pos);
                                lexer.token = COMMA;
                            } else {
                                lexer.nextTokenCommaValue();
                            }

                            if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                                throw new ParserException("insert value error, " + lexer.info());
                            }

                            valueHandler.processDecimal(row, j, number);
                            break;
                        }
                        case NULL: {
                            lexer.nextTokenCommaValue();
                            if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                                throw new ParserException("insert value error, " + lexer.info());
                            }

                            valueHandler.processNull(row, j);
                            break;
                        }
                        case TRUE:
                            valueHandler.processBoolean(row, j, true);
                            lexer.nextTokenComma();
                            break;
                        case FALSE:
                            valueHandler.processBoolean(row, j, false);
                            lexer.nextTokenComma();
                            break;
                        case IDENTIFIER: {
                            long hash = lexer.hash_lower();
                            if (hash == FnvHash.Constants.DATE) {
                                lexer.nextTokenValue();
                                String strVal = lexer.stringVal();
                                valueHandler.processDate(row, j, strVal);
                                lexer.nextTokenComma();
                            } else if (hash == FnvHash.Constants.TIMESTAMP) {
                                lexer.nextTokenValue();
                                String strVal = lexer.stringVal();
                                valueHandler.processTimestamp(row, j, strVal);
                                lexer.nextTokenComma();
                            } else if (hash == FnvHash.Constants.TIME) {
                                lexer.nextTokenValue();
                                String strVal = lexer.stringVal();
                                valueHandler.processTime(row, j, strVal);
                                lexer.nextTokenComma();
                            } else if (hash == FnvHash.Constants.DECIMAL) {
                                lexer.nextTokenValue();
                                String strVal = lexer.stringVal();
                                BigDecimal decimal = new BigDecimal(strVal);
                                valueHandler.processDecimal(row, j, decimal);
                                lexer.nextTokenComma();
                            } else if (hash == FnvHash.Constants.CURDATE
                                    || hash == FnvHash.Constants.CUR_DATE
                                    || hash == FnvHash.Constants.CURRENT_DATE
                                    || hash == FnvHash.Constants.SYSDATE)
                            {
                                lexer.nextTokenLParen();

                                if (lexer.token == Token.LPAREN) {
                                    lexer.nextToken();
                                    accept(Token.RPAREN);
                                }

                                if (currentDate == null) {
                                    currentDate = new java.sql.Date(now.getTime());
                                }

                                valueHandler.processDate(row, j, currentDate);
                            } else if (hash == FnvHash.Constants.NOW
                                    || hash == FnvHash.Constants.CURRENT_TIMESTAMP)
                            {
                                lexer.nextTokenLParen();

                                if (lexer.token == Token.LPAREN) {
                                    lexer.nextToken();
                                    accept(Token.RPAREN);
                                }

                                if (now == null) {
                                    now = new java.sql.Timestamp(System.currentTimeMillis());
                                }

                                valueHandler.processTimestamp(row, j, now);
                            } else if (hash == FnvHash.Constants.UUID) {
                                String funcName = lexer.stringVal();
                                lexer.nextTokenLParen();

                                if (lexer.token == Token.LPAREN) {
                                    lexer.nextToken();
                                    accept(Token.RPAREN);
                                } else {
                                    throw new ParserException("insert value error, " + lexer.info());
                                }

                                if (now == null) {
                                    now = new java.sql.Timestamp(System.currentTimeMillis());
                                }

                                valueHandler.processFunction(row, j, funcName, hash);
                            } else if (hash == FnvHash.Constants.STR_TO_DATE || hash == FnvHash.Constants.DATE_PARSE) {
                                String funcName = lexer.stringVal();
                                lexer.nextTokenLParen();

                                String strVal, format;
                                if (lexer.token == Token.LPAREN) {
                                    lexer.nextTokenValue();
                                    strVal = lexer.stringVal();
                                    lexer.nextTokenComma();
                                    accept(Token.COMMA);
                                    format = lexer.stringVal();
                                    lexer.nextTokenValue();
                                    accept(Token.RPAREN);
                                } else {
                                    throw new ParserException("insert value error, " + lexer.info());
                                }

                                valueHandler.processFunction(row, j, funcName, hash, strVal, format);
                            } else if (FnvHash.Constants.CLOTHES_FEATURE_EXTRACT_V1 == hash ||
                                    FnvHash.Constants.CLOTHES_ATTRIBUTE_EXTRACT_V1 == hash ||
                                    Constants.GENERIC_FEATURE_EXTRACT_V1 == hash ||
                                    Constants.TEXT_FEATURE_EXTRACT_V1 == hash ||
                                    Constants.FACE_FEATURE_EXTRACT_V1 == hash) {
                                String funcName = lexer.stringVal();
                                lexer.nextTokenLParen();

                                String urlVal;
                                if (Token.LPAREN == lexer.token) {
                                    lexer.nextTokenValue();
                                    urlVal = lexer.stringVal();
                                    lexer.nextToken();
                                    accept(Token.RPAREN);
                                } else {
                                    throw new ParserException("insert value error, " + lexer.info());
                                }

                                valueHandler.processFunction(row, j, funcName, hash, urlVal);
                            } else {
                                throw new ParserException("insert value error, " + lexer.info());
                            }
                            break;
                        }
                        default:
                            throw new ParserException("insert value error, " + lexer.info());
                    }

                    if (lexer.token == Token.COMMA) {
                        if (lexer.ch == '\'') { // for performance
                            lexer.bufPos = 0;
                            if (dbType == DbType.mysql) {
                                lexer.scanString2();
                            } else {
                                lexer.scanString();
                            }
                        } else if (lexer.ch == '0') {
                            lexer.bufPos = 0;
                            if (lexer.charAt(lexer.pos + 1) == 'x') {
                                lexer.scanChar();
                                lexer.scanChar();
                                lexer.scanHexaDecimal();
                            } else {
                                lexer.scanNumber();
                            }
                        } else if (lexer.ch > '0' && lexer.ch <= '9') {
                            lexer.bufPos = 0;
                            lexer.scanNumber();
                        } else if (lexer.ch == '-' && lexer.charAt(lexer.pos + 1) != '-') {
                            lexer.bufPos = 0;
                            lexer.scanNumber();
                        } else {
                            lexer.nextTokenValue();
                        }
                        continue;
                    } else if (lexer.token == Token.RPAREN) {
                        break;
                    } else {
                        throw new ParserException("insert value error, " + lexer.info());
//
//                        if (lexer.token == Token.COMMA) {
//                            lexer.nextTokenValue();
//                            continue;
//                        } else {
//                            break;
//                        }
                    }
                } // for j

                valueHandler.processRow(row);
            }

            if (lexer.token != Token.RPAREN) {
                throw new ParserException("syntax error. " + lexer.info());
            }

            lexer.nextTokenComma();
            if (lexer.token == Token.COMMA) {
                lexer.nextTokenLParen();
                continue;
            } else {
                valueHandler.processComplete();
                break;
            }
        }

    }

    protected void parseValueClause(List<SQLInsertStatement.ValuesClause> valueClauseList
            , List<SQLColumnDefinition> columnDefinitionList
            , int columnSize
            , SQLObject parent)
    {
        final boolean optimizedForParameterized = lexer.isEnabled(SQLParserFeature.OptimizedForForParameterizedSkipValue);

        SQLInsertStatement.ValuesClause values;
        for (int i = 0; ; ++i) {
            int startPos = lexer.pos - 1;

            if (lexer.token != Token.LPAREN) {
                throw new ParserException("syntax error, expect ')', " + lexer.info());
            }
//            lexer.nextTokenValue();

            if (lexer.ch == '\'') { // for performance
                lexer.bufPos = 0;
                if (dbType == DbType.mysql) {
                    lexer.scanString2();
                } else {
                    lexer.scanString();
                }
            } else if (lexer.ch == '0') {
                lexer.bufPos = 0;
                if (lexer.charAt(lexer.pos + 1) == 'x') {
                    lexer.scanChar();
                    lexer.scanChar();
                    lexer.scanHexaDecimal();
                } else {
                    lexer.scanNumber();
                }
            } else if (lexer.ch > '0' && lexer.ch <= '9') {
                lexer.bufPos = 0;
                lexer.scanNumber();
            } else if (lexer.ch == '-' && lexer.charAt(lexer.pos + 1) != '-') {
                lexer.bufPos = 0;
                lexer.scanNumber();
            } else {
                lexer.nextTokenValue();
            }

            if (lexer.token() != Token.RPAREN) {
                List valueExprList;
                if (columnSize > 0) {
                    valueExprList = new ArrayList(columnSize);
                } else {
                    valueExprList = new ArrayList();
                }
                values = new SQLInsertStatement.ValuesClause(valueExprList, parent);

                for (int j = 0; ; ++j) {
                    SQLExpr expr;

                    SQLColumnDefinition columnDefinition = null;
                    if (columnDefinitionList != null && j < columnDefinitionList.size()) {
                        columnDefinition = columnDefinitionList.get(j);
                    }

                    SQLDataType dataType = null;
                    if (columnDefinition != null) {
                        dataType = columnDefinition.getDataType();
                    }

                    if (lexer.token == Token.LITERAL_INT) {
                        if (optimizedForParameterized) {
                            expr = new SQLVariantRefExpr("?", values);
                            values.incrementReplaceCount();
                        } else {
                            expr = new SQLIntegerExpr(lexer.integerValue(), values);
                        }
                        if (lexer.ch == ',') {
                            lexer.ch = lexer.charAt(++lexer.pos);
                            lexer.token = COMMA;
                        } else {
                            lexer.nextTokenCommaValue();
                        }

                        if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                            expr = this.exprParser.exprRest(expr);
                            expr.setParent(values);
                        }
                    } else if (lexer.token == Token.LITERAL_CHARS) {
                        if (optimizedForParameterized) {
                            expr = new SQLVariantRefExpr("?", values);
                            values.incrementReplaceCount();
                        } else {
                            expr = new SQLCharExpr(lexer.stringVal(), values);
                        }

                        if (lexer.ch == ',') {
                            lexer.ch = lexer.charAt(++lexer.pos);
                            lexer.token = COMMA;
                        } else {
                            lexer.nextTokenCommaValue();
                        }

                        if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                            expr = this.exprParser.exprRest(expr);
                            expr.setParent(values);
                        }
                    } else if (lexer.token == Token.LITERAL_NCHARS) {
                        if (optimizedForParameterized) {
                            expr = new SQLVariantRefExpr("?", values);
                            values.incrementReplaceCount();
                        } else {
                            expr = new SQLNCharExpr(lexer.stringVal(), values);
                        }

                        if (lexer.ch == ',') {
                            lexer.ch = lexer.charAt(++lexer.pos);
                            lexer.token = COMMA;
                        } else {
                            lexer.nextTokenCommaValue();
                        }

                        if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                            expr = this.exprParser.exprRest(expr);
                            expr.setParent(values);
                        }
                    } else if (lexer.token == Token.LITERAL_FLOAT) {
                        if (optimizedForParameterized) {
                            expr = new SQLVariantRefExpr("?", values);
                            values.incrementReplaceCount();
                        } else {
                            SQLNumberExpr numberExpr = lexer.numberExpr(parent);

                            if (dataType != null
                                    && dataType.nameHashCode64() == FnvHash.Constants.DECIMAL)
                            {
                                Number number = numberExpr.getNumber();

                                int precision = 0, scale = 0;
                                List<SQLExpr> arguments = dataType.getArguments();
                                if (arguments.size() > 0) {
                                    SQLExpr arg0 = arguments.get(0);
                                    if (arg0 instanceof SQLIntegerExpr) {
                                        precision = ((SQLIntegerExpr) arg0).getNumber().intValue();
                                    }
                                }
                                if (arguments.size() > 1) {
                                    SQLExpr arg0 = arguments.get(1);
                                    if (arg0 instanceof SQLIntegerExpr) {
                                        scale = ((SQLIntegerExpr) arg0).getNumber().intValue();
                                    }
                                }

                                if (number instanceof BigDecimal) {
                                    number = MySqlUtils.decimal((BigDecimal) number, precision, scale);
                                    numberExpr.setNumber(number);
                                }
                            }

                            expr = numberExpr;
                        }

                        if (lexer.ch == ',') {
                            lexer.ch = lexer.charAt(++lexer.pos);
                            lexer.token = COMMA;
                        } else {
                            lexer.nextTokenCommaValue();
                        }

                        if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                            expr = this.exprParser.exprRest(expr);
                            expr.setParent(values);
                        }
                    } else if (lexer.token == Token.NULL) {
                        if (optimizedForParameterized) {
                            expr = new SQLVariantRefExpr("?", parent);
                            values.incrementReplaceCount();
                        } else {
                            expr = new SQLNullExpr(parent);
                        }
                        lexer.nextTokenCommaValue();
                        if (lexer.token != Token.COMMA && lexer.token != Token.RPAREN) {
                            expr = this.exprParser.exprRest(expr);
                            expr.setParent(values);
                        }
                    } else {
                        expr = exprParser.expr();
                        expr.setParent(values);
                    }

                    if (lexer.token == Token.COMMA) {
                        valueExprList.add(expr);

                        if (lexer.ch == '\'') { // for performance
                            lexer.bufPos = 0;
                            if (dbType == DbType.mysql) {
                                lexer.scanString2();
                            } else {
                                lexer.scanString();
                            }
                        } else if (lexer.ch == '0') {
                            lexer.bufPos = 0;
                            if (lexer.charAt(lexer.pos + 1) == 'x') {
                                lexer.scanChar();
                                lexer.scanChar();
                                lexer.scanHexaDecimal();
                            } else {
                                lexer.scanNumber();
                            }
                        } else if (lexer.ch > '0' && lexer.ch <= '9') {
                            lexer.bufPos = 0;
                            lexer.scanNumber();
                        } else if (lexer.ch == '-' && lexer.charAt(lexer.pos + 1) != '-') {
                            lexer.bufPos = 0;
                            lexer.scanNumber();
                        } else {
                            lexer.nextTokenValue();
                        }
                        continue;
                    } else if (lexer.token == Token.RPAREN) {
                        valueExprList.add(expr);
                        break;
                    } else {
                        expr = this.exprParser.primaryRest(expr);
                        if (lexer.token != Token.COMMA && lexer.token() != Token.RPAREN) {
                            expr = this.exprParser.exprRest(expr);
                        }
                        expr.setParent(values);

                        valueExprList.add(expr);
                        if (lexer.token == Token.COMMA) {
                            lexer.nextTokenValue();
                            continue;
                        } else {
                            break;
                        }
                    }
                }

                if (lexer.isEnabled(SQLParserFeature.KeepInsertValueClauseOriginalString)) {
                    int endPos = lexer.pos();
                    String orginalString = lexer.subString(startPos, endPos - startPos);
                    values.setOriginalString(orginalString);
                }
            } else {
                values = new SQLInsertStatement.ValuesClause(new ArrayList<SQLExpr>(0));
            }

            valueClauseList.add(values);

            if (lexer.token != Token.RPAREN) {
                throw new ParserException("syntax error. " + lexer.info());
            }

            if (!parseCompleteValues && valueClauseList.size() >= parseValuesSize) {
                lexer.skipToEOF();
                break;
            }

            lexer.nextTokenComma();
            if (lexer.token == Token.COMMA) {
                lexer.nextTokenLParen();
                if(values != null) {
                    columnSize = values.getValues().size();
                }
                continue;
            } else {
                break;
            }
        }
    }

    public SQLSelectListCache getSelectListCache() {
        return selectListCache;
    }

    public void setSelectListCache(SQLSelectListCache selectListCache) {
        this.selectListCache = selectListCache;
    }

    protected HiveInsertStatement parseHiveInsertStmt() {
        HiveInsertStatement insert = new HiveInsertStatement();
        insert.setDbType(dbType);

        if (lexer.isKeepComments() && lexer.hasComment()) {
            insert.addBeforeComment(lexer.readAndResetComments());
        }

        SQLSelectParser selectParser = createSQLSelectParser();

        accept(Token.INSERT);

        if (lexer.token == Token.INTO) {
            lexer.nextToken();
        } else {
            accept(Token.OVERWRITE);
            insert.setOverwrite(true);
        }

        if (lexer.token == Token.TABLE) {
            lexer.nextToken();
        }
        insert.setTableSource(this.exprParser.name());

        boolean columnsParsed = false;

        if (lexer.token == (Token.LPAREN)) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (lexer.token == Token.SELECT) {
                lexer.reset(mark);
            } else {
                parseInsertColumns(insert);
                columnsParsed = true;
                accept(Token.RPAREN);
            }
        }

        if (lexer.token == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.LPAREN);
            for (;;) {
                SQLAssignItem ptExpr = new SQLAssignItem();
                ptExpr.setTarget(this.exprParser.name());
                if (lexer.token == Token.EQ ||lexer.token == Token.EQEQ) {
                    lexer.nextTokenValue();
                    SQLExpr ptValue = this.exprParser.expr();
                    ptExpr.setValue(ptValue);
                }
                insert.addPartition(ptExpr);
                if (!(lexer.token == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        if (!columnsParsed && lexer.token == Token.LPAREN) {
            Lexer.SavePoint savePoint = lexer.mark();

            lexer.nextToken();
            if (lexer.token != SELECT) {
                parseInsertColumns(insert);
                accept(Token.RPAREN);
            } else {
                lexer.reset(savePoint);
            }
        }

        if (lexer.token == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            insert.setIfNotExists(true);
        }

        if (lexer.token == Token.VALUES) {
            lexer.nextToken();

            for (;;) {
                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();

                    SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
                    this.exprParser.exprList(values.getValues(), values);
                    insert.addValueCause(values);
                    accept(Token.RPAREN);
                }

                if (lexer.token == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }
        } else {
            SQLSelect query = selectParser.select();
            insert.setQuery(query);
        }

        return insert;
    }

    protected HiveInsert parseHiveInsert() {
        HiveInsert insert = new HiveInsert();

        if (lexer.isKeepComments() && lexer.hasComment()) {
            insert.addBeforeComment(lexer.readAndResetComments());
        }

        SQLSelectParser selectParser = createSQLSelectParser();

        accept(Token.INSERT);

        if (lexer.token == Token.INTO) {
            lexer.nextToken();
        } else {
            accept(Token.OVERWRITE);
            insert.setOverwrite(true);
        }

        if (lexer.token == Token.TABLE) {
            lexer.nextToken();
        }
        insert.setTableSource(this.exprParser.name());

        if (lexer.token == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.LPAREN);
            for (;;) {
                SQLAssignItem ptExpr = new SQLAssignItem();
                ptExpr.setTarget(this.exprParser.name());
                if (lexer.token == Token.EQ) {
                    lexer.nextToken();
                    SQLExpr ptValue = this.exprParser.expr();
                    ptExpr.setValue(ptValue);
                }
                insert.addPartition(ptExpr);
                if (lexer.token != Token.COMMA) {
                    break;
                } else {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        if (lexer.token == Token.VALUES) {
            lexer.nextToken();

            for (;;) {
                if (lexer.token == Token.LPAREN) {
                    lexer.nextToken();

                    SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
                    this.exprParser.exprList(values.getValues(), values);
                    insert.addValueCause(values);
                    accept(Token.RPAREN);
                }

                if (lexer.token == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }
        } else {
            SQLSelect query = selectParser.select();
            insert.setQuery(query);
        }

        return insert;
    }

    protected SQLShowDatabasesStatement parseShowDatabases(boolean isPhysical) {
        SQLShowDatabasesStatement stmt = new SQLShowDatabasesStatement();

        stmt.setPhysical(isPhysical);
        if (lexer.token == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTRA)) {
            lexer.nextToken();
            stmt.setExtra(true);
        }

        return stmt;
    }

    protected SQLShowTableGroupsStatement parseShowTableGroups() {
        SQLShowTableGroupsStatement stmt = new SQLShowTableGroupsStatement();

        if (lexer.token == Token.IN) {
            lexer.nextToken();
            SQLName db = exprParser.name();
            stmt.setDatabase(db);
        }

        return stmt;
    }

    protected SQLShowTablesStatement parseShowTables() {
        SQLShowTablesStatement stmt = new SQLShowTablesStatement();

        if (lexer.identifierEquals(FnvHash.Constants.SHOW)) {
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.TABLES)) {
            lexer.nextToken();
        }

        if (lexer.identifierEquals(Constants.EXTENDED)) {
            lexer.nextToken();
            stmt.setExtended(true);
        }

        if (lexer.token == Token.FROM || lexer.token == Token.IN) {
            lexer.nextToken();
            SQLName database = exprParser.name();
            if (lexer.token == Token.SUB && database instanceof SQLIdentifierExpr) {
                lexer.mark();
                lexer.nextToken();
                String strVal = lexer.stringVal();
                lexer.nextToken();
                if (database instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr ident = (SQLIdentifierExpr) database;
                    database = new SQLIdentifierExpr(ident.getName() + "-" + strVal);
                }
            }
            stmt.setDatabase(database);
        }

        if (lexer.token == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    protected SQLShowColumnsStatement parseShowColumns() {
        SQLShowColumnsStatement stmt = new SQLShowColumnsStatement();

        if (lexer.token == Token.FROM) {
            lexer.nextToken();
            SQLName table = exprParser.name();
            stmt.setTable(table);

            if (lexer.token == Token.FROM || lexer.token == Token.IN) {
                lexer.nextToken();
                SQLName database = exprParser.name();
                stmt.setDatabase(database);
            }
        } else if (lexer.token == Token.IN) {
            lexer.nextToken();
            SQLName table = exprParser.name();
            stmt.setTable(table);
        }

        if (lexer.token == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    protected SQLStatement parseAlterIndex() {
        accept(Token.ALTER);
        lexer.nextToken();
        SQLAlterIndexStatement stmt = new SQLAlterIndexStatement();
        stmt.setName(this.exprParser.name());

        if (lexer.identifierEquals("RENAME")) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.setRenameTo(this.exprParser.name());
        }

        if (lexer.token == Token.ON) {
            lexer.nextToken();
            if (lexer.token == Token.TABLE) {
                lexer.nextToken();
            }
            SQLName table = this.exprParser.name();
            stmt.setTable(table);
        }

        if (lexer.token == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.LPAREN);
            parseAssignItems(stmt.getPartitions(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.DBPARTITION)) {
            SQLPartitionBy partitionClause = this.getSQLCreateTableParser().parsePartitionBy();
            stmt.setDbPartitionBy(partitionClause);
        }

        if (lexer.token == Token.ENABLE) {
            lexer.nextToken();
            stmt.setEnable(true);
        }

        if (lexer.token == Token.DISABLE) {
            lexer.nextToken();
            stmt.setEnable(false);
        }
        if (lexer.hash_lower == FnvHash.Constants.UNUSABLE) {
            lexer.nextToken();
            stmt.setUnusable(true);
        }

        for (;;) {
            if (lexer.identifierEquals("rebuild")) {
                lexer.nextToken();

                SQLAlterIndexStatement.Rebuild rebuild = new SQLAlterIndexStatement.Rebuild();
                stmt.setRebuild(rebuild);
                continue;
            } else if (lexer.identifierEquals("MONITORING")) {
                lexer.nextToken();
                acceptIdentifier("USAGE");
                stmt.setMonitoringUsage(Boolean.TRUE);
                continue;
            } else if (lexer.identifierEquals("PARALLEL")) {
                lexer.nextToken();
                stmt.setParallel(this.exprParser.expr());
            }
            break;
        }

        return stmt;
    }

    protected SQLStatement parseAnalyze() {
        lexer.nextToken();
        accept(Token.TABLE);

        SQLAnalyzeTableStatement stmt = new SQLAnalyzeTableStatement();

        SQLName table = this.exprParser.name();
        stmt.setTable(table);

        if (lexer.token() == Token.PARTITION) {
            stmt.setPartition(parsePartitionRef());
        }

        accept(Token.COMPUTE);
        acceptIdentifier("STATISTICS");
        stmt.setComputeStatistics(true);

        if (lexer.token == Token.FOR) {
            lexer.nextToken();
            acceptIdentifier("COLUMNS");
            stmt.setForColums(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.CACHE)) {
            lexer.nextToken();
            acceptIdentifier("METADATA");
            stmt.setCacheMetadata(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.NOSCAN)) {
            lexer.nextToken();
            stmt.setNoscan(true);
        }

        return stmt;
    }

    public SQLAlterSequenceStatement parseAlterSequence() {
        accept(Token.ALTER);

        accept(Token.SEQUENCE);

        SQLAlterSequenceStatement stmt = new SQLAlterSequenceStatement();
        stmt.setDbType(dbType);
        stmt.setName(this.exprParser.name());

        if (lexer.identifierEquals(FnvHash.Constants.CHANGE)) {
            lexer.nextToken();
            accept(Token.TO);

            if (lexer.identifierEquals(FnvHash.Constants.SIMPLE)) {
                stmt.setChangeToSimple(true);
                lexer.nextToken();
                if (lexer.hash_lower() ==FnvHash.Constants.WITH) {
                    lexer.nextToken();
                    accept(Token.CACHE);
                    stmt.setWithCache(true);
                }
            } else if (lexer.token == Token.GROUP) {
                stmt.setChangeToGroup(true);
                lexer.nextToken();
            } else if (lexer.identifierEquals(FnvHash.Constants.TIME)) {
                stmt.setChangeToTime(true);
                lexer.nextToken();
            } else {
                throw new ParserException("TODO " + lexer.info());
            }
        }

        for (;;) {
            if (lexer.token() == Token.START || lexer.identifierEquals(FnvHash.Constants.START)) {
                lexer.nextToken();
                accept(Token.WITH);
                stmt.setStartWith(this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals("INCREMENT")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setIncrementBy(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.CACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.TRUE);

                if (lexer.token() == Token.LITERAL_INT || lexer.token() == Token.QUES) {
                    stmt.setCacheValue(this.exprParser.primary());
                }

                continue;
            } else if (lexer.token() == Token.NOCACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.ORDER) {
                lexer.nextToken();
                stmt.setOrder(Boolean.TRUE);
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.RESTART)) {
                lexer.nextToken();
                stmt.setRestart(true);

                if (lexer.token == Token.WITH || lexer.token == Token.EQ) {
                    lexer.nextToken();
                    stmt.setRestartWith(this.exprParser.primary());
                } else if(lexer.token == LITERAL_INT) {
                    stmt.setRestartWith(this.exprParser.primary());
                }

                continue;
            } else if (lexer.identifierEquals("NOORDER")) {
                lexer.nextToken();
                stmt.setOrder(Boolean.FALSE);
                continue;
            } else if (lexer.identifierEquals("CYCLE")) {
                lexer.nextToken();
                stmt.setCycle(Boolean.TRUE);
                continue;
            } else if (lexer.identifierEquals("NOCYCLE")) {
                lexer.nextToken();
                stmt.setCycle(Boolean.FALSE);
                continue;
            } else if (lexer.identifierEquals("MINVALUE")) {
                lexer.nextToken();
                stmt.setMinValue(this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals("MAXVALUE")) {
                lexer.nextToken();
                stmt.setMaxValue(this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals("NOMAXVALUE")) {
                lexer.nextToken();
                stmt.setNoMaxValue(true);
                continue;
            } else if (lexer.identifierEquals("NOMINVALUE")) {
                lexer.nextToken();
                stmt.setNoMinValue(true);
                continue;
            }
            break;
        }

        return stmt;
    }

    protected SQLStatement parseMsck() {
        lexer.nextToken();
        lexer.identifierEquals("REPAIR");

        lexer.nextToken();

        HiveMsckRepairStatement stmt = new HiveMsckRepairStatement();

        if (lexer.token() == Token.DATABASE || lexer.token() == Token.SCHEMA) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setDatabase(name);
        }

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();

            SQLExpr tableExpr = this.exprParser.expr();
            stmt.setTable(tableExpr);
        }

        if (lexer.identifierEquals(FnvHash.Constants.ADD)) {
            lexer.nextToken();
            acceptIdentifier("PARTITIONS");
            stmt.setAddPartitions(true);
        }

        return stmt;
    }

    protected SQLStatement parseCreateResourceGroup()
    {
        accept(Token.CREATE);
        acceptIdentifier("RESOURCE");
        accept(Token.GROUP);

        SQLCreateResourceGroupStatement stmt = new SQLCreateResourceGroupStatement();
        stmt.setName(this.exprParser.name());

        for (;;) {
            if (lexer.token() == Token.SEMI || lexer.token() == Token.EOF) {
                break;
            }
            if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                stmt.setEnable(true);
            } else if (lexer.token() == Token.DISABLE) {
                lexer.nextToken();
                stmt.setEnable(false);
            }

            Lexer.SavePoint m = lexer.mark();
            String name = lexer.stringVal();
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
                SQLExpr value = this.exprParser.expr();
                if (lexer.token() == Token.COMMA) {
                    SQLListExpr list = new SQLListExpr();
                    list.addItem(value);
                    while (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        list.addItem(this.exprParser.expr());
                    }
                    stmt.addProperty(name, list);
                } else {
                    stmt.addProperty(name, value);
                }
            } else {
                lexer.reset(m);
                break;
            }
        }

        return stmt;
    }

    protected SQLStatement parseAlterResourceGroup() {
        accept(ALTER);
        acceptIdentifier("RESOURCE");
        accept(Token.GROUP);

        SQLAlterResourceGroupStatement stmt = new SQLAlterResourceGroupStatement();
        stmt.setName(this.exprParser.name());

        for (;;) {
            if (lexer.token() == Token.SEMI || lexer.token() == Token.EOF) {
                break;
            }

            if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                stmt.setEnable(true);
            } else if (lexer.token() == Token.DISABLE) {
                lexer.nextToken();
                stmt.setEnable(false);
            }

            Lexer.SavePoint m = lexer.mark();
            String name = lexer.stringVal();
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
                SQLExpr value = this.exprParser.expr();
                if (lexer.token() == Token.COMMA) {
                    SQLListExpr list = new SQLListExpr();
                    list.addItem(value);
                    while (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        list.addItem(this.exprParser.expr());
                    }
                    stmt.addProperty(name, list);
                } else {
                    stmt.addProperty(name, value);
                }
            } else {
                lexer.reset(m);
                break;
            }

        }

        return stmt;
    }

    public SQLStatement parseAlterMaterialized() {
        SQLAlterMaterializedViewStatement stmt = new SQLAlterMaterializedViewStatement();
        stmt.setDbType(dbType);

        accept(ALTER);
        acceptIdentifier("MATERIALIZED");
        accept(Token.VIEW);

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.EOF) {
            throw new ParserException("syntax error. " + lexer.info());
        }

        for (; ; ) {
            if (lexer.identifierEquals("REFRESH")) {
                lexer.nextToken();

                if (lexer.token() == Token.EOF) {
                    throw new ParserException("syntax error. " + lexer.info());
                }

                boolean refresh = false;
                for (; ; ) {
                    if (lexer.identifierEquals("FAST")) {
                        lexer.nextToken();
                        stmt.setRefreshFast(true);

                        refresh = true;
                    } else if (lexer.identifierEquals("COMPLETE")) {
                        lexer.nextToken();
                        stmt.setRefreshComplete(true);

                        refresh = true;
                    } else if (lexer.identifierEquals(FnvHash.Constants.FORCE)) {
                        lexer.nextToken();
                        stmt.setRefreshForce(true);

                        refresh = true;
                    } else if (lexer.token == Token.ON) {
                        lexer.nextToken();
                        if (lexer.token == Token.COMMIT || lexer.identifierEquals(FnvHash.Constants.COMMIT)) {
                            lexer.nextToken();
                            stmt.setRefreshOnCommit(true);
                        } else if (lexer.identifierEquals(FnvHash.Constants.OVERWRITE)) {
                            lexer.nextToken();
                            stmt.setRefreshOnOverWrite(true);
                        } else {
                            acceptIdentifier("DEMAND");
                            stmt.setRefreshOnDemand(true);
                        }

                        refresh = true;
                    } else if (lexer.identifierEquals(Constants.START)) {
                        lexer.nextToken();
                        accept(Token.WITH);
                        SQLExpr startWith = this.exprParser.expr();
                        stmt.setStartWith(startWith);
                        stmt.setRefreshStartWith(true);

                        refresh = true;
                    } else if (lexer.identifierEquals(FnvHash.Constants.NEXT)) {
                        lexer.nextToken();
                        SQLExpr next = this.exprParser.expr();
                        stmt.setNext(next);
                        stmt.setRefreshNext(true);

                        refresh = true;
                    } else {
                        break;
                    }
                }
                if (!refresh) {
                    throw new ParserException("refresh clause is empty. " + lexer.info());
                }
            } else {
                break;
            }
        }

        Boolean enableQueryRewrite = null;
        if (lexer.token == Token.ENABLE) {
            lexer.nextToken();
            enableQueryRewrite = true;
        }

        if (lexer.token == Token.DISABLE) {
            lexer.nextToken();
            enableQueryRewrite = false;
        }

        if (enableQueryRewrite != null) {
            acceptIdentifier("QUERY");
            acceptIdentifier("REWRITE");
            stmt.setEnableQueryRewrite(enableQueryRewrite);
        }

        return stmt;
    }

    public SQLCreateFunctionStatement parseHiveCreateFunction() {
        HiveCreateFunctionStatement stmt = new HiveCreateFunctionStatement();
        stmt.setDbType(dbType);

        accept(Token.CREATE);
        if (lexer.identifierEquals(FnvHash.Constants.TEMPORARY)) {
            lexer.nextToken();
            stmt.setTemporary(true);
        }
        accept(Token.FUNCTION);

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        if (lexer.token() == Token.AS) {
            lexer.setToken(Token.IDENTIFIER);
            lexer.nextToken();
            SQLExpr className = this.exprParser.expr();
            stmt.setClassName(className);
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOCATION)) {
            lexer.nextToken();
            SQLExpr location = this.exprParser.primary();
            stmt.setLocationn(location);
        }

        if (lexer.identifierEquals(FnvHash.Constants.SYMBOL)) {
            lexer.nextToken();
            accept(Token.EQ);
            SQLExpr symbol = this.exprParser.primary();
            stmt.setSymbol(symbol);
        }

        if (lexer.token() == Token.USING || lexer.hash_lower() == Constants.USING) {
            lexer.nextToken();

            if (lexer.identifierEquals(FnvHash.Constants.JAR)) {
                lexer.nextToken();
                stmt.setResourceType(HiveCreateFunctionStatement.ResourceType.JAR);
            } else if (lexer.identifierEquals(FnvHash.Constants.ARCHIVE)) {
                lexer.nextToken();
                stmt.setResourceType(HiveCreateFunctionStatement.ResourceType.ARCHIVE);
            } else if (lexer.identifierEquals(FnvHash.Constants.FILE)) {
                lexer.nextToken();
                stmt.setResourceType(HiveCreateFunctionStatement.ResourceType.FILE);
            }

            SQLExpr location = this.exprParser.primary();
            stmt.setLocationn(location);
        }

        return stmt;
    }


    protected SQLShowCreateTableStatement parseShowCreateTable() {
        lexer.nextToken();
        accept(Token.TABLE);

        SQLShowCreateTableStatement stmt = new SQLShowCreateTableStatement();
        stmt.setDbType(dbType);
        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.MAPPING)) {
                lexer.nextToken();
                accept(Token.LPAREN);
                SQLName name = this.exprParser.name();
                stmt.setLikeMapping(name);
                accept(Token.RPAREN);
            }
        }
        return stmt;
    }
}
