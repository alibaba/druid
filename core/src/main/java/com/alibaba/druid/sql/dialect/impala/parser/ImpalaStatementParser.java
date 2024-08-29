package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.dialect.hive.parser.HiveSelectParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaInsertStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.List;

public class ImpalaStatementParser extends HiveStatementParser {
    {
        dbType = DbType.impala;
    }

    public ImpalaStatementParser(String sql, SQLParserFeature... features) {
        super(new ImpalaExprParser(sql, features));
    }

    public HiveSelectParser createSQLSelectParser() {
        return new ImpalaSelectParser(this.exprParser, selectListCache);
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new ImpalaCreateTableParser(this.exprParser);
    }

    public SQLCreateTableStatement parseCreateTable() {
        SQLCreateTableParser parser = new ImpalaCreateTableParser(this.exprParser);
        return parser.parseCreateTable();
    }

    @Override
    public ImpalaExprParser getExprParser() {
        return (ImpalaExprParser) exprParser;
    }

    @Override
    public SQLStatement parseUpsert() {
        ImpalaInsertStatement insertStatement = new ImpalaInsertStatement();

        if (lexer.token() == Token.UPSERT || lexer.identifierEquals("UPSERT")) {
            lexer.nextToken();
            insertStatement.setUpsert(true);
        }

        parseInsert0(insertStatement);
        return insertStatement;
    }

    @Override
    protected void parseInsert0(SQLInsertInto insertStatement) {
        parseInsert0Hints(insertStatement, true);
        parseInsert0(insertStatement, true);
    }

    @Override
    protected void parseInsert0Hints(SQLInsertInto insertStatement, boolean isInsert) {
        if (insertStatement instanceof ImpalaInsertStatement) {
            ImpalaInsertStatement stmt = (ImpalaInsertStatement) insertStatement;
            List<SQLHint> hints = isInsert ? stmt.getInsertHints() : stmt.getSelectHints();
            this.getExprParser().parseHints(hints);
        }
    }
}
