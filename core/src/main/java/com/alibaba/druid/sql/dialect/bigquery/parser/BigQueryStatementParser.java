package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.sql.ast.SQLDeclareItem;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryAssertStatement;
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
                    createFunction.setWrappedSource(
                            lexer.stringVal()
                    );
                    accept(Token.LITERAL_TEXT_BLOCK);
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

    protected void parseInsert0(SQLInsertInto insertStatement, boolean acceptSubQuery) {
        if (lexer.token() == Token.IDENTIFIER) {
            SQLName tableName = this.exprParser.name();
            insertStatement.setTableName(tableName);

            if (lexer.token() == Token.LITERAL_ALIAS) {
                insertStatement.setAlias(tableAlias());
            }

            parseInsert0Hints(insertStatement, false);

            if (lexer.token() == Token.IDENTIFIER) {
                insertStatement.setAlias(lexer.stringVal());
                lexer.nextToken();
            }
        }

        super.parseInsert0(insertStatement, acceptSubQuery);
    }

    protected void createViewAs(SQLCreateViewStatement createView) {
        if (lexer.nextIfIdentifier(FnvHash.Constants.OPTIONS)) {
            exprParser.parseAssignItem(createView.getOptions(), createView);
        }
        super.createViewAs(createView);
    }
}
