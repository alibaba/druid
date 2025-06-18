package com.alibaba.druid.sql.dialect.synapse.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.synapse.ast.stmt.SynapseCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class SynapseCreateTableParser extends SQLCreateTableParser {
    public SynapseCreateTableParser(String sql) {
        super(sql);
        this.dbType = DbType.synapse;
    }

    public SynapseCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        this.dbType = DbType.synapse;
    }

    @Override
    protected SynapseCreateTableStatement newCreateStatement() {
        return new SynapseCreateTableStatement();
    }

    @Override
    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        if (stmt instanceof SynapseCreateTableStatement) {
            SynapseCreateTableStatement synapseStmt = (SynapseCreateTableStatement) stmt;

            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                accept(Token.LPAREN);

                parseSynapseWithOptions(synapseStmt);

                accept(Token.RPAREN);
            }
        }

        super.parseCreateTableRest(stmt);
    }

    private void parseSynapseWithOptions(SynapseCreateTableStatement stmt) {
        for (;;) {
            if (lexer.identifierEquals("DISTRIBUTION")) {
                lexer.nextToken();
                accept(Token.EQ);

                if (lexer.identifierEquals("HASH")) {
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    SQLExpr distributionColumn = this.exprParser.expr();
                    stmt.setDistribution(distributionColumn);
                    stmt.setDistributionHash(true);
                    accept(Token.RPAREN);
                } else if (lexer.identifierEquals("ROUND_ROBIN")) {
                    lexer.nextToken();
                    stmt.setDistributionHash(false);
                } else if (lexer.identifierEquals("REPLICATE")) {
                    lexer.nextToken();
                    stmt.setDistributionHash(false);
                }
            } else if (lexer.identifierEquals("CLUSTERED")) {
                lexer.nextToken();
                accept(Token.INDEX);
                accept(Token.LPAREN);

                for (;;) {
                    SQLExpr column = this.exprParser.expr();
                    stmt.getClusteredIndexColumns().add(column);

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }

                accept(Token.RPAREN);
            } else {
                break;
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
    }
}
