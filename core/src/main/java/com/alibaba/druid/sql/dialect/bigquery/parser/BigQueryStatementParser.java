package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.sql.ast.SQLDeclareItem;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryAssertStatement;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryCreateModelStatement;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryExecuteImmediateStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

public class BigQueryStatementParser extends SQLStatementParser {
    public BigQueryStatementParser(String sql) {
        super(new BigQueryExprParser(sql));
    }

    public BigQueryStatementParser(String sql, SQLParserFeature... features) {
        super(new BigQueryExprParser(sql, features));
    }

    public BigQueryStatementParser(Lexer lexer) {
        super(new BigQueryExprParser(lexer));
    }

    public BigQuerySelectParser createSQLSelectParser() {
        return new BigQuerySelectParser(this.exprParser, selectListCache);
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new BigQueryCreateTableParser(this.exprParser);
    }

    @Override
    public SQLCreateFunctionStatement parseCreateFunction() {
        SQLCreateFunctionStatement createFunction = new SQLCreateFunctionStatement();
        accept(Token.CREATE);
        if (lexer.nextIfIdentifier("TEMP")
                || lexer.nextIfIdentifier(FnvHash.Constants.TEMPORARY)) {
            createFunction.setTemporary(true);
        }
        accept(Token.FUNCTION);
        createFunction.setName(
                this.exprParser.name());

        parameters(createFunction.getParameters(), createFunction);
        if (lexer.nextIfIdentifier(FnvHash.Constants.RETURNS)) {
            createFunction.setReturnDataType(
                    this.exprParser.parseDataType()
            );
        }

        for (;;) {
            if (lexer.nextIfIdentifier("LANGUAGE")) {
                createFunction.setLanguage(
                        lexer.stringVal()
                );
                accept(Token.IDENTIFIER);
                continue;
            }

            if (lexer.nextIfIdentifier(FnvHash.Constants.OPTIONS)) {
                exprParser.parseAssignItem(createFunction.getOptions(), createFunction);
                continue;
            }

            if (lexer.nextIf(Token.AS)) {
                if (lexer.nextIf(Token.LPAREN)) {
                    createFunction.setBlock(
                            new SQLExprStatement(
                                    this.exprParser.expr()));
                    accept(Token.RPAREN);
                } else {
                    lexer.nextIfIdentifier("R");
                    String script = lexer.stringVal();
                    if (script.startsWith("\"") && script.endsWith("\"")) {
                        script = script.substring(1, script.length() - 1);
                    }
                    createFunction.setWrappedSource(
                            script
                    );
                    if (lexer.token() == Token.LITERAL_TEXT_BLOCK || lexer.token() == Token.LITERAL_CHARS) {
                        lexer.nextToken();
                    } else {
                        setErrorEndPos(lexer.pos());
                        printError(lexer.token());
                    }
                }
                continue;
            }

            break;
        }

        if (lexer.nextIf(Token.SEMI)) {
            createFunction.setAfterSemi(true);
        }
        return createFunction;
    }

