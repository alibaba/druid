package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class DorisCreateTableParser
        extends StarRocksCreateTableParser {
    public DorisCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.doris;
    }

    public SQLPartitionBy parsePartitionBy() {
        if (lexer.nextIfIdentifier("AUTO")) {
            SQLPartitionBy partitionBy = super.parsePartitionBy();
            if (partitionBy != null) {
                partitionBy.setAuto(true);
            }
            return partitionBy;
        } else {
            return super.parsePartitionBy();
        }
    }
}
