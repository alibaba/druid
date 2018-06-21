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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.SQLParameter.ParameterType;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.ConditionValue;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.ConditionValue.ConditionType;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareConditionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareHandlerStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlHandlerType;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlIterateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlLeaveStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement.LockType;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class MySqlStatementParser extends SQLStatementParser {

    private static final String AUTO_INCREMENT = "AUTO_INCREMENT";
    private static final String COLLATE2 = "COLLATE";
    private static final String CHAIN = "CHAIN";
    private static final String ENGINES = "ENGINES";
    private static final String ENGINE = "ENGINE";
    private static final String BINLOG = "BINLOG";
    private static final String EVENTS = "EVENTS";
    private static final String SESSION = "SESSION";
    private static final String GLOBAL = "GLOBAL";
    private static final String VARIABLES = "VARIABLES";
    private static final String STATUS = "STATUS";
    private static final String RESET = "RESET";
    private static final String DESCRIBE = "DESCRIBE";
    private static final String WRITE = "WRITE";
    private static final String READ = "READ";
    private static final String LOCAL = "LOCAL";
    private static final String TABLES = "TABLES";
    private static final String TEMPORARY = "TEMPORARY";
    private static final String SPATIAL = "SPATIAL";
    private static final String LOW_PRIORITY = "LOW_PRIORITY";
    private static final String CONNECTION = "CONNECTION";
    private static final String EXTENDED = "EXTENDED";
    private static final String PARTITIONS = "PARTITIONS";
    private static final String FORMAT = "FORMAT";

    private int maxIntoClause = -1;

    public MySqlStatementParser(String sql) {
        super(new MySqlExprParser(sql));
    }

    public MySqlStatementParser(String sql, SQLParserFeature... features) {
        super(new MySqlExprParser(sql, features));
    }

    public MySqlStatementParser(String sql, boolean keepComments) {
        super(new MySqlExprParser(sql, keepComments));
    }

    public MySqlStatementParser(String sql, boolean skipComment, boolean keepComments) {
        super(new MySqlExprParser(sql, skipComment, keepComments));
    }

    public MySqlStatementParser(Lexer lexer) {
        super(new MySqlExprParser(lexer));
    }

    public int getMaxIntoClause() {
        return maxIntoClause;
    }

    public void setMaxIntoClause(int maxIntoClause) {
        this.maxIntoClause = maxIntoClause;
    }

    public SQLCreateTableStatement parseCreateTable() {
        MySqlCreateTableParser parser = new MySqlCreateTableParser(this.exprParser);
        return parser.parseCreateTable();
    }

    public SQLStatement parseSelect() {
        MySqlSelectParser selectParser = createSQLSelectParser();

        SQLSelect select = selectParser.select();

        if (selectParser.returningFlag) {
            return selectParser.updateStmt;
        }

        return new SQLSelectStatement(select, JdbcConstants.MYSQL);
    }

    public SQLUpdateStatement parseUpdateStatement() {
        return new MySqlSelectParser(this.exprParser, selectListCache).parseUpdateStatment();
    }

    protected MySqlUpdateStatement createUpdateStatement() {
        return new MySqlUpdateStatement();
    }

    public MySqlDeleteStatement parseDeleteStatement() {
        MySqlDeleteStatement deleteStatement = new MySqlDeleteStatement();

        if (lexer.token() == Token.DELETE) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.HINT) {
                this.getExprParser().parseHints(deleteStatement.getHints());
            }

            if (lexer.identifierEquals(FnvHash.Constants.LOW_PRIORITY)) {
                deleteStatement.setLowPriority(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals("QUICK")) {
                deleteStatement.setQuick(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.IGNORE)) {
                deleteStatement.setIgnore(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.FORCE)) {
                Lexer.SavePoint savePoint = lexer.mark();
                lexer.nextToken();

                if (lexer.token() == Token.ALL) {
                    lexer.nextToken();
                    acceptIdentifier("PARTITIONS");
                    deleteStatement.setForceAllPartitions(true);
                } else if (lexer.identifierEquals(FnvHash.Constants.PARTITIONS)){
                    lexer.nextToken();
                    deleteStatement.setForceAllPartitions(true);
                } else if (lexer.token() == Token.PARTITION) {
                    lexer.nextToken();
                    SQLName partition = this.exprParser.name();
                    deleteStatement.setForcePartition(partition);
                } else {
                    lexer.reset(savePoint);
                }
            }

            if (lexer.token() == Token.IDENTIFIER) {
                deleteStatement.setTableSource(createSQLSelectParser().parseTableSource());

                if (lexer.token() == Token.FROM) {
                    lexer.nextToken();
                    SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
                    deleteStatement.setFrom(tableSource);
                }
            } else if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                deleteStatement.setTableSource(createSQLSelectParser().parseTableSource());
            } else {
                throw new ParserException("syntax error. " + lexer.info());
            }

            if (lexer.identifierEquals(FnvHash.Constants.USING)) {
                lexer.nextToken();

                SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
                deleteStatement.setUsing(tableSource);
            }
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        if (lexer.token() == (Token.ORDER)) {
            SQLOrderBy orderBy = exprParser.parseOrderBy();
            deleteStatement.setOrderBy(orderBy);
        }

        deleteStatement.setLimit(this.exprParser.parseLimit());

        return deleteStatement;
    }

    public SQLStatement parseCreate() {
        char markChar = lexer.current();
        int markBp = lexer.bp();

        List<String> comments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            comments = lexer.readAndResetComments();
        }

        accept(Token.CREATE);

        boolean replace = false;
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            replace = true;
        }

        List<SQLCommentHint> hints = this.exprParser.parseHints();

        if (lexer.token() == Token.TABLE || lexer.identifierEquals(TEMPORARY)) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            MySqlCreateTableParser parser = new MySqlCreateTableParser(this.exprParser);
            MySqlCreateTableStatement stmt = parser.parseCreateTable(false);
            stmt.setHints(hints);

            if (comments != null) {
                stmt.addBeforeComment(comments);
            }

            return stmt;
        }

        if (lexer.token() == Token.DATABASE
                || lexer.token() == Token.SCHEMA) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            return parseCreateDatabase();
        }

        if (lexer.token() == Token.UNIQUE || lexer.token() == Token.INDEX || lexer.token() == Token.FULLTEXT
                || lexer.identifierEquals(SPATIAL)) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            return parseCreateIndex(false);
        }

        if (lexer.token() == Token.USER) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            return parseCreateUser();
        }

        if (lexer.token() == Token.VIEW
                || lexer.identifierEquals(FnvHash.Constants.ALGORITHM)) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }

            return parseCreateView();
        }

        if (lexer.token() == Token.TRIGGER) {
            lexer.reset(markBp, markChar, Token.CREATE);
            return parseCreateTrigger();
        }

        // parse create procedure
        if (lexer.token() == Token.PROCEDURE ) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            return parseCreateProcedure();
        }

        if (lexer.identifierEquals(FnvHash.Constants.DEFINER)) {
//            Lexer.SavePoint savePoint = lexer.mark();
            lexer.nextToken();
            accept(Token.EQ);
            this.getExprParser().userName();

            if (lexer.identifierEquals(FnvHash.Constants.SQL)) {
                lexer.nextToken();
                acceptIdentifier("SECURITY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                lexer.nextToken();
            }
            if (lexer.identifierEquals(FnvHash.Constants.EVENT)) {
                lexer.reset(markBp, markChar, Token.CREATE);
                return parseCreateEvent();
            } else if (lexer.token() == Token.TRIGGER) {
                lexer.reset(markBp, markChar, Token.CREATE);
                return parseCreateTrigger();
            } else if (lexer.token() == Token.VIEW) {
                lexer.reset(markBp, markChar, Token.CREATE);
                return parseCreateView();
            } else if (lexer.token() == Token.FUNCTION) {
                lexer.reset(markBp, markChar, Token.CREATE);
                return parseCreateFunction();
            } else {
                lexer.reset(markBp, markChar, Token.CREATE);
                return parseCreateProcedure();
            }
        }

        if (lexer.token() == Token.FUNCTION) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            return parseCreateFunction();
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOGFILE)) {
            return parseCreateLogFileGroup();
        }

        if (lexer.identifierEquals(FnvHash.Constants.SERVER)) {
            return parseCreateServer();
        }

        if (lexer.token() == Token.TABLESPACE) {
            return parseCreateTableSpace();
        }

        throw new ParserException("TODO " + lexer.info());
    }

    public SQLStatement parseCreateTableSpace() {
        if (lexer.token() == Token.CREATE) {
            accept(Token.CREATE);
        }

        MySqlCreateTableSpaceStatement stmt = new MySqlCreateTableSpaceStatement();

        accept(Token.TABLESPACE);
        stmt.setName(this.exprParser.name());

        if (lexer.identifierEquals(FnvHash.Constants.ADD)) {
            lexer.nextToken();
            acceptIdentifier("DATAFILE");
            SQLExpr file = this.exprParser.primary();
            stmt.setAddDataFile(file);
        }

        for (;;) {
            if (lexer.identifierEquals(FnvHash.Constants.INITIAL_SIZE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr initialSize = this.exprParser.expr();
                stmt.setInitialSize(initialSize);
            } else if (lexer.identifierEquals(FnvHash.Constants.FILE_BLOCK_SIZE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr fileBlockSize = this.exprParser.expr();
                stmt.setFileBlockSize(fileBlockSize);
            } else if (lexer.identifierEquals(FnvHash.Constants.EXTENT_SIZE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr extentSize = this.exprParser.expr();
                stmt.setExtentSize(extentSize);
            } else if (lexer.identifierEquals(FnvHash.Constants.AUTOEXTEND_SIZE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr extentSize = this.exprParser.expr();
                stmt.setAutoExtentSize(extentSize);
            } else if (lexer.identifierEquals(FnvHash.Constants.MAX_SIZE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr size = this.exprParser.expr();
                stmt.setMaxSize(size);
            } else if (lexer.identifierEquals(FnvHash.Constants.NODEGROUP)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr size = this.exprParser.expr();
                stmt.setNodeGroup(size);
            } else if (lexer.identifierEquals(FnvHash.Constants.WAIT)) {
                lexer.nextToken();
                stmt.setWait(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr engine = this.exprParser.expr();
                stmt.setEngine(engine);
            } else if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                SQLExpr comment = this.exprParser.expr();
                stmt.setComment(comment);
            } else if (lexer.token() == Token.USE) {
                lexer.nextToken();
                acceptIdentifier("LOGFILE");
                accept(Token.GROUP);

                SQLExpr logFileGroup = this.exprParser.expr();
                stmt.setFileBlockSize(logFileGroup);
            } else {
                break;
            }
        }
        return stmt;
    }

    public SQLStatement parseCreateServer() {
        if (lexer.token() == Token.CREATE) {
            accept(Token.CREATE);
        }

        MySqlCreateServerStatement stmt = new MySqlCreateServerStatement();

        acceptIdentifier("SERVER");
        stmt.setName(this.exprParser.name());

        accept(Token.FOREIGN);
        acceptIdentifier("DATA");
        acceptIdentifier("WRAPPER");
        stmt.setForeignDataWrapper(this.exprParser.name());

        acceptIdentifier("OPTIONS");
        accept(Token.LPAREN);
        for (;;) {
            if (lexer.identifierEquals(FnvHash.Constants.HOST)) {
                lexer.nextToken();
                SQLExpr host = this.exprParser.expr();
                stmt.setHost(host);
            } else if (lexer.token() == Token.USER) {
                lexer.nextToken();
                SQLExpr user = this.exprParser.expr();
                stmt.setUser(user);
            } else if (lexer.token() == Token.DATABASE) {
                lexer.nextToken();
                SQLExpr db = this.exprParser.expr();
                stmt.setDatabase(db);
            } else if (lexer.identifierEquals(FnvHash.Constants.PASSWORD)) {
                lexer.nextToken();
                SQLExpr pwd = this.exprParser.expr();
                stmt.setPassword(pwd);
            } else if (lexer.identifierEquals(FnvHash.Constants.SOCKET)) {
                lexer.nextToken();
                SQLExpr sock = this.exprParser.expr();
                stmt.setSocket(sock);
            } else if (lexer.identifierEquals(FnvHash.Constants.OWNER)) {
                lexer.nextToken();
                SQLExpr owner = this.exprParser.expr();
                stmt.setOwner(owner);
            } else if (lexer.identifierEquals(FnvHash.Constants.PORT)) {
                lexer.nextToken();
                SQLExpr port = this.exprParser.expr();
                stmt.setPort(port);
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
            } else {
                break;
            }
        }
        accept(Token.RPAREN);
        return stmt;
    }

    public SQLStatement parseCreateIndex(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        SQLCreateIndexStatement stmt = new SQLCreateIndexStatement();

        if (lexer.token() == Token.UNIQUE) {
            stmt.setType("UNIQUE");
            lexer.nextToken();
        } else if (lexer.token() == Token.FULLTEXT) {
            stmt.setType("FULLTEXT");
            lexer.nextToken();
        } else if (lexer.identifierEquals(SPATIAL)) {
            stmt.setType(SPATIAL);
            lexer.nextToken();
        }

        accept(Token.INDEX);

        stmt.setName(this.exprParser.name());

        parseCreateIndexUsing(stmt);

        accept(Token.ON);

        stmt.setTable(this.exprParser.name());

        accept(Token.LPAREN);

        for (; ; ) {
            SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
            stmt.addItem(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);

        for (;;) {
            if (lexer.identifierEquals(FnvHash.Constants.USING)) {
                parseCreateIndexUsing(stmt);
            } else if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                SQLExpr comment = this.exprParser.expr();
                stmt.setComment(comment);
            } else {
                break;
            }
        }

        return stmt;
    }

    private void parseCreateIndexUsing(SQLCreateIndexStatement stmt) {
        if (lexer.identifierEquals(FnvHash.Constants.USING)) {
            lexer.nextToken();

            if (lexer.identifierEquals(FnvHash.Constants.BTREE)) {
                stmt.setUsing("BTREE");
                lexer.nextToken();
            } else if (lexer.identifierEquals(FnvHash.Constants.HASH)) {
                stmt.setUsing("HASH");
                lexer.nextToken();
            } else {
                throw new ParserException("TODO " + lexer.info());
            }
        }
    }

    public SQLStatement parseCreateUser() {
        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
        }

        accept(Token.USER);

        MySqlCreateUserStatement stmt = new MySqlCreateUserStatement();

        for (; ; ) {
            MySqlCreateUserStatement.UserSpecification userSpec = new MySqlCreateUserStatement.UserSpecification();

            SQLExpr expr = exprParser.primary();
            userSpec.setUser(expr);

            if (lexer.token() == Token.IDENTIFIED) {
                lexer.nextToken();
                if (lexer.token() == Token.BY) {
                    lexer.nextToken();

                    if (lexer.identifierEquals("PASSWORD")) {
                        lexer.nextToken();
                        userSpec.setPasswordHash(true);
                    }

                    SQLCharExpr password = (SQLCharExpr) this.exprParser.expr();
                    userSpec.setPassword(password);
                } else if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    userSpec.setAuthPlugin(this.exprParser.expr());
                }
            }

            stmt.addUser(userSpec);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return stmt;
    }

    public SQLStatement parseKill() {
        accept(Token.KILL);

        MySqlKillStatement stmt = new MySqlKillStatement();

        if (lexer.identifierEquals("CONNECTION")) {
            stmt.setType(MySqlKillStatement.Type.CONNECTION);
            lexer.nextToken();
        } else if (lexer.identifierEquals("QUERY")) {
            stmt.setType(MySqlKillStatement.Type.QUERY);
            lexer.nextToken();
        } else if (lexer.token() == Token.LITERAL_INT) {
            // skip
        } else {
            throw new ParserException("not support kill type " + lexer.token() + ". " + lexer.info());
        }

        this.exprParser.exprList(stmt.getThreadIds(), stmt);
        return stmt;
    }

    public SQLStatement parseBinlog() {
        acceptIdentifier("binlog");

        MySqlBinlogStatement stmt = new MySqlBinlogStatement();

        SQLExpr expr = this.exprParser.expr();
        stmt.setExpr(expr);

        return stmt;
    }

    public MySqlAnalyzeStatement parseAnalyze() {
        accept(Token.ANALYZE);
        accept(Token.TABLE);

        MySqlAnalyzeStatement stmt = new MySqlAnalyzeStatement();
        List<SQLName> names = new ArrayList<SQLName>();
        this.exprParser.names(names, stmt);

        for (SQLName name : names) {
            stmt.addTableSource(new SQLExprTableSource(name));
        }
        return stmt;
    }

    public MySqlOptimizeStatement parseOptimize() {
        accept(Token.OPTIMIZE);
        accept(Token.TABLE);

        MySqlOptimizeStatement stmt = new MySqlOptimizeStatement();
        List<SQLName> names = new ArrayList<SQLName>();
        this.exprParser.names(names, stmt);

        for (SQLName name : names) {
            stmt.addTableSource(new SQLExprTableSource(name));
        }
        return stmt;
    }

    public SQLStatement parseReset() {
        acceptIdentifier(RESET);

        MySqlResetStatement stmt = new MySqlResetStatement();

        for (; ; ) {
            if (lexer.token() == Token.IDENTIFIER) {
                if (lexer.identifierEquals("QUERY")) {
                    lexer.nextToken();
                    accept(Token.CACHE);
                    stmt.getOptions().add("QUERY CACHE");
                } else {
                    stmt.getOptions().add(lexer.stringVal());
                    lexer.nextToken();
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
            }
            break;
        }

        return stmt;
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.identifierEquals("PREPARE")) {
            MySqlPrepareStatement stmt = parsePrepare();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("EXECUTE")) {
            MySqlExecuteStatement stmt = parseExecute();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("DEALLOCATE")) {
            MysqlDeallocatePrepareStatement stmt = parseDeallocatePrepare();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("LOAD")) {
            SQLStatement stmt = parseLoad();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.REPLACE) {
            SQLReplaceStatement stmt = parseReplace();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("START")) {
            SQLStartTransactionStatement stmt = parseStart();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.SHOW) {
            SQLStatement stmt = parseShow();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.EXPLAIN) {
            SQLStatement stmt = this.parseExplain();
            statementList.add(stmt);
            return true;
        }


        if (lexer.identifierEquals(BINLOG)) {
            SQLStatement stmt = parseBinlog();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(RESET)) {
            SQLStatement stmt = parseReset();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.ANALYZE) {
            SQLStatement stmt = parseAnalyze();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.OPTIMIZE) {
            SQLStatement stmt = parseOptimize();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("HELP")) {
            lexer.nextToken();
            MySqlHelpStatement stmt = new MySqlHelpStatement();
            stmt.setContent(this.exprParser.primary());
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("FLUSH")) {
            SQLStatement stmt = parseFlush();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.DESC || lexer.identifierEquals(DESCRIBE)) {
            SQLStatement stmt = parseDescribe();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.LOCK) {
            lexer.nextToken();
            String val = lexer.stringVal();
            boolean isLockTables = TABLES.equalsIgnoreCase(val) && lexer.token() == Token.IDENTIFIER;
            boolean isLockTable = "TABLE".equalsIgnoreCase(val) && lexer.token() == Token.TABLE;
            if (isLockTables || isLockTable) {
                lexer.nextToken();
            } else {
                setErrorEndPos(lexer.pos());
                throw new ParserException("syntax error, expect TABLES or TABLE, actual " + lexer.token() + ", " + lexer.info());
            }

            MySqlLockTableStatement stmt = new MySqlLockTableStatement();

            for(;;) {
                MySqlLockTableStatement.Item item = new MySqlLockTableStatement.Item();

                SQLExprTableSource tableSource = null;
                SQLName tableName = this.exprParser.name();


                if (lexer.token() == Token.AS) {
                    lexer.nextToken();
                    String as = lexer.stringVal();
                    tableSource = new SQLExprTableSource(tableName, as);
                    lexer.nextToken();
                } else {
                    tableSource = new SQLExprTableSource(tableName);
                }
                item.setTableSource(tableSource);
                stmt.getItems().add(item);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                if (lexer.identifierEquals(READ)) {
                    lexer.nextToken();
                    if (lexer.identifierEquals(LOCAL)) {
                        lexer.nextToken();
                        item.setLockType(LockType.READ_LOCAL);
                    } else {
                        item.setLockType(LockType.READ);
                    }
                } else if (lexer.identifierEquals(WRITE)) {
                    lexer.nextToken();
                    item.setLockType(LockType.WRITE);
                } else if (lexer.identifierEquals(FnvHash.Constants.LOW_PRIORITY)) {
                    lexer.nextToken();
                    acceptIdentifier(WRITE);
                    lexer.nextToken();
                    item.setLockType(LockType.LOW_PRIORITY_WRITE);
                } else {
                    throw new ParserException(
                            "syntax error, expect READ or WRITE OR AS, actual " + lexer.token() + ", " + lexer.info());
                }

                if (lexer.token() == Token.HINT) {
                    item.setHints(this.exprParser.parseHints());
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }

            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals("UNLOCK")) {
            lexer.nextToken();
            String val = lexer.stringVal();
            boolean isUnLockTables = TABLES.equalsIgnoreCase(val) && lexer.token() == Token.IDENTIFIER;
            boolean isUnLockTable = "TABLE".equalsIgnoreCase(val) && lexer.token() == Token.TABLE;
            statementList.add(new MySqlUnlockTablesStatement());
            if (isUnLockTables || isUnLockTable) {
                lexer.nextToken();
            } else {
                setErrorEndPos(lexer.pos());
                throw new ParserException("syntax error, expect TABLES or TABLE, actual " + lexer.token() + ", " + lexer.info());
            }
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.CHECKSUM)) {
            statementList.add(this.parseChecksum());
            return true;
        }

        if (lexer.token() == Token.HINT) {
            List<SQLCommentHint> hints = this.exprParser.parseHints();

            boolean tddlHints = false;
            boolean accept = false;


            boolean acceptHint = false;
            switch (lexer.token()) {
                case SELECT:
                case WITH:
                case DELETE:
                case UPDATE:
                case INSERT:
                case SHOW:
                case REPLACE:
                case TRUNCATE:
                case DROP:
                case ALTER:
                case CREATE:
                case CHECK:
                case SET:
                    acceptHint = true;
                    break;
                case IDENTIFIER:
                    acceptHint = lexer.hash_lower() == FnvHash.Constants.DUMP
                            || lexer.hash_lower() == FnvHash.Constants.RENAME;
                    break;
                default:
                    break;
            }
            if (hints.size() == 1
                    && statementList.size() == 0
                    && acceptHint) {
                SQLCommentHint hint = hints.get(0);
                String hintText = hint.getText().toUpperCase();
                if (hintText.startsWith("+TDDL")
                        || hintText.startsWith("+ TDDL")
                        || hintText.startsWith("TDDL")
                        || hintText.startsWith("!TDDL"))
                {
                    tddlHints = true;
                } else if(hintText.startsWith("+")) {
                    accept = true;
                }
            }

            if (tddlHints) {
                SQLStatementImpl stmt = (SQLStatementImpl)this.parseStatement();
                stmt.setHeadHints(hints);
                statementList.add(stmt);
                return true;
            } else if (accept) {
                SQLStatementImpl stmt = (SQLStatementImpl) this.parseStatement();
                stmt.setHeadHints(hints);
                statementList.add(stmt);
                return true;
            }

            MySqlHintStatement stmt = new MySqlHintStatement();
            stmt.setHints(hints);

            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.BEGIN) {
            statementList.add(this.parseBlock());
            return true;
        }

        if (lexer.token() == Token.IDENTIFIER) {
            String label = lexer.stringVal();
            char ch = lexer.current();
            int bp = lexer.bp();
            lexer.nextToken();
            if (lexer.token() == Token.VARIANT && lexer.stringVal().equals(":")) {
                lexer.nextToken();
                if (lexer.token() == Token.LOOP) {
                    // parse loop statement
                    statementList.add(this.parseLoop(label));
                } else if (lexer.token() == Token.WHILE) {
                    // parse while statement with label
                    statementList.add(this.parseWhile(label));
                } else if (lexer.token() == Token.BEGIN) {
                    // parse begin-end statement with label
                    SQLBlockStatement block = this.parseBlock(label);
                    statementList.add(block);
                } else if (lexer.token() == Token.REPEAT) {
                    // parse repeat statement with label
                    statementList.add(this.parseRepeat(label));
                }
                return true;
            } else {
                lexer.reset(bp, ch, Token.IDENTIFIER);
            }

        }

        return false;
    }

    public SQLStatement parseFlush() {
        acceptIdentifier("FLUSH");
        MySqlFlushStatement stmt = new MySqlFlushStatement();

        if (lexer.identifierEquals("NO_WRITE_TO_BINLOG")) {
            lexer.nextToken();
            stmt.setNoWriteToBinlog(true);
        }

        if (lexer.identifierEquals("LOCAL")) {
            lexer.nextToken();
            stmt.setLocal(true);
        }

        for (;;) {
            if (lexer.token() == Token.BINARY || lexer.identifierEquals("BINARY")) {
                lexer.nextToken();
                acceptIdentifier("LOGS");
                stmt.setBinaryLogs(true);
            } else if (lexer.identifierEquals("DES_KEY_FILE")) {
                lexer.nextToken();
                stmt.setDesKeyFile(true);
            } else if (lexer.identifierEquals("ENGINE")) {
                lexer.nextToken();
                acceptIdentifier("LOGS");
                stmt.setEngineLogs(true);
            } else if (lexer.identifierEquals("ERROR")) {
                lexer.nextToken();
                acceptIdentifier("LOGS");
                stmt.setErrorLogs(true);
            } else if (lexer.identifierEquals("GENERAL")) {
                lexer.nextToken();
                acceptIdentifier("LOGS");
                stmt.setGeneralLogs(true);
            } else if (lexer.identifierEquals("HOSTS")) {
                lexer.nextToken();
                stmt.setHots(true);
            } else if (lexer.identifierEquals("LOGS")) {
                lexer.nextToken();
                stmt.setLogs(true);
            } else if (lexer.identifierEquals("PRIVILEGES")) {
                lexer.nextToken();
                stmt.setPrivileges(true);
            } else if (lexer.identifierEquals("OPTIMIZER_COSTS")) {
                lexer.nextToken();
                stmt.setOptimizerCosts(true);
            } else if (lexer.identifierEquals("QUERY")) {
                lexer.nextToken();
                accept(Token.CACHE);
                stmt.setQueryCache(true);
            }  else if (lexer.identifierEquals("RELAY")) {
                lexer.nextToken();
                acceptIdentifier("LOGS");
                stmt.setRelayLogs(true);
                if (lexer.token() == Token.FOR) {
                    lexer.nextToken();
                    acceptIdentifier("CHANNEL");
                    stmt.setRelayLogsForChannel(this.exprParser.primary());
                }
            } else if (lexer.identifierEquals("SLOW")) {
                lexer.nextToken();
                acceptIdentifier("LOGS");
                stmt.setSlowLogs(true);
            } else if (lexer.identifierEquals(STATUS)) {
                lexer.nextToken();
                stmt.setStatus(true);
            } else  if (lexer.identifierEquals("USER_RESOURCES")) {
                lexer.nextToken();
                stmt.setUserResources(true);
            } else if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            } else {
                break;
            }
        }

        if (lexer.identifierEquals("TABLES")) {
            lexer.nextToken();

            stmt.setTableOption(true);

            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                acceptIdentifier("READ");
                accept(Token.LOCK);
                stmt.setWithReadLock(true);
            }
            for(;;) {
                if (lexer.token() == Token.IDENTIFIER) {
                    for (; ; ) {
                        SQLName name = this.exprParser.name();
                        stmt.addTable(name);

                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    break;
                }
                break;
            }

            if (stmt.getTables().size() != 0) {
                if (lexer.token() == Token.FOR) {
                    lexer.nextToken();
                    acceptIdentifier("EXPORT");
                    stmt.setForExport(true);
                } else if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    acceptIdentifier("READ");
                    accept(Token.LOCK);
                    stmt.setWithReadLock(true);
                }
            }

        }

        return stmt;
    }

    public SQLBlockStatement parseBlock() {
        SQLBlockStatement block = new SQLBlockStatement();
        block.setDbType(dbType);

        accept(Token.BEGIN);
        List<SQLStatement> statementList = block.getStatementList();
        this.parseStatementList(statementList, -1, block);

        if (lexer.token() != Token.END
                && statementList.size() > 0
                && (statementList.get(statementList.size() - 1) instanceof SQLCommitStatement
                    || statementList.get(statementList.size() - 1) instanceof SQLRollbackStatement)) {
            block.setEndOfCommit(true);
            return block;
        }
        accept(Token.END);

        return block;
    }

    public MySqlExplainStatement parseDescribe() {
        // see https://dev.mysql.com/doc/refman/5.7/en/explain.html
        MySqlExplainStatement describe = new MySqlExplainStatement();

        // {DESCRIBE | DESC}
        if (lexer.token() == Token.DESC || lexer.identifierEquals(DESCRIBE)) {
            lexer.nextToken();
            describe.setDescribe(true);
        } else {
            throw new ParserException("expect one of {DESCRIBE | DESC} , actual " + lexer.token() + ", " + lexer.info());
        }

        return parseExplain(describe);
    }

    public MySqlExplainStatement parseExplain() {
        // see https://dev.mysql.com/doc/refman/5.7/en/explain.html
        MySqlExplainStatement explain = new MySqlExplainStatement();

        // {EXPLAIN}
        if (lexer.token() == Token.EXPLAIN) {
            lexer.nextToken();
        } else {
            throw new ParserException("expect EXPLAIN , actual " + lexer.token() + ", " + lexer.info());
        }

        return parseExplain(explain);
    }


    private MySqlExplainStatement parseExplain(MySqlExplainStatement explain) {

        if (lexer.token() == Token.HINT) {
            List<SQLCommentHint> hints = this.exprParser.parseHints();
            explain.setHints(hints);
        }
        // see https://dev.mysql.com/doc/refman/5.7/en/explain.html

        boolean table = false;
        if (lexer.token() == Token.IDENTIFIER) {
             String stringVal = lexer.stringVal();

             if (stringVal.equalsIgnoreCase(EXTENDED)
                     || stringVal.equalsIgnoreCase(PARTITIONS)) {
                 explain.setType(stringVal);
                 lexer.nextToken();
             } else if (stringVal.equalsIgnoreCase(FORMAT)) {
                 explain.setType(stringVal);
                 lexer.nextToken();
                 accept(Token.EQ);

                 String format = lexer.stringVal();
                 explain.setFormat(format);
                 accept(Token.IDENTIFIER);
             } else {
                 explain.setTableName(exprParser.name());
                 if (lexer.token() == Token.IDENTIFIER) {
                     explain.setColumnName(exprParser.name());
                 } else if (lexer.token() == Token.LITERAL_CHARS) {
                     explain.setWild(exprParser.expr());
                 }
                 table = true;
             }
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();
            acceptIdentifier(CONNECTION);
            explain.setConnectionId(exprParser.expr());
        } else if (!table) {
            explain.setStatement(this.parseStatement());
        }

        return explain;
    }

    public SQLStatement parseShow() {
        accept(Token.SHOW);

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
        }

        boolean full = false;
        if (lexer.token() == Token.FULL) {
            lexer.nextToken();
            full = true;
        }

        if (lexer.identifierEquals("PROCESSLIST")) {
            lexer.nextToken();
            MySqlShowProcessListStatement stmt = new MySqlShowProcessListStatement();
            stmt.setFull(full);
            return stmt;
        }

        if (lexer.identifierEquals("COLUMNS") || lexer.identifierEquals("FIELDS")) {
            lexer.nextToken();

            MySqlShowColumnsStatement stmt = parseShowColumns();
            stmt.setFull(full);

            return stmt;
        }

        if (lexer.identifierEquals("COLUMNS")) {
            lexer.nextToken();

            MySqlShowColumnsStatement stmt = parseShowColumns();

            return stmt;
        }

        if (lexer.identifierEquals(TABLES)) {
            lexer.nextToken();

            SQLShowTablesStatement stmt = parseShowTabless();
            stmt.setFull(full);

            return stmt;
        }

        if (lexer.identifierEquals("DATABASES")) {
            lexer.nextToken();

            MySqlShowDatabasesStatement stmt = parseShowDatabases();

            return stmt;
        }

        if (lexer.identifierEquals("WARNINGS")) {
            lexer.nextToken();

            MySqlShowWarningsStatement stmt = parseShowWarnings();

            return stmt;
        }

        if (lexer.identifierEquals("COUNT")) {
            lexer.nextToken();
            accept(Token.LPAREN);
            accept(Token.STAR);
            accept(Token.RPAREN);

            if (lexer.identifierEquals(FnvHash.Constants.ERRORS)) {
                lexer.nextToken();

                MySqlShowErrorsStatement stmt = new MySqlShowErrorsStatement();
                stmt.setCount(true);

                return stmt;
            } else {
                acceptIdentifier("WARNINGS");

                MySqlShowWarningsStatement stmt = new MySqlShowWarningsStatement();
                stmt.setCount(true);

                return stmt;
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.ERRORS)) {
            lexer.nextToken();

            MySqlShowErrorsStatement stmt = new MySqlShowErrorsStatement();
            stmt.setLimit(this.exprParser.parseLimit());

            return stmt;
        }

        if (lexer.identifierEquals(STATUS)) {
            lexer.nextToken();

            MySqlShowStatusStatement stmt = parseShowStatus();

            return stmt;
        }

        if (lexer.identifierEquals(VARIABLES)) {
            lexer.nextToken();

            MySqlShowVariantsStatement stmt = parseShowVariants();

            return stmt;
        }

        if (lexer.identifierEquals(GLOBAL)) {
            lexer.nextToken();

            if (lexer.identifierEquals(STATUS)) {
                lexer.nextToken();
                MySqlShowStatusStatement stmt = parseShowStatus();
                stmt.setGlobal(true);
                return stmt;
            }

            if (lexer.identifierEquals(VARIABLES)) {
                lexer.nextToken();
                MySqlShowVariantsStatement stmt = parseShowVariants();
                stmt.setGlobal(true);
                return stmt;
            }
        }

        if (lexer.identifierEquals(SESSION)) {
            lexer.nextToken();

            if (lexer.identifierEquals(STATUS)) {
                lexer.nextToken();
                MySqlShowStatusStatement stmt = parseShowStatus();
                stmt.setSession(true);
                return stmt;
            }

            if (lexer.identifierEquals(VARIABLES)) {
                lexer.nextToken();
                MySqlShowVariantsStatement stmt = parseShowVariants();
                stmt.setSession(true);
                return stmt;
            }
        }

        if (lexer.identifierEquals("COBAR_STATUS")) {
            lexer.nextToken();
            return new CobarShowStatus();
        }

        if (lexer.identifierEquals("AUTHORS")) {
            lexer.nextToken();
            return new MySqlShowAuthorsStatement();
        }

        if (lexer.token() == Token.BINARY) {
            lexer.nextToken();
            acceptIdentifier("LOGS");
            return new MySqlShowBinaryLogsStatement();
        }

        if (lexer.identifierEquals("MASTER")) {
            lexer.nextToken();
            if (lexer.identifierEquals("LOGS")) {
                lexer.nextToken();
                return new MySqlShowMasterLogsStatement();
            }
            acceptIdentifier(STATUS);
            return new MySqlShowMasterStatusStatement();
        }

        if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
            lexer.nextToken();
            accept(Token.SET);
            MySqlShowCharacterSetStatement stmt = new MySqlShowCharacterSetStatement();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setPattern(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }

            return stmt;
        }

        if (lexer.identifierEquals("COLLATION")) {
            lexer.nextToken();
            MySqlShowCollationStatement stmt = new MySqlShowCollationStatement();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setPattern(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }

            return stmt;
        }

        if (lexer.identifierEquals(BINLOG)) {
            lexer.nextToken();
            acceptIdentifier(EVENTS);
            MySqlShowBinLogEventsStatement stmt = new MySqlShowBinLogEventsStatement();

            if (lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setIn(this.exprParser.expr());
            }

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                stmt.setFrom(this.exprParser.expr());
            }

            stmt.setLimit(this.exprParser.parseLimit());

            return stmt;
        }

        if (lexer.identifierEquals("CONTRIBUTORS")) {
            lexer.nextToken();
            return new MySqlShowContributorsStatement();
        }

        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();

            if (lexer.token() == Token.DATABASE) {
                lexer.nextToken();

                MySqlShowCreateDatabaseStatement stmt = new MySqlShowCreateDatabaseStatement();
                stmt.setDatabase(this.exprParser.name());
                return stmt;
            }

            if (lexer.identifierEquals("EVENT")) {
                lexer.nextToken();

                MySqlShowCreateEventStatement stmt = new MySqlShowCreateEventStatement();
                stmt.setEventName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.FUNCTION) {
                lexer.nextToken();

                MySqlShowCreateFunctionStatement stmt = new MySqlShowCreateFunctionStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.PROCEDURE) {
                lexer.nextToken();

                MySqlShowCreateProcedureStatement stmt = new MySqlShowCreateProcedureStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.TABLE) {
                lexer.nextToken();

                MySqlShowCreateTableStatement stmt = new MySqlShowCreateTableStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.VIEW) {
                lexer.nextToken();

                MySqlShowCreateViewStatement stmt = new MySqlShowCreateViewStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.TRIGGER) {
                lexer.nextToken();

                MySqlShowCreateTriggerStatement stmt = new MySqlShowCreateTriggerStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            throw new ParserException("TODO " + lexer.info());
        }

        if (lexer.identifierEquals(ENGINE)) {
            lexer.nextToken();
            MySqlShowEngineStatement stmt = new MySqlShowEngineStatement();
            stmt.setName(this.exprParser.name());
            stmt.setOption(MySqlShowEngineStatement.Option.valueOf(lexer.stringVal().toUpperCase()));
            lexer.nextToken();
            return stmt;
        }

        if (lexer.identifierEquals("STORAGE")) {
            lexer.nextToken();
            acceptIdentifier(ENGINES);
            MySqlShowEnginesStatement stmt = new MySqlShowEnginesStatement();
            stmt.setStorage(true);
            return stmt;
        }

        if (lexer.identifierEquals(ENGINES)) {
            lexer.nextToken();
            MySqlShowEnginesStatement stmt = new MySqlShowEnginesStatement();
            return stmt;
        }

        if (lexer.identifierEquals(EVENTS)) {
            lexer.nextToken();
            MySqlShowEventsStatement stmt = new MySqlShowEventsStatement();

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setSchema(this.exprParser.name());
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }
            return stmt;
        }

        if (lexer.token() == Token.FUNCTION) {
            lexer.nextToken();

            if (lexer.identifierEquals("CODE")) {
                lexer.nextToken();
                MySqlShowFunctionCodeStatement stmt = new MySqlShowFunctionCodeStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            acceptIdentifier(STATUS);
            MySqlShowFunctionStatusStatement stmt = new MySqlShowFunctionStatusStatement();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }
            return stmt;
        }

        // MySqlShowFunctionStatusStatement

        if (lexer.identifierEquals(ENGINE)) {
            lexer.nextToken();
            MySqlShowEngineStatement stmt = new MySqlShowEngineStatement();
            stmt.setName(this.exprParser.name());
            stmt.setOption(MySqlShowEngineStatement.Option.valueOf(lexer.stringVal().toUpperCase()));
            lexer.nextToken();
            return stmt;
        }

        if (lexer.identifierEquals("STORAGE")) {
            lexer.nextToken();
            accept(Token.EQ);
            accept(Token.DEFAULT);
            MySqlShowEnginesStatement stmt = new MySqlShowEnginesStatement();
            stmt.setStorage(true);
            return stmt;
        }

        if (lexer.identifierEquals(ENGINES)) {
            lexer.nextToken();
            MySqlShowEnginesStatement stmt = new MySqlShowEnginesStatement();
            return stmt;
        }

        if (lexer.identifierEquals("GRANTS")) {
            lexer.nextToken();
            MySqlShowGrantsStatement stmt = new MySqlShowGrantsStatement();

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                stmt.setUser(this.exprParser.expr());
            }

            return stmt;
        }

        if (lexer.token() == Token.INDEX || lexer.identifierEquals("INDEXES")) {
            lexer.nextToken();
            MySqlShowIndexesStatement stmt = new MySqlShowIndexesStatement();

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                SQLName table = exprParser.name();
                stmt.setTable(table);

                if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                    lexer.nextToken();
                    SQLName database = exprParser.name();
                    stmt.setDatabase(database);
                }
            }

            if (lexer.token() == Token.HINT) {
                stmt.setHints(this.exprParser.parseHints());
            }

            return stmt;
        }

        if (lexer.identifierEquals("KEYS")) {
            lexer.nextToken();
            MySqlShowKeysStatement stmt = new MySqlShowKeysStatement();

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                SQLName table = exprParser.name();
                stmt.setTable(table);

                if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                    lexer.nextToken();
                    SQLName database = exprParser.name();
                    stmt.setDatabase(database);
                }
            }

            return stmt;
        }

        if (lexer.token() == Token.OPEN || lexer.identifierEquals("OPEN")) {
            lexer.nextToken();
            acceptIdentifier(TABLES);
            MySqlShowOpenTablesStatement stmt = new MySqlShowOpenTablesStatement();

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setDatabase(this.exprParser.name());
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }
            return stmt;
        }

        if (lexer.identifierEquals("PLUGINS")) {
            lexer.nextToken();
            MySqlShowPluginsStatement stmt = new MySqlShowPluginsStatement();
            return stmt;
        }

        if (lexer.identifierEquals("PRIVILEGES")) {
            lexer.nextToken();
            MySqlShowPrivilegesStatement stmt = new MySqlShowPrivilegesStatement();
            return stmt;
        }

        if (lexer.token() == Token.PROCEDURE) {
            lexer.nextToken();

            if (lexer.identifierEquals("CODE")) {
                lexer.nextToken();
                MySqlShowProcedureCodeStatement stmt = new MySqlShowProcedureCodeStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            acceptIdentifier(STATUS);
            MySqlShowProcedureStatusStatement stmt = new MySqlShowProcedureStatusStatement();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }
            return stmt;
        }

        if (lexer.identifierEquals("PROCESSLIST")) {
            lexer.nextToken();
            MySqlShowProcessListStatement stmt = new MySqlShowProcessListStatement();
            return stmt;
        }

        if (lexer.identifierEquals("PROFILES")) {
            lexer.nextToken();
            MySqlShowProfilesStatement stmt = new MySqlShowProfilesStatement();
            return stmt;
        }

        if (lexer.identifierEquals("PROFILE")) {
            lexer.nextToken();
            MySqlShowProfileStatement stmt = new MySqlShowProfileStatement();

            for (; ; ) {
                if (lexer.token() == Token.ALL) {
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.ALL);
                    lexer.nextToken();
                } else if (lexer.identifierEquals("BLOCK")) {
                    lexer.nextToken();
                    acceptIdentifier("IO");
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.BLOCK_IO);
                } else if (lexer.identifierEquals("CONTEXT")) {
                    lexer.nextToken();
                    acceptIdentifier("SWITCHES");
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.CONTEXT_SWITCHES);
                } else if (lexer.identifierEquals("CPU")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.CPU);
                } else if (lexer.identifierEquals("IPC")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.IPC);
                } else if (lexer.identifierEquals("MEMORY")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.MEMORY);
                } else if (lexer.identifierEquals("PAGE")) {
                    lexer.nextToken();
                    acceptIdentifier("FAULTS");
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.PAGE_FAULTS);
                } else if (lexer.identifierEquals("SOURCE")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.SOURCE);
                } else if (lexer.identifierEquals("SWAPS")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.SWAPS);
                } else {
                    break;
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                acceptIdentifier("QUERY");
                stmt.setForQuery(this.exprParser.primary());
            }

            stmt.setLimit(this.exprParser.parseLimit());

            return stmt;
        }

        if (lexer.identifierEquals("RELAYLOG")) {
            lexer.nextToken();
            acceptIdentifier(EVENTS);
            MySqlShowRelayLogEventsStatement stmt = new MySqlShowRelayLogEventsStatement();

            if (lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setLogName(this.exprParser.primary());
            }

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                stmt.setFrom(this.exprParser.primary());
            }

            stmt.setLimit(this.exprParser.parseLimit());

            return stmt;
        }

        if (lexer.identifierEquals("RELAYLOG")) {
            lexer.nextToken();
            acceptIdentifier(EVENTS);
            MySqlShowRelayLogEventsStatement stmt = new MySqlShowRelayLogEventsStatement();

            if (lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setLogName(this.exprParser.primary());
            }

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                stmt.setFrom(this.exprParser.primary());
            }

            stmt.setLimit(this.exprParser.parseLimit());

            return stmt;
        }

        if (lexer.identifierEquals("SLAVE")) {
            lexer.nextToken();
            if (lexer.identifierEquals(STATUS)) {
                lexer.nextToken();
                return new MySqlShowSlaveStatusStatement();
            } else {
                acceptIdentifier("HOSTS");
                MySqlShowSlaveHostsStatement stmt = new MySqlShowSlaveHostsStatement();
                return stmt;
            }
        }

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();
            acceptIdentifier(STATUS);
            MySqlShowTableStatusStatement stmt = new MySqlShowTableStatusStatement();
            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setDatabase(this.exprParser.name());
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }

            return stmt;
        }

        if (lexer.token() == Token.DATABASE) {
            lexer.nextToken();
            accept(Token.PARTITION);
            acceptIdentifier("STATUS");
            accept(Token.FOR);
            MySqlShowDatabasePartitionStatusStatement stmt = new MySqlShowDatabasePartitionStatusStatement();
            stmt.setDatabase(this.exprParser.name());
            return stmt;
        }

        if (lexer.identifierEquals("TRIGGERS")) {
            lexer.nextToken();
            MySqlShowTriggersStatement stmt = new MySqlShowTriggersStatement();

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                SQLName database = exprParser.name();
                stmt.setDatabase(database);
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                SQLExpr like = exprParser.expr();
                stmt.setLike(like);
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                SQLExpr where = exprParser.expr();
                stmt.setWhere(where);
            }

            return stmt;
        }

        // MySqlShowSlaveHostsStatement
        throw new ParserException("TODO " + lexer.info());
    }

    private MySqlShowStatusStatement parseShowStatus() {
        MySqlShowStatusStatement stmt = new MySqlShowStatusStatement();

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    private MySqlShowVariantsStatement parseShowVariants() {
        MySqlShowVariantsStatement stmt = new MySqlShowVariantsStatement();

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    private MySqlShowWarningsStatement parseShowWarnings() {
        MySqlShowWarningsStatement stmt = new MySqlShowWarningsStatement();

        stmt.setLimit(this.exprParser.parseLimit());

        return stmt;
    }

    private MySqlShowDatabasesStatement parseShowDatabases() {
        MySqlShowDatabasesStatement stmt = new MySqlShowDatabasesStatement();

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    private SQLShowTablesStatement parseShowTabless() {
        SQLShowTablesStatement stmt = new SQLShowTablesStatement();

        if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
            lexer.nextToken();
            SQLName database = exprParser.name();
            if (lexer.token() == Token.SUB && database instanceof SQLIdentifierExpr) {
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

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    private MySqlShowColumnsStatement parseShowColumns() {
        MySqlShowColumnsStatement stmt = new MySqlShowColumnsStatement();

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLName table = exprParser.name();
            stmt.setTable(table);

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                SQLName database = exprParser.name();
                stmt.setDatabase(database);
            }
        }

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    public SQLStartTransactionStatement parseStart() {
        acceptIdentifier("START");
        acceptIdentifier("TRANSACTION");

        SQLStartTransactionStatement stmt = new SQLStartTransactionStatement();
        stmt.setDbType(dbType);

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            acceptIdentifier("CONSISTENT");
            acceptIdentifier("SNAPSHOT");
            stmt.setConsistentSnapshot(true);
        }

        if (lexer.token() == Token.BEGIN) {
            lexer.nextToken();
            stmt.setBegin(true);
            if (lexer.identifierEquals("WORK")) {
                lexer.nextToken();
                stmt.setWork(true);
            }
        }

        if (lexer.token() == Token.HINT) {
            stmt.setHints(this.exprParser.parseHints());
        }

        return stmt;
    }

    @Override
    public SQLRollbackStatement parseRollback() {
        acceptIdentifier("ROLLBACK");

        SQLRollbackStatement stmt = new SQLRollbackStatement();

        if (lexer.identifierEquals("WORK")) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.AND) {
            lexer.nextToken();
            if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                acceptIdentifier(CHAIN);
                stmt.setChain(Boolean.FALSE);
            } else {
                acceptIdentifier(CHAIN);
                stmt.setChain(Boolean.TRUE);
            }
        }

        if (lexer.token() == Token.TO) {
            lexer.nextToken();

            if (lexer.identifierEquals("SAVEPOINT")) {
                lexer.nextToken();
            }

            stmt.setTo(this.exprParser.name());
        }

        return stmt;
    }

    public SQLStatement parseCommit() {
        acceptIdentifier("COMMIT");

        SQLCommitStatement stmt = new SQLCommitStatement();

        if (lexer.identifierEquals("WORK")) {
            lexer.nextToken();
            stmt.setWork(true);
        }

        if (lexer.token() == Token.AND) {
            lexer.nextToken();
            if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                acceptIdentifier(CHAIN);
                stmt.setChain(Boolean.FALSE);
            } else {
                acceptIdentifier(CHAIN);
                stmt.setChain(Boolean.TRUE);
            }
        }

        return stmt;
    }

    public SQLReplaceStatement parseReplace() {
        SQLReplaceStatement stmt = new SQLReplaceStatement();

        accept(Token.REPLACE);

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOW_PRIORITY)) {
            stmt.setLowPriority(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.DELAYED)) {
            stmt.setDelayed(true);
            lexer.nextToken();
        }

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
        }

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            if (lexer.token() == Token.SELECT) {
                SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
                stmt.setQuery(queryExpr);
            } else {
                this.exprParser.exprList(stmt.getColumns(), stmt);
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.VALUES || lexer.identifierEquals("VALUE")) {
            lexer.nextToken();

            parseValueClause(stmt.getValuesList(), 0, stmt);
        } else if (lexer.token() == Token.SELECT) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            stmt.setQuery(queryExpr);
        } else if (lexer.token() == Token.SET) {
            lexer.nextToken();

            SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
            values.setParent(stmt);
            stmt.getValuesList().add(values);
            for (; ; ) {
                stmt.addColumn(this.exprParser.name());
                if (lexer.token() == Token.COLONEQ) {
                    lexer.nextToken();
                } else {
                    accept(Token.EQ);
                }
                values.addValue(this.exprParser.expr());

                if (lexer.token() == (Token.COMMA)) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
        } else if (lexer.token() == Token.LPAREN) {
            SQLSelect select = this.createSQLSelectParser().select();
            SQLQueryExpr queryExpr = new SQLQueryExpr(select);
            stmt.setQuery(queryExpr);
        }

        return stmt;
    }

    protected SQLStatement parseLoad() {
        acceptIdentifier("LOAD");

        if (lexer.identifierEquals("DATA")) {
            SQLStatement stmt = parseLoadDataInFile();
            return stmt;
        }

        if (lexer.identifierEquals("XML")) {
            SQLStatement stmt = parseLoadXml();
            return stmt;
        }

        throw new ParserException("TODO. " + lexer.info());
    }

    protected MySqlLoadXmlStatement parseLoadXml() {
        acceptIdentifier("XML");

        MySqlLoadXmlStatement stmt = new MySqlLoadXmlStatement();

        if (lexer.identifierEquals(FnvHash.Constants.LOW_PRIORITY)) {
            stmt.setLowPriority(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals("CONCURRENT")) {
            stmt.setConcurrent(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals(LOCAL)) {
            stmt.setLocal(true);
            lexer.nextToken();
        }

        acceptIdentifier("INFILE");

        SQLLiteralExpr fileName = (SQLLiteralExpr) exprParser.expr();
        stmt.setFileName(fileName);

        if (lexer.token() == Token.REPLACE) {
            stmt.setReplicate(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.IGNORE)) {
            stmt.setIgnore(true);
            lexer.nextToken();
        }

        accept(Token.INTO);
        accept(Token.TABLE);

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
            lexer.nextToken();
            accept(Token.SET);

            if (lexer.token() != Token.LITERAL_CHARS) {
                throw new ParserException("syntax error, illegal charset. "  + lexer.info());
            }

            String charset = lexer.stringVal();
            lexer.nextToken();
            stmt.setCharset(charset);
        }

        if (lexer.identifierEquals("ROWS")) {
            lexer.nextToken();
            accept(Token.IDENTIFIED);
            accept(Token.BY);
            SQLExpr rowsIdentifiedBy = exprParser.expr();
            stmt.setRowsIdentifiedBy(rowsIdentifiedBy);
        }

        if (lexer.identifierEquals(FnvHash.Constants.IGNORE)) {
            throw new ParserException("TODO. " + lexer.info());
        }

        if (lexer.token() == Token.SET) {
            throw new ParserException("TODO. " + lexer.info());
        }

        return stmt;
    }

    protected MySqlLoadDataInFileStatement parseLoadDataInFile() {

        acceptIdentifier("DATA");

        MySqlLoadDataInFileStatement stmt = new MySqlLoadDataInFileStatement();

        if (lexer.identifierEquals(FnvHash.Constants.LOW_PRIORITY)) {
            stmt.setLowPriority(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals("CONCURRENT")) {
            stmt.setConcurrent(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals(LOCAL)) {
            stmt.setLocal(true);
            lexer.nextToken();
        }

        acceptIdentifier("INFILE");

        SQLLiteralExpr fileName = (SQLLiteralExpr) exprParser.expr();
        stmt.setFileName(fileName);

        if (lexer.token() == Token.REPLACE) {
            stmt.setReplicate(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.IGNORE)) {
            stmt.setIgnore(true);
            lexer.nextToken();
        }

        accept(Token.INTO);
        accept(Token.TABLE);

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
            lexer.nextToken();
            accept(Token.SET);

            if (lexer.token() != Token.LITERAL_CHARS) {
                throw new ParserException("syntax error, illegal charset. " + lexer.info());
            }

            String charset = lexer.stringVal();
            lexer.nextToken();
            stmt.setCharset(charset);
        }

        if (lexer.identifierEquals("FIELDS") || lexer.identifierEquals("COLUMNS")) {
            lexer.nextToken();
            if (lexer.identifierEquals("TERMINATED")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setColumnsTerminatedBy(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            if (lexer.identifierEquals("OPTIONALLY")) {
                stmt.setColumnsEnclosedOptionally(true);
                lexer.nextToken();
            }

            if (lexer.identifierEquals("ENCLOSED")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setColumnsEnclosedBy(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            if (lexer.identifierEquals("ESCAPED")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setColumnsEscaped(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }
        }

        if (lexer.identifierEquals("LINES")) {
            lexer.nextToken();
            if (lexer.identifierEquals("STARTING")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setLinesStartingBy(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            if (lexer.identifierEquals("TERMINATED")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setLinesTerminatedBy(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.IGNORE)) {
            lexer.nextToken();
            stmt.setIgnoreLinesNumber(this.exprParser.expr());
            acceptIdentifier("LINES");
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            this.exprParser.exprList(stmt.getColumns(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.SET) {
            lexer.nextToken();
            this.exprParser.exprList(stmt.getSetList(), stmt);
        }

        return stmt;

    }

    public MySqlPrepareStatement parsePrepare() {
        acceptIdentifier("PREPARE");

        SQLName name = exprParser.name();
        accept(Token.FROM);
        SQLExpr from = exprParser.expr();

        return new MySqlPrepareStatement(name, from);
    }

    public MySqlExecuteStatement parseExecute() {
        acceptIdentifier("EXECUTE");

        MySqlExecuteStatement stmt = new MySqlExecuteStatement();

        SQLName statementName = exprParser.name();
        stmt.setStatementName(statementName);

        if (lexer.identifierEquals("USING")) {
            lexer.nextToken();
            exprParser.exprList(stmt.getParameters(), stmt);
        } else if (lexer.token() == Token.IDENTIFIER) {
            exprParser.exprList(stmt.getParameters(), stmt);
        }

        return stmt;
    }

    public MysqlDeallocatePrepareStatement parseDeallocatePrepare() {
        acceptIdentifier("DEALLOCATE");
        acceptIdentifier("PREPARE");

        MysqlDeallocatePrepareStatement stmt = new MysqlDeallocatePrepareStatement();
        SQLName statementName = exprParser.name();
        stmt.setStatementName(statementName);

        return stmt;
    }

    public SQLInsertStatement parseInsert() {
        MySqlInsertStatement stmt = new MySqlInsertStatement();

        SQLName tableName = null;
        if (lexer.token() == Token.INSERT) {
            lexer.nextToken();

            for (; ; ) {
                if (lexer.token() == Token.IDENTIFIER) {
                    long hash = lexer.hash_lower();

                    if (hash == FnvHash.Constants.LOW_PRIORITY) {
                        stmt.setLowPriority(true);
                        lexer.nextToken();
                        continue;
                    }

                    if (hash == FnvHash.Constants.DELAYED) {
                        stmt.setDelayed(true);
                        lexer.nextToken();
                        continue;
                    }

                    if (hash == FnvHash.Constants.HIGH_PRIORITY) {
                        stmt.setHighPriority(true);
                        lexer.nextToken();
                        continue;
                    }

                    if (hash == FnvHash.Constants.IGNORE) {
                        stmt.setIgnore(true);
                        lexer.nextToken();
                        continue;
                    }

                    if (hash == FnvHash.Constants.ROLLBACK_ON_FAIL) {
                        stmt.setRollbackOnFail(true);
                        lexer.nextToken();
                        continue;
                    }
                }


                break;
            }

            if (lexer.token() == Token.INTO) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.LINE_COMMENT) {
                lexer.nextToken();
            }

            tableName = this.exprParser.name();
            stmt.setTableName(tableName);

            if (lexer.token() == Token.HINT) {
                String comment = "/*" + lexer.stringVal() + "*/";
                lexer.nextToken();
                stmt.getTableSource().addAfterComment(comment);
            }

            if (lexer.token() == Token.IDENTIFIER
                    && !lexer.identifierEquals(FnvHash.Constants.VALUE)) {
                stmt.setAlias(lexer.stringVal());
                lexer.nextToken();
            }

        }

        int columnSize = 0;
        if (lexer.token() == Token.LPAREN) {
            boolean useInsertColumnsCache = lexer.isEnabled(SQLParserFeature.UseInsertColumnsCache);
            InsertColumnsCache insertColumnsCache = null;

            InsertColumnsCache.Entry cachedColumns = null;
            if (useInsertColumnsCache) {
                insertColumnsCache = this.insertColumnsCache;
                if (insertColumnsCache == null) {
                    insertColumnsCache = InsertColumnsCache.global;
                }

                if (tableName != null) {
                    cachedColumns = insertColumnsCache.get(tableName.hashCode64());
                }
            }

            int pos = lexer.pos();
            if (cachedColumns != null
                    && lexer.text.startsWith(cachedColumns.columnsString, pos)) {
                if (!lexer.isEnabled(SQLParserFeature.OptimizedForParameterized)) {
                    List<SQLExpr> columns = stmt.getColumns();
                    List<SQLExpr> cachedColumns2 = cachedColumns.columns;
                    for (int i = 0, size = cachedColumns2.size(); i < size; i++) {
                        columns.add(cachedColumns2.get(i).clone());
                    }
                }
                stmt.setColumnsString(cachedColumns.columnsFormattedString, cachedColumns.columnsFormattedStringHash);
                int p2 = pos + cachedColumns.columnsString.length();
                lexer.reset(p2);
                lexer.nextToken();
            } else {
                lexer.nextToken();
                if (lexer.token() == Token.SELECT) {
                    SQLSelect select = this.exprParser.createSelectParser().select();
                    select.setParent(stmt);
                    stmt.setQuery(select);
                } else {
                    List<SQLExpr> columns = stmt.getColumns();

                    if (lexer.token() != Token.RPAREN) {
                        for (; ; ) {
                            String identName;
                            long hash;

                            Token token = lexer.token();
                            if (token == Token.IDENTIFIER) {
                                identName = lexer.stringVal();
                                hash = lexer.hash_lower();
                            } else if (token == Token.LITERAL_CHARS) {
                                identName = '\'' + lexer.stringVal() + '\'';
                                hash = 0;
                            } else {
                                identName = lexer.stringVal();
                                hash = 0;
                            }
                            lexer.nextTokenComma();
                            SQLExpr expr = new SQLIdentifierExpr(identName, hash);
                            while (lexer.token() == Token.DOT) {
                                lexer.nextToken();
                                String propertyName = lexer.stringVal();
                                lexer.nextToken();
                                expr = new SQLPropertyExpr(expr, propertyName);
                            }

                            expr.setParent(stmt);
                            columns.add(expr);
                            columnSize++;

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextTokenIdent();
                                continue;
                            }

                            break;
                        }
                        columnSize = stmt.getColumns().size();

                        if (insertColumnsCache != null && tableName != null) {
                            String columnsString = lexer.subString(pos, lexer.pos() - pos);

                            List<SQLExpr> clonedColumns = new ArrayList<SQLExpr>(columnSize);
                            for (int i = 0; i < columns.size(); i++) {
                                clonedColumns.add(columns.get(i).clone());
                            }

                            StringBuilder buf = new StringBuilder();
                            SQLASTOutputVisitor outputVisitor = SQLUtils.createOutputVisitor(buf, dbType);
                            outputVisitor.printInsertColumns(columns);

                            String formattedColumnsString = buf.toString();
                            long columnsFormattedStringHash = FnvHash.fnv1a_64_lower(formattedColumnsString);

                            insertColumnsCache.put(tableName.hashCode64(), columnsString, formattedColumnsString, clonedColumns);
                            stmt.setColumnsString(formattedColumnsString, columnsFormattedStringHash);
                        }
                    }
                }
                accept(Token.RPAREN);
            }
        }

        if (lexer.token() == Token.LINE_COMMENT) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.VALUES || lexer.identifierEquals(FnvHash.Constants.VALUE)) {
            lexer.nextTokenLParen();
            parseValueClause(stmt.getValuesList(), columnSize, stmt);
        } else if (lexer.token() == Token.SET) {
            lexer.nextToken();

            SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
            stmt.addValueCause(values);

            for (; ; ) {
                SQLName name = this.exprParser.name();
                stmt.addColumn(name);
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                } else {
                    accept(Token.COLONEQ);
                }
                values.addValue(this.exprParser.expr());

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }

        } else if (lexer.token() == (Token.SELECT)) {
            SQLSelect select = this.exprParser.createSelectParser().select();
            select.setParent(stmt);
            stmt.setQuery(select);
        } else if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            SQLSelect select = this.exprParser.createSelectParser().select();
            select.setParent(stmt);
            stmt.setQuery(select);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            acceptIdentifier("DUPLICATE");
            accept(Token.KEY);
            accept(Token.UPDATE);

            List<SQLExpr> duplicateKeyUpdate = stmt.getDuplicateKeyUpdate();
            for (;;) {
                SQLName name = this.exprParser.name();
                accept(Token.EQ);
                SQLExpr value;
                try {
                    value = this.exprParser.expr();
                } catch (EOFParserException e) {
                    throw new ParserException("EOF, " + name + "=", e);
                }

                SQLBinaryOpExpr assignment = new SQLBinaryOpExpr(name, SQLBinaryOperator.Equality, value);
                assignment.setParent(stmt);
                duplicateKeyUpdate.add(assignment);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextTokenIdent();
                    continue;
                }
                break;
            }
        }

        return stmt;
    }

    public MySqlSelectParser createSQLSelectParser() {
        return new MySqlSelectParser(this.exprParser, selectListCache);
    }

    public SQLStatement parseSet() {
        accept(Token.SET);

        if (lexer.identifierEquals(FnvHash.Constants.PASSWORD)) {
            lexer.nextToken();
            SQLSetStatement stmt = new SQLSetStatement();
            stmt.setDbType(dbType);
            stmt.setOption(SQLSetStatement.Option.PASSWORD);

            SQLExpr user = null;
            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                user = this.exprParser.name();
            }

            accept(Token.EQ);

            SQLExpr password = this.exprParser.expr();

            stmt.set(user, password);

            return stmt;
        }

        Boolean global = null;
        Boolean session = null;
        if (lexer.identifierEquals(GLOBAL)) {
            global = Boolean.TRUE;
            lexer.nextToken();
        } else if (lexer.identifierEquals(SESSION)) {
            global = Boolean.FALSE;
            session = Boolean.TRUE;
            lexer.nextToken();
        }

        if (lexer.identifierEquals("TRANSACTION")) {
            MySqlSetTransactionStatement stmt = new MySqlSetTransactionStatement();
            stmt.setGlobal(global);
            stmt.setSession(session);

            lexer.nextToken();
            if (lexer.identifierEquals("ISOLATION")) {
                lexer.nextToken();
                acceptIdentifier("LEVEL");

                if (lexer.identifierEquals(READ)) {
                    lexer.nextToken();

                    if (lexer.identifierEquals("UNCOMMITTED")) {
                        stmt.setIsolationLevel("READ UNCOMMITTED");
                        lexer.nextToken();
                    } else if (lexer.identifierEquals(WRITE)) {
                        stmt.setIsolationLevel("READ WRITE");
                        lexer.nextToken();
                    } else if (lexer.identifierEquals("ONLY")) {
                        stmt.setIsolationLevel("READ ONLY");
                        lexer.nextToken();
                    } else if (lexer.identifierEquals("COMMITTED")) {
                        stmt.setIsolationLevel("READ COMMITTED");
                        lexer.nextToken();
                    } else {
                        throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal() + ", " + lexer.info());
                    }
                } else if (lexer.identifierEquals("SERIALIZABLE")) {
                    stmt.setIsolationLevel("SERIALIZABLE");
                    lexer.nextToken();
                } else if (lexer.identifierEquals("REPEATABLE")) {
                    lexer.nextToken();
                    if (lexer.identifierEquals(READ)) {
                        stmt.setIsolationLevel("REPEATABLE READ");
                        lexer.nextToken();
                    } else {
                        throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal() + ", " + lexer.info());
                    }
                } else {
                    throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal() + ", " + lexer.info());
                }
            } else if (lexer.identifierEquals(READ)) {
                lexer.nextToken();
                if (lexer.identifierEquals("ONLY")) {
                    stmt.setAccessModel("ONLY");
                    lexer.nextToken();
                } else if (lexer.identifierEquals("WRITE")) {
                    stmt.setAccessModel("WRITE");
                    lexer.nextToken();
                } else {
                    throw new ParserException("UNKOWN ACCESS MODEL : " + lexer.stringVal() + ", " + lexer.info());
                }
            }

            return stmt;
//        } else if (lexer.identifierEquals("NAMES")) {
//            lexer.nextToken();
//
//            MySqlSetNamesStatement stmt = new MySqlSetNamesStatement();
//            if (lexer.token() == Token.DEFAULT) {
//                lexer.nextToken();
//                stmt.setDefault(true);
//            } else {
//                String charSet = lexer.stringVal();
//                stmt.setCharSet(charSet);
//                lexer.nextToken();
//                if (lexer.identifierEquals(COLLATE2)) {
//                    lexer.nextToken();
//
//                    String collate = lexer.stringVal();
//                    stmt.setCollate(collate);
//                    lexer.nextToken();
//                }
//            }
//            return stmt;
//        } else if (lexer.identifierEquals(CHARACTER)) {
//            lexer.nextToken();
//
//            accept(Token.SET);
//
//            MySqlSetCharSetStatement stmt = new MySqlSetCharSetStatement();
//            if (lexer.token() == Token.DEFAULT) {
//                lexer.nextToken();
//                stmt.setDefault(true);
//            } else {
//                String charSet = lexer.stringVal();
//                stmt.setCharSet(charSet);
//                lexer.nextToken();
//                if (lexer.identifierEquals(COLLATE2)) {
//                    lexer.nextToken();
//
//                    String collate = lexer.stringVal();
//                    stmt.setCollate(collate);
//                    lexer.nextToken();
//                }
//            }
//            return stmt;
        } else {
            SQLSetStatement stmt = new SQLSetStatement(getDbType());

            parseAssignItems(stmt.getItems(), stmt);

            if (global != null && global) {
                SQLVariantRefExpr varRef = (SQLVariantRefExpr) stmt.getItems().get(0).getTarget();
                varRef.setGlobal(true);
            }

            if(session != null && session){
                SQLVariantRefExpr varRef = (SQLVariantRefExpr) stmt.getItems().get(0).getTarget();
                varRef.setSession(true);
            }

            if (lexer.token() == Token.HINT) {
                stmt.setHints(this.exprParser.parseHints());
            }

            return stmt;
        }
    }

    public SQLStatement parseAlter() {
        accept(Token.ALTER);

        if (lexer.token() == Token.USER) {
            return parseAlterUser();
        }

        boolean ignore = false;

        if (lexer.identifierEquals(FnvHash.Constants.IGNORE)) {
            ignore = true;
            lexer.nextToken();
        }

        if (lexer.token() == Token.TABLE) {
            return parseAlterTable(ignore);
        }

        if (lexer.token() == Token.DATABASE
                || lexer.token() == Token.SCHEMA) {
            return parseAlterDatabase();
        }

        if (lexer.identifierEquals(FnvHash.Constants.EVENT)) {
            return parseAlterEvent();
        }

        if (lexer.token() == Token.FUNCTION) {
            return parseAlterFunction();
        }

        if (lexer.token() == Token.PROCEDURE) {
            return parseAlterProcedure();
        }

        if (lexer.token() == Token.TABLESPACE) {
            return parseAlterTableSpace();
        }

        if (lexer.token() == Token.VIEW) {
            return parseAlterView();
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOGFILE)) {
            return parseAlterLogFileGroup();
        }

        if (lexer.identifierEquals(FnvHash.Constants.SERVER)) {
            return parseAlterServer();
        }

        if (lexer.identifierEquals(FnvHash.Constants.ALGORITHM)) {
            return parseAlterView();
        }

        if (lexer.identifierEquals(FnvHash.Constants.DEFINER)) {
            Lexer.SavePoint savePoint = lexer.mark();
            lexer.nextToken();
            accept(Token.EQ);
            this.getExprParser().userName();
            if (lexer.identifierEquals(FnvHash.Constants.EVENT)) {
                lexer.reset(savePoint);
                return parseAlterEvent();
            } else {
                lexer.reset(savePoint);
                return parseAlterView();
            }
        }

        throw new ParserException("TODO " + lexer.info());
    }

    protected SQLStatement parseAlterView() {
        if (lexer.token() == Token.ALTER) {
            lexer.nextToken();
        }

        SQLAlterViewStatement createView = new SQLAlterViewStatement(getDbType());

        if (lexer.identifierEquals("ALGORITHM")) {
            lexer.nextToken();
            accept(Token.EQ);
            String algorithm = lexer.stringVal();
            createView.setAlgorithm(algorithm);
            lexer.nextToken();
        }

        if (lexer.identifierEquals("DEFINER")) {
            lexer.nextToken();
            accept(Token.EQ);
            SQLName definer = (SQLName) this.exprParser.expr();
            createView.setDefiner(definer);
        }

        if (lexer.identifierEquals("SQL")) {
            lexer.nextToken();
            acceptIdentifier("SECURITY");
            String sqlSecurity = lexer.stringVal();
            createView.setSqlSecurity(sqlSecurity);
            lexer.nextToken();
        }

        if (lexer.identifierEquals("FORCE")) {
            lexer.nextToken();
            createView.setForce(true);
        }

        this.accept(Token.VIEW);

        if (lexer.token() == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            createView.setIfNotExists(true);
        }

        createView.setName(exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (;;) {

                if (lexer.token() == Token.CONSTRAINT) {
                    SQLTableConstraint constraint = (SQLTableConstraint) this.exprParser.parseConstaint();
                    createView.addColumn(constraint);
                } else {
                    SQLColumnDefinition column = new SQLColumnDefinition();
                    column.setDbType(dbType);
                    SQLName expr = this.exprParser.name();
                    column.setName(expr);

                    this.exprParser.parseColumnRest(column);

                    if (lexer.token() == Token.COMMENT) {
                        lexer.nextToken();

                        SQLExpr comment;
                        if (lexer.token() == Token.LITERAL_ALIAS) {
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

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }

            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLCharExpr comment = (SQLCharExpr) exprParser.primary();
            createView.setComment(comment);
        }

        this.accept(Token.AS);

        SQLSelectParser selectParser = this.createSQLSelectParser();
        createView.setSubQuery(selectParser.select());

        if (lexer.token() == Token.WITH) {
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

            if (lexer.token() == Token.CHECK) {
                lexer.nextToken();
                acceptIdentifier("OPTION");
                createView.setWithCheckOption(true);
            }
        }

        return createView;
    }

    protected SQLStatement parseAlterTableSpace() {
        if (lexer.token() == Token.ALTER) {
            lexer.nextToken();
        }

        accept(Token.TABLESPACE);

        SQLName name = this.exprParser.name();

        MySqlAlterTablespaceStatement stmt = new MySqlAlterTablespaceStatement();
        stmt.setName(name);

        if (lexer.identifierEquals(FnvHash.Constants.ADD)) {
            lexer.nextToken();
            acceptIdentifier("DATAFILE");
            SQLExpr file = this.exprParser.primary();
            stmt.setAddDataFile(file);
        } else if (lexer.token() == Token.DROP) {
            lexer.nextToken();
            acceptIdentifier("DATAFILE");
            SQLExpr file = this.exprParser.primary();
            stmt.setDropDataFile(file);
        }

        if (lexer.identifierEquals(FnvHash.Constants.INITIAL_SIZE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr initialSize = this.exprParser.expr();
            stmt.setInitialSize(initialSize);
        }

        if (lexer.identifierEquals(FnvHash.Constants.WAIT)) {
            lexer.nextToken();
            stmt.setWait(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr engine = this.exprParser.expr();
            stmt.setEngine(engine);
        }

        return stmt;
    }

    protected SQLStatement parseAlterServer() {
        if (lexer.token() == Token.ALTER) {
            lexer.nextToken();
        }
        acceptIdentifier("SERVER");

        SQLName name = this.exprParser.name();

        MySqlAlterServerStatement stmt = new MySqlAlterServerStatement();
        stmt.setName(name);

        acceptIdentifier("OPTIONS");
        accept(Token.LPAREN);
        if (lexer.token() == Token.USER) {
            lexer.nextToken();
            SQLExpr user = this.exprParser.name();
            stmt.setUser(user);
        }
        accept(Token.RPAREN);

        return stmt;
    }

    protected SQLStatement parseCreateLogFileGroup() {
        if (lexer.token() == Token.ALTER) {
            lexer.nextToken();
        }
        acceptIdentifier("LOGFILE");
        accept(Token.GROUP);

        SQLName name = this.exprParser.name();

        MySqlCreateAddLogFileGroupStatement stmt = new MySqlCreateAddLogFileGroupStatement();
        stmt.setName(name);

        acceptIdentifier("ADD");
        acceptIdentifier("UNDOFILE");

        SQLExpr fileName = this.exprParser.primary();
        stmt.setAddUndoFile(fileName);

        if (lexer.identifierEquals(FnvHash.Constants.INITIAL_SIZE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr initialSize = this.exprParser.expr();
            stmt.setInitialSize(initialSize);
        }

        if (lexer.identifierEquals(FnvHash.Constants.WAIT)) {
            lexer.nextToken();
            stmt.setWait(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr engine = this.exprParser.expr();
            stmt.setEngine(engine);
        }

        return stmt;
    }

    protected SQLStatement parseAlterLogFileGroup() {
        if (lexer.token() == Token.ALTER) {
            lexer.nextToken();
        }
        acceptIdentifier("LOGFILE");
        accept(Token.GROUP);

        SQLName name = this.exprParser.name();

        MySqlAlterLogFileGroupStatement stmt = new MySqlAlterLogFileGroupStatement();
        stmt.setName(name);

        acceptIdentifier("ADD");
        acceptIdentifier("UNDOFILE");

        SQLExpr fileName = this.exprParser.primary();
        stmt.setAddUndoFile(fileName);

        if (lexer.identifierEquals(FnvHash.Constants.INITIAL_SIZE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr initialSize = this.exprParser.expr();
            stmt.setInitialSize(initialSize);
        }

        if (lexer.identifierEquals(FnvHash.Constants.WAIT)) {
            lexer.nextToken();
            stmt.setWait(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr engine = this.exprParser.expr();
            stmt.setEngine(engine);
        }

        return stmt;
    }

    protected SQLStatement parseAlterProcedure() {
        if (lexer.token() == Token.ALTER) {
            lexer.nextToken();
        }
        accept(Token.PROCEDURE);

        SQLAlterProcedureStatement stmt = new SQLAlterProcedureStatement();
        stmt.setDbType(dbType);

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        // for mysql
        for (;;) {
            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                SQLExpr comment = this.exprParser.primary();
                stmt.setComment(comment);
            } else if (lexer.identifierEquals(FnvHash.Constants.LANGUAGE)) {
                lexer.nextToken();
                acceptIdentifier("SQL");
                stmt.setLanguageSql(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.SQL)) {
                lexer.nextToken();
                acceptIdentifier("SECURITY");

                SQLExpr sqlSecurity = this.exprParser.name();
                stmt.setSqlSecurity(sqlSecurity);
            } else if (lexer.identifierEquals(FnvHash.Constants.CONTAINS) || lexer.token() == Token.CONTAINS) {
                lexer.nextToken();
                acceptIdentifier("SQL");
                stmt.setContainsSql(true);
            } else {
                break;
            }
        }

        return stmt;
    }

    protected SQLStatement parseAlterFunction() {
        if (lexer.token() == Token.ALTER) {
            lexer.nextToken();
        }
        accept(Token.FUNCTION);

        SQLAlterFunctionStatement stmt = new SQLAlterFunctionStatement();
        stmt.setDbType(dbType);

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        // for mysql
        for (;;) {
            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                SQLExpr comment = this.exprParser.primary();
                stmt.setComment(comment);
            } else if (lexer.identifierEquals(FnvHash.Constants.LANGUAGE)) {
                lexer.nextToken();
                acceptIdentifier("SQL");
                stmt.setLanguageSql(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.SQL)) {
                lexer.nextToken();
                acceptIdentifier("SECURITY");

                SQLExpr sqlSecurity = this.exprParser.name();
                stmt.setSqlSecurity(sqlSecurity);
            } else if (lexer.identifierEquals(FnvHash.Constants.CONTAINS) || lexer.token() == Token.CONTAINS) {
                lexer.nextToken();
                acceptIdentifier("SQL");
                stmt.setContainsSql(true);
            } else {
                break;
            }
        }

        return stmt;
    }

    protected SQLStatement parseCreateEvent() {
        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
        }

        MySqlCreateEventStatement stmt = new MySqlCreateEventStatement();

        if (lexer.identifierEquals(FnvHash.Constants.DEFINER)) {
            lexer.nextToken();
            accept(Token.EQ);
            SQLName definer = this.getExprParser().userName();
            stmt.setDefiner(definer);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                accept(Token.RPAREN);
            }
        }

        acceptIdentifier("EVENT");

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        SQLName eventName = this.exprParser.name();
        stmt.setName(eventName);

        while (lexer.token() == Token.ON) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.SCHEDULE)) {
                lexer.nextToken();
                MySqlEventSchedule schedule = parseSchedule();
                stmt.setSchedule(schedule);
            } else if (lexer.identifierEquals(FnvHash.Constants.COMPLETION)) {
                lexer.nextToken();

                boolean value;
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    value = false;
                } else {
                    value = true;
                }
                acceptIdentifier("PRESERVE");
                stmt.setOnCompletionPreserve(value);
            } else {
                throw new ParserException("TODO " + lexer.info());
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.RENAME)) {
            lexer.nextToken();
            accept(Token.TO);
            SQLName renameTo = this.exprParser.name();
            stmt.setRenameTo(renameTo);
        }

        if (lexer.token() == Token.ENABLE) {
            stmt.setEnable(true);
            lexer.nextToken();
        } else if (lexer.token() == Token.DISABLE) {
            lexer.nextToken();
            stmt.setEnable(false);

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                acceptIdentifier("SLAVE");
                stmt.setDisableOnSlave(true);
            }
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.primary();
            stmt.setComment(comment);
        }

        if (lexer.token() == Token.DO) {
            lexer.nextToken();
            SQLStatement eventBody = this.parseStatement();
            stmt.setEventBody(eventBody);
        } else if (lexer.token() == Token.IDENTIFIER) {
            SQLExpr expr = this.exprParser.expr();
            SQLExprStatement eventBody = new SQLExprStatement(expr);
            eventBody.setDbType(dbType);
            stmt.setEventBody(eventBody);
        }

        return stmt;
    }

    protected SQLStatement parseAlterEvent() {
        if (lexer.token() == Token.ALTER) {
            lexer.nextToken();
        }

        MySqlAlterEventStatement stmt = new MySqlAlterEventStatement();

        if (lexer.identifierEquals(FnvHash.Constants.DEFINER)) {
            lexer.nextToken();
            accept(Token.EQ);
            SQLName definer = this.getExprParser().userName();
            stmt.setDefiner(definer);
        }

        acceptIdentifier("EVENT");

        SQLName eventName = this.exprParser.name();
        stmt.setName(eventName);

        while (lexer.token() == Token.ON) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.SCHEDULE)) {
                lexer.nextToken();
                MySqlEventSchedule schedule = parseSchedule();
                stmt.setSchedule(schedule);
            } else if (lexer.identifierEquals(FnvHash.Constants.COMPLETION)) {
                lexer.nextToken();

                boolean value;
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    value = false;
                } else {
                    value = true;
                }
                acceptIdentifier("PRESERVE");
                stmt.setOnCompletionPreserve(value);
            } else {
                throw new ParserException("TODO " + lexer.info());
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.RENAME)) {
            lexer.nextToken();
            accept(Token.TO);
            SQLName renameTo = this.exprParser.name();
            stmt.setRenameTo(renameTo);
        }

        if (lexer.token() == Token.ENABLE) {
            stmt.setEnable(true);
            lexer.nextToken();
        } else if (lexer.token() == Token.DISABLE) {
            lexer.nextToken();
            stmt.setEnable(false);

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                acceptIdentifier("SLAVE");
                stmt.setDisableOnSlave(true);
            }
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.primary();
            stmt.setComment(comment);
        }

        if (lexer.token() == Token.DO) {
            lexer.nextToken();
            SQLStatement eventBody = this.parseStatement();
            stmt.setEventBody(eventBody);
        } else if (lexer.token() == Token.IDENTIFIER) {
            SQLExpr expr = this.exprParser.expr();
            SQLExprStatement eventBody = new SQLExprStatement(expr);
            eventBody.setDbType(dbType);
            stmt.setEventBody(eventBody);
        }

        return stmt;
    }

    private MySqlEventSchedule parseSchedule() {
        MySqlEventSchedule schedule = new MySqlEventSchedule();

        if (lexer.identifierEquals(FnvHash.Constants.AT)) {
            lexer.nextToken();
            schedule.setAt(this.exprParser.expr());
        } else if (lexer.identifierEquals(FnvHash.Constants.EVERY)) {
            lexer.nextToken();
            SQLExpr value = this.exprParser.expr();
            String unit = lexer.stringVal();
            lexer.nextToken();

            SQLIntervalExpr intervalExpr = new SQLIntervalExpr();
            intervalExpr.setValue(value);
            intervalExpr.setUnit(SQLIntervalUnit.valueOf(unit.toUpperCase()));

            schedule.setEvery(intervalExpr);
        }

        if (lexer.identifierEquals(FnvHash.Constants.STARTS)) {
            lexer.nextToken();
            schedule.setStarts(this.exprParser.expr());

            if (lexer.identifierEquals(FnvHash.Constants.ENDS)) {
                lexer.nextToken();
                schedule.setEnds(this.exprParser.expr());
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.ENDS)) {
            lexer.nextToken();
            schedule.setEnds(this.exprParser.expr());
        }

        return schedule;
    }

    protected SQLStatement parseAlterTable(boolean ignore) {
        lexer.nextToken();

        SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());
        stmt.setIgnore(ignore);
        stmt.setName(this.exprParser.name());

        for (; ; ) {
            if (lexer.token() == Token.DROP) {
                parseAlterDrop(stmt);
            } else if (lexer.token() == Token.TRUNCATE) {
                lexer.nextToken();
                accept(Token.PARTITION);

                SQLAlterTableTruncatePartition item = new SQLAlterTableTruncatePartition();
                if (lexer.token() == Token.ALL) {
                    item.getPartitions().add(new SQLIdentifierExpr("ALL"));
                    lexer.nextToken();
                } else {
                    this.exprParser.names(item.getPartitions(), item);
                }
                stmt.addItem(item);
            } else if (lexer.identifierEquals("ADD")) {
                lexer.nextToken();

                if (lexer.token() == Token.COLUMN) {
                    lexer.nextToken();
                    parseAlterTableAddColumn(stmt);
                } else if (lexer.token() == Token.INDEX
                        || lexer.token() == Token.FULLTEXT
                        || lexer.identifierEquals(FnvHash.Constants.SPATIAL)) {
                    SQLAlterTableAddIndex item = parseAlterTableAddIndex();
                    item.setParent(stmt);
                    stmt.addItem(item);
                } else if (lexer.token() == Token.UNIQUE) {
                    SQLAlterTableAddIndex item = parseAlterTableAddIndex();
                    item.setParent(stmt);
                    stmt.addItem(item);
                } else if (lexer.token() == Token.PRIMARY) {
                    SQLPrimaryKey primaryKey = this.exprParser.parsePrimaryKey();
                    SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(primaryKey);
                    stmt.addItem(item);
                } else if (lexer.token() == Token.KEY) {
                    // throw new ParserException("TODO " + lexer.token() +
                    // " " + lexer.stringVal());
                    SQLAlterTableAddIndex item = parseAlterTableAddIndex();
                    item.setParent(stmt);
                    stmt.addItem(item);
                } else if (lexer.token() == Token.FOREIGN) {
                    MysqlForeignKey fk = this.getExprParser().parseForeignKey();
                    SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(fk);

                    stmt.addItem(item);
                } else if (lexer.token() == Token.CONSTRAINT) {
                    lexer.nextToken();

                    if (lexer.token() == Token.PRIMARY) {
                        SQLPrimaryKey primaryKey = ((MySqlExprParser) this.exprParser).parsePrimaryKey();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(primaryKey);
                        item.setParent(stmt);

                        stmt.addItem(item);
                    } else if (lexer.token() == Token.FOREIGN) {
                        MysqlForeignKey fk = this.getExprParser().parseForeignKey();
                        fk.setHasConstraint(true);

                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(fk);

                        stmt.addItem(item);
                    } else if (lexer.token() == Token.UNIQUE) {

                        SQLUnique unique = this.exprParser.parseUnique();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(unique);
                        stmt.addItem(item);
                    } else {
                        SQLName constraintName = this.exprParser.name();

                        if (lexer.token() == Token.PRIMARY) {
                            SQLPrimaryKey primaryKey = ((MySqlExprParser) this.exprParser).parsePrimaryKey();

                            primaryKey.setName(constraintName);

                            SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(primaryKey);
                            item.setParent(stmt);

                            stmt.addItem(item);
                        } else if (lexer.token() == Token.FOREIGN) {
                            MysqlForeignKey fk = this.getExprParser().parseForeignKey();
                            fk.setName(constraintName);
                            fk.setHasConstraint(true);

                            SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(fk);

                            stmt.addItem(item);
                        } else if (lexer.token() == Token.UNIQUE) {
                            SQLUnique unique = this.exprParser.parseUnique();
                            SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(unique);
                            stmt.addItem(item);
                        } else {
                            throw new ParserException("TODO " + lexer.info());
                        }
                    }
                } else if (lexer.token() == Token.PARTITION) {
                    lexer.nextToken();

                    SQLAlterTableAddPartition item = new SQLAlterTableAddPartition();

                    if (lexer.identifierEquals("PARTITIONS")) {
                        lexer.nextToken();
                        item.setPartitionCount(this.exprParser.integerExpr());
                    }

                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        SQLPartition partition = this.getExprParser().parsePartition();
                        accept(Token.RPAREN);
                        item.addPartition(partition);
                    }

                    stmt.addItem(item);
                } else {
                    parseAlterTableAddColumn(stmt);
                }
            } else if (lexer.token() == Token.ALTER) {
                lexer.nextToken();
                if (lexer.token() == Token.COLUMN) {
                    lexer.nextToken();
                }

                MySqlAlterTableAlterColumn alterColumn = new MySqlAlterTableAlterColumn();
                alterColumn.setColumn(this.exprParser.name());

                if (lexer.token() == Token.SET) {
                    lexer.nextToken();
                    accept(Token.DEFAULT);

                    alterColumn.setDefaultExpr(this.exprParser.expr());
                } else {
                    accept(Token.DROP);
                    accept(Token.DEFAULT);
                    alterColumn.setDropDefault(true);
                }

                stmt.addItem(alterColumn);
            } else if (lexer.identifierEquals("CHANGE")) {
                lexer.nextToken();
                if (lexer.token() == Token.COLUMN) {
                    lexer.nextToken();
                }
                MySqlAlterTableChangeColumn item = new MySqlAlterTableChangeColumn();
                item.setColumnName(this.exprParser.name());
                item.setNewColumnDefinition(this.exprParser.parseColumn());
                if (lexer.identifierEquals("AFTER")) {
                    lexer.nextToken();
                    item.setAfterColumn(this.exprParser.name());
                } else if (lexer.identifierEquals("FIRST")) {
                    lexer.nextToken();
                    if (lexer.token() == Token.IDENTIFIER) {
                        item.setFirstColumn(this.exprParser.name());
                    } else {
                        item.setFirst(true);
                    }
                }
                stmt.addItem(item);
            } else if (lexer.identifierEquals("MODIFY")) {
                lexer.nextToken();

                if (lexer.token() == Token.COLUMN) {
                    lexer.nextToken();
                }

                boolean paren = false;
                if (lexer.token() == Token.LPAREN) {
                    paren = true;
                    lexer.nextToken();
                }

                for (; ; ) {
                    MySqlAlterTableModifyColumn item = new MySqlAlterTableModifyColumn();
                    item.setNewColumnDefinition(this.exprParser.parseColumn());
                    if (lexer.identifierEquals("AFTER")) {
                        lexer.nextToken();
                        item.setAfterColumn(this.exprParser.name());
                    } else if (lexer.identifierEquals("FIRST")) {
                        lexer.nextToken();
                        if (lexer.token() == Token.IDENTIFIER) {
                            item.setFirstColumn(this.exprParser.name());
                        } else {
                            item.setFirst(true);
                        }
                    }
                    stmt.addItem(item);

                    if (paren && lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }

                if (paren) {
                    accept(Token.RPAREN);
                }
            } else if (lexer.token() == Token.DISABLE) {
                lexer.nextToken();

                if (lexer.token() == Token.CONSTRAINT) {
                    lexer.nextToken();
                    SQLAlterTableDisableConstraint item = new SQLAlterTableDisableConstraint();
                    item.setConstraintName(this.exprParser.name());
                    stmt.addItem(item);
                } else {
                    acceptIdentifier("KEYS");
                    SQLAlterTableDisableKeys item = new SQLAlterTableDisableKeys();
                    stmt.addItem(item);
                }
            } else if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                if (lexer.token() == Token.CONSTRAINT) {
                    lexer.nextToken();
                    SQLAlterTableEnableConstraint item = new SQLAlterTableEnableConstraint();
                    item.setConstraintName(this.exprParser.name());
                    stmt.addItem(item);
                } else {
                    acceptIdentifier("KEYS");
                    SQLAlterTableEnableKeys item = new SQLAlterTableEnableKeys();
                    stmt.addItem(item);
                }
            } else if (lexer.identifierEquals("RENAME")) {
                lexer.nextToken();

                if (lexer.token() == Token.INDEX) {
                    lexer.nextToken();
                    SQLName name = this.exprParser.name();
                    accept(Token.TO);
                    SQLName to = this.exprParser.name();
                    SQLAlterTableRenameIndex item = new SQLAlterTableRenameIndex(name, to);
                    stmt.addItem(item);
                    continue;
                }

                if (lexer.token() == Token.TO || lexer.token() == Token.AS) {
                    lexer.nextToken();
                }

                if (stmt.getItems().size() > 0) {
                    SQLAlterTableRename item = new SQLAlterTableRename();
                    SQLName to = this.exprParser.name();
                    item.setTo(to);
                    stmt.addItem(item);
                } else {
                    MySqlRenameTableStatement renameStmt = new MySqlRenameTableStatement();
                    MySqlRenameTableStatement.Item item = new MySqlRenameTableStatement.Item();
                    item.setName((SQLName) stmt.getTableSource().getExpr());
                    item.setTo(this.exprParser.name());
                    // SQLAlterTableRename
                    renameStmt.addItem(item);

                    return renameStmt;
                }   
            } else if (lexer.token() == Token.ORDER) {
                throw new ParserException("TODO " + lexer.info());
            } else if (lexer.identifierEquals("CONVERT")) {
                lexer.nextToken();
                accept(Token.TO);
                acceptIdentifier("CHARACTER");
                accept(Token.SET);

                SQLAlterTableConvertCharSet item = new SQLAlterTableConvertCharSet();
                SQLExpr charset = this.exprParser.primary();
                item.setCharset(charset);

                if (lexer.identifierEquals("COLLATE")) {
                    lexer.nextToken();
                    SQLExpr collate = this.exprParser.primary();
                    item.setCollate(collate);
                }

                stmt.addItem(item);
            } else if (lexer.token() == Token.DEFAULT) {
                lexer.nextToken();
                if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
                    SQLAlterCharacter item = alterTableCharacter();
                    stmt.addItem(item);
                } else {
                    throw new ParserException("TODO " + lexer.info());
                }
            } else if (lexer.identifierEquals("DISCARD")) {
                lexer.nextToken();

                if (lexer.token() == Token.PARTITION) {
                    lexer.nextToken();
                    SQLAlterTableDiscardPartition item = new SQLAlterTableDiscardPartition();

                    if (lexer.token() == Token.ALL) {
                        lexer.nextToken();
                        item.getPartitions().add(new SQLIdentifierExpr("ALL"));
                    } else {
                        this.exprParser.names(item.getPartitions(), item);
                    }

                    if (lexer.token() == Token.TABLESPACE) {
                        lexer.nextToken();
                        item.setTablespace(true);
                    }

                    stmt.addItem(item);
                } else {
                    accept(Token.TABLESPACE);
                    MySqlAlterTableDiscardTablespace item = new MySqlAlterTableDiscardTablespace();
                    stmt.addItem(item);
                }
            } else if (lexer.token() == Token.CHECK) {
                lexer.nextToken();
                accept(Token.PARTITION);

                SQLAlterTableCheckPartition item = new SQLAlterTableCheckPartition();

                if (lexer.token() == Token.ALL) {
                    lexer.nextToken();
                    item.getPartitions().add(new SQLIdentifierExpr("ALL"));
                } else {
                    this.exprParser.names(item.getPartitions(), item);
                }

                stmt.addItem(item);

            } else if (lexer.identifierEquals("IMPORT")) {
                lexer.nextToken();

                if (lexer.token() == Token.PARTITION) {
                    lexer.nextToken();
                    SQLAlterTableImportPartition item = new SQLAlterTableImportPartition();

                    if (lexer.token() == Token.ALL) {
                        lexer.nextToken();
                        item.getPartitions().add(new SQLIdentifierExpr("ALL"));
                    } else {
                        this.exprParser.names(item.getPartitions(), item);
                    }

                    stmt.addItem(item);
                } else {
                    accept(Token.TABLESPACE);
                    MySqlAlterTableImportTablespace item = new MySqlAlterTableImportTablespace();
                    stmt.addItem(item);
                }
            } else if (lexer.token() == Token.ANALYZE) {
                lexer.nextToken();
                accept(Token.PARTITION);

                SQLAlterTableAnalyzePartition item = new SQLAlterTableAnalyzePartition();

                if (lexer.token() == Token.ALL) {
                    lexer.nextToken();
                    item.getPartitions().add(new SQLIdentifierExpr("ALL"));
                } else {
                    this.exprParser.names(item.getPartitions(), item);
                }
                stmt.addItem(item);
            } else if (lexer.identifierEquals("FORCE")) {
                throw new ParserException("TODO " + lexer.info());
            } else if (lexer.identifierEquals("COALESCE")) {
                lexer.nextToken();
                accept(Token.PARTITION);

                SQLAlterTableCoalescePartition item = new SQLAlterTableCoalescePartition();
                SQLIntegerExpr countExpr = this.exprParser.integerExpr();
                item.setCount(countExpr);
                stmt.addItem(item);
            } else if (lexer.identifierEquals("REORGANIZE")) {
                lexer.nextToken();
                accept(Token.PARTITION);

                SQLAlterTableReOrganizePartition item = new SQLAlterTableReOrganizePartition();

                this.exprParser.names(item.getNames(), item);

                accept(Token.INTO);
                accept(Token.LPAREN);
                for (; ; ) {
                    SQLPartition partition = this.getExprParser().parsePartition();

                    item.addPartition(partition);

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    } else {
                        break;
                    }
                }
                accept(Token.RPAREN);
                stmt.addItem(item);
            } else if (lexer.identifierEquals("EXCHANGE")) {
                lexer.nextToken();
                accept(Token.PARTITION);

                SQLAlterTableExchangePartition item = new SQLAlterTableExchangePartition();

                SQLName partition = this.exprParser.name();
                item.setPartition(partition);

                accept(Token.WITH);
                accept(Token.TABLE);
                SQLName table = this.exprParser.name();
                item.setTable(table);

                if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    acceptIdentifier("VALIDATION");
                    item.setValidation(true);
                } else if (lexer.identifierEquals(FnvHash.Constants.WITHOUT)) {
                    lexer.nextToken();
                    acceptIdentifier("VALIDATION");
                    item.setValidation(false);
                }


                stmt.addItem(item);
            } else if (lexer.token() == Token.OPTIMIZE) {
                lexer.nextToken();

                accept(Token.PARTITION);

                SQLAlterTableOptimizePartition item = new SQLAlterTableOptimizePartition();

                if (lexer.token() == Token.ALL) {
                    lexer.nextToken();
                    item.getPartitions().add(new SQLIdentifierExpr("ALL"));
                } else {
                    this.exprParser.names(item.getPartitions(), item);
                }

                stmt.addItem(item);
            } else if (lexer.identifierEquals("REBUILD")) {
                lexer.nextToken();

                accept(Token.PARTITION);

                SQLAlterTableRebuildPartition item = new SQLAlterTableRebuildPartition();

                if (lexer.token() == Token.ALL) {
                    lexer.nextToken();
                    item.getPartitions().add(new SQLIdentifierExpr("ALL"));
                } else {
                    this.exprParser.names(item.getPartitions(), item);
                }

                stmt.addItem(item);
            } else if (lexer.identifierEquals("REPAIR")) {
                lexer.nextToken();

                accept(Token.PARTITION);

                SQLAlterTableRepairPartition item = new SQLAlterTableRepairPartition();

                if (lexer.token() == Token.ALL) {
                    lexer.nextToken();
                    item.getPartitions().add(new SQLIdentifierExpr("ALL"));
                } else {
                    this.exprParser.names(item.getPartitions(), item);
                }

                stmt.addItem(item);
            } else if (lexer.identifierEquals("REMOVE")) {
                lexer.nextToken();
                acceptIdentifier("PARTITIONING");
                stmt.setRemovePatiting(true);
            } else if (lexer.identifierEquals("UPGRADE")) {
                lexer.nextToken();
                acceptIdentifier("PARTITIONING");
                stmt.setUpgradePatiting(true);
            } else if (lexer.identifierEquals("ALGORITHM")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.addItem(new MySqlAlterTableOption("ALGORITHM", lexer.stringVal()));
                lexer.nextToken();
            } else if (lexer.identifierEquals(ENGINE)) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.addItem(new MySqlAlterTableOption(ENGINE, lexer.stringVal()));
                lexer.nextToken();
            } else if (lexer.identifierEquals(AUTO_INCREMENT)) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.addItem(new MySqlAlterTableOption(AUTO_INCREMENT, new SQLIntegerExpr(lexer.integerValue())));
                lexer.nextToken();
            } else if (lexer.identifierEquals(COLLATE2)) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.addItem(new MySqlAlterTableOption(COLLATE2, lexer.stringVal()));
                lexer.nextToken();
            } else if (lexer.identifierEquals("PACK_KEYS")) {
                lexer.nextToken();
                accept(Token.EQ);
                if (lexer.identifierEquals("PACK")) {
                    lexer.nextToken();
                    accept(Token.ALL);
                    stmt.addItem(new MySqlAlterTableOption("PACK_KEYS", "PACK ALL"));
                } else {
                    stmt.addItem(new MySqlAlterTableOption("PACK_KEYS", lexer.stringVal()));
                    lexer.nextToken();
                }
            } else if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
                SQLAlterCharacter item = alterTableCharacter();
                stmt.addItem(item);
            } else if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    accept(Token.EQ);
                }
                stmt.addItem(new MySqlAlterTableOption("COMMENT", '\'' + lexer.stringVal() + '\''));
                lexer.nextToken();
            } else if (lexer.token() == Token.UNION) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                accept(Token.LPAREN);
                SQLTableSource tableSrc = this.createSQLSelectParser().parseTableSource();
                stmt.getTableOptions().put("UNION", tableSrc);
                accept(Token.RPAREN);
            } else if (lexer.identifierEquals("ROW_FORMAT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                if (lexer.token() == Token.DEFAULT || lexer.token() == Token.IDENTIFIER) {
                    SQLIdentifierExpr rowFormat = new SQLIdentifierExpr(lexer.stringVal());
                    lexer.nextToken();
                    stmt.getTableOptions().put("ROW_FORMAT", rowFormat);
                } else {
                    throw new ParserException("illegal syntax. " + lexer.info());
                }

            } else {
                break;
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            } else {
                break;
            }
        }

        return stmt;
    }

    private SQLAlterCharacter alterTableCharacter() {
        lexer.nextToken();
        accept(Token.SET);
        accept(Token.EQ);
        SQLAlterCharacter item = new SQLAlterCharacter();
        item.setCharacterSet(this.exprParser.primary());
        if (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            acceptIdentifier(COLLATE2);
            accept(Token.EQ);
            item.setCollate(this.exprParser.primary());
        }
        return item;
    }

    protected void parseAlterTableAddColumn(SQLAlterTableStatement stmt) {
        boolean parenFlag = false;
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            parenFlag = true;
        }

        SQLAlterTableAddColumn item = new SQLAlterTableAddColumn();
        for (; ; ) {

            SQLColumnDefinition columnDef = this.exprParser.parseColumn();
            item.addColumn(columnDef);
            if (lexer.identifierEquals("AFTER")) {
                lexer.nextToken();
                item.setAfterColumn(this.exprParser.name());
            } else if (lexer.identifierEquals("FIRST")) {
                lexer.nextToken();
                if (lexer.token() == Token.IDENTIFIER) {
                    item.setFirstColumn(this.exprParser.name());
                } else {
                    item.setFirst(true);
                }
            }

            if (parenFlag && lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        stmt.addItem(item);

        if (parenFlag) {
            accept(Token.RPAREN);
        }
    }

    public void parseAlterDrop(SQLAlterTableStatement stmt) {
        lexer.nextToken();
        if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
            SQLName indexName = this.exprParser.name();
            SQLAlterTableDropIndex item = new SQLAlterTableDropIndex();
            item.setIndexName(indexName);
            stmt.addItem(item);
        } else if (lexer.token() == Token.FOREIGN) {
            lexer.nextToken();
            accept(Token.KEY);
            SQLName indexName = this.exprParser.name();
            SQLAlterTableDropForeignKey item = new SQLAlterTableDropForeignKey();
            item.setIndexName(indexName);
            stmt.addItem(item);
        } else if (lexer.token() == Token.KEY) {
            lexer.nextToken();
            SQLName keyName = this.exprParser.name();
            SQLAlterTableDropKey item = new SQLAlterTableDropKey();
            item.setKeyName(keyName);
            stmt.addItem(item);
        } else if (lexer.token() == Token.PRIMARY) {
            lexer.nextToken();
            accept(Token.KEY);
            SQLAlterTableDropPrimaryKey item = new SQLAlterTableDropPrimaryKey();
            stmt.addItem(item);
        } else if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLAlterTableDropConstraint item = new SQLAlterTableDropConstraint();
            item.setConstraintName(this.exprParser.name());
            stmt.addItem(item);
        } else if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();

            SQLName name = exprParser.name();
            name.setParent(item);
            item.addColumn(name);

            while (lexer.token() == Token.COMMA) {
                char markChar = lexer.current();
                int markBp = lexer.bp();

                lexer.nextToken();
                if (lexer.identifierEquals("CHANGE")) {
                    lexer.reset(markBp, markChar, Token.COMMA);
                    break;
                }

                if (lexer.token() == Token.IDENTIFIER) {
                    if ("ADD".equalsIgnoreCase(lexer.stringVal())) {
                        lexer.reset(markBp, markChar, Token.COMMA);
                        break;
                    }
                    name = exprParser.name();
                    name.setParent(item);
                } else {
                    lexer.reset(markBp, markChar, Token.COMMA);
                    break;
                }
            }

            stmt.addItem(item);
        } else if (lexer.token() == Token.PARTITION) {
            SQLAlterTableDropPartition dropPartition = parseAlterTableDropPartition(false);
            stmt.addItem(dropPartition);
        } else if (lexer.token() == Token.IDENTIFIER) {
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();

            SQLName name = this.exprParser.name();
            item.addColumn(name);
            stmt.addItem(item);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.DROP) {
                parseAlterDrop(stmt);
            }
        } else {
            super.parseAlterDrop(stmt);
        }
    }

    public SQLStatement parseRename() {
        MySqlRenameTableStatement stmt = new MySqlRenameTableStatement();

        acceptIdentifier("RENAME");

        accept(Token.TABLE);

        for (; ; ) {
            MySqlRenameTableStatement.Item item = new MySqlRenameTableStatement.Item();
            item.setName(this.exprParser.name());
            accept(Token.TO);
            item.setTo(this.exprParser.name());

            stmt.addItem(item);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return stmt;
    }

    public SQLStatement parseCreateDatabase() {
        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.SCHEMA) {
            lexer.nextToken();
        } else {
            accept(Token.DATABASE);
        }

        SQLCreateDatabaseStatement stmt = new SQLCreateDatabaseStatement(JdbcConstants.MYSQL);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.HINT) {
            stmt.setHints(this.exprParser.parseHints());
        }

        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
        }

        for (;;) {
            if (lexer.identifierEquals("CHARACTER")) {
                lexer.nextToken();
                accept(Token.SET);
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                String charset = lexer.stringVal();
                accept(Token.IDENTIFIER);
                stmt.setCharacterSet(charset);
            } else if (lexer.identifierEquals("CHARSET")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                String charset = lexer.stringVal();
                accept(Token.IDENTIFIER);
                stmt.setCharacterSet(charset);
            } else if (lexer.token() == Token.DEFAULT) {
                lexer.nextToken();
            } else if (lexer.identifierEquals("COLLATE")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                String collate = lexer.stringVal();
                accept(Token.IDENTIFIER);
                stmt.setCollate(collate);
            } else {
                break;
            }




        }

        return stmt;
    }

    protected void parseUpdateSet(SQLUpdateStatement update) {
        accept(Token.SET);

        for (; ; ) {
            SQLUpdateSetItem item = this.exprParser.parseUpdateSetItem();
            update.addItem(item);

            if (lexer.token() != Token.COMMA) {
                break;
            }

            lexer.nextToken();
        }
    }

    public SQLStatement parseAlterDatabase() {
        if (lexer.token() == Token.SCHEMA) {
            lexer.nextToken();
        } else {
            accept(Token.DATABASE);
        }

        SQLAlterDatabaseStatement stmt = new SQLAlterDatabaseStatement(dbType);

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        if (lexer.identifierEquals("UPGRADE")) {
            lexer.nextToken();
            acceptIdentifier("DATA");
            acceptIdentifier("DIRECTORY");
            acceptIdentifier("NAME");
            stmt.setUpgradeDataDirectoryName(true);
        }

        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
                SQLAlterCharacter item = alterTableCharacter();
                stmt.setCharacter(item);
            } else {
                throw new ParserException("TODO " + lexer.info());
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
            SQLAlterCharacter item = alterTableCharacter();
            stmt.setCharacter(item);
        }

        return stmt;
    }

    public MySqlAlterUserStatement parseAlterUser() {
        accept(Token.USER);

        MySqlAlterUserStatement stmt = new MySqlAlterUserStatement();
        for (; ; ) {
            SQLExpr user = this.exprParser.expr();
            acceptIdentifier("PASSWORD");
            acceptIdentifier("EXPIRE");
            stmt.addUser(user);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }
        return stmt;
    }

    public MySqlExprParser getExprParser() {
        return (MySqlExprParser) exprParser;
    }


    public SQLCreateFunctionStatement parseCreateFunction() {
        SQLCreateFunctionStatement stmt = new SQLCreateFunctionStatement();
        stmt.setDbType(dbType);

        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();

            if (lexer.token() == Token.OR) {
                lexer.nextToken();
                accept(Token.REPLACE);
                stmt.setOrReplace(true);
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.DEFINER)) {
            lexer.nextToken();
            accept(Token.EQ);
            SQLName definer = this.getExprParser().userName();
            stmt.setDefiner(definer);
        }

        accept(Token.FUNCTION);

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {// match "("
            lexer.nextToken();
            parserParameters(stmt.getParameters(), stmt);
            accept(Token.RPAREN);// match ")"
        }

        acceptIdentifier("RETURNS");
        SQLDataType dataType = this.exprParser.parseDataType();
        stmt.setReturnDataType(dataType);

        for (;;) {
            if (lexer.identifierEquals("DETERMINISTIC")) {
                lexer.nextToken();
                stmt.setDeterministic(true);
                continue;
            }

            break;
        }

        SQLStatement block;
        if (lexer.token() == Token.BEGIN) {
            block = this.parseBlock();
        } else {
            block = this.parseStatement();
        }

        stmt.setBlock(block);

        return stmt;
    }
    /**
     * parse create procedure statement
     */
    public SQLCreateProcedureStatement parseCreateProcedure() {
        /**
         * CREATE OR REPALCE PROCEDURE SP_NAME(parameter_list) BEGIN block_statement END
         */
        SQLCreateProcedureStatement stmt = new SQLCreateProcedureStatement();
        stmt.setDbType(dbType);

        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();

            if (lexer.token() == Token.OR) {
                lexer.nextToken();
                accept(Token.REPLACE);
                stmt.setOrReplace(true);
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.DEFINER)) {
            lexer.nextToken();
            accept(Token.EQ);
            SQLName definer = this.getExprParser().userName();
            stmt.setDefiner(definer);
        }

        accept(Token.PROCEDURE);

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {// match "("
            lexer.nextToken();
            parserParameters(stmt.getParameters(), stmt);
            accept(Token.RPAREN);// match ")"
        }

        for (;;) {
            if (lexer.identifierEquals(FnvHash.Constants.DETERMINISTIC)) {
                lexer.nextToken();
                stmt.setDeterministic(true);
                continue;
            }
            if (lexer.identifierEquals(FnvHash.Constants.CONTAINS) || lexer.token() == Token.CONTAINS) {
                lexer.nextToken();
                acceptIdentifier("SQL");
                stmt.setContainsSql(true);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.SQL)) {
                lexer.nextToken();
                acceptIdentifier("SECURITY");
                SQLName authid = this.exprParser.name();
                stmt.setAuthid(authid);
            }

            break;
        }

        SQLStatement block;
        if (lexer.token() == Token.BEGIN) {
            block = this.parseBlock();
        } else {
            block = this.parseStatement();
        }

        stmt.setBlock(block);

        return stmt;
    }

    /**
     * parse create procedure parameters
     *
     * @param parameters
     */
    private void parserParameters(List<SQLParameter> parameters, SQLObject parent) {
        if (lexer.token() == Token.RPAREN) {
            return;
        }

        for (; ; ) {
            SQLParameter parameter = new SQLParameter();

            if (lexer.token() == Token.CURSOR) {
                lexer.nextToken();

                parameter.setName(this.exprParser.name());

                accept(Token.IS);
                SQLSelect select = this.createSQLSelectParser().select();

                SQLDataTypeImpl dataType = new SQLDataTypeImpl();
                dataType.setName("CURSOR");
                parameter.setDataType(dataType);

                parameter.setDefaultValue(new SQLQueryExpr(select));

            } else if (lexer.token() == Token.IN || lexer.token() == Token.OUT || lexer.token() == Token.INOUT) {

                if (lexer.token() == Token.IN) {
                    parameter.setParamType(ParameterType.IN);
                } else if (lexer.token() == Token.OUT) {
                    parameter.setParamType(ParameterType.OUT);
                } else if (lexer.token() == Token.INOUT) {
                    parameter.setParamType(ParameterType.INOUT);
                }
                lexer.nextToken();

                parameter.setName(this.exprParser.name());

                parameter.setDataType(this.exprParser.parseDataType());
            } else {
                parameter.setParamType(ParameterType.DEFAULT);// default parameter type is in
                parameter.setName(this.exprParser.name());
                parameter.setDataType(this.exprParser.parseDataType());

                if (lexer.token() == Token.COLONEQ) {
                    lexer.nextToken();
                    parameter.setDefaultValue(this.exprParser.expr());
                }
            }

            parameters.add(parameter);
            if (lexer.token() == Token.COMMA || lexer.token() == Token.SEMI) {
                lexer.nextToken();
            }

            if (lexer.token() != Token.BEGIN && lexer.token() != Token.RPAREN) {
                continue;
            }

            break;
        }
    }

    /**
     * parse procedure statement block
     *
     * @param statementList
     */
    private void parseProcedureStatementList(List<SQLStatement> statementList) {
        parseProcedureStatementList(statementList, -1);
    }

    /**
     * parse procedure statement block
     */
    private void parseProcedureStatementList(List<SQLStatement> statementList, int max) {

        for (; ; ) {
            if (max != -1) {
                if (statementList.size() >= max) {
                    return;
                }
            }

            if (lexer.token() == Token.EOF) {
                return;
            }
            if (lexer.token() == Token.END) {
                return;
            }
            if (lexer.token() == Token.ELSE) {
                return;
            }
            if (lexer.token() == (Token.SEMI)) {
                lexer.nextToken();
                continue;
            }
            if (lexer.token() == Token.WHEN) {
                return;
            }
            if (lexer.token() == Token.UNTIL) {
                return;
            }
            // select into
            if (lexer.token() == (Token.SELECT)) {
                statementList.add(this.parseSelectInto());
                continue;
            }

            // update
            if (lexer.token() == (Token.UPDATE)) {
                statementList.add(parseUpdateStatement());
                continue;
            }

            // create
            if (lexer.token() == (Token.CREATE)) {
                statementList.add(parseCreate());
                continue;
            }

            // insert
            if (lexer.token() == Token.INSERT) {
                SQLStatement stmt = parseInsert();
                statementList.add(stmt);
                continue;
            }

            // delete
            if (lexer.token() == (Token.DELETE)) {
                statementList.add(parseDeleteStatement());
                continue;
            }

            // call
            if (lexer.token() == Token.LBRACE || lexer.identifierEquals("CALL")) {
                statementList.add(this.parseCall());
                continue;
            }

            // begin
            if (lexer.token() == Token.BEGIN) {
                statementList.add(this.parseBlock());
                continue;
            }

            if (lexer.token() == Token.VARIANT) {
                SQLExpr variant = this.exprParser.primary();
                if (variant instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) variant;
                    if (binaryOpExpr.getOperator() == SQLBinaryOperator.Assignment) {
                        SQLSetStatement stmt = new SQLSetStatement(binaryOpExpr.getLeft(), binaryOpExpr.getRight(),
                                getDbType());
                        statementList.add(stmt);
                        continue;
                    }
                }
                accept(Token.COLONEQ);
                SQLExpr value = this.exprParser.expr();

                SQLSetStatement stmt = new SQLSetStatement(variant, value, getDbType());
                statementList.add(stmt);
                continue;
            }

            // select
            if (lexer.token() == Token.LPAREN) {
                char ch = lexer.current();
                int bp = lexer.bp();
                lexer.nextToken();

                if (lexer.token() == Token.SELECT) {
                    lexer.reset(bp, ch, Token.LPAREN);
                    statementList.add(this.parseSelect());
                    continue;
                } else {
                    throw new ParserException("TODO. " + lexer.info());
                }
            }
            // assign statement
            if (lexer.token() == Token.SET) {
                statementList.add(this.parseAssign());
                continue;
            }

            // while statement
            if (lexer.token() == Token.WHILE) {
                SQLStatement stmt = this.parseWhile();
                statementList.add(stmt);
                continue;
            }

            // loop statement
            if (lexer.token() == Token.LOOP) {
                statementList.add(this.parseLoop());
                continue;
            }

            // if statement
            if (lexer.token() == Token.IF) {
                statementList.add(this.parseIf());
                continue;
            }

            // case statement
            if (lexer.token() == Token.CASE) {
                statementList.add(this.parseCase());
                continue;
            }

            // declare statement
            if (lexer.token() == Token.DECLARE) {
                SQLStatement stmt = this.parseDeclare();
                statementList.add(stmt);
                continue;
            }

            // leave statement
            if (lexer.token() == Token.LEAVE) {
                statementList.add(this.parseLeave());
                continue;
            }

            // iterate statement
            if (lexer.token() == Token.ITERATE) {
                statementList.add(this.parseIterate());
                continue;
            }

            // repeat statement
            if (lexer.token() == Token.REPEAT) {
                statementList.add(this.parseRepeat());
                continue;
            }

            // open cursor
            if (lexer.token() == Token.OPEN) {
                statementList.add(this.parseOpen());
                continue;
            }

            // close cursor
            if (lexer.token() == Token.CLOSE) {
                statementList.add(this.parseClose());
                continue;
            }

            // fetch cursor into
            if (lexer.token() == Token.FETCH) {
                statementList.add(this.parseFetch());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.CHECKSUM)) {
                statementList.add(this.parseChecksum());
                continue;
            }

            if (lexer.token() == Token.IDENTIFIER) {
                String label = lexer.stringVal();
                char ch = lexer.current();
                int bp = lexer.bp();
                lexer.nextToken();
                if (lexer.token() == Token.VARIANT && lexer.stringVal().equals(":")) {
                    lexer.nextToken();
                    if (lexer.token() == Token.LOOP) {
                        // parse loop statement
                        statementList.add(this.parseLoop(label));
                    } else if (lexer.token() == Token.WHILE) {
                        // parse while statement with label
                        statementList.add(this.parseWhile(label));
                    } else if (lexer.token() == Token.BEGIN) {
                        // parse begin-end statement with label
                        statementList.add(this.parseBlock(label));
                    } else if (lexer.token() == Token.REPEAT) {
                        // parse repeat statement with label
                        statementList.add(this.parseRepeat(label));
                    }
                    continue;
                } else {
                    lexer.reset(bp, ch, Token.IDENTIFIER);
                }

            }
            throw new ParserException("TODO, " + lexer.info());
        }

    }


    public MySqlChecksumTableStatement parseChecksum() {
        MySqlChecksumTableStatement stmt = new MySqlChecksumTableStatement();
        if (lexer.identifierEquals(FnvHash.Constants.CHECKSUM)) {
            lexer.nextToken();
        } else {
            throw new ParserException("TODO " + lexer.info());
        }

        for (;;) {
            SQLExprTableSource table = (SQLExprTableSource) this.createSQLSelectParser().parseTableSource();
            stmt.addTable(table);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return stmt;
    }

    /**
     * parse if statement
     *
     * @return MySqlIfStatement
     */
    public SQLIfStatement parseIf() {
        accept(Token.IF);

        SQLIfStatement stmt = new SQLIfStatement();

        stmt.setCondition(this.exprParser.expr());

        accept(Token.THEN);

        this.parseStatementList(stmt.getStatements(), -1, stmt);

        while (lexer.token() == Token.ELSE) {
            lexer.nextToken();

            if (lexer.token() == Token.IF) {
                lexer.nextToken();

                SQLIfStatement.ElseIf elseIf = new SQLIfStatement.ElseIf();

                elseIf.setCondition(this.exprParser.expr());
                elseIf.setParent(stmt);

                accept(Token.THEN);
                this.parseStatementList(elseIf.getStatements(), -1, elseIf);


                stmt.getElseIfList().add(elseIf);
            } else {
                SQLIfStatement.Else elseItem = new SQLIfStatement.Else();
                this.parseStatementList(elseItem.getStatements(), -1, elseItem);
                stmt.setElseItem(elseItem);
                break;
            }
        }

        accept(Token.END);
        accept(Token.IF);
        accept(Token.SEMI);
        stmt.setAfterSemi(true);

        return stmt;
    }

    /**
     * parse while statement
     *
     * @return MySqlWhileStatement
     */
    public SQLWhileStatement parseWhile() {
        accept(Token.WHILE);
        SQLWhileStatement stmt = new SQLWhileStatement();

        stmt.setCondition(this.exprParser.expr());

        accept(Token.DO);

        this.parseStatementList(stmt.getStatements(), -1, stmt);

        accept(Token.END);

        accept(Token.WHILE);

        accept(Token.SEMI);

        return stmt;

    }

    /**
     * parse while statement with label
     *
     * @return MySqlWhileStatement
     */
    public SQLWhileStatement parseWhile(String label) {
        accept(Token.WHILE);

        SQLWhileStatement stmt = new SQLWhileStatement();

        stmt.setLabelName(label);

        stmt.setCondition(this.exprParser.expr());

        accept(Token.DO);

        this.parseStatementList(stmt.getStatements(), -1, stmt);

        accept(Token.END);

        accept(Token.WHILE);

        acceptIdentifier(label);

        accept(Token.SEMI);

        return stmt;

    }

    /**
     * parse case statement
     *
     * @return MySqlCaseStatement
     */
    public MySqlCaseStatement parseCase() {
        MySqlCaseStatement stmt = new MySqlCaseStatement();
        accept(Token.CASE);

        if (lexer.token() == Token.WHEN)// grammar 1
        {
            while (lexer.token() == Token.WHEN) {

                MySqlWhenStatement when = new MySqlWhenStatement();
                // when expr
                when.setCondition(exprParser.expr());

                accept(Token.THEN);

                // when block
                this.parseStatementList(when.getStatements(), -1, when);

                stmt.addWhenStatement(when);
            }
            if (lexer.token() == Token.ELSE) {
                // parse else block
                SQLIfStatement.Else elseStmt = new SQLIfStatement.Else();
                this.parseStatementList(elseStmt.getStatements(), -1, elseStmt);
                stmt.setElseItem(elseStmt);
            }
        } else// grammar 2
        {
            // case expr
            stmt.setCondition(exprParser.expr());

            while (lexer.token() == Token.WHEN) {
                accept(Token.WHEN);
                MySqlWhenStatement when = new MySqlWhenStatement();
                // when expr
                when.setCondition(exprParser.expr());

                accept(Token.THEN);

                // when block
                this.parseStatementList(when.getStatements(), -1, when);

                stmt.addWhenStatement(when);
            }
            if (lexer.token() == Token.ELSE) {
                accept(Token.ELSE);
                // else block
                SQLIfStatement.Else elseStmt = new SQLIfStatement.Else();
                this.parseStatementList(elseStmt.getStatements(), -1, elseStmt);
                stmt.setElseItem(elseStmt);
            }
        }
        accept(Token.END);
        accept(Token.CASE);
        accept(Token.SEMI);
        return stmt;

    }

    /**
     * parse declare statement
     */
    public SQLStatement parseDeclare() {
        char markChar = lexer.current();
        int markBp = lexer.bp();

        lexer.nextToken();

        if (lexer.token() == Token.CONTINUE) {
            lexer.reset(markBp, markChar, Token.DECLARE);
            return this.parseDeclareHandler();
        }

        lexer.nextToken();
        if (lexer.token() == Token.CURSOR) {
            lexer.reset(markBp, markChar, Token.DECLARE);
            return this.parseCursorDeclare();
        } else if (lexer.identifierEquals("HANDLER")) {
            //DECLARE异常处理程序 [add by zhujun 2016-04-16]
            lexer.reset(markBp, markChar, Token.DECLARE);
            return this.parseDeclareHandler();
        } else if (lexer.token() == Token.CONDITION) {
            //DECLARE异常 [add by zhujun 2016-04-17]
            lexer.reset(markBp, markChar, Token.DECLARE);
            return this.parseDeclareCondition();
        } else {
            lexer.reset(markBp, markChar, Token.DECLARE);
        }

        MySqlDeclareStatement stmt = new MySqlDeclareStatement();
        accept(Token.DECLARE);
        // lexer.nextToken();
        for (; ; ) {
            SQLDeclareItem item = new SQLDeclareItem();
            item.setName(exprParser.name());

            stmt.addVar(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                stmt.setAfterSemi(true);
                continue;
            } else if (lexer.token() != Token.EOF) {
                // var type
                item.setDataType(exprParser.parseDataType());

                if (lexer.token() == Token.DEFAULT) {
                    lexer.nextToken();
                    SQLExpr defaultValue = this.exprParser.primary();
                    item.setValue(defaultValue);
                }

                break;
            } else {
                throw new ParserException("TODO. " + lexer.info());
            }
        }
        return stmt;
    }

    /**
     * parse assign statement
     */
    public SQLSetStatement parseAssign() {
        accept(Token.SET);
        SQLSetStatement stmt = new SQLSetStatement(getDbType());
        parseAssignItems(stmt.getItems(), stmt);
        return stmt;
    }

    /**
     * parse select into
     */
    public MySqlSelectIntoStatement parseSelectInto() {
        MySqlSelectIntoParser parse = new MySqlSelectIntoParser(this.exprParser);
        return parse.parseSelectInto();
    }

    /**
     * parse loop statement
     */
    public SQLLoopStatement parseLoop() {
        SQLLoopStatement loopStmt = new SQLLoopStatement();
        accept(Token.LOOP);
        this.parseStatementList(loopStmt.getStatements(), -1, loopStmt);
        accept(Token.END);
        accept(Token.LOOP);
        accept(Token.SEMI);
        loopStmt.setAfterSemi(true);
        return loopStmt;
    }

    /**
     * parse loop statement with label
     */
    public SQLLoopStatement parseLoop(String label) {
        SQLLoopStatement loopStmt = new SQLLoopStatement();
        loopStmt.setLabelName(label);
        accept(Token.LOOP);
        this.parseStatementList(loopStmt.getStatements(), -1, loopStmt);
        accept(Token.END);
        accept(Token.LOOP);
        if (lexer.token() != Token.SEMI) {
            acceptIdentifier(label);
        }
        accept(Token.SEMI);
        loopStmt.setAfterSemi(true);
        return loopStmt;
    }

    /**
     * parse loop statement with label
     */
    public SQLBlockStatement parseBlock(String label) {
        SQLBlockStatement block = new SQLBlockStatement();
        block.setLabelName(label);
        accept(Token.BEGIN);
        this.parseStatementList(block.getStatementList(), -1, block);
        accept(Token.END);
        acceptIdentifier(label);
        return block;
    }

    /**
     * parse leave statement
     */
    public MySqlLeaveStatement parseLeave() {
        accept(Token.LEAVE);
        MySqlLeaveStatement leaveStmt = new MySqlLeaveStatement();
        leaveStmt.setLabelName(exprParser.name().getSimpleName());
        accept(Token.SEMI);
        return leaveStmt;
    }

    /**
     * parse iterate statement
     */
    public MySqlIterateStatement parseIterate() {
        accept(Token.ITERATE);
        MySqlIterateStatement iterateStmt = new MySqlIterateStatement();
        iterateStmt.setLabelName(exprParser.name().getSimpleName());
        accept(Token.SEMI);
        return iterateStmt;
    }

    /**
     * parse repeat statement
     *
     * @return
     */
    public MySqlRepeatStatement parseRepeat() {
        MySqlRepeatStatement stmt = new MySqlRepeatStatement();
        accept(Token.REPEAT);
        parseStatementList(stmt.getStatements(), -1, stmt);
        accept(Token.UNTIL);
        stmt.setCondition(exprParser.expr());
        accept(Token.END);
        accept(Token.REPEAT);
        accept(Token.SEMI);
        stmt.setAfterSemi(true);
        return stmt;
    }

    /**
     * parse repeat statement with label
     *
     * @param label
     * @return
     */
    public MySqlRepeatStatement parseRepeat(String label) {
        MySqlRepeatStatement repeatStmt = new MySqlRepeatStatement();
        repeatStmt.setLabelName(label);
        accept(Token.REPEAT);
        this.parseStatementList(repeatStmt.getStatements(), -1, repeatStmt);
        accept(Token.UNTIL);
        repeatStmt.setCondition(exprParser.expr());
        accept(Token.END);
        accept(Token.REPEAT);
        acceptIdentifier(label);
        accept(Token.SEMI);
        return repeatStmt;
    }

    /**
     * parse cursor declare statement
     *
     * @return
     */
    public MySqlCursorDeclareStatement parseCursorDeclare() {
        MySqlCursorDeclareStatement stmt = new MySqlCursorDeclareStatement();
        accept(Token.DECLARE);

        stmt.setCursorName(exprParser.name());

        accept(Token.CURSOR);

        accept(Token.FOR);

        //SQLSelectStatement selelctStmt = (SQLSelectStatement) parseSelect();
        SQLSelect select = this.createSQLSelectParser().select();
        stmt.setSelect(select);

        accept(Token.SEMI);

        return stmt;
    }

    /**
     * zhujun [455910092@qq.com]
     * parse spstatement
     *
     * @return
     */
    public SQLStatement parseSpStatement() {

        // update
        if (lexer.token() == (Token.UPDATE)) {
            return parseUpdateStatement();
        }

        // create
        if (lexer.token() == (Token.CREATE)) {
            return parseCreate();
        }

        // insert
        if (lexer.token() == Token.INSERT) {
            return parseInsert();
        }

        // delete
        if (lexer.token() == (Token.DELETE)) {
            return parseDeleteStatement();
        }

        // begin
        if (lexer.token() == Token.BEGIN) {
            return this.parseBlock();
        }

        // select
        if (lexer.token() == Token.LPAREN) {
            char ch = lexer.current();
            int bp = lexer.bp();
            lexer.nextToken();

            if (lexer.token() == Token.SELECT) {
                lexer.reset(bp, ch, Token.LPAREN);
                return this.parseSelect();
            } else {
                throw new ParserException("TODO. " + lexer.info());
            }
        }
        // assign statement
        if (lexer.token() == Token.SET) {
            return parseAssign();
        }

        throw new ParserException("error sp_statement. " + lexer.info());
    }

    /**
     * 定义异常处理程序
     *
     * @return
     * @author zhujun [455910092@qq.com]
     * 2016-04-16
     */
    public MySqlDeclareHandlerStatement parseDeclareHandler() {
        //DECLARE handler_type HANDLER FOR condition_value[,...] sp_statement
        //handler_type 取值为 CONTINUE | EXIT | UNDO
        //condition_value 取值为 SQLWARNING | NOT FOUND | SQLEXCEPTION | SQLSTATE value(异常码 e.g 1062)

        MySqlDeclareHandlerStatement stmt = new MySqlDeclareHandlerStatement();
        accept(Token.DECLARE);
        //String handlerType = exprParser.name().getSimpleName();
        if (lexer.token() == Token.CONTINUE) {
            stmt.setHandleType(MySqlHandlerType.CONTINUE);
        } else if (lexer.token() == Token.EXIT) {
            stmt.setHandleType(MySqlHandlerType.CONTINUE);
        } else if (lexer.token() == Token.UNDO) {
            stmt.setHandleType(MySqlHandlerType.CONTINUE);
        } else {
            throw new ParserException("unkown handle type. " + lexer.info());
        }
        lexer.nextToken();

        acceptIdentifier("HANDLER");

        accept(Token.FOR);

        for (; ; ) {
            String tokenName = lexer.stringVal();
            ConditionValue condition = new ConditionValue();

            if (tokenName.equalsIgnoreCase("NOT")) {//for 'NOT FOUND'
                lexer.nextToken();
                acceptIdentifier("FOUND");
                condition.setType(ConditionType.SYSTEM);
                condition.setValue("NOT FOUND");

            } else if (tokenName.equalsIgnoreCase("SQLSTATE")) { //for SQLSTATE (SQLSTATE '10001')
                condition.setType(ConditionType.SQLSTATE);
                lexer.nextToken();
                //condition.setValue(lexer.stringVal());
                //lexer.nextToken();
                condition.setValue(exprParser.name().toString());
            } else if (lexer.identifierEquals("SQLEXCEPTION")) { //for SQLEXCEPTION
                condition.setType(ConditionType.SYSTEM);
                condition.setValue(lexer.stringVal());
                lexer.nextToken();
            } else if (lexer.identifierEquals("SQLWARNING")) { //for SQLWARNING
                condition.setType(ConditionType.SYSTEM);
                condition.setValue(lexer.stringVal());
                lexer.nextToken();
            } else { //for condition_name or mysql_error_code
                if (lexer.token() == Token.LITERAL_INT) {
                    condition.setType(ConditionType.MYSQL_ERROR_CODE);
                    condition.setValue(lexer.integerValue().toString());
                } else {
                    condition.setType(ConditionType.SELF);
                    condition.setValue(tokenName);
                }
                lexer.nextToken();
            }
            stmt.getConditionValues().add(condition);
            if (lexer.token() == Token.COMMA) {
                accept(Token.COMMA);
                continue;
            } else if (lexer.token() != Token.EOF) {
                break;
            } else {
                throw new ParserException("declare handle not eof");
            }
        }

        stmt.setSpStatement(parseSpStatement());

        if (!(stmt.getSpStatement() instanceof SQLBlockStatement)) {
            accept(Token.SEMI);
        }


        return stmt;
    }

    /**
     * zhujun [455910092@qq.com]
     * 2016-04-17
     * 定义条件
     *
     * @return
     */
    public MySqlDeclareConditionStatement parseDeclareCondition() {
        /*
        DECLARE condition_name CONDITION FOR condition_value

    	condition_value:
    	    SQLSTATE [VALUE] sqlstate_value
    	  | mysql_error_code
    	*/
        MySqlDeclareConditionStatement stmt = new MySqlDeclareConditionStatement();
        accept(Token.DECLARE);

        stmt.setConditionName(exprParser.name().toString());

        accept(Token.CONDITION);

        accept(Token.FOR);

        String tokenName = lexer.stringVal();
        ConditionValue condition = new ConditionValue();
        if (tokenName.equalsIgnoreCase("SQLSTATE")) { //for SQLSTATE (SQLSTATE '10001')
            condition.setType(ConditionType.SQLSTATE);
            lexer.nextToken();
            condition.setValue(exprParser.name().toString());
        } else if (lexer.token() == Token.LITERAL_INT) {
            condition.setType(ConditionType.MYSQL_ERROR_CODE);
            condition.setValue(lexer.integerValue().toString());
            lexer.nextToken();
        } else {
            throw new ParserException("declare condition grammer error. " + lexer.info());
        }

        stmt.setConditionValue(condition);

        accept(Token.SEMI);

        return stmt;
    }
}
