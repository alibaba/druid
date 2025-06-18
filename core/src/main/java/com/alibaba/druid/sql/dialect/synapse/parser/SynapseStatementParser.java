package com.alibaba.druid.sql.dialect.synapse.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLSelectParser;

public class SynapseStatementParser extends SQLServerStatementParser {
    public SynapseStatementParser(String sql) {
        super(sql);
        this.dbType = DbType.synapse;
    }

    public SynapseStatementParser(String sql, SQLParserFeature... features) {
        super(sql, features);
        this.dbType = DbType.synapse;
    }

    public SynapseStatementParser(Lexer lexer) {
        super(lexer);
        this.dbType = DbType.synapse;
    }

    @Override
    public SQLSelectParser createSQLSelectParser() {
        return new SynapseSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new SynapseCreateTableParser(this.exprParser);
    }
}
