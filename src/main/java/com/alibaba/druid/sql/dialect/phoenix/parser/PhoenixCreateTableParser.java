package com.alibaba.druid.sql.dialect.phoenix.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.Token;

/**
 * @author dove at 2020-03-13
 */
public class PhoenixCreateTableParser extends SQLCreateTableParser {
    public PhoenixCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    /**
     * document: http://phoenix.apache.org/language/index.html#create_table
     *
     * @param acceptCreate
     * @return
     */
    @Override
    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        SQLCreateTableStatement createTable = newCreateStatement();

        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                createTable.addBeforeComment(lexer.readAndResetComments());
            }

            accept(Token.CREATE);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IDENTIFIER && lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            createTable.setIfNotExiists(true);
        }

        createTable.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (; ; ) {
                Token token = lexer.token();
                if (token == Token.IDENTIFIER
                        || token == Token.LITERAL_ALIAS) {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    createTable.getTableElementList().add(column);
                } else if (token == Token.CONSTRAINT) {
                    lexer.nextToken();
                    String tbName = lexer.stringVal();
                    lexer.nextToken();
                    accept(Token.PRIMARY);
                    accept(Token.KEY);

                    accept(Token.LPAREN);
                    for (; ; ) {
                        // TODO .. parse Phoenix PRIMARY attribute
                        if (lexer.token() != Token.RPAREN) {
                            lexer.nextToken();
                            continue;
                        }
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            }
                            continue;
                        }
                        break;
                    }
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();

                    if (lexer.token() == Token.RPAREN) {
                        break;
                    }
                    continue;
                }

                break;
            }

            accept(Token.RPAREN);

        }

        for (; ; ) {
            // todo ... add some Phoenix Table attribute
            lexer.nextToken();
            if (lexer.token() == Token.EOF) {
                break;
            }
        }
        return createTable;
    }
}