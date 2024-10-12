package com.alibaba.druid.sql.dialect.redshift.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLTop;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSelectParser;
import com.alibaba.druid.sql.dialect.redshift.stmt.RedshiftSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.Token;

public class RedshiftSelectParser
        extends PGSelectParser {
    public RedshiftSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.redshift;
    }

    protected RedshiftExprParser createExprParser() {
        return new RedshiftExprParser(lexer);
    }

    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select, acceptUnion);
        }

        RedshiftSelectQueryBlock queryBlock = new RedshiftSelectQueryBlock();

        if (lexer.token() == Token.SELECT) {
            lexer.nextToken();

            if (lexer.token() == Token.TOP) {
                SQLTop top = this.createExprParser().parseTop();
                queryBlock.setTop(top);
            }

            if (lexer.token() == Token.DISTINCT) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                lexer.nextToken();
            } else if (lexer.token() == Token.ALL) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                lexer.nextToken();
            }

            parseSelectList(queryBlock);
        }

        parseInto(queryBlock);

        parseFrom(queryBlock);

        //TODO(lingo): Support oracle-style outer join, see https://docs.aws.amazon.com/redshift/latest/dg/r_WHERE_oracle_outer.html
        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        parseGroupBy(queryBlock);

        qualify(queryBlock);

        parseSortBy(queryBlock);

        parseFetchClause(queryBlock);

        return queryRest(queryBlock, acceptUnion);
    }

    protected void parseInto(RedshiftSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
            if (lexer.nextIfIdentifier("TEMP")) {
                queryBlock.setInsertTemp(true);
            }

            if (lexer.nextIfIdentifier("TEMPORARY")) {
                queryBlock.setInsertTemporary(true);
            }

            if (lexer.nextIf(Token.TABLE)) {
                queryBlock.setInsertTable(true);
            }

            SQLTableSource into = this.parseTableSource();
            queryBlock.setInto((SQLExprTableSource) into);
        }
    }
}
