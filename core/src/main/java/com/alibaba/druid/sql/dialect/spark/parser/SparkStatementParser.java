/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.spark.parser;

import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.Token;

/**
 * @author peiheng.qph
 * @version $Id: AntsparkStateMentParser.java, v 0.1 2018年09月14日 15:07 peiheng.qph Exp $
 */
public class SparkStatementParser extends HiveStatementParser {
    public SparkStatementParser(String sql) {
        super(new SparkExprParser(sql));
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new SparkCreateTableParser(this.exprParser);
    }

    protected void alterTableUnset(SQLAlterTableStatement stmt) {
        acceptIdentifier("TBLPROPERTIES");
        accept(Token.LPAREN);
        exprParser.names(stmt.getUnsetTableOptions(), stmt);
        accept(Token.RPAREN);
    }
}
