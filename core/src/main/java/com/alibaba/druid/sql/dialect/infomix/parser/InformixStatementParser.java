package com.alibaba.druid.sql.dialect.infomix.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.*;

public class InformixStatementParser
        extends SQLStatementParser {
    public InformixStatementParser(String sql, SQLParserFeature... features) {
        super(sql, DbType.informix, features);
    }

    public InformixSelectParser createSQLSelectParser() {
        return new InformixSelectParser(this.exprParser, selectListCache);
    }
}
