package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQueryCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class BigQueryCreateTableParser extends SQLCreateTableParser {
    public BigQueryCreateTableParser(String sql) {
        super(new BigQueryExprParser(sql));
    }

    public BigQueryCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    protected SQLCreateTableStatement newCreateStatement() {
        return new BigQueryCreateTableStatement();
    }

    public SQLSelectParser createSQLSelectParser() {
        return new BigQuerySelectParser(this.exprParser, selectListCache);
    }

    protected void parseCreateTableRest(SQLCreateTableStatement x) {
        BigQueryCreateTableStatement stmt = (BigQueryCreateTableStatement) x;
        for (;;) {
            if (lexer.nextIf(Token.DEFAULT)) {
                acceptIdentifier("COLLATE");
                SQLExpr collate = exprParser.expr();
                stmt.setCollate(collate);
            }

            if (lexer.nextIf(Token.PARTITION)) {
                accept(Token.BY);

                this.exprParser.exprList(stmt.getPartitionBy(), stmt);
                continue;
            }

            if (lexer.nextIfIdentifier("CLUSTER")) {
                accept(Token.BY);
                for (;;) {
                    SQLSelectOrderByItem item = exprParser.parseSelectOrderByItem();
                    item.setParent(stmt);
                    stmt.getClusteredBy().add(item);
                    if (lexer.nextIf(Token.COMMA)) {
                        continue;
                    }
                    break;
                }
                continue;
            }

            if (lexer.nextIfIdentifier("OPTIONS")) {
                exprParser.parseAssignItem(stmt.getTableOptions(), stmt);
                continue;
            }

            if (lexer.nextIfIdentifier("CLONE")) {
                stmt.setLike(exprParser.name());
                continue;
            }

            if (lexer.nextIf(Token.COMMENT)) {
                SQLExpr comment = this.exprParser.expr();
                stmt.setComment(comment);
                continue;
            }

            if (lexer.nextIfIdentifier(FnvHash.Constants.LIFECYCLE)) {
                lexer.nextIf(Token.EQ);
                stmt.setLifeCycle(this.exprParser.primary());

                continue;
            }

            if (lexer.nextIf(Token.AS)) {
                stmt.setSelect(
                        this.createSQLSelectParser().select()
                );
                continue;
            }

            break;
        }
    }

    protected void createTableBefore(SQLCreateTableStatement createTable) {
        if (lexer.nextIfIdentifier("TEMPORARY") || lexer.nextIfIdentifier("TEMP")) {
            createTable.setTemporary(true);
        } else if (lexer.nextIfIdentifier(FnvHash.Constants.EXTERNAL)) {
            createTable.setExternal(true);
        }

        if (lexer.nextIf(Token.OR)) {
            accept(Token.REPLACE);
            createTable.config(SQLCreateTableStatement.Feature.OrReplace);
        }

        if (lexer.nextIfIdentifier("TEMPORARY") || lexer.nextIfIdentifier("TEMP")) {
            createTable.setTemporary(true);
        }
    }

    @Override
    protected SQLSelect createTableQueryRest() {
        return new BigQuerySelectParser(this.exprParser, selectListCache).select();
    }
}
