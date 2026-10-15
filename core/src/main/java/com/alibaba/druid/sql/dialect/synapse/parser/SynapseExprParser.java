package com.alibaba.druid.sql.dialect.synapse.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class SynapseExprParser extends SQLServerExprParser {
    public SynapseExprParser(String sql, SQLParserFeature... features) {
        super(sql, features);
        this.dbType = DbType.synapse;
    }

    public SynapseExprParser(Lexer lexer) {
        super(lexer);
        this.dbType = DbType.synapse;
    }
}
