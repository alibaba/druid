package com.alibaba.druid.sql.dialect.saphana.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaDeleteStatement;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaInsertStatement;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaUpdateStatement;
import com.alibaba.druid.sql.parser.*;

/**
 * @author nukiyoam
 */
public class SAPHanaStatementParser extends SQLStatementParser {
    public SAPHanaStatementParser(String sql) {
        super(new SAPHanaExprParser(sql));
    }

    public SAPHanaStatementParser(String sql, SQLParserFeature... features) {
        super(new SAPHanaExprParser(sql, features));
    }

    public SAPHanaStatementParser(Lexer lexer) {
        super(new SAPHanaExprParser(lexer));
    }

    @Override
    public SQLSelectParser createSQLSelectParser() {
        return new SAPHanaSelectParser(exprParser, selectListCache);
    }

    @Override
    protected SQLUpdateStatement createUpdateStatement() {
        return new SAPHanaUpdateStatement();
    }

    @Override
    public SQLStatement parseInsert() {
        SAPHanaInsertStatement stmt = new SAPHanaInsertStatement();

        if (lexer.token() == Token.INSERT) {
            accept(Token.INSERT);
        }

        parseInsert0(stmt);
        return stmt;
    }

    @Override
    public SQLDeleteStatement parseDeleteStatement() {
        SAPHanaDeleteStatement deleteStatement = new SAPHanaDeleteStatement();

        if (lexer.token() == Token.DELETE) {
            lexer.nextToken();
            if (lexer.token() == (Token.FROM)) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            SQLName tableName = exprParser.name();

            deleteStatement.setTableName(tableName);

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
                deleteStatement.setFrom(tableSource);
            }
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        return deleteStatement;
    }

    @Override
    public SQLCreateTableStatement parseCreateTable() {
        SAPHanaCreateTableParser parser = new SAPHanaCreateTableParser(this.exprParser);
        return parser.parseCreateTable();
    }
}
