package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.parser.*;

public class ClickhouseStatementParser extends SQLStatementParser {
    public ClickhouseStatementParser(String sql) {
        super (new ClickhouseExprParser(sql));
    }

    public ClickhouseStatementParser(String sql, SQLParserFeature... features) {
        super (new ClickhouseExprParser(sql, features));
    }

    public ClickhouseStatementParser(Lexer lexer){
        super(new ClickhouseExprParser(lexer));
    }


    @Override
    public SQLWithSubqueryClause parseWithQuery() {
        SQLWithSubqueryClause withQueryClause = new SQLWithSubqueryClause();
        if (lexer.hasComment() && lexer.isKeepComments()) {
            withQueryClause.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.WITH);

        for (; ; ) {
            SQLWithSubqueryClause.Entry entry = new SQLWithSubqueryClause.Entry();
            entry.setParent(withQueryClause);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                switch (lexer.token()) {
                    case VALUES:
                    case WITH:
                    case SELECT:
                        entry.setSubQuery(
                                this.createSQLSelectParser()
                                        .select());
                        break;
                    default:
                        break;
                }
                accept(Token.RPAREN);

            } else {
                entry.setExpr(exprParser.expr());
            }

            accept(Token.AS);
            String alias = this.lexer.stringVal();
            lexer.nextToken();
            entry.setAlias(alias);

            withQueryClause.addEntry(entry);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return withQueryClause;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new ClickhouseCreateTableParser(this.exprParser);
    }
}
