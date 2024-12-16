package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.bigquery.ast.BigQuerySelectQueryBlock;
import com.alibaba.druid.sql.parser.*;

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
        if (lexer.nextIf(Token.DISTINCT)) {
            x.setDistinct();
        }
        if (lexer.nextIf(Token.AS)) {
            acceptIdentifier("STRUCT");
            ((BigQuerySelectQueryBlock) x).setAsStruct(true);
        }
        if (lexer.nextIf(Token.WITH)) {
            acceptIdentifier("DIFFERENTIAL_PRIVACY");
            acceptIdentifier("OPTIONS");
            BigQuerySelectQueryBlock.DifferentialPrivacy clause = new BigQuerySelectQueryBlock.DifferentialPrivacy();
            exprParser.parseAssignItem(clause.getOptions(), clause);
            ((BigQuerySelectQueryBlock) x).setDifferentialPrivacy(clause);
        }
    }

    protected boolean parseSelectListFromError() {
        return false;
    }

    protected String tableAlias(boolean must) {
        Token tok = lexer.token();
        if (tok == Token.TABLE || tok == Token.UPDATE) {
            String alias = lexer.stringVal();
            lexer.nextToken();
            return alias;
        }
        return super.tableAlias(must);
    }

    protected void queryBefore(SQLSelectQueryBlock x) {
        if (lexer.token() == Token.WITH) {
            BigQuerySelectQueryBlock queryBlock = (BigQuerySelectQueryBlock) x;
            queryBlock.setWith(
                    this.parseWith()
            );
        }
    }
}
