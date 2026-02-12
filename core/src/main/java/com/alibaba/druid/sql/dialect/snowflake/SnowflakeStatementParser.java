package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

public class SnowflakeStatementParser extends SQLStatementParser {
    public SnowflakeStatementParser(String sql) {
        super(new SnowflakeExprParser(sql));
    }

    public SnowflakeStatementParser(String sql, SQLParserFeature... features) {
        super(new SnowflakeExprParser(sql, features));
    }

    public SnowflakeStatementParser(Lexer lexer) {
        super(new SnowflakeExprParser(lexer));
    }

    @Override
    protected void createOptionSkip() {
        super.createOptionSkip();
        if (lexer.identifierEquals("TRANSIENT") || lexer.identifierEquals("VOLATILE")) {
            lexer.nextToken();
        }
    }

    public SnowflakeSelectParser createSQLSelectParser() {
        return new SnowflakeSelectParser(this.exprParser, selectListCache);
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new SnowflakeCreateTableParser(this.exprParser);
    }

    @Override
    protected SQLStatement alterRest(Lexer.SavePoint mark) {
        // Handle Snowflake-specific ALTER statements
        // Note: ALTER token has already been consumed when we reach here
        if (lexer.token() == Token.SESSION) {
            lexer.nextToken(); // consume SESSION
            return parseAlterSessionRest();
        }
        if (lexer.identifierEquals("WAREHOUSE")) {
            lexer.nextToken(); // consume WAREHOUSE
            return parseAlterWarehouseRest();
        }
        if (lexer.identifierEquals("STAGE")) {
            lexer.nextToken();
            return parseAlterStageRest();
        }
        if (lexer.identifierEquals("TASK")) {
            lexer.nextToken();
            return parseAlterTaskRest();
        }
        if (lexer.identifierEquals("STREAM")) {
            lexer.nextToken();
            return parseAlterStreamRest();
        }
        if (lexer.identifierEquals("PIPE")) {
            lexer.nextToken();
            return parseAlterPipeRest();
        }
        if (lexer.identifierEquals("FILE")) {
            lexer.nextToken();
            if (lexer.identifierEquals("FORMAT")) {
                lexer.nextToken();
            }
            return parseAlterFileFormatRest();
        }
        if (lexer.identifierEquals("FORMAT")) {
            lexer.nextToken();
            return parseAlterFileFormatRest();
        }
        return super.alterRest(mark);
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.BEGIN) {
            statementList.add(parseBlock());
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.COPY)) {
            statementList.add(parseCopy());
            return true;
        }

        if (lexer.identifierEquals("CALL")) {
            statementList.add(parseCall());
            return true;
        }

        // COMMIT / ROLLBACK
        if (lexer.token() == Token.COMMIT) {
            statementList.add(parseCommit());
            return true;
        }

        return false;
    }

    public SQLStatement parseBlock() {
        accept(Token.BEGIN);
        if (lexer.identifierEquals("TRANSACTION") || lexer.identifierEquals("TRAN")) {
            lexer.nextToken();
            // Handle optional NAME clause
            if (lexer.identifierEquals("NAME")) {
                lexer.nextToken();
                this.exprParser.name();
            }
            SQLStartTransactionStatement startTrans = new SQLStartTransactionStatement(dbType);
            return startTrans;
        }
        SQLBlockStatement block = new SQLBlockStatement();
        parseStatementList(block.getStatementList(), -1, block);
        if (lexer.token() == Token.EXCEPTION) {
            block.setException(parseException());
        }
        accept(Token.END);
        return block;
    }

    @Override
    protected void parseInsertOverwrite(SQLInsertInto insertStatement) {
        insertStatement.setOverwrite(true);
        lexer.nextIf(Token.INTO);
    }

    @Override
    protected void mergeBeforeName() {
        this.lexer.nextIf(Token.INTO);
    }

    public SQLDeleteStatement parseDeleteStatement() {
        SQLDeleteStatement deleteStatement = new SQLDeleteStatement(getDbType());

        accept(Token.DELETE);
        lexer.nextIf(Token.FROM);

        SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
        deleteStatement.setTableSource(tableSource);

        if (lexer.nextIf(Token.USING)) {
            SQLTableSource using = createSQLSelectParser().parseTableSource();
            deleteStatement.setUsing(using);
        }

        if (lexer.nextIf(Token.WHERE)) {
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        return deleteStatement;
    }

    @Override
    public SQLUseStatement parseUse() {
        accept(Token.USE);

        SQLUseStatement stmt = new SQLUseStatement(getDbType());

        // USE DATABASE db_name / USE SCHEMA schema_name / USE WAREHOUSE wh_name / USE ROLE role_name
        if (lexer.token() == Token.DATABASE) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setDatabase(new SQLPropertyExpr(new SQLIdentifierExpr("DATABASE"), name.getSimpleName()));
        } else if (lexer.token() == Token.SCHEMA) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setDatabase(new SQLPropertyExpr(new SQLIdentifierExpr("SCHEMA"), name.getSimpleName()));
        } else if (lexer.identifierEquals("WAREHOUSE")) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setDatabase(new SQLPropertyExpr(new SQLIdentifierExpr("WAREHOUSE"), name.getSimpleName()));
        } else if (lexer.identifierEquals("ROLE")) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setDatabase(new SQLPropertyExpr(new SQLIdentifierExpr("ROLE"), name.getSimpleName()));
        } else {
            stmt.setDatabase(this.exprParser.name());
        }

        return stmt;
    }

    @Override
    public SQLStatement parseCopy() {
        acceptIdentifier("COPY");
        accept(Token.INTO);

        SQLCopyFromStatement copyStmt = new SQLCopyFromStatement();
        copyStmt.setDbType(getDbType());

        SQLName tableName = this.exprParser.name();
        copyStmt.setTable(new SQLExprTableSource(tableName));

        if (lexer.nextIf(Token.LPAREN)) {
            for (;;) {
                copyStmt.getColumns().add(this.exprParser.name());
                if (!lexer.nextIf(Token.COMMA)) {
                    break;
                }
            }
            accept(Token.RPAREN);
        }

        accept(Token.FROM);

        SQLExpr from = this.exprParser.expr();
        copyStmt.setFrom(from);

        parseCopyIntoOptions();

        return copyStmt;
    }

    private void parseCopyIntoOptions() {
        for (;;) {
            if (lexer.identifierEquals("FILES")) {
                lexer.nextToken();
                accept(Token.EQ);
                accept(Token.LPAREN);
                for (;;) {
                    this.exprParser.expr();
                    if (!lexer.nextIf(Token.COMMA)) {
                        break;
                    }
                }
                accept(Token.RPAREN);
                continue;
            }

            if (lexer.identifierEquals("PATTERN")) {
                lexer.nextToken();
                accept(Token.EQ);
                this.exprParser.expr();
                continue;
            }

            if (lexer.identifierEquals("FILE_FORMAT")) {
                lexer.nextToken();
                accept(Token.EQ);
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    parseKeyValuePairs();
                    accept(Token.RPAREN);
                } else {
                    this.exprParser.name();
                }
                continue;
            }

            if (lexer.identifierEquals("VALIDATION_MODE")) {
                lexer.nextToken();
                accept(Token.EQ);
                this.exprParser.expr();
                continue;
            }

            // generic key = value options
            if (lexer.token() == Token.IDENTIFIER) {
                Lexer.SavePoint mark = lexer.mark();
                String key = lexer.stringVal();
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                    this.exprParser.expr();
                    continue;
                } else {
                    lexer.reset(mark);
                }
            }

            break;
        }
    }

    private void parseKeyValuePairs() {
        for (;;) {
            if (lexer.token() == Token.RPAREN) {
                break;
            }
            // Handle both IDENTIFIER and keyword tokens (like TYPE, FORMAT, etc.)
            if (lexer.token() == Token.IDENTIFIER
                    || lexer.token() == Token.TYPE
                    || lexer.token() == Token.FORMAT
                    || lexer.token() == Token.COMMENT
                    || lexer.token() == Token.LOGGING) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    accept(Token.EQ);
                    this.exprParser.expr();
                }
                // Check for comma separator between key-value pairs
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                // If no comma, check if we're at the closing paren
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                continue;
            }
            break;
        }
    }

    @Override
    public SQLStatement parseExecute() {
        acceptIdentifier("EXECUTE");
        // IMMEDIATE is a Token, use token comparison
        if (lexer.token() == Token.IDENTIFIER && lexer.identifierEquals("IMMEDIATE")) {
            lexer.nextToken();
        } else if (lexer.token() == Token.IMMEDIATE) {
            lexer.nextToken();
        } else {
            acceptIdentifier("IMMEDIATE");
        }

        SQLCallStatement stmt = new SQLCallStatement(getDbType());
        stmt.setProcedureName(new SQLIdentifierExpr("EXECUTE IMMEDIATE"));

        SQLExpr expr = this.exprParser.expr();
        stmt.getParameters().add(expr);

        return stmt;
    }

    @Override
    public SQLCallStatement parseCall() {
        acceptIdentifier("CALL");

        SQLCallStatement stmt = new SQLCallStatement(getDbType());
        stmt.setProcedureName(this.exprParser.name());

        accept(Token.LPAREN);
        this.exprParser.exprList(stmt.getParameters(), stmt);
        accept(Token.RPAREN);

        return stmt;
    }

    public SQLStatement parseShow() {
        accept(Token.SHOW);

        if (lexer.identifierEquals("TABLES")) {
            lexer.nextToken();
            SQLShowTablesStatement stmt = new SQLShowTablesStatement();
            parseShowLikeAndIn(stmt);
            return stmt;
        }

        if (lexer.token() == Token.DATABASE || lexer.identifierEquals("DATABASES")) {
            if (lexer.token() == Token.DATABASE) {
                lexer.nextToken();
            } else {
                lexer.nextToken();
            }
            SQLShowDatabasesStatement stmt = new SQLShowDatabasesStatement();
            if (lexer.nextIf(Token.LIKE)) {
                stmt.setLike(this.exprParser.expr());
            }
            return stmt;
        }

        if (lexer.identifierEquals("SCHEMAS")) {
            lexer.nextToken();
            SQLShowDatabasesStatement stmt = new SQLShowDatabasesStatement();
            if (lexer.nextIf(Token.LIKE)) {
                stmt.setLike(this.exprParser.expr());
            }
            if (lexer.nextIf(Token.IN) || lexer.nextIf(Token.FROM)) {
                if (lexer.token() == Token.DATABASE) {
                    lexer.nextToken();
                }
                stmt.setDatabase(this.exprParser.name());
            }
            return stmt;
        }

        if (lexer.identifierEquals("COLUMNS")) {
            lexer.nextToken();
            SQLShowColumnsStatement stmt = new SQLShowColumnsStatement();
            if (lexer.nextIf(Token.LIKE)) {
                stmt.setLike(this.exprParser.expr());
            }
            if (lexer.nextIf(Token.IN) || lexer.nextIf(Token.FROM)) {
                lexer.nextIf(Token.TABLE);
                stmt.setTable(this.exprParser.name());
            }
            return stmt;
        }

        if (lexer.token() == Token.VIEW || lexer.identifierEquals("VIEWS")) {
            if (lexer.token() == Token.VIEW) {
                lexer.nextToken();
            } else {
                lexer.nextToken();
            }
            SQLShowViewsStatement stmt = new SQLShowViewsStatement();
            if (lexer.nextIf(Token.LIKE)) {
                stmt.setLike(this.exprParser.expr());
            }
            if (lexer.nextIf(Token.IN) || lexer.nextIf(Token.FROM)) {
                if (lexer.token() == Token.DATABASE) {
                    lexer.nextToken();
                }
                if (lexer.token() == Token.SCHEMA) {
                    lexer.nextToken();
                }
                stmt.setDatabase(this.exprParser.name());
            }
            return stmt;
        }

        if (lexer.identifierEquals("WAREHOUSES")) {
            lexer.nextToken();
            SQLShowTablesStatement stmt = new SQLShowTablesStatement();
            if (lexer.nextIf(Token.LIKE)) {
                stmt.setLike(this.exprParser.expr());
            }
            return stmt;
        }

        if (lexer.identifierEquals("GRANTS")) {
            lexer.nextToken();
            SQLShowGrantsStatement stmt = new SQLShowGrantsStatement();
            if (lexer.nextIf(Token.ON)) {
                lexer.nextIf(Token.TABLE);
                lexer.nextIf(Token.DATABASE);
                lexer.nextIf(Token.SCHEMA);
                lexer.nextIf(Token.VIEW);
                lexer.nextIfIdentifier("WAREHOUSE");
                lexer.nextIfIdentifier("ROLE");
                stmt.setOn(this.exprParser.name());
            }
            if (lexer.nextIf(Token.TO)) {
                lexer.nextIfIdentifier("ROLE");
                lexer.nextIfIdentifier("USER");
                stmt.setUser(this.exprParser.name());
            }
            return stmt;
        }

        // Generic SHOW handler
        String showItem = lexer.stringVal();
        lexer.nextToken();

        SQLShowTablesStatement stmt = new SQLShowTablesStatement();
        if (lexer.nextIf(Token.LIKE)) {
            stmt.setLike(this.exprParser.expr());
        }
        if (lexer.nextIf(Token.IN) || lexer.nextIf(Token.FROM)) {
            if (lexer.token() == Token.DATABASE) {
                lexer.nextToken();
            }
            if (lexer.token() == Token.SCHEMA) {
                lexer.nextToken();
            }
            lexer.nextIf(Token.TABLE);
            stmt.setDatabase(this.exprParser.name());
        }
        return stmt;
    }

    private void parseShowLikeAndIn(SQLShowTablesStatement stmt) {
        if (lexer.nextIf(Token.LIKE)) {
            stmt.setLike(this.exprParser.expr());
        }
        if (lexer.nextIf(Token.IN) || lexer.nextIf(Token.FROM)) {
            if (lexer.token() == Token.DATABASE) {
                lexer.nextToken();
            }
            if (lexer.token() == Token.SCHEMA) {
                lexer.nextToken();
            }
            stmt.setDatabase(this.exprParser.name());
        }
    }

    @Override
    public SQLStatement parseDescribe() {
        if (lexer.token() == Token.DESC || lexer.identifierEquals("DESCRIBE")) {
            lexer.nextToken();
        }

        SQLDescribeStatement stmt = new SQLDescribeStatement();
        stmt.setDbType(getDbType());

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();
        } else if (lexer.token() == Token.FUNCTION) {
            lexer.nextToken();
        } else if (lexer.token() == Token.VIEW) {
            lexer.nextToken();
        } else if (lexer.identifierEquals("SCHEMA")) {
            lexer.nextToken();
        } else if (lexer.identifierEquals("DATABASE")) {
            lexer.nextToken();
        } else if (lexer.identifierEquals("STAGE")) {
            lexer.nextToken();
        } else if (lexer.identifierEquals("TASK")) {
            lexer.nextToken();
        } else if (lexer.identifierEquals("STREAM")) {
            lexer.nextToken();
        } else if (lexer.identifierEquals("PIPE")) {
            lexer.nextToken();
        }

        stmt.setObject(this.exprParser.name());

        return stmt;
    }

    // ==================== ALTER SESSION ====================

    public SQLStatement parseAlterSession() {
        accept(Token.ALTER);
        accept(Token.SESSION);
        return parseAlterSessionRest();
    }

    private SQLStatement parseAlterSessionRest() {
        SQLSetStatement stmt = new SQLSetStatement(getDbType());

        if (lexer.nextIf(Token.SET)) {
            for (;;) {
                SQLName name = this.exprParser.name();
                accept(Token.EQ);
                SQLExpr value = this.exprParser.expr();
                stmt.getItems().add(new SQLAssignItem(name, value));
                if (!lexer.nextIf(Token.COMMA)) {
                    break;
                }
            }
        } else if (lexer.identifierEquals("UNSET")) {
            lexer.nextToken();
            for (;;) {
                SQLName name = this.exprParser.name();
                stmt.getItems().add(new SQLAssignItem(name, new SQLIdentifierExpr("UNSET")));
                if (!lexer.nextIf(Token.COMMA)) {
                    break;
                }
            }
        }

        return stmt;
    }

    // ==================== ALTER WAREHOUSE ====================

    public SQLStatement parseAlterWarehouse() {
        accept(Token.ALTER);
        acceptIdentifier("WAREHOUSE");
        return parseAlterWarehouseRest();
    }

    private SQLStatement parseAlterWarehouseRest() {
        SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());
        stmt.putAttribute("objectType", "WAREHOUSE");

        lexer.nextIf(Token.IF);

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        if (lexer.identifierEquals("SUSPEND") || lexer.identifierEquals("RESUME")
                || lexer.identifierEquals("ABORT")) {
            lexer.nextToken();
            if (lexer.identifierEquals("ALL")) {
                lexer.nextToken();
                acceptIdentifier("QUERIES");
            }
        } else if (lexer.nextIf(Token.SET)) {
            parseWarehouseParameters(stmt);
        } else if (lexer.identifierEquals("UNSET")) {
            lexer.nextToken();
            while (lexer.token() == Token.IDENTIFIER) {
                this.exprParser.name();
                if (!lexer.nextIf(Token.COMMA)) {
                    break;
                }
            }
        } else if (lexer.identifierEquals("RENAME")) {
            lexer.nextToken();
            accept(Token.TO);
            this.exprParser.name();
        }

        return stmt;
    }

    private void parseWarehouseParameters(SQLAlterTableStatement stmt) {
        for (;;) {
            if (lexer.token() == Token.IDENTIFIER) {
                SQLName paramName = this.exprParser.name();
                accept(Token.EQ);
                SQLExpr value = this.exprParser.expr();
                stmt.putAttribute(paramName.getSimpleName(), value);
                if (!lexer.nextIf(Token.COMMA)) {
                    break;
                }
                continue;
            }
            break;
        }
    }

    // ==================== ALTER STAGE ====================

    public SQLStatement parseAlterStage() {
        accept(Token.ALTER);
        acceptIdentifier("STAGE");
        return parseAlterStageRest();
    }

    private SQLStatement parseAlterStageRest() {
        SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());
        stmt.putAttribute("objectType", "STAGE");

        lexer.nextIf(Token.IF);

        stmt.setName(this.exprParser.name());

        if (lexer.identifierEquals("RENAME")) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.putAttribute("NEW_NAME", this.exprParser.name());
        } else if (lexer.nextIf(Token.SET)) {
            parseStageOptions(stmt);
        } else if (lexer.identifierEquals("UNSET")) {
            lexer.nextToken();
            while (lexer.token() == Token.IDENTIFIER) {
                this.exprParser.name();
                if (!lexer.nextIf(Token.COMMA)) {
                    break;
                }
            }
        } else if (lexer.identifierEquals("REFRESH")) {
            lexer.nextToken();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                lexer.nextIfIdentifier("SUBPATH");
                accept(Token.EQ);
                this.exprParser.expr();
                accept(Token.RPAREN);
            }
        }

        return stmt;
    }

    private void parseStageOptions(SQLAlterTableStatement stmt) {
        for (;;) {
            if (lexer.identifierEquals("URL")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("URL", this.exprParser.expr());
                continue;
            }
            if (lexer.identifierEquals("STORAGE_INTEGRATION")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("STORAGE_INTEGRATION", this.exprParser.name());
                continue;
            }
            if (lexer.identifierEquals("CREDENTIALS")) {
                lexer.nextToken();
                accept(Token.EQ);
                accept(Token.LPAREN);
                parseKeyValuePairs();
                accept(Token.RPAREN);
                continue;
            }
            if (lexer.identifierEquals("ENCRYPTION")) {
                lexer.nextToken();
                accept(Token.EQ);
                accept(Token.LPAREN);
                parseKeyValuePairs();
                accept(Token.RPAREN);
                continue;
            }
            if (lexer.identifierEquals("FILE_FORMAT")) {
                lexer.nextToken();
                accept(Token.EQ);
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    parseKeyValuePairs();
                    accept(Token.RPAREN);
                } else {
                    stmt.putAttribute("FILE_FORMAT", this.exprParser.name());
                }
                continue;
            }
            if (lexer.identifierEquals("COPY_OPTIONS")) {
                lexer.nextToken();
                accept(Token.EQ);
                accept(Token.LPAREN);
                parseKeyValuePairs();
                accept(Token.RPAREN);
                continue;
            }
            if (lexer.identifierEquals("COMMENT")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("COMMENT", this.exprParser.expr());
                continue;
            }
            break;
        }
    }

    // ==================== ALTER TASK ====================

    public SQLStatement parseAlterTask() {
        accept(Token.ALTER);
        acceptIdentifier("TASK");
        return parseAlterTaskRest();
    }

    private SQLStatement parseAlterTaskRest() {
        SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());
        stmt.putAttribute("objectType", "TASK");

        lexer.nextIf(Token.IF);

        stmt.setName(this.exprParser.name());

        if (lexer.identifierEquals("SUSPEND") || lexer.identifierEquals("RESUME")) {
            lexer.nextToken();
            stmt.putAttribute("ACTION", lexer.stringVal());
        } else if (lexer.identifierEquals("REMOVE")) {
            lexer.nextToken();
            acceptIdentifier("AFTER");
            stmt.putAttribute("REMOVE_AFTER", true);
        } else if (lexer.identifierEquals("ADD")) {
            lexer.nextToken();
            acceptIdentifier("AFTER");
            stmt.putAttribute("ADD_AFTER", this.exprParser.name());
        } else if (lexer.identifierEquals("RENAME")) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.putAttribute("NEW_NAME", this.exprParser.name());
        } else if (lexer.nextIf(Token.SET)) {
            parseTaskOptions(stmt);
        } else if (lexer.identifierEquals("UNSET")) {
            lexer.nextToken();
            while (lexer.token() == Token.IDENTIFIER) {
                this.exprParser.name();
                if (!lexer.nextIf(Token.COMMA)) {
                    break;
                }
            }
        } else if (lexer.identifierEquals("FINALIZE")) {
            lexer.nextToken();
            stmt.putAttribute("FINALIZE", true);
        } else if (lexer.identifierEquals("MODIFY")) {
            lexer.nextToken();
            accept(Token.AS);
            SQLStatement taskSql = parseStatement();
            stmt.putAttribute("TASK_SQL", taskSql);
        }

        return stmt;
    }

    private void parseTaskOptions(SQLAlterTableStatement stmt) {
        for (;;) {
            if (lexer.identifierEquals("WAREHOUSE")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("WAREHOUSE", this.exprParser.name());
                continue;
            }
            if (lexer.identifierEquals("SCHEDULE")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("SCHEDULE", this.exprParser.expr());
                continue;
            }
            if (lexer.identifierEquals("ALLOW_OVERLAPPING_EXECUTION")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("ALLOW_OVERLAPPING_EXECUTION", this.exprParser.expr());
                continue;
            }
            if (lexer.identifierEquals("COMMENT")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("COMMENT", this.exprParser.expr());
                continue;
            }
            if (lexer.identifierEquals("AFTER")) {
                lexer.nextToken();
                stmt.putAttribute("AFTER", this.exprParser.name());
                continue;
            }
            if (lexer.token() == Token.WHEN) {
                lexer.nextToken();
                stmt.putAttribute("WHEN", this.exprParser.expr());
                continue;
            }
            break;
        }
    }

    // ==================== ALTER STREAM ====================

    public SQLStatement parseAlterStream() {
        accept(Token.ALTER);
        acceptIdentifier("STREAM");
        return parseAlterStreamRest();
    }

    private SQLStatement parseAlterStreamRest() {
        SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());
        stmt.putAttribute("objectType", "STREAM");

        lexer.nextIf(Token.IF);

        stmt.setName(this.exprParser.name());

        if (lexer.identifierEquals("RENAME")) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.putAttribute("NEW_NAME", this.exprParser.name());
        } else if (lexer.nextIf(Token.SET)) {
            if (lexer.identifierEquals("COMMENT")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("COMMENT", this.exprParser.expr());
            }
        } else if (lexer.identifierEquals("UNSET")) {
            lexer.nextToken();
            if (lexer.identifierEquals("COMMENT")) {
                lexer.nextToken();
            }
        }

        return stmt;
    }

    // ==================== ALTER PIPE ====================

    public SQLStatement parseAlterPipe() {
        accept(Token.ALTER);
        acceptIdentifier("PIPE");
        return parseAlterPipeRest();
    }

    private SQLStatement parseAlterPipeRest() {
        SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());
        stmt.putAttribute("objectType", "PIPE");

        lexer.nextIf(Token.IF);

        stmt.setName(this.exprParser.name());

        if (lexer.identifierEquals("RENAME")) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.putAttribute("NEW_NAME", this.exprParser.name());
        } else if (lexer.nextIf(Token.SET)) {
            if (lexer.identifierEquals("COMMENT")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("COMMENT", this.exprParser.expr());
            }
        } else if (lexer.identifierEquals("UNSET")) {
            lexer.nextToken();
            if (lexer.identifierEquals("COMMENT")) {
                lexer.nextToken();
            }
        } else if (lexer.identifierEquals("REFRESH")) {
            lexer.nextToken();
            if (lexer.identifierEquals("PREFIX")) {
                lexer.nextToken();
                accept(Token.EQ);
                this.exprParser.expr();
            }
        } else if (lexer.identifierEquals("PAUSE") || lexer.identifierEquals("RESUME")) {
            lexer.nextToken();
            stmt.putAttribute("ACTION", lexer.stringVal());
        }

        return stmt;
    }

    // ==================== ALTER FILE FORMAT ====================

    public SQLStatement parseAlterFileFormat() {
        accept(Token.ALTER);
        if (lexer.identifierEquals("FILE")) {
            lexer.nextToken();
        }
        if (lexer.identifierEquals("FORMAT")) {
            lexer.nextToken();
        }
        return parseAlterFileFormatRest();
    }

    private SQLStatement parseAlterFileFormatRest() {
        SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());
        stmt.putAttribute("objectType", "FILE_FORMAT");

        lexer.nextIf(Token.IF);

        stmt.setName(this.exprParser.name());

        if (lexer.identifierEquals("RENAME")) {
            lexer.nextToken();
            accept(Token.TO);
            stmt.putAttribute("NEW_NAME", this.exprParser.name());
        } else if (lexer.nextIf(Token.SET)) {
            if (lexer.identifierEquals("COMMENT")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("COMMENT", this.exprParser.expr());
            }
        }

        return stmt;
    }

    // ==================== COMMIT ====================

    public SQLStatement parseCommit() {
        accept(Token.COMMIT);
        SQLCommitStatement stmt = new SQLCommitStatement();
        stmt.setDbType(getDbType());
        return stmt;
    }
}
