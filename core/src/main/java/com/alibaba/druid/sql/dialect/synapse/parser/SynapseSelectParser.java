package com.alibaba.druid.sql.dialect.synapse.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;

public class SynapseSelectParser extends SQLServerSelectParser {
    public SynapseSelectParser(SQLExprParser exprParser) {
        super(exprParser);
        this.dbType = DbType.synapse;
    }

    public SynapseSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        this.dbType = DbType.synapse;
    }

    public SynapseSelectParser(String sql) {
        super(sql);
        this.dbType = DbType.synapse;
    }

    @Override
    protected SynapseExprParser createExprParser() {
        return new SynapseExprParser(this.lexer);
    }
}