    public SQLStatement parseDeclare() {
        accept(Token.DECLARE);
        SQLDeclareStatement declareStatement = new SQLDeclareStatement();
        for (; ; ) {
            SQLDeclareItem item = new SQLDeclareItem();
            item.setName(exprParser.name());
            declareStatement.addItem(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
            } else if (lexer.token() != Token.EOF) {
                item.setDataType(exprParser.parseDataType());
                if (lexer.nextIf(Token.DEFAULT)) {
                    item.setValue(exprParser.expr());
                }
                break;
            } else {
                throw new ParserException("TODO. " + lexer.info());
            }
        }
        return declareStatement;
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.identifierEquals("ASSERT")) {
            statementList.add(parseAssert());
            return true;
        }
        if (lexer.token() == Token.BEGIN) {
            statementList.add(parseBlock());
            return true;
        }
        if (lexer.token() == Token.RAISE) {
            statementList.add(parseRaise());
            return true;
        }
        return false;
    }

    @Override
    public SQLStatement parseExecute() {
        acceptIdentifier(FnvHash.Constants.EXECUTE);
        acceptIdentifier("IMMEDIATE");

        BigQueryExecuteImmediateStatement stmt = new BigQueryExecuteImmediateStatement();
        stmt.setDynamicSql(
                this.exprParser.expr()
        );
        if (lexer.nextIf(Token.INTO)) {
            this.exprParser.exprList(stmt.getInto(), stmt);
        }
        if (lexer.nextIf(Token.USING)) {
            for (;;) {
                SQLExpr expr = this.exprParser.expr();
                String alias = null;
                if (lexer.nextIf(Token.AS)) {
                    alias = lexer.stringVal();
                    lexer.nextToken();
                }
                stmt.addUsing(expr, alias);
                if (lexer.nextIf(Token.COMMA)) {
                    continue;
                }
                break;
            }
        }
        return stmt;
    }

    public SQLStatement parseRaise() {
        accept(Token.RAISE);
        SQLRaiseStatement sqlRaiseStatement = new SQLRaiseStatement();
        if (lexer.nextIf(Token.USING)) {
            acceptIdentifier("MESSAGE");
            accept(Token.EQ);
            sqlRaiseStatement.setMessage(exprParser.expr());
        }

        return sqlRaiseStatement;
    }

    protected SQLStatement parseAssert() {
        acceptIdentifier("ASSERT");
        BigQueryAssertStatement stmt = new BigQueryAssertStatement();
        stmt.setExpr(
                exprParser.expr()
        );
        if (lexer.nextIf(Token.AS)) {
            stmt.setAs((SQLCharExpr) exprParser.primary());
        }
        return stmt;
    }

    public SQLDeleteStatement parseDeleteStatement() {
        SQLDeleteStatement deleteStatement = new SQLDeleteStatement(getDbType());

        accept(Token.DELETE);
        lexer.nextIf(Token.FROM);

        SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
        deleteStatement.setTableSource(tableSource);

        if (lexer.nextIf(Token.WHERE)) {
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        return deleteStatement;
    }

    @Override
    protected void mergeBeforeName() {
        this.lexer.nextIf(Token.INTO);
    }

    public SQLStatement parseBlock() {
        accept(Token.BEGIN);
        if (lexer.identifierEquals("TRANSACTION") || lexer.identifierEquals("TRAN")) {
            lexer.nextToken();
            SQLStartTransactionStatement startTrans = new SQLStartTransactionStatement(dbType);
            if (lexer.token() == Token.IDENTIFIER) {
                SQLName name = this.exprParser.name();
                startTrans.setName(name);
            }
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

    protected void createViewAs(SQLCreateViewStatement createView) {
        if (lexer.nextIfIdentifier(FnvHash.Constants.OPTIONS)) {
            exprParser.parseAssignItem(createView.getOptions(), createView);
        }
        super.createViewAs(createView);
    }

    @Override
    protected SQLStatement parseCreateModel() {
        accept(Token.CREATE);
        acceptIdentifier("MODEL");

        BigQueryCreateModelStatement stmt = new BigQueryCreateModelStatement();
        if (lexer.nextIf(Token.IF)) {
           accept(Token.NOT);
           accept(Token.EXISTS);
           stmt.setIfNotExists(true);
        } else if (lexer.nextIf(Token.OR)) {
            accept(Token.REPLACE);
            stmt.setReplace(true);
        }
        stmt.setName(
                exprParser.name()
        );

        if (lexer.nextIfIdentifier("OPTIONS")) {
            exprParser.parseAssignItem(stmt.getOptions(), stmt);
        }

        if (lexer.nextIf(Token.AS)) {
            accept(Token.LPAREN);
            acceptIdentifier("TRAINING_DATA");
            accept(Token.AS);
            accept(Token.LPAREN);
            stmt.setTrainingData(
                    parseStatement0()
            );
            accept(Token.RPAREN);

            accept(Token.COMMA);
            acceptIdentifier("CUSTOM_HOLIDAY");
            accept(Token.AS);
            accept(Token.LPAREN);
            stmt.setCustomHoliday(
                    parseStatement0()
            );
            accept(Token.RPAREN);
            accept(Token.RPAREN);
        }

        return stmt;
    }
}
