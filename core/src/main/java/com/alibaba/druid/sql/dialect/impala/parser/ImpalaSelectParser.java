package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.hive.parser.HiveSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;

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
    protected SQLExprParser createExprParser() {
        return new ImpalaExprParser(lexer);
    }

    @Override
    protected void parseJoinHint(SQLJoinTableSource join) {
        List<SQLHint> hints = new ArrayList<>();
        this.exprParser.parseHints(hints);
        join.setHints(hints);
    }

    @Override
    protected void parseBeforeSelectList(SQLSelectQueryBlock queryBlock) {
        if (lexer.nextIfIdentifier("STRAIGHT_JOIN")) {
            queryBlock.setDistionOption(SQLSetQuantifier.STRAIGHT_JOIN);
        } else {
            super.parseBeforeSelectList(queryBlock);
        }
    }
}
