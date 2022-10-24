package com.alibaba.druid.sql.dialect.saphana.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;

/**
 * @author nukiyoam
 */
public class SAPHanaCreateTableParser extends SQLCreateTableParser {
    public SAPHanaCreateTableParser(String sql) {
        super(new SAPHanaExprParser(sql));
    }

    public SAPHanaCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    @Override
    public SAPHanaExprParser getExprParser() {
        return (SAPHanaExprParser) exprParser;
    }

    @Override
    protected SQLCreateTableStatement newCreateStatement() {
        return new SAPHanaCreateTableStatement();
    }

}
