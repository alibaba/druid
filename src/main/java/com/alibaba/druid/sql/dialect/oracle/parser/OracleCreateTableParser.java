package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
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
                continue;
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
            } else if (identifierEquals("LOGGING")) {
                lexer.nextToken();
                stmt.setLogging(Boolean.TRUE);
                continue;
            } else if (identifierEquals("NOCOMPRESS")) {
                lexer.nextToken();
                stmt.setCompress(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.ON) {
                lexer.nextToken();
                accept(Token.COMMIT);
                stmt.setOnCommit(true);
                continue;
            } else if (identifierEquals("PRESERVE")) {
                lexer.nextToken();
                acceptIdentifier("ROWS");
                stmt.setPreserveRows(true);
                continue;
            } else if (identifierEquals("STORAGE")) {
                lexer.nextToken();
                accept(Token.LPAREN);

                OracleStorageClause storage = new OracleStorageClause();
                for (;;) {
                    if (identifierEquals("INITIAL")) {
                        lexer.nextToken();
                        storage.setInitial(this.exprParser.expr());
                        continue;
                    } else if (identifierEquals("FREELISTS")) {
                        lexer.nextToken();
                        storage.setFreeLists(this.exprParser.expr());
                        continue;
                    } else if (identifierEquals("FREELIST")) {
                        lexer.nextToken();
                        acceptIdentifier("GROUPS");
                        storage.setFreeListGroups(this.exprParser.expr());
                        continue;
                    } else if (identifierEquals("BUFFER_POOL")) {
                        lexer.nextToken();
                        storage.setBufferPool(this.exprParser.expr());
                        continue;
                    } else if (identifierEquals("OBJNO")) {
                        lexer.nextToken();
                        storage.setObjno(this.exprParser.expr());
                        continue;
                    }

                    break;
                }
                accept(Token.RPAREN);
                stmt.setStorage(storage);
                continue;
            } else if (identifierEquals("organization")) {
                lexer.nextToken();
                accept(Token.INDEX);
                stmt.setOrganizationIndex(true);
                continue;
            } else if (identifierEquals("PCTFREE")) {
                lexer.nextToken();
                stmt.setPtcfree(this.exprParser.expr());
                continue;
            } else if (identifierEquals("PCTUSED")) {
                lexer.nextToken();
                stmt.setPctused(this.exprParser.expr());
                continue;
            } else if (identifierEquals("INITRANS")) {
                lexer.nextToken();
                stmt.setInitrans(this.exprParser.expr());
                continue;
            } else if (identifierEquals("MAXTRANS")) {
                lexer.nextToken();
                stmt.setMaxtrans(this.exprParser.expr());
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
