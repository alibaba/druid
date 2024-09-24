package com.alibaba.druid.sql.dialect.redshift.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

public class RedshiftStatementParser
        extends PGSQLStatementParser {
    public RedshiftStatementParser(RedshiftExprParser parser) {
        super(parser);
        dbType = DbType.redshift;
    }

    public RedshiftStatementParser(String sql, SQLParserFeature... features) {
        this(new RedshiftExprParser(sql, features));
    }

    @Override
    public RedshiftSelectParser createSQLSelectParser() {
        return new RedshiftSelectParser(this.exprParser, selectListCache);
    }

    public RedshiftCreateTableParser getSQLCreateTableParser() {
        return new RedshiftCreateTableParser(this.exprParser);
    }

    protected void createOptionSkip() {
        lexer.nextIf(Token.LOCAL);
        super.createOptionSkip();
    }
}
