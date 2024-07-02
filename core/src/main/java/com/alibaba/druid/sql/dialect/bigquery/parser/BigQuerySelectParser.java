package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQuerySelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class BigQuerySelectParser extends SQLSelectParser {
    public BigQuerySelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.bigquery;
    }

    protected SQLExprParser createExprParser() {
        return new BigQueryExprParser(lexer);
    }

    @Override
    protected BigQuerySelectQueryBlock createSelectQueryBlock() {
        return new BigQuerySelectQueryBlock();
    }

    protected void querySelectListBefore(SQLSelectQueryBlock x) {
        if (lexer.nextIf(Token.WITH)) {
            acceptIdentifier("DIFFERENTIAL_PRIVACY");
            acceptIdentifier("OPTIONS");
            BigQuerySelectQueryBlock.DifferentialPrivacy clause = new BigQuerySelectQueryBlock.DifferentialPrivacy();
            exprParser.parseAssignItem(clause.getOptions(), clause);
            ((BigQuerySelectQueryBlock) x).setDifferentialPrivacy(clause);
        }
    }
}
