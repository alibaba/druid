package com.alibaba.druid.sql.dialect.hologres.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class HologresExprParser
        extends PGExprParser {
    public HologresExprParser(String sql, SQLParserFeature... features) {
        super(new HologresLexer(sql, features));
        lexer.nextToken();
        dbType = DbType.hologres;
    }

    public HologresExprParser(Lexer lexer) {
        super(lexer);
        dbType = DbType.hologres;
    }

    @Override
    protected void parseAssignItemOnComma(SQLExpr sqlExpr, SQLAssignItem item, SQLObject parent) {
        item.setValue(sqlExpr);
    }
}
