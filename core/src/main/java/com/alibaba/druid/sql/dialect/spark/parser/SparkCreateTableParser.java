/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.spark.parser;

import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.parser.HiveCreateTableParser;
import com.alibaba.druid.sql.dialect.spark.ast.SparkCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.util.FnvHash;

/**
 * @author peiheng.qph
 * @version $Id: AntsparkCreateTableParser.java, v 0.1 2018年09月14日 15:03 peiheng.qph Exp $
 */
public class SparkCreateTableParser extends HiveCreateTableParser {
    public SparkCreateTableParser(String sql) {
        super(new SparkExprParser(sql));
    }

    public SparkCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    protected void createTableBefore(SQLCreateTableStatement stmt) {
        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            stmt.setExternal(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.TEMPORARY)) {
            lexer.nextToken();
            stmt.setTemporary(true);
        }
    }

    private void parseRowFormat(SparkCreateTableStatement stmt) {
    }

    private void parseSortedBy(SparkCreateTableStatement stmt) {
    }

    public SQLSelectParser createSQLSelectParser() {
        return new SparkSelectParser(this.exprParser, selectListCache);
    }
}
