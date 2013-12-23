package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsCreateTableParser extends SQLCreateTableParser {

    public OdpsCreateTableParser(String sql){
        super(new OdpsExprParser(sql));
    }

    public OdpsCreateTableParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLCreateTableStatement parseCrateTable(boolean acceptCreate) {
        OdpsCreateTableStatement stmt = new OdpsCreateTableStatement();
        
        if (acceptCreate) {
            accept(Token.CREATE);
        }
        
        accept(Token.TABLE);

        if (lexer.token() == Token.IF || identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExiists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setLike(name);
        } else {
            accept(Token.LPAREN);
            
            for (;;) {
                if (lexer.token() != Token.IDENTIFIER) {
                    throw new ParserException("expect identifier");
                }
                
                SQLColumnDefinition column = this.exprParser.parseColumn();
                stmt.getTableElementList().add(column);
                
                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }
        
        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            stmt.setComment(this.exprParser.primary());
        }
        
        if (lexer.token() == Token.PARTITIONED) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);
            
            for (;;) {
                if (lexer.token() != Token.IDENTIFIER) {
                    throw new ParserException("expect identifier");
                }
                
                SQLColumnDefinition column = this.exprParser.parseColumn();
                stmt.getPartitionColumns().add(column);
                
                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();
                }
            }
            
            accept(Token.RPAREN);
        }
        
        return stmt;
    }
}
