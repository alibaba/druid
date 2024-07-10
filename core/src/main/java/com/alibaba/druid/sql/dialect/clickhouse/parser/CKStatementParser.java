package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKAlterTableUpdateStatement;
import com.alibaba.druid.sql.parser.*;

import static com.alibaba.druid.sql.parser.Token.ALTER;
import static com.alibaba.druid.sql.parser.Token.TABLE;

public class CKStatementParser extends SQLStatementParser {
    public CKStatementParser(String sql) {
        super(new CKExprParser(sql));
    }

    public CKStatementParser(String sql, SQLParserFeature... features) {
        super(new CKExprParser(sql, features));
    }

    public CKStatementParser(Lexer lexer) {
        super(new CKExprParser(lexer));
    }

    public SQLSelectParser createSQLSelectParser() {
        return new CKSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public SQLWithSubqueryClause parseWithQuery() {
        return this.createSQLSelectParser().parseWith();
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new CKCreateTableParser(this.exprParser);
    }

    protected SQLAlterStatement alterTable() {
        Lexer.SavePoint mark = lexer.mark();
        accept(ALTER);
        accept(TABLE);

        SQLName tableName = this.exprParser.name();
        SQLName clusterName = null;
        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            acceptIdentifier("CLUSTER");
            clusterName = this.exprParser.name();
        }

        if (lexer.token() == Token.UPDATE) {
            CKAlterTableUpdateStatement stmt = new CKAlterTableUpdateStatement(getDbType());
            stmt.setTableName(tableName);
            stmt.setClusterName(clusterName);
            lexer.nextToken();
            for (; ; ) {
                SQLUpdateSetItem item = this.exprParser.parseUpdateSetItem();
                stmt.getItems().add(item);
                if (lexer.token() != Token.COMMA) {
                    break;
                }
                lexer.nextToken();
            }
            SQLName partitionId = null;
            if (lexer.token() == Token.IN) {
                lexer.nextToken();
                accept(Token.PARTITION);
                partitionId = this.exprParser.name();
            }
            stmt.setPartitionId(partitionId);
            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }
            return stmt;
        }

        lexer.reset(mark);
        return super.alterTable();
    }
}
