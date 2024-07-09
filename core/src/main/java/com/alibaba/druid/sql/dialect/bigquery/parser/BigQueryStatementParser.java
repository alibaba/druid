package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

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
        acceptIdentifier("RETURNS");
        createFunction.setReturnDataType(
                this.exprParser.parseDataType()
        );

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
                lexer.nextIfIdentifier("R");
                createFunction.setWrappedSource(
                        lexer.stringVal()
                );
                accept(Token.LITERAL_TEXT_BLOCK);
                continue;
            }

            break;
        }

        if (lexer.nextIf(Token.SEMI)) {
            createFunction.setAfterSemi(true);
        }
        return createFunction;
    }
}
