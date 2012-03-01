package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.dialect.oracle.ast.clause.StorageItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelect;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleCreateTableParser extends SQLCreateTableParser {

    public OracleCreateTableParser(Lexer lexer){
        super(lexer);
        this.exprParser = new OracleExprParser(lexer);
    }

    public OracleCreateTableParser(String sql){
        super(new OracleLexer(sql));
        this.lexer.nextToken();
        this.exprParser = new OracleExprParser(lexer);
    }

    protected OracleCreateTableStatement newCreateStatement() {
        return new OracleCreateTableStatement();
    }

    public OracleCreateTableStatement parseCrateTable(boolean acceptCreate) {
        OracleCreateTableStatement stmt = (OracleCreateTableStatement) super.parseCrateTable(acceptCreate);

        for (;;) {
            if (identifierEquals("TABLESPACE")) {
                lexer.nextToken();
                stmt.setTablespace(this.exprParser.name());
            } else if (identifierEquals("IN_MEMORY_METADATA")) {
                lexer.nextToken();
                stmt.setInMemoryMetadata(true);
                continue;
            } else if (identifierEquals("CURSOR_SPECIFIC_SEGMENT")) {
                lexer.nextToken();
                stmt.setCursorSpecificSegment(true);
                continue;
            } else if (identifierEquals("NOPARALLEL")) {
                lexer.nextToken();
                stmt.setParallel(false);
                continue;
            } else if (identifierEquals("STORAGE")) {
                lexer.nextToken();
                accept(Token.LPAREN);

                for (;;) {
                    if (lexer.token() == Token.IDENTIFIER) {
                        StorageItem item = new StorageItem();
                        item.setName(this.exprParser.name());
                        item.setValue(this.exprParser.expr());
                        stmt.getStorage().add(item);
                    } else {
                        break;
                    }
                }
                accept(Token.RPAREN);
                continue;
            }
            break;
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            OracleSelect select = new OracleSelectParser(lexer).select();
            stmt.setSelect(select);
        }

        return stmt;
    }
}
