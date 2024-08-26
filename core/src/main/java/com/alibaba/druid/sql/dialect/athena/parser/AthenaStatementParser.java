package com.alibaba.druid.sql.dialect.athena.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoStatementParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class AthenaStatementParser extends PrestoStatementParser {
    public AthenaStatementParser(String sql) {
        super(sql);
        this.dbType = DbType.athena;
    }

    public AthenaStatementParser(String sql, SQLParserFeature... features) {
        super(sql, features);
        this.dbType = DbType.athena;
    }

    public AthenaStatementParser(Lexer lexer) {
        super(lexer);
        this.dbType = DbType.athena;
    }

    @Override
    public AthenaSelectParser createSQLSelectParser() {
        return new AthenaSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new AthenaCreateTableParser(this.exprParser);
    }

}
