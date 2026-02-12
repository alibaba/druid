package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class SnowflakeSelectParser extends SQLSelectParser {
    public SnowflakeSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.snowflake;
    }

    @Override
    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock(dbType);

        if (lexer.nextIf(Token.SELECT)) {
            if (lexer.nextIf(Token.DISTINCT)) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            } else if (lexer.nextIf(Token.ALL)) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            }

            parseSelectList(queryBlock);
        }

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);

        qualify(queryBlock);

        SQLOrderBy orderBy = parseOrderBy();
        if (orderBy != null) {
            queryBlock.setOrderBy(orderBy);
        }

        if (lexer.nextIf(Token.LIMIT)) {
            queryBlock.setLimit(this.exprParser.parseLimit());
        }

        return queryRest(queryBlock, acceptUnion);
    }
}
