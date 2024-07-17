package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.dialect.hive.parser.HiveSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.Token;

import java.util.ArrayList;
import java.util.List;

public class ImpalaSelectParser extends HiveSelectParser {
    {
        dbType = DbType.impala;
    }
    public ImpalaSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    @Override
    protected void parseJoinHint(SQLJoinTableSource join) {
        List<SQLHint> hints = new ArrayList<>();
        if (lexer.token() == Token.HINT) {
            this.exprParser.parseHints(hints);
        } else if (lexer.token() == Token.LBRACKET) {
            lexer.nextToken();
            hints.add(new SQLExprHint(expr()));
            accept(Token.RBRACKET);
        }
        join.setHints(hints);
    }
}
