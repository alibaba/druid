package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.*;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

public class StarRocksStatementParser extends SQLStatementParser {
    @Override
    public SQLSelectParser createSQLSelectParser() {
        return new StarRocksSelectParser(this.exprParser, selectListCache);
    }
    public StarRocksStatementParser(SQLExprParser exprParser) {
        super(exprParser);
    }
    public StarRocksStatementParser(String sql) {
        super(new StarRocksExprParser(sql));
    }

    public StarRocksStatementParser(String sql, SQLParserFeature... features) {
        super(new StarRocksExprParser(sql, features));
    }

    public StarRocksStatementParser(String sql, boolean keepComments) {
        super(new StarRocksExprParser(sql, keepComments));
    }

    public StarRocksStatementParser(String sql, boolean skipComment, boolean keepComments) {
        super(new StarRocksExprParser(sql, skipComment, keepComments));
    }

    public StarRocksStatementParser(Lexer lexer) {
        super(new StarRocksExprParser(lexer));
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new StarRocksCreateTableParser(this.exprParser);
    }

    @Override
    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.identifierEquals("SUBMIT")) {
            SQLStatement stmt = parseSubmitTask();
            statementList.add(stmt);
            return true;
        }
        if (lexer.identifierEquals("LOAD")) {
            SQLStatement stmt = parseLoadLabel();
            statementList.add(stmt);
            return true;
        }
        if (lexer.identifierEquals("BACKUP")) {
            SQLStatement stmt = parseBackup();
            statementList.add(stmt);
            return true;
        }
        if (lexer.identifierEquals("RESTORE")) {
            SQLStatement stmt = parseRestore();
            statementList.add(stmt);
            return true;
        }
        return false;
    }

    @Override
    protected void parseInsertOverwrite(SQLInsertInto insertStatement) {
        insertStatement.setOverwrite(true);
    }

    @Override
    protected void parseInsert0AfterTableName(SQLInsertInto insertStatement, SQLName tableName) {
        if (lexer.token() == Token.LPAREN
                && tableName instanceof SQLIdentifierExpr
                && ("FILES".equalsIgnoreCase(tableName.getSimpleName())
                    || "BLACKHOLE".equalsIgnoreCase(tableName.getSimpleName()))) {
            insertStatement.setDbType(DbType.starrocks);
            lexer.nextToken();
            if (lexer.token() != Token.RPAREN) {
                for (; ; ) {
                    SQLAssignItem item = this.exprParser.parseAssignItem(true, insertStatement);
                    insertStatement.addTableOption(item);
                    if (lexer.token() != Token.COMMA) {
                        break;
                    }
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }
    }

    @Override
    protected void parseInsert0AfterAlias(SQLInsertInto insertStatement) {
        if (lexer.token() == Token.WITH) {
            Lexer.SavePoint mark = lexer.markOut();
            lexer.nextToken();
            if (lexer.identifierEquals("LABEL")) {
                lexer.nextToken();
                insertStatement.setDbType(DbType.starrocks);
                insertStatement.setLabel(this.exprParser.name());
            } else {
                lexer.reset(mark);
            }
        }
    }

    @Override
    protected void parseInsert0BeforeColumns(SQLInsertInto insertStatement) {
        if (lexer.token() == Token.BY) {
            lexer.nextToken();
            acceptIdentifier("NAME");
            insertStatement.setDbType(DbType.starrocks);
            insertStatement.setByName(true);
        }
    }

    @Override
    public SQLStatement parseRefresh() {
        if (lexer.identifierEquals("REFRESH")) {
            lexer.nextToken();
        }
        acceptIdentifier("MATERIALIZED");
        accept(Token.VIEW);

        SQLRefreshMaterializedViewStatement stmt = new SQLRefreshMaterializedViewStatement(DbType.starrocks);
        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.FORCE || lexer.identifierEquals(FnvHash.Constants.FORCE)) {
            lexer.nextToken();
            stmt.setForce(true);
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            if (lexer.identifierEquals("SYNC")) {
                lexer.nextToken();
                acceptIdentifier("MODE");
                stmt.setSyncMode(true);
            } else if (lexer.identifierEquals("ASYNC")) {
                lexer.nextToken();
                acceptIdentifier("MODE");
                stmt.setAsyncMode(true);
            } else {
                throw new ParserException("syntax error, expect SYNC or ASYNC, actual " + lexer.stringVal() + ", " + lexer.info());
            }
        }

        return stmt;
    }

    @Override
    public SQLStatement parseCreateMaterializedView() {
        accept(Token.CREATE);
        acceptIdentifier("MATERIALIZED");
        accept(Token.VIEW);

        StarRocksCreateMaterializedViewStatement stmt = new StarRocksCreateMaterializedViewStatement();

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            for (; ; ) {
                SQLName col = this.exprParser.name();
                col.setParent(stmt);
                stmt.getColumns().add(col);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            stmt.setComment(this.exprParser.expr());
        }

        // PARTITION BY and DISTRIBUTED BY may appear in either order. Canonical StarRocks DDL
        // places PARTITION BY first, but DISTRIBUTED BY first is also accepted.
        boolean hasPartition = false;
        boolean hasDistributed = false;
        for (; ; ) {
            if (!hasPartition && lexer.token() == Token.PARTITION) {
                stmt.setPartitionBy(parseMvPartitionBy());
                hasPartition = true;
            } else if (!hasDistributed && lexer.identifierEquals(FnvHash.Constants.DISTRIBUTED)) {
                lexer.nextToken();
                accept(Token.BY);
                if (lexer.nextIfIdentifier(FnvHash.Constants.HASH)) {
                    accept(Token.LPAREN);
                    for (; ; ) {
                        SQLName distCol = this.exprParser.name();
                        distCol.setParent(stmt);
                        stmt.getDistributedBy().add(distCol);
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                }
                if (lexer.nextIfIdentifier(FnvHash.Constants.BUCKETS)) {
                    stmt.setBuckets(this.exprParser.expr());
                }
                hasDistributed = true;
            } else {
                break;
            }
        }

        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = this.exprParser.parseOrderBy();
            stmt.setOrderBy(orderBy);
        }

        if (lexer.identifierEquals("REFRESH")) {
            lexer.nextToken();
            boolean hasRefreshModifier = false;

            if (lexer.token() == Token.IMMEDIATE || lexer.identifierEquals("IMMEDIATE")) {
                lexer.nextToken();
                stmt.setRefreshImmediate(true);
                hasRefreshModifier = true;
            } else if (lexer.token() == Token.DEFERRED || lexer.identifierEquals("DEFERRED")) {
                lexer.nextToken();
                stmt.setRefreshDeferred(true);
                hasRefreshModifier = true;
            }

            if (lexer.identifierEquals("ASYNC")) {
                lexer.nextToken();
                stmt.setRefreshAsync(true);
                hasRefreshModifier = true;
            } else if (lexer.identifierEquals("MANUAL")) {
                lexer.nextToken();
                stmt.setRefreshManual(true);
                hasRefreshModifier = true;
            }

            if (lexer.identifierEquals(FnvHash.Constants.START)) {
                lexer.nextToken();
                accept(Token.LPAREN);
                stmt.setRefreshStart(this.exprParser.expr());
                accept(Token.RPAREN);
                hasRefreshModifier = true;
            }

            if (lexer.identifierEquals("EVERY")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                stmt.setRefreshEvery(this.exprParser.expr());
                accept(Token.RPAREN);
                hasRefreshModifier = true;
            }

            if (!hasRefreshModifier) {
                throw new ParserException("syntax error, expected ASYNC/MANUAL/IMMEDIATE/DEFERRED after REFRESH. " + lexer.info());
            }
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                SQLAssignItem prop = this.exprParser.parseAssignItem(true, stmt);
                prop.setParent(stmt);
                stmt.getMvProperties().add(prop);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        accept(Token.AS);
        SQLSelect select = this.createSQLSelectParser().select();
        stmt.setQuery(select);

        return stmt;
    }

    /**
     * Parses the {@code PARTITION BY} clause of a StarRocks materialized view.
     * <p>
     * StarRocks async MVs are partitioned by an <em>expression</em> or a bare column with NO
     * trailing partition-definition list, e.g. {@code PARTITION BY date_trunc('day', dt)},
     * {@code PARTITION BY (dt)}, or {@code PARTITION BY dt}. The shared
     * {@link StarRocksCreateTableParser#parsePartitionBy()} unconditionally requires a trailing
     * {@code (...)} definition list, so it cannot parse these MV forms. Here we delegate to it only
     * for the explicit {@code RANGE(...)}/{@code LIST(...)} forms (which do carry a definition list)
     * and otherwise parse the bare expression/column keys directly.
     */
    protected SQLPartitionBy parseMvPartitionBy() {
        Lexer.SavePoint mark = lexer.markOut();
        accept(Token.PARTITION);
        accept(Token.BY);

        if (lexer.identifierEquals(FnvHash.Constants.RANGE) || lexer.identifierEquals(FnvHash.Constants.LIST)) {
            // Explicit RANGE(...)/LIST(...) with a partition-definition list — reuse the table parser.
            lexer.reset(mark);
            return getSQLCreateTableParser().parsePartitionBy();
        }

        // Bare expression / column key form, with no trailing partition-definition list.
        SQLPartitionByRange partitionBy = new SQLPartitionByRange();
        boolean parens = lexer.nextIf(Token.LPAREN);
        for (; ; ) {
            partitionBy.addColumn(this.exprParser.expr());
            if (lexer.nextIf(Token.COMMA)) {
                continue;
            }
            break;
        }
        if (parens) {
            accept(Token.RPAREN);
        }
        // A trailing (...) partition-definition list is optional for MVs; consume it if present.
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                partitionBy.addPartition(this.getExprParser().parsePartition());
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }
        return partitionBy;
    }

    protected SQLStatement parseSubmitTask() {
        StarRocksSubmitTaskStatement stmt = new StarRocksSubmitTaskStatement();

        acceptIdentifier("SUBMIT");
        acceptIdentifier("TASK");

        if (lexer.token() == Token.IDENTIFIER
                && !lexer.identifierEquals(FnvHash.Constants.SCHEDULE)
                && !lexer.identifierEquals(FnvHash.Constants.PROPERTIES)) {
            stmt.setName(this.exprParser.name());
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.SCHEDULE)) {
            boolean hasSchedule = false;
            if (lexer.nextIfIdentifier(FnvHash.Constants.START)) {
                accept(Token.LPAREN);
                stmt.setScheduleStart(this.exprParser.expr());
                accept(Token.RPAREN);
                hasSchedule = true;
            }
            if (lexer.nextIfIdentifier(FnvHash.Constants.EVERY)) {
                accept(Token.LPAREN);
                stmt.setScheduleEvery(this.exprParser.expr());
                accept(Token.RPAREN);
                hasSchedule = true;
            }
            if (!hasSchedule) {
                throw new ParserException("syntax error, expected START or EVERY after SCHEDULE, " + lexer.info());
            }
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        accept(Token.AS);
        if (lexer.token() == Token.EOF) {
            throw new ParserException("syntax error, expected statement after AS, " + lexer.info());
        }
        SQLStatement body = this.parseStatement();
        stmt.setBody(body);

        return stmt;
    }

    @Override
    public SQLStatement parseCreate() {
        Lexer.SavePoint mark = lexer.markOut();
        accept(Token.CREATE);

        // Skip past OR REPLACE / EXTERNAL so the dispatch checks below land on the object keyword.
        // Each sub-parser does lexer.reset(mark) and re-parses these tokens itself.
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.CATALOG)) {
            lexer.reset(mark);
            return parseCreateCatalog();
        }

        if (lexer.identifierEquals(FnvHash.Constants.RESOURCE)) {
            lexer.reset(mark);
            return createResource();
        }

        if (lexer.identifierEquals("PIPE")) {
            lexer.reset(mark);
            return parseCreatePipe();
        }

        if (lexer.identifierEquals("DICTIONARY")) {
            lexer.reset(mark);
            return parseCreateDictionary();
        }

        if (lexer.identifierEquals("STORAGE")) {
            lexer.reset(mark);
            return parseCreateStorageVolume();
        }

        if (lexer.identifierEquals(FnvHash.Constants.ROUTINE)) {
            lexer.reset(mark);
            return parseCreateRoutineLoad();
        }

        lexer.reset(mark);
        return super.parseCreate();
    }

    @Override
    public SQLStatement parseCreateExternalCatalog() {
        return parseCreateCatalog();
    }

    protected SQLStatement parseCreateCatalog() {
        StarRocksCreateCatalogStatement stmt = new StarRocksCreateCatalogStatement();

        accept(Token.CREATE);
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            stmt.setOrReplace(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            acceptIdentifier("EXTERNAL");
            stmt.setExternal(true);
        }

        acceptIdentifier("CATALOG");

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            stmt.setComment(this.exprParser.expr());
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }

    protected SQLStatement parseCreatePipe() {
        StarRocksCreatePipeStatement stmt = new StarRocksCreatePipeStatement();

        accept(Token.CREATE);
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            stmt.setOrReplace(true);
        }

        acceptIdentifier("PIPE");

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        accept(Token.AS);
        if (lexer.token() == Token.EOF) {
            throw new ParserException("syntax error, expected statement after AS, " + lexer.info());
        }
        SQLStatement body = this.parseStatement();
        stmt.setBody(body);

        return stmt;
    }

    protected SQLStatement parseCreateDictionary() {
        StarRocksCreateDictionaryStatement stmt = new StarRocksCreateDictionaryStatement();

        accept(Token.CREATE);
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            stmt.setOrReplace(true);
        }
        acceptIdentifier("DICTIONARY");

        stmt.setName(this.exprParser.name());
        accept(Token.USING);
        stmt.setSourceTable(this.exprParser.name());

        accept(Token.LPAREN);
        for (; ; ) {
            if (lexer.token() == Token.RPAREN) {
                break;
            }
            SQLAssignItem item = new SQLAssignItem();
            item.setTarget(this.exprParser.name());
            if (lexer.token() == Token.RPAREN || lexer.token() == Token.COMMA) {
                throw new ParserException("syntax error, expected mapping value after column name, " + lexer.info());
            }
            SQLExpr value = this.exprParser.primary();
            item.setValue(value);
            stmt.addColumnMapping(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
            }
        }
        accept(Token.RPAREN);

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }

    protected SQLStatement parseCreateStorageVolume() {
        StarRocksCreateStorageVolumeStatement stmt = new StarRocksCreateStorageVolumeStatement();

        accept(Token.CREATE);
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            stmt.setOrReplace(true);
        }
        acceptIdentifier("STORAGE");
        acceptIdentifier("VOLUME");

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());

        // TYPE = S3|HDFS|...
        acceptIdentifier("TYPE");
        accept(Token.EQ);
        stmt.setType(this.exprParser.expr());

        // LOCATIONS = ('s3://...')
        acceptIdentifier("LOCATIONS");
        accept(Token.EQ);
        accept(Token.LPAREN);
        for (; ; ) {
            stmt.addLocation(this.exprParser.expr());
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            stmt.setComment(this.exprParser.expr());
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }

    protected SQLStatement parseLoadLabel() {
        StarRocksLoadStatement stmt = new StarRocksLoadStatement();

        acceptIdentifier("LOAD");
        acceptIdentifier("LABEL");
        stmt.setLabel(this.exprParser.name());

        accept(Token.LPAREN);
        for (; ; ) {
            if (lexer.token() == Token.RPAREN) {
                break;
            }
            StarRocksLoadStatement.DataDescription desc = new StarRocksLoadStatement.DataDescription();
            acceptIdentifier("DATA");
            acceptIdentifier("INFILE");
            accept(Token.LPAREN);
            for (; ; ) {
                desc.addFilePath(this.exprParser.expr());
                if (lexer.token() != Token.COMMA) {
                    break;
                }
                lexer.nextToken();
            }
            accept(Token.RPAREN);

            accept(Token.INTO);
            accept(Token.TABLE);
            desc.setTableName(this.exprParser.name());

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                accept(Token.LPAREN);
                for (; ; ) {
                    desc.addPartition(this.exprParser.name());
                    if (lexer.token() != Token.COMMA) {
                        break;
                    }
                    lexer.nextToken();
                }
                accept(Token.RPAREN);
            }

            if (lexer.identifierEquals("COLUMNS")) {
                lexer.nextToken();
                acceptIdentifier("TERMINATED");
                accept(Token.BY);
                desc.setColumnTerminatedBy(parseLoadValue());
            }

            if (lexer.identifierEquals("FORMAT")) {
                lexer.nextToken();
                accept(Token.AS);
                desc.setFormat(parseLoadValue());
            }

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                for (; ; ) {
                    desc.addColumn(this.exprParser.expr());
                    if (lexer.token() != Token.COMMA) {
                        break;
                    }
                    lexer.nextToken();
                }
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.SET) {
                lexer.nextToken();
                accept(Token.LPAREN);
                for (; ; ) {
                    desc.addColumnMapping(this.exprParser.parseAssignItem(true, stmt));
                    if (lexer.token() != Token.COMMA) {
                        break;
                    }
                    lexer.nextToken();
                }
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                desc.setWhereCondition(this.exprParser.expr());
            }

            stmt.addDataDescription(desc);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
            }
        }
        accept(Token.RPAREN);

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            acceptIdentifier("BROKER");
            // Optional broker name as a string literal: WITH BROKER "my_broker" (...).
            // StarRocksLoadStatement has no brokerName field, so the name is consumed but not
            // stored (it would otherwise be dropped silently and abort the parse on the next
            // token). TODO: add a brokerName field to StarRocksLoadStatement to preserve it.
            if (lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS) {
                lexer.nextToken();
            }
            if (lexer.token() == Token.LPAREN) {
                accept(Token.LPAREN);
                for (; ; ) {
                    if (lexer.token() == Token.RPAREN) {
                        break;
                    }
                    stmt.addBrokerProperty(this.exprParser.parseAssignItem(true, stmt));
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                    }
                }
                accept(Token.RPAREN);
            }
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }

    /**
     * Reads a single delimiter/format value for Broker Load (COLUMNS TERMINATED BY / FORMAT AS).
     * Must NOT use {@code expr()}: in StarRocks the column mapping list {@code (c1, c2, ...)} may
     * immediately follow the value, and {@code expr()} would greedily absorb it as a method-invoke
     * (turning the value into the callee), losing the column list. Reading a single literal token
     * keeps the trailing {@code (...)} available for the column-list parser below.
     */
    protected SQLExpr parseLoadValue() {
        if (lexer.token() == Token.LITERAL_CHARS) {
            // single-quoted: stringVal() is already unquoted, e.g. ',' -> ,
            SQLCharExpr value = new SQLCharExpr(lexer.stringVal());
            lexer.nextToken();
            return value;
        }
        if (lexer.token() == Token.LITERAL_ALIAS) {
            // double-quoted: StarRocks keeps the surrounding quotes in stringVal(), e.g. "," -> ","
            String text = lexer.stringVal();
            if (text.length() >= 2 && text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"') {
                text = text.substring(1, text.length() - 1);
            }
            SQLCharExpr value = new SQLCharExpr(text);
            lexer.nextToken();
            return value;
        }
        // hex / other primary literal — primary() does not consume a following (...) as arguments
        return this.exprParser.primary();
    }

    protected SQLStatement parseCreateRoutineLoad() {
        StarRocksCreateRoutineLoadStatement stmt = new StarRocksCreateRoutineLoadStatement();

        accept(Token.CREATE);
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            stmt.setOrReplace(true);
        }
        acceptIdentifier("ROUTINE");
        acceptIdentifier("LOAD");
        stmt.setName(this.exprParser.name());
        accept(Token.ON);
        stmt.setTableName(this.exprParser.name());

        if (lexer.identifierEquals("COLUMNS")) {
            Lexer.SavePoint mark = lexer.markOut();
            lexer.nextToken();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                for (; ; ) {
                    stmt.addColumn(this.exprParser.expr());
                    if (lexer.token() != Token.COMMA) {
                        break;
                    }
                    lexer.nextToken();
                }
                accept(Token.RPAREN);
            } else {
                lexer.reset(mark);
            }
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            stmt.setWhereCondition(this.exprParser.expr());
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            stmt.setDataSourceType(this.exprParser.name());
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addDataSourceProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }

    protected SQLStatement parseBackup() {
        StarRocksBackupStatement stmt = new StarRocksBackupStatement();

        acceptIdentifier("BACKUP");
        acceptIdentifier("SNAPSHOT");
        stmt.setSnapshotName(this.exprParser.name());

        accept(Token.TO);
        stmt.setRepository(this.exprParser.name());

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            accept(Token.LPAREN);
            for (; ; ) {
                stmt.addOnTable(this.exprParser.expr());
                if (lexer.token() != Token.COMMA) {
                    break;
                }
                lexer.nextToken();
            }
            accept(Token.RPAREN);
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }

    protected SQLStatement parseRestore() {
        StarRocksRestoreStatement stmt = new StarRocksRestoreStatement();

        acceptIdentifier("RESTORE");
        acceptIdentifier("SNAPSHOT");
        stmt.setSnapshotName(this.exprParser.name());

        accept(Token.FROM);
        stmt.setRepository(this.exprParser.name());

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            accept(Token.LPAREN);
            for (; ; ) {
                stmt.addOnTable(this.exprParser.expr());
                if (lexer.token() != Token.COMMA) {
                    break;
                }
                lexer.nextToken();
            }
            accept(Token.RPAREN);
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }

    protected SQLStatement createResource() {
        StarRocksCreateResourceStatement stmt = new StarRocksCreateResourceStatement();
        accept(Token.CREATE);
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            stmt.setOrReplace(true);
        }
        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            acceptIdentifier("EXTERNAL");
            stmt.setExternal(true);
        }

        acceptIdentifier("RESOURCE");

        stmt.setName(this.exprParser.name());
        acceptIdentifier("PROPERTIES");
        accept(Token.LPAREN);

        for (; ; ) {
            if (lexer.token() == Token.RPAREN) {
                accept(Token.RPAREN);
                break;
            }

            stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
            if (lexer.token() == Token.COMMA) {
                accept(Token.COMMA);
            }
        }

        return stmt;
    }

}
