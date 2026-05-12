package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class StarRocksSelectParser extends SQLSelectParser {
    public StarRocksSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.starrocks;
    }

    protected StarRocksExprParser createExprParser() {
        return new StarRocksExprParser(lexer);
    }

    /**
     * Parse StarRocks Join Hint.
     * <p>
     * StarRocks supports join hints using bracket syntax between the JOIN keyword and the right table:
     * <pre>
     *   ... JOIN [BROADCAST] t2 ON ...
     *   ... JOIN [SHUFFLE] t2 ON ...
     *   ... JOIN [BUCKET] t2 ON ...
     *   ... JOIN [COLOCATE] t2 ON ...
     * </pre>
     *
     * @see <a href="https://docs.starrocks.io/zh/docs/3.3/administration/Query_planning/">StarRocks Query Hint</a>
     */
    @Override
    protected void parseJoinHint(SQLJoinTableSource join) {
        if (lexer.token() == Token.LBRACKET) {
            lexer.nextToken();
            String hintName = lexer.stringVal();
            long hash = lexer.hashLCase();
            if (hash == FnvHash.Constants.BROADCAST
                    || hash == FnvHash.Constants.SHUFFLE
                    || hash == FnvHash.Constants.BUCKET
                    || hash == FnvHash.Constants.COLOCATE) {
                SQLExpr hintExpr = new SQLIdentifierExpr(hintName);
                SQLExprHint hint = new SQLExprHint(hintExpr);
                hint.setParent(join);
                join.getHints().add(hint);
                lexer.nextToken();
            }
            accept(Token.RBRACKET);
        }
    }
}
