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
    public SQLCreateTableStatement parseCreateTable() {
        return getSQLCreateTableParser().parseCreateTable();
    }

    @Override
    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.identifierEquals("SUBMIT")) {
            SQLStatement stmt = parseSubmitTask();
            statementList.add(stmt);
            return true;
        }
        return false;
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
                stmt.getColumns().add(this.exprParser.name());
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
                    stmt.getDistributedBy().add(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }
            if (lexer.nextIfIdentifier(FnvHash.Constants.BUCKETS)) {
                lexer.nextToken();
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
            if (lexer.token() == Token.IMMEDIATE || lexer.identifierEquals("IMMEDIATE")) {
                lexer.nextToken();
                stmt.setRefreshImmediate(true);
            } else if (lexer.token() == Token.DEFERRED || lexer.identifierEquals("DEFERRED")) {
                lexer.nextToken();
                stmt.setRefreshDeferred(true);
            }

            if (lexer.identifierEquals("ASYNC")) {
                lexer.nextToken();
                stmt.setRefreshAsync(true);
            } else if (lexer.identifierEquals("MANUAL")) {
                lexer.nextToken();
                stmt.setRefreshManual(true);
            }

            if (lexer.identifierEquals(FnvHash.Constants.START)) {
                lexer.nextToken();
                accept(Token.LPAREN);
                stmt.setRefreshStart(this.exprParser.expr());
                accept(Token.RPAREN);
            }

            if (lexer.identifierEquals("EVERY")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                stmt.setRefreshEvery(this.exprParser.expr());
                accept(Token.RPAREN);
            }
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() == Token.RPAREN) {
                    break;
                }
                stmt.getMvProperties().add(this.exprParser.parseAssignItem(true, stmt));
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
    protected SQLAlterStatement alterTableAfterName(SQLAlterTableStatement stmt) {
        if (lexer.identifierEquals("SWAP")) {
            lexer.nextToken();
            accept(Token.WITH);
            SQLAlterTableSwap swap = new SQLAlterTableSwap();
            swap.setName(this.exprParser.name());
            stmt.addItem(swap);
            return stmt;
        }
        return super.alterTableAfterName(stmt);
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
            SQLExpr role = new SQLIdentifierExpr(lexer.stringVal());
            item.setValue(role);
            lexer.nextToken();
            stmt.getColumnMappings().add(item);
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
            stmt.getLocations().add(this.exprParser.expr());
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

    protected SQLStatement createResource() {
        StarRocksCreateResourceStatement stmt = new StarRocksCreateResourceStatement();
        accept(Token.CREATE);
        // create external source
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
