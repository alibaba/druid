package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
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

        if (lexer.nextIfIdentifier(FnvHash.Constants.DISTRIBUTED)) {
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
        }

        if (lexer.token() == Token.PARTITION) {
            SQLPartitionBy partitionBy = getSQLCreateTableParser().parsePartitionBy();
            stmt.setPartitionBy(partitionBy);
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
            if (lexer.nextIfIdentifier(FnvHash.Constants.START)) {
                accept(Token.LPAREN);
                stmt.setScheduleStart(this.exprParser.expr());
                accept(Token.RPAREN);
            }
            if (lexer.nextIfIdentifier(FnvHash.Constants.EVERY)) {
                accept(Token.LPAREN);
                stmt.setScheduleEvery(this.exprParser.expr());
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

        accept(Token.AS);
        SQLStatement body = this.parseStatement();
        stmt.setBody(body);

        return stmt;
    }

    @Override
    public SQLStatement parseCreate() {
        Lexer.SavePoint mark = lexer.markOut();
        accept(Token.CREATE);

        boolean orReplace = false;
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            orReplace = true;
        }

        boolean external = false;
        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            external = true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.CATALOG)) {
            lexer.reset(mark);
            return parseCreateCatalog();
        }

        if (external && lexer.identifierEquals(FnvHash.Constants.RESOURCE)) {
            lexer.reset(mark);
            return createResource();
        }

        if (lexer.identifierEquals("PIPE")) {
            lexer.reset(mark);
            return parseCreatePipe(orReplace);
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

    protected SQLStatement parseCreatePipe(boolean orReplace) {
        StarRocksCreatePipeStatement stmt = new StarRocksCreatePipeStatement();
        stmt.setOrReplace(orReplace);

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
                throw new ParserException("syntax error, expected role identifier after column name, " + lexer.info());
            }
            SQLExpr role = new SQLIdentifierExpr(lexer.stringVal());
            item.setValue(role);
            lexer.nextToken();
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
                desc.setColumnTerminatedBy(this.exprParser.expr());
            }

            if (lexer.identifierEquals("FORMAT")) {
                lexer.nextToken();
                accept(Token.AS);
                desc.setFormat(this.exprParser.expr());
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
            stmt.setDataSourceType(lexer.stringVal());
            lexer.nextToken();
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
